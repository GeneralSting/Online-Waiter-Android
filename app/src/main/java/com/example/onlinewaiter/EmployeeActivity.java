package com.example.onlinewaiter;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.onlinewaiter.Models.Cafe;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EmployeeActivity extends AppCompatActivity {

    //Activity view
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityEmployeeBinding binding;
    NavigationView navigationView;
    TextView tvCafeName;

    //gloabl variables/objects
    private Boolean isCategoriesDisplayed = true;
    String employeeCafeId, phoneNumber;
    ToastMessage toastMessage;
    FirebaseRefPaths firebaseRefPaths;
    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;
    final String channelId = "ORDER_NOTIFICATION_CHANNEL";

    private ChildEventListener cafeChildsEventListener;
    DatabaseReference cafeRef;

    //viewModels
    private MenuViewModel menuViewModel;
    private OrderViewModel orderViewModel;
    private CafeUpdateViewModel cafeUpdateViewModel;

    //other
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        cafeUpdateViewModel = new ViewModelProvider(this).get(CafeUpdateViewModel.class);

        Bundle bundle = getIntent().getExtras();
        employeeCafeId = bundle.getString("cafeId");
        phoneNumber = bundle.getString("phoneNumber");

        //when app is closed in background
        if ((Objects.equals(bundle.getString("cafeId"), "") || Objects.equals(bundle.get("phoneNumber"), ""))) {
            logout();
        } else {
            menuViewModel.setCafeId(employeeCafeId);
            menuViewModel.setPhoneNumber(bundle.getString("phoneNumber"));
        }

        PendingIntent notificationEmptyIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_IMMUTABLE);

        builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.nav_header_img)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(notificationEmptyIntent);

        createNotificationChannel();
        notificationManagerCompat = NotificationManagerCompat.from(this);

        startService(new Intent(getBaseContext(), OnAppKilledService.class));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "notificationChannelForOrders";
        String description = "Waiters recieve notification when their order is ready to serve";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toastMessage = new ToastMessage(this);
        firebaseRefPaths = new FirebaseRefPaths();
        setSupportActionBar(binding.appBarEmployee.toolbar);

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
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
        getCafeInfo(navigationView);
        setCategoriesListener();
    }

    private void getCafeInfo(NavigationView navigationView) {
        DatabaseReference cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefPathCafes() + employeeCafeId);
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cafe cafe = snapshot.getValue(Cafe.class);
                orderViewModel = new ViewModelProvider(EmployeeActivity.this).get(OrderViewModel.class);
                orderViewModel.setCafeTablesNumber(cafe.getCafeTables());
                View navHeaderView = navigationView.getHeaderView(0);
                tvCafeName = (TextView) navHeaderView.findViewById(R.id.tvEmployeeNavHeader);
                if (!cafe.getCafeName().equals("")) {
                    tvCafeName.setText(cafe.getCafeName());
                }
                cafeUpdateViewModel.setCafeTables(cafe.getCafeTables());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(EmployeeActivity.this);
                serverAlertDialog.makeAlertDialog();
            }
        });
    }

    private void setCategoriesListener() {
        cafeRef = FirebaseDatabase.getInstance().getReference(firebaseRefPaths.getRefPathCafes() + employeeCafeId);
        cafeChildsEventListener = cafeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot cafeSnapshot, @Nullable String previousChildName) {
                switch (Objects.requireNonNull(cafeSnapshot.getKey())) {
                    case "cafeDrinksCategories":

                        break;
                    case "cafeTables":
                        orderViewModel = new ViewModelProvider(EmployeeActivity.this).get(OrderViewModel.class);
                        orderViewModel.setCafeTablesNumber(cafeSnapshot.getValue(Integer.class));
                        if (ActivityCompat.checkSelfPermission(EmployeeActivity.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    builder.setContentTitle(getResources().getString(R.string.act_employee_notification_header) + " " +
                                            cafeSnapshot.getValue(Integer.class));
                            notificationManagerCompat.notify(1, builder.build());
                        }
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

            }
        });
    }

    @Override
    public void onBackPressed() {
        //if category drinks is displayed then close RV category drinks and show RV categories
        if (navigationView.getMenu().findItem(R.id.nav_employee_menu).isChecked()) {
            if (isCategoriesDisplayed) {
                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                    super.onBackPressed();
                    logout();
                } else {
                    toastMessage.showToast(getResources().getString(R.string.employee_back_button_pressed), 0);
                }
                back_pressed = System.currentTimeMillis();
            } else {
                MenuViewModel menuViewModel = new ViewModelProvider(EmployeeActivity.this).get(MenuViewModel.class);
                menuViewModel.setDisplayingCategories(true);
            }
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
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(EmployeeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cafeRef != null) {
            cafeRef.removeEventListener(cafeChildsEventListener);
        }
    }
}