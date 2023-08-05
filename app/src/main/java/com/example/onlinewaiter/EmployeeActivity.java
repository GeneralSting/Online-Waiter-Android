package com.example.onlinewaiter;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.Cafe;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CafeCurrentOrder;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Models.RegisteredCountry;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.CustomAlertDialog;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Services.OnAppKilledService;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.employeeUI.GlobalViewModel.EmployeeViewModel;
import com.example.onlinewaiter.employeeUI.cafeUpdate.CafeUpdateViewModel;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.example.onlinewaiter.employeeUI.order.OrderViewModel;
import com.example.onlinewaiter.employeeUI.pendingOrders.PendingOrdersViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinewaiter.databinding.ActivityEmployeeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    //Activity view
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityEmployeeBinding binding;
    private NavigationView navigationView;
    private TextView tvCafeName, tvNumberMemoryWord;

    //gloabl variables/objects
    private Boolean isCategoriesDisplayed = true;
    private int employee_nav_selected = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_MENU;
    private int notificationId = 0;
    private ToastMessage toastMessage;
    private FirebaseRefPaths firebaseRefPaths;
    private NotificationManagerCompat notificationManagerCompat;
    private NotificationCompat.Builder builder;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private AppError appError;
    private MenuViewModel menuViewModel;
    private OrderViewModel orderViewModel;
    private CafeUpdateViewModel cafeUpdateViewModel;
    private PendingOrdersViewModel pendingOrdersViewModel;
    private static long back_pressed;

    //firebase
    private ChildEventListener cafeChildsEventListener, cafeCurrentOrdersListener, registeredNumberListener;
    DatabaseReference cafeRef, cafeCurrentOrdersRef, registeredNumberRef;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference cafeDrinksCategoriesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        String employeeCafeId = bundle.getString(AppConstValue.bundleConstValue.LOGIN_CAFE_ID);
        String phoneNumber = bundle.getString(AppConstValue.bundleConstValue.LOGIN_PHONE_NUMBER);
        String numberMemoryWord = bundle.getString(AppConstValue.bundleConstValue.NUMBER_MEMORY_WORD);
        int registeredNumberWeb = bundle.getInt(AppConstValue.bundleConstValue.REGISTERED_NUMBER_WEB);

        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        cafeUpdateViewModel = new ViewModelProvider(this).get(CafeUpdateViewModel.class);
        pendingOrdersViewModel = new ViewModelProvider(this).get(PendingOrdersViewModel.class);
        toastMessage = new ToastMessage(this);
        firebaseRefPaths = new FirebaseRefPaths(this);

        //when app is closed in background
        if ((Objects.equals(AppConstValue.bundleConstValue.LOGIN_CAFE_ID, AppConstValue.variableConstValue.EMPTY_VALUE) ||
                Objects.equals(AppConstValue.bundleConstValue.LOGIN_PHONE_NUMBER, AppConstValue.variableConstValue.EMPTY_VALUE))) {
            logout(false);
        } else {
            menuViewModel.setCafeId(employeeCafeId);
            menuViewModel.setPhoneNumber(phoneNumber);
            orderViewModel.setRegisteredNumberWeb(registeredNumberWeb > 0);
            orderViewModel.setNumberMemoryWord(numberMemoryWord);
        }

        startService(new Intent(getBaseContext(), OnAppKilledService.class));
        setNavigationDrawer();
        creatingNotificationChannel();
        setDisplayingObserver();
        collectCafeCountry();
        setFabAction();
        setListeners();
    }

    private void setNavigationDrawer() {
        setSupportActionBar(binding.appBarEmployee.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navEmployeeView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_employee_menu,
                R.id.nav_employee_orders,
                R.id.nav_employee_pending_orders,
                R.id.nav_employee_cafe_update)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_employee);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_employee_logout).setOnMenuItemClickListener(menuItem -> {
            logout(false);
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_employee_menu).setOnMenuItemClickListener(menuItem -> {
            employee_nav_selected = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_MENU;
            invalidateMenu();
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_employee_orders).setOnMenuItemClickListener(menuItem -> {
            employee_nav_selected = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_OTHERS;
            invalidateMenu();
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_employee_pending_orders).setOnMenuItemClickListener(menuItem -> {
            employee_nav_selected = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_PENDING_ORDERS;
            invalidateMenu();
            return false;
        });

        navigationView.getMenu().findItem(R.id.nav_employee_cafe_update).setOnMenuItemClickListener(menuItem -> {
            employee_nav_selected = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_OTHERS;
            invalidateMenu();
            return false;
        });

        getCafeInfo(navigationView);
    }

    private void getCafeInfo(NavigationView navigationView) {
        DatabaseReference cafeRef = firebaseDatabase.getReference(firebaseRefPaths.getCafe());
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cafe cafe = snapshot.getValue(Cafe.class);
                orderViewModel.setCafeTablesNumber(cafe.getCafeTables());
                View navHeaderView = navigationView.getHeaderView(0);
                tvCafeName = (TextView) navHeaderView.findViewById(R.id.tvEmployeeNavHeader);
                tvNumberMemoryWord = (TextView) navHeaderView.findViewById(R.id.tvEmployeeNumberAlias);
                tvNumberMemoryWord.setText(orderViewModel.getNumberMemoryWord().getValue());
                if (!cafe.getCafeName().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                    tvCafeName.setText(cafe.getCafeName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void creatingNotificationChannel() {
        PendingIntent notificationEmptyIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_IMMUTABLE);

        builder = new NotificationCompat.Builder(this, AppConstValue.notificationConstValue.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.nav_header_img)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(notificationEmptyIntent);

        createNotificationChannel();
        notificationManagerCompat = NotificationManagerCompat.from(this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(
                AppConstValue.notificationConstValue.NOTIFICATION_CHANNEL_ID, AppConstValue.notificationConstValue.NOTIFICATION_CHANNEL_NAME, importance);
        channel.setDescription(AppConstValue.notificationConstValue.NOTIFICATION_CHANNEL_DESCRIPTION);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void setDisplayingObserver() {
        final Observer<Boolean> displayingCategoriesObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isCategoriesDisplayed = aBoolean;
            }
        };
        menuViewModel.getDisplayingCategories().observe(this, displayingCategoriesObserver);
    }

    private void collectCafeCountry() {
        DatabaseReference cafeCountryRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCountry());
        cafeCountryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot countrySnapshot) {
                cafeUpdateViewModel.setCafeCountry(countrySnapshot.getValue(String.class));
                collectCountryStandards(countrySnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void collectCountryStandards(String countryCode) {
        DatabaseReference refRegisteredCountry = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredCountry(countryCode));
        refRegisteredCountry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot registeredCountrySnapshot) {
                if(!registeredCountrySnapshot.exists()) {
                    ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                    serverAlertDialog.makeAlertDialog();

                    String currentDateTime = simpleDateFormat.format(new Date());
                    appError = new AppError(
                            menuViewModel.getCafeId().getValue(),
                            menuViewModel.getPhoneNumber().getValue(),
                            AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                            AppErrorMessages.ErrorsMessage.FIREBASE_PATH_REGISTERED_COUNTRY,
                            currentDateTime
                    );
                    appError.sendError(appError);
                    return;
                }
                EmployeeViewModel employeeViewModel = new ViewModelProvider(EmployeeActivity.this).get(EmployeeViewModel.class);
                RegisteredCountry registeredCountry = registeredCountrySnapshot.getValue(RegisteredCountry.class);
                employeeViewModel.setCafeCurrency(registeredCountry.getCurrency());
                employeeViewModel.setCafeDateTimeFormat(registeredCountry.getDateTimeFormat());
                employeeViewModel.setCafeDecimalSeperator(registeredCountry.getDecimalSeperator());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void setFabAction() {
        binding.appBarEmployee.ordersNumberFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cartDrinksNumber = 0;
                HashMap<String, CafeBillDrink> orderDrinks = orderViewModel.getDrinksInOrder().getValue();
                if (orderDrinks != null && !orderDrinks.isEmpty()) {
                    for (String key : orderDrinks.keySet()) {
                        CafeBillDrink cafeBillDrink = orderDrinks.get(key);
                        cartDrinksNumber += cafeBillDrink.getDrinkAmount();
                    }
                    Snackbar.make(view, getResources().getString(R.string.act_employee_order_fab_txt) + AppConstValue.characterConstValue.CHARACTER_SPACING + cartDrinksNumber,
                                    Snackbar.LENGTH_LONG)
                            .setAction(AppConstValue.variableConstValue.FAB_DRINKS_ACTION_TEXT, null).show();
                }
                else {
                    Snackbar.make(view, getResources().getString(R.string.act_employee_empty_order_fab_txt), Snackbar.LENGTH_LONG)
                            .setAction(AppConstValue.variableConstValue.FAB_DRINKS_ACTION_TEXT, null).show();
                }
            }
        });
    }

    private void setListeners() {
        cafeRef = firebaseDatabase.getReference(firebaseRefPaths.getCafe());
        cafeChildsEventListener = cafeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot cafeSnapshot, @Nullable String previousChildName) {
                if(Objects.requireNonNull(cafeSnapshot.getKey()).equals(firebaseRefPaths.getCafeCategoriesChild())) {
                    checkDrinkDeletion();
                    checkSearchedDrinkDeletion();
                }
                else if(Objects.requireNonNull(cafeSnapshot.getKey().equals(firebaseRefPaths.getCafeNameChild()))) {
                    tvCafeName.setText(cafeSnapshot.getValue(String.class));
                }
                else if(Objects.requireNonNull(cafeSnapshot.getKey().equals(firebaseRefPaths.getCafeTablesChild()))) {
                    orderViewModel.setCafeTablesNumber(cafeSnapshot.getValue(Integer.class));
                }
                else if(Objects.requireNonNull(cafeSnapshot.getKey().equals(firebaseRefPaths.getCafeCountryChild()))) {
                    cafeUpdateViewModel.setCafeCountry(cafeSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot cafeSnapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });

        cafeCurrentOrdersRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCurrentOrders());
        cafeCurrentOrdersListener = cafeCurrentOrdersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String previousChildName) {
                CafeCurrentOrder cafeCurrentOrder = orderSnapshot.getValue(CafeCurrentOrder.class);
                if(cafeCurrentOrder.getCurrentOrderDelivererEmployee().equals(menuViewModel.getPhoneNumber().getValue()) &&
                cafeCurrentOrder.getCurrentOrderStatus() == AppConstValue.orderStatusConstValue.ORDER_STATUS_READY) {
                    if (ActivityCompat.checkSelfPermission(EmployeeActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        builder.setContentTitle(getResources().getString(R.string.act_employee_notification_header) + AppConstValue.characterConstValue.CHARACTER_SPACING +
                                String.valueOf(cafeCurrentOrder.getCurrentOrderTableNumber()));
                        notificationManagerCompat.notify(notificationId++, builder.build());
                    }
                }
                if (cafeCurrentOrder.getCurrentOrderDelivererEmployee().equals(menuViewModel.getPhoneNumber().getValue()) &&
                        cafeCurrentOrder.getCurrentOrderStatus() == AppConstValue.orderStatusConstValue.ORDER_STATUS_DECLINED &&
                        ActivityCompat.checkSelfPermission(EmployeeActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                        cafeCurrentOrder.getCurrentOrderMessage() == null) {
                    builder.setContentTitle(getResources().getString(R.string.act_employee_deleted_notification_header) + AppConstValue.characterConstValue.CHARACTER_SPACING +
                            String.valueOf(cafeCurrentOrder.getCurrentOrderTableNumber()));
                    notificationManagerCompat.notify(notificationId++, builder.build());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });

        registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredNumber(menuViewModel.getCafeId().getValue(), menuViewModel.getPhoneNumber().getValue()));
        registeredNumberListener = registeredNumberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot registeredNumberSnapshot, @Nullable String previousChildName) {
                if(Objects.equals(registeredNumberSnapshot.getKey(), firebaseRefPaths.getRegisteredNumberAllowedChild())) {
                    if(Boolean.FALSE.equals(registeredNumberSnapshot.getValue(boolean.class))) {
                        logout(true);
                    }
                }
                else if(Objects.equals(registeredNumberSnapshot.getKey(), firebaseRefPaths.getRegisteredNumberWebChild())) {
                    int webRegisteredNumber = registeredNumberSnapshot.getValue(int.class);
                    orderViewModel.setRegisteredNumberWeb(webRegisteredNumber > 0);
                }
                else if(Objects.equals(registeredNumberSnapshot.getKey(), firebaseRefPaths.getRNMemoryWordChild())) {
                    tvNumberMemoryWord.setText(registeredNumberSnapshot.getValue(String.class));
                    orderViewModel.setNumberMemoryWord(registeredNumberSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                logout(true);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkDrinkDeletion() {
        HashMap<String, CafeBillDrink> orderDrinks = orderViewModel.getDrinksInOrder().getValue();
        final boolean[] deletedDrinkFounded = {false};
        if(orderDrinks != null && !orderDrinks.isEmpty()) {
            for(String key : orderDrinks.keySet()) {
                CafeBillDrink cafeBillDrink = orderDrinks.get(key);
                if(!deletedDrinkFounded[0]) {
                    cafeDrinksCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getCategoryDrink(cafeBillDrink.getCategoryId(), key));
                    cafeDrinksCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot categoryDrinkSnapshot) {
                            if(!categoryDrinkSnapshot.exists()) {
                                deletedDrinkFounded[0] = true;
                                if(orderDrinks.get(key) != null) {
                                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(EmployeeActivity.this,
                                            getResources().getString(R.string.act_employee_drink_deleted_title) + AppConstValue.characterConstValue.CHARACTER_SPACING +
                                                    orderDrinks.get(key).getDrinkName(),
                                            getResources().getString(R.string.act_employee_drink_deleted_body),
                                            getResources().getDrawable(R.drawable.modal_danger));
                                    customAlertDialog.makeAlertDialog();

                                    HashMap<String, CafeBillDrink> updatedDrinksInOrder = orderViewModel.getDrinksInOrder().getValue();
                                    updatedDrinksInOrder.remove(key);
                                    orderViewModel.setDrinksInOrder(updatedDrinksInOrder);
                                    if(orderViewModel.getCheckDrinksOrder().getValue() == null) {
                                        orderViewModel.setCheckDrinksOrder(1);
                                    }
                                    else {
                                        orderViewModel.setCheckDrinksOrder(orderViewModel.getCheckDrinksOrder().getValue() + 1);
                                    }
                                }
                            }
                            else if(cafeBillDrink.getDrinkAmount() > categoryDrinkSnapshot.getValue(CategoryDrink.class).getCategoryDrinkQuantity()) {
                                CategoryDrink categoryDrink = categoryDrinkSnapshot.getValue(CategoryDrink.class);
                                if(categoryDrinkSnapshot.getValue(CategoryDrink.class).getCategoryDrinkQuantity() == 0) {
                                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(EmployeeActivity.this,
                                            getResources().getString(R.string.act_employee_drink_deleted_title),
                                            getResources().getString(R.string.act_employee_drink_no_quantity_body),
                                            getResources().getDrawable(R.drawable.modal_no_quantity));
                                    customAlertDialog.makeAlertDialog();
                                    HashMap<String, CafeBillDrink> updatedDrinksInOrder = orderViewModel.getDrinksInOrder().getValue();
                                    updatedDrinksInOrder.remove(key);
                                    orderViewModel.setDrinksInOrder(updatedDrinksInOrder);
                                }
                                else {
                                    CustomAlertDialog customAlertDialog = new CustomAlertDialog(EmployeeActivity.this,
                                            getResources().getString(R.string.act_employee_drink_quantity_change_title),
                                            getResources().getString(R.string.act_employee_drink_quantity_change_body),
                                            getResources().getDrawable(R.drawable.modal_unavailable_quantity));
                                    customAlertDialog.makeAlertDialog();
                                    HashMap<String, CafeBillDrink> updatedDrinksInOrder = orderViewModel.getDrinksInOrder().getValue();
                                    updatedDrinksInOrder.get(key).setDrinkAmount(categoryDrink.getCategoryDrinkQuantity());
                                    orderViewModel.setDrinksInOrder(updatedDrinksInOrder);
                                }
                                if(cafeBillDrink.getDrinkQuantity() != categoryDrink.getCategoryDrinkQuantity()) {
                                    cafeBillDrink.setDrinkQuantity(categoryDrink.getCategoryDrinkQuantity());
                                }
                                if(orderViewModel.getCheckDrinksOrder().getValue() == null) {
                                    orderViewModel.setCheckDrinksOrder(1);
                                }
                                else {
                                    orderViewModel.setCheckDrinksOrder(orderViewModel.getCheckDrinksOrder().getValue() + 1);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                            serverAlertDialog.makeAlertDialog();

                            String currentDateTime = simpleDateFormat.format(new Date());
                            appError = new AppError(
                                    menuViewModel.getCafeId().getValue(),
                                    menuViewModel.getPhoneNumber().getValue(),
                                    AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                                    error.getMessage().toString(),
                                    currentDateTime
                            );
                            appError.sendError(appError);
                        }
                    });
                }
                else {
                    break;
                }
            }
        }
    }

    private void checkSearchedDrinkDeletion() {
        final boolean[] drinksSearched = {false};
        if(menuViewModel.getSearchedDrinks().getValue() != null && !menuViewModel.getSearchedDrinks().getValue().isEmpty()) {
            drinksSearched[0] = true;
            for(String key : menuViewModel.getSearchedDrinks().getValue().keySet()) {
                cafeDrinksCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCategories());
                cafeDrinksCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot cafeDrinksCategoriesSnapshot) {
                        boolean drinkDeleted = true;
                        for(DataSnapshot categorySnapshot : cafeDrinksCategoriesSnapshot.getChildren()) {
                            for (DataSnapshot drinkSnapshot : categorySnapshot.child(firebaseRefPaths.getCategoryDrinksChild()).getChildren()) {
                                if(Objects.equals(drinkSnapshot.getKey(), key)) {
                                    drinkDeleted = false;
                                }
                            }
                        }
                        if(drinkDeleted) {
                            HashMap<String, CategoryDrink> updatedSearchedDrinks = menuViewModel.getSearchedDrinks().getValue();
                            updatedSearchedDrinks.remove(key);
                            menuViewModel.setSearchedDrinks(updatedSearchedDrinks);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                        serverAlertDialog.makeAlertDialog();

                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                menuViewModel.getCafeId().getValue(),
                                menuViewModel.getPhoneNumber().getValue(),
                                AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                                error.getMessage().toString(),
                                currentDateTime
                        );
                        appError.sendError(appError);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employee, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.itemSearch);

        SearchView svEmployeeAppBar = (SearchView) searchMenuItem.getActionView();
        final int EMPLOYEE_NAV_MENU = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_MENU;
        final int EMPLOYEE_NAV_PENDING_ORDERS = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_PENDING_ORDERS;
        switch(employee_nav_selected) {
            case EMPLOYEE_NAV_MENU: {
                searchMenuItem.setVisible(true);
                svEmployeeAppBar.setQueryHint(getResources().getString(R.string.act_employee_search_query_orders));
                svEmployeeAppBar.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                svEmployeeAppBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        if(!query.equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                            menuDrinksQuery(query);
                        }
                        return false;
                    }
                });
                break;
            }
            case EMPLOYEE_NAV_PENDING_ORDERS: {
                searchMenuItem.setVisible(true);
                svEmployeeAppBar.setQueryHint(getResources().getString(R.string.act_employee_search_query_menu));
                svEmployeeAppBar.setInputType(InputType.TYPE_CLASS_NUMBER);
                svEmployeeAppBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        if(query.equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                            pendingOrdersViewModel.setSearchedOrder(Integer.parseInt(AppConstValue.variableConstValue.ZERO_VALUE));
                        }
                        else {
                            pendingOrdersViewModel.setSearchedOrder(Integer.parseInt(query));
                        }
                        return false;
                    }
                });
                break;
            }
            default: {
                searchMenuItem.setVisible(false);
                break;
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void menuDrinksQuery(String query) {
        HashMap<String, CategoryDrink> searchedDrinks = new HashMap<>();
        DatabaseReference cafeRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCategories());
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeCategoriesSnapshot) {
                for(DataSnapshot categorySnapshot : cafeCategoriesSnapshot.getChildren()) {
                    DataSnapshot drinksSnapshot = categorySnapshot.child(firebaseRefPaths.getCategoryDrinksChild());
                    for(DataSnapshot drinkSnapshot : drinksSnapshot.getChildren()) {
                        CategoryDrink categoryDrink = drinkSnapshot.getValue(CategoryDrink.class);
                        if(categoryDrink.getCategoryDrinkName() != null
                                && categoryDrink.getCategoryDrinkName().toLowerCase().contains(query.toLowerCase())) {
                            CategoryDrink drinkWithCategoryId = new CategoryDrink(
                                    categorySnapshot.getKey(),
                                    categoryDrink.getCategoryDrinkDescription(),
                                    categoryDrink.getCategoryDrinkImage(),
                                    categoryDrink.getCategoryDrinkName(),
                                    categoryDrink.getCategoryDrinkPrice(),
                                    categoryDrink.getCategoryDrinkQuantity());
                            searchedDrinks.put(drinkSnapshot.getKey(), drinkWithCategoryId);
                        }
                    }
                }
                menuViewModel.setSearchedDrinks(searchedDrinks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void removeListeners() {
        if (cafeRef != null) {
            cafeRef.removeEventListener(cafeChildsEventListener);
        }
        if (cafeCurrentOrdersRef != null) {
            cafeCurrentOrdersRef.removeEventListener(cafeCurrentOrdersListener);
        }
        if (registeredNumberRef != null) {
            registeredNumberRef.removeEventListener(registeredNumberListener);
        }
    }

    private void logout(boolean showChangeDialog) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(EmployeeActivity.this, MainActivity.class);
        if(showChangeDialog) {
            intent.putExtra(AppConstValue.bundleConstValue.LOGOUT_NUMBER_CHANGE, AppConstValue.variableConstValue.LOGOUT_NUMBER_CHANGE);
        }
        if(currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //if category drinks is displayed then close RV category drinks and show RV categories
        if (navigationView.getMenu().findItem(R.id.nav_employee_menu).isChecked()) {
            if (isCategoriesDisplayed) {
                if (back_pressed + AppConstValue.variableConstValue.EXIT_TIME_DELAY > System.currentTimeMillis()) {
                    super.onBackPressed();
                    logout(false);
                } else {
                    toastMessage.showToast(getResources().getString(R.string.act_employee_back_button_pressed), 0);
                }
                back_pressed = System.currentTimeMillis();
            } else {
                MenuViewModel menuViewModel = new ViewModelProvider(EmployeeActivity.this).get(MenuViewModel.class);
                menuViewModel.setDisplayingCategories(true);
            }
        }
        else if(navigationView.getMenu().findItem(R.id.nav_employee_cafe_update).isChecked()) {
            switch (cafeUpdateViewModel.getCafeUpdateRvDispalyed().getValue()) {
                case 0:
                    if(back_pressed + AppConstValue.variableConstValue.EXIT_TIME_DELAY > System.currentTimeMillis()) {
                        super.onBackPressed();
                        logout(false);
                    }
                    else {
                        toastMessage.showToast(getResources().getString(R.string.act_employee_back_button_pressed), 0);
                    }
                    back_pressed = System.currentTimeMillis();
                    break;
                case 1:
                    cafeUpdateViewModel.setCafeUpdateDisplayChange(true);
                    cafeUpdateViewModel.setCafeUpdateRvDisplayed(AppConstValue.recyclerViewDisplayed.UPDATE_NON_DISPLAYED);
                    break;
                case 2:
                    cafeUpdateViewModel.setCafeUpdateDisplayChange(true);
                    cafeUpdateViewModel.setCafeUpdateRvDisplayed(AppConstValue.recyclerViewDisplayed.UPDATE_CATEGORIES_DISPLAYED);
                    break;
            }
        }
        else {
            if(back_pressed + AppConstValue.variableConstValue.EXIT_TIME_DELAY > System.currentTimeMillis()) {
                super.onBackPressed();
                logout(false);
            }
            else {
                toastMessage.showToast(getResources().getString(R.string.act_employee_back_button_pressed), 0);
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_employee);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListeners();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}