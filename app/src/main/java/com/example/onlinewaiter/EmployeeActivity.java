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
import com.example.onlinewaiter.Models.CafeDrinksCategory;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.CustomAlertDialog;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Services.OnAppKilledService;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.employeeUI.cafeUpdate.CafeUpdateViewModel;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.example.onlinewaiter.employeeUI.order.OrderViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    //Activity view
    private AppBarConfiguration mAppBarConfiguration;
    private SearchView svEmployeeAppBar;
    private ActivityEmployeeBinding binding;
    NavigationView navigationView;
    TextView tvCafeName;

    //gloabl variables/objects
    private Boolean isCategoriesDisplayed = true;
    private int employee_nav_selected = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_MENU;
    String employeeCafeId, phoneNumber;
    ToastMessage toastMessage;
    FirebaseRefPaths firebaseRefPaths;
    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;
    int notificationId = 0;
    private final int EMPLOYEE_NAV_MENU = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_MENU;
    private final int EMPLOYEE_NAV_PENDING_ORDERS = AppConstValue.employeeNavigationSelected.EMPLOYEE_NAV_PENDING_ORDERS;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
    private AppError appError;


    //firebase
    private ChildEventListener cafeChildsEventListener, cafeCurrentOrdersListener;
    DatabaseReference cafeRef, cafeCurrentOrdersRef;

    //viewModels
    private MenuViewModel menuViewModel;
    private OrderViewModel orderViewModel;
    private CafeUpdateViewModel cafeUpdateViewModel;

    //other
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        cafeUpdateViewModel = new ViewModelProvider(this).get(CafeUpdateViewModel.class);

        Bundle bundle = getIntent().getExtras();
        employeeCafeId = bundle.getString(AppConstValue.bundleConstValue.BUNDLE_CAFE_ID);
        phoneNumber = bundle.getString(AppConstValue.bundleConstValue.BUNDLE_PHONE_NUMBER);

        //when app is closed in background
        if ((Objects.equals(AppConstValue.bundleConstValue.BUNDLE_CAFE_ID, AppConstValue.variableConstValue.EMPTY_VALUE) ||
                Objects.equals(AppConstValue.bundleConstValue.BUNDLE_PHONE_NUMBER, AppConstValue.variableConstValue.EMPTY_VALUE))) {
            logout();
        } else {
            menuViewModel.setCafeId(employeeCafeId);
            menuViewModel.setPhoneNumber(phoneNumber);
        }

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

        startService(new Intent(getBaseContext(), OnAppKilledService.class));
        toastMessage = new ToastMessage(this);
        firebaseRefPaths = new FirebaseRefPaths(this);
        //liveData for onBackPressed
        final Observer<Boolean> displayingCategoriesObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isCategoriesDisplayed = aBoolean;
            }
        };
        menuViewModel.getDisplayingCategories().observe(this, displayingCategoriesObserver);

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
                    Snackbar.make(view, getResources().getString(R.string.act_employee_order_fab_txt) + AppConstValue.characterConstValue.CHARACTER_SPACING + cartDrinksNumber
                                    , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(view, getResources().getString(R.string.act_employee_empty_order_fab_txt), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employee, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.itemSearch);
        svEmployeeAppBar = (SearchView) searchMenuItem.getActionView();
        switch(employee_nav_selected) {
            case EMPLOYEE_NAV_MENU: {
                searchMenuItem.setVisible(true);
                svEmployeeAppBar.setQueryHint(getResources().getString(R.string.act_employee_search_query_orders));
                svEmployeeAppBar.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                svEmployeeAppBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        HashMap<String, CategoryDrink> searchedDrinks = new HashMap<>();
                        //function
                        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefCafeCategories());
                        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot cafeCategoriesSnapshot) {
                                for(DataSnapshot categorySnapshot : cafeCategoriesSnapshot.getChildren()) {
                                    DataSnapshot drinksSnapshot = categorySnapshot.child(firebaseRefPaths.getRefCategoryDrinksChild());
                                    for(DataSnapshot drinkSnapshot : drinksSnapshot.getChildren()) {
                                        CategoryDrink categoryDrink = drinkSnapshot.getValue(CategoryDrink.class);
                                        if(categoryDrink.getCategoryDrinkName() != null
                                                && categoryDrink.getCategoryDrinkName().toLowerCase().contains(query.toLowerCase())) {
                                            searchedDrinks.put(drinkSnapshot.getKey(), categoryDrink);
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
                                        AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED,
                                        error.getMessage().toString(),
                                        currentDateTime
                                );
                                appError.sendError(appError);
                            }
                        });
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                break;
            }
            case EMPLOYEE_NAV_PENDING_ORDERS: {
                searchMenuItem.setVisible(true);
                svEmployeeAppBar.setQueryHint(getResources().getString(R.string.act_employee_search_query_menu));
                svEmployeeAppBar.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            }
            default: {
                searchMenuItem.setVisible(false);
                break;
            }
        }
        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onResume() {
        super.onResume();

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
            logout();
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
        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefCafe());
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cafe cafe = snapshot.getValue(Cafe.class);
                orderViewModel.setCafeTablesNumber(cafe.getCafeTables());
                View navHeaderView = navigationView.getHeaderView(0);
                tvCafeName = (TextView) navHeaderView.findViewById(R.id.tvEmployeeNavHeader);
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
                        AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void setListeners() {
        cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefCafe());
        cafeChildsEventListener = cafeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot cafeSnapshot, @Nullable String previousChildName) {
                if(!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                    removeCafeListener();
                }
                if(Objects.requireNonNull(cafeSnapshot.getKey()).equals(firebaseRefPaths.getRefCafeCategoriesChild())) {
                    checkDrinkDeletion();
                }
                else if(Objects.requireNonNull(cafeSnapshot.getKey().equals(firebaseRefPaths.getRefCafeNameChild()))) {
                    tvCafeName.setText(cafeSnapshot.getValue(String.class));
                }
                else if(Objects.requireNonNull(cafeSnapshot.getKey().equals(firebaseRefPaths.getRefCafeTablesChild()))) {
                    orderViewModel.setCafeTablesNumber(cafeSnapshot.getValue(Integer.class));
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
                        AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });

        cafeCurrentOrdersRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefCafeCurrentOrders());
        cafeCurrentOrdersListener = cafeCurrentOrdersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String previousChildName) {
                /*if(!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                    removeCafeOrdersListener();
                }*/
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
                        AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void checkDrinkDeletion() {
        HashMap<String, CafeBillDrink> orderDrinks = orderViewModel.getDrinksInOrder().getValue();
        final boolean[] deletedDrinkFounded = {false};
        if(orderDrinks != null && !orderDrinks.isEmpty()) {
            for(String key : orderDrinks.keySet()) {
                if(!deletedDrinkFounded[0]) {
                    DatabaseReference cafeDrinksCategoriesRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefCafeCategories());
                    cafeDrinksCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot cafeDrinksCategoriesSnapshot) {
                            boolean drinkDeleted = true;
                            for(DataSnapshot categorySnapshot : cafeDrinksCategoriesSnapshot.getChildren()) {
                                for(DataSnapshot drinkSnapshot: categorySnapshot.child(firebaseRefPaths.getRefCategoryDrinksChild()).getChildren()) {
                                    if(Objects.equals(drinkSnapshot.getKey(), key)) {
                                        drinkDeleted = false;
                                    }
                                }
                            }
                            if(drinkDeleted) {
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                            serverAlertDialog.makeAlertDialog();

                            String currentDateTime = simpleDateFormat.format(new Date());
                            appError = new AppError(
                                    menuViewModel.getCafeId().getValue(),
                                    menuViewModel.getPhoneNumber().getValue(),
                                    AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED,
                                    error.getMessage().toString(),
                                    currentDateTime
                            );
                            appError.sendError(appError);
                        }
                    });
                }
            }
        }
    }

    private void removeCafeListener() {
        if (cafeRef != null) {
            cafeRef.removeEventListener(cafeChildsEventListener);
        }
    }

    private void removeCafeOrdersListener() {
        if (cafeCurrentOrdersRef != null) {
            cafeCurrentOrdersRef.removeEventListener(cafeCurrentOrdersListener);
        }
    }

    @Override
    public void onBackPressed() {
        //if category drinks is displayed then close RV category drinks and show RV categories
        if (navigationView.getMenu().findItem(R.id.nav_employee_menu).isChecked()) {
            if (isCategoriesDisplayed) {
                if (back_pressed + AppConstValue.variableConstValue.EXIT_TIME_DELAY > System.currentTimeMillis()) {
                    super.onBackPressed();
                    logout();
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
                        logout();
                    }
                    else {
                        toastMessage.showToast(getResources().getString(R.string.act_employee_back_button_pressed), 0);
                    }
                    back_pressed = System.currentTimeMillis();
                    break;
                case 1:
                    cafeUpdateViewModel.setCafeUpdateDisplayChange(true);
                    cafeUpdateViewModel.setCafeUpdateRvDisplayed(0);
                    break;
                case 2:
                    cafeUpdateViewModel.setCafeUpdateDisplayChange(true);
                    cafeUpdateViewModel.setCafeUpdateRvDisplayed(1);
                    break;
            }
        }
        else {
            if(back_pressed + AppConstValue.variableConstValue.EXIT_TIME_DELAY > System.currentTimeMillis()) {
                super.onBackPressed();
                logout();
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
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void logout() {
        //firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(EmployeeActivity.this, MainActivity.class));
            finish();
        }
        if(currentUser == null) {
            startActivity(new Intent(EmployeeActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeCafeListener();
        removeCafeOrdersListener();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}