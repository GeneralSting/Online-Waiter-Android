package com.example.onlinewaiter.employeeUI.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinewaiter.Adapter.MenuDrinksAdapter;
import com.example.onlinewaiter.Interfaces.CallBackOrder;
import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CafeDrinksCategory;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Other.ActionDialog;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.MenuCategoryViewHolder;
import com.example.onlinewaiter.ViewHolder.MenuDrinkViewHolder;
import com.example.onlinewaiter.databinding.FragmentMenuBinding;
import com.example.onlinewaiter.employeeUI.GlobalViewModel.EmployeeViewModel;
import com.example.onlinewaiter.employeeUI.order.OrderViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MenuFragment extends Fragment implements CallBackOrder {

    //fragment views
    private RecyclerView rvMenuCategories, rvMenuCategoryDrinks;
    private TextView tvSearchNoResult;

    //global variables/objects
    private FragmentMenuBinding binding;
    private boolean emptyOrder;
    private OrderViewModel orderViewModel;
    private MenuViewModel menuViewModel;
    private EmployeeViewModel employeeViewModel;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private AppError appError;

    //firebase
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference menuCategoryRef, menuCategoryDrinkRef;
    private FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder> adapterCategories;
    private FirebaseRecyclerAdapter<CategoryDrink, MenuDrinkViewHolder> adapterDrinks;
    private FirebaseRefPaths firebaseRefPaths;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firebaseRefPaths = new FirebaseRefPaths(getActivity());
        employeeViewModel = new ViewModelProvider(requireActivity()).get(EmployeeViewModel.class);
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);

        rvMenuCategories = (RecyclerView) binding.rvMenuCategories;
        rvMenuCategoryDrinks = (RecyclerView) binding.rvMenuCategoryDrinks;
        RecyclerView.LayoutManager rvCategoriesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMenuCategories.setLayoutManager(rvCategoriesLayoutManager);
        RecyclerView.LayoutManager rvDrinksLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMenuCategoryDrinks.setLayoutManager(rvDrinksLayoutManager);
        tvSearchNoResult = binding.tvSearchNoResult;

        searchedDrinksObserver();
        displayingRvObserver();

        menuViewModel.setDisplayingCategories(true);

        return root;
    }

    private void searchedDrinksObserver() {
        final Observer<HashMap<String, CategoryDrink>> observingSearchedDrinks = new Observer<HashMap<String, CategoryDrink>>() {
            @Override
            public void onChanged(HashMap<String, CategoryDrink> searchedDrinks) {
                menuViewModel.setDisplayingCategories(false);
                insertSearchedDrinks(searchedDrinks);
            }
        };
        menuViewModel.getSearchedDrinks().observe(requireActivity(), observingSearchedDrinks);
    }

    private void displayingRvObserver() {
        //displaying ceratin recyclerView (categories/drinks of category)
        final Observer<Boolean> observingCurrentRv = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean showCategories) {
                if(showCategories) {
                    tvSearchNoResult.setVisibility(View.GONE);
                    rvMenuCategoryDrinks.setVisibility(View.GONE);
                    rvMenuCategories.setVisibility(View.VISIBLE);
                }
                else {
                    rvMenuCategories.setVisibility(View.GONE);
                    rvMenuCategoryDrinks.setVisibility(View.VISIBLE);
                }
            }
        };
        menuViewModel.getDisplayingCategories().observe(requireActivity(), observingCurrentRv);
    }

    @Override
    public void onResume() {
        super.onResume();
        insertCafeCategories();
    }

    private void insertSearchedDrinks(HashMap<String, CategoryDrink> searchedDrinks) {
        if(isAdded()) {
            MenuDrinksAdapter menuDrinksAdapter = new MenuDrinksAdapter(getContext(),
                    searchedDrinks,
                    orderViewModel.getDrinksInOrder().getValue(),
                    employeeViewModel.getCafeCurrency().getValue(),
                    this);
            rvMenuCategoryDrinks.setAdapter(menuDrinksAdapter);
            menuDrinksAdapter.notifyDataSetChanged();
            if(searchedDrinks.size() == 0) {
                tvSearchNoResult.setVisibility(View.VISIBLE);
            }
            else {
                tvSearchNoResult.setVisibility(View.GONE);
            }
        }
    }

    private void insertCafeCategories() {
        DatabaseReference menuCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCategories());
        FirebaseRecyclerOptions<CafeDrinksCategory> options = new FirebaseRecyclerOptions
                .Builder<CafeDrinksCategory>()
                .setQuery(menuCategoriesRef, CafeDrinksCategory.class)
                .build();
        adapterCategories = new FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position, @NonNull CafeDrinksCategory model) {
                menuCategoryRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeCategory(getRef(position).getKey()));
                menuCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                        if(!categorySnapshot.exists()) {
                            return;
                        }
                        String categoryImage, categoryName;
                        categoryImage = Objects.requireNonNull(categorySnapshot.child(firebaseRefPaths.getCafeCategoryImageChild()).getValue()).toString();
                        categoryName = Objects.requireNonNull(categorySnapshot.child(firebaseRefPaths.getCafeCategoryNameChild()).getValue()).toString();
                        holder.tvMenuCategory.setText(categoryName);
                        Glide.with(requireActivity()).load(categoryImage).into(holder.ivMenuCategory);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                menuViewModel.setDisplayingCategories(false);
                                insertCategoryDrinks(categorySnapshot.getKey());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
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

            @NonNull
            @Override
            public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_category_item, parent, false);
                return new MenuCategoryViewHolder(view);
            }
        };
        rvMenuCategories.setAdapter(adapterCategories);
        adapterCategories.startListening();
    }

    private void insertCategoryDrinks(String clickedCategoryId) {
        menuViewModel.setDisplayingCategories(false);
        final HashMap<String, CafeBillDrink>[] orderDrinks = new HashMap[]{new HashMap<>()};

        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(Objects.requireNonNull(employeeViewModel.getCafeDecimalSeperator().getValue()).charAt(0));
        DecimalFormat cafeDecimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO, decimalFormatSymbols);

        DatabaseReference menuCategoryDrinksRef = firebaseDatabase.getReference(firebaseRefPaths.getCategoryDrinks(clickedCategoryId));
        FirebaseRecyclerOptions<CategoryDrink> options = new FirebaseRecyclerOptions
                .Builder<CategoryDrink>()
                .setQuery(menuCategoryDrinksRef, CategoryDrink.class)
                .build();
        adapterDrinks = new FirebaseRecyclerAdapter<CategoryDrink, MenuDrinkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuDrinkViewHolder holder, int position, @NonNull CategoryDrink model) {
                menuCategoryDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getCategoryDrink(clickedCategoryId, getRef(position).getKey()));
                menuCategoryDrinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categoryDrinkSnapshot) {
                        if(!categoryDrinkSnapshot.exists()) {
                            return;
                        }
                        CategoryDrink categoryDrink = categoryDrinkSnapshot.getValue(CategoryDrink.class);
                        holder.tvMenuDrinkName.setText(categoryDrink.getCategoryDrinkName());
                        holder.tvMenuDrinkPrice.setText(cafeDecimalFormat.format(categoryDrink.getCategoryDrinkPrice()) + employeeViewModel.getCafeCurrency().getValue());
                        Glide.with(getActivity()).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivMenuDrink);

                        if(categoryDrink.getCategoryDrinkDescription().length() > AppConstValue.variableConstValue.MENU_DRINK_DESCRIPTION_LENGTH) {
                            holder.tvMenuDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription().substring(
                                    0, AppConstValue.variableConstValue.MENU_DRINK_DESCRIPTION_LENGTH_SUBSTRING) + getResources().getString(R.string.etc_dots));
                            holder.tvMenuDrinkDescription.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActionDialog.showInfoDialog(
                                            requireActivity(),
                                            getResources().getString(R.string.menu_drink_description_dialog_title),
                                            categoryDrink.getCategoryDrinkDescription());
                                }
                            });
                        }
                        else {
                            holder.tvMenuDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription());
                        }

                        final int[] drinkAmountCounter = {0};
                        emptyOrder = false;
                        HashMap<String, CafeBillDrink> addedOrderDrinks = orderViewModel.getDrinksInOrder().getValue();
                        if(addedOrderDrinks == null || addedOrderDrinks.isEmpty()) {
                            emptyOrder = true;
                        }
                        if(!emptyOrder) {
                            for(String key: addedOrderDrinks.keySet()) {
                                CafeBillDrink cafeBillDrink = addedOrderDrinks.get(key);
                                if(Objects.equals(cafeBillDrink.getDrinkId(), categoryDrinkSnapshot.getKey())) {
                                    drinkAmountCounter[0] = cafeBillDrink.getDrinkAmount();
                                }
                            }
                            orderDrinks[0] = addedOrderDrinks;
                        }

                        holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                        holder.btnMenuDrinkAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drinkAmountCounter[0]++;
                                CafeBillDrink addingCafeBillDrink;
                                if(drinkAmountCounter[0] > 1) {
                                    addingCafeBillDrink = orderDrinks[0].get(categoryDrinkSnapshot.getKey());
                                    addingCafeBillDrink.setDrinkAmount(drinkAmountCounter[0]);
                                    addingCafeBillDrink.setDrinkTotalPrice(
                                        roundDecimal((float) (addingCafeBillDrink.getDrinkPrice() * drinkAmountCounter[0]),
                                        AppConstValue.variableConstValue.DRINK_PRICE_ROUND_DECIMAL_PLACE));
                                    orderDrinks[0].put(categoryDrinkSnapshot.getKey(), addingCafeBillDrink);
                                }
                                else {
                                    addingCafeBillDrink = new CafeBillDrink(
                                            categoryDrinkSnapshot.getKey(),
                                            clickedCategoryId,
                                            categoryDrink.getCategoryDrinkName(),
                                            categoryDrink.getCategoryDrinkImage(),
                                            categoryDrink.getCategoryDrinkPrice(),
                                            categoryDrink.getCategoryDrinkPrice(),
                                            drinkAmountCounter[0]
                                    );
                                    orderDrinks[0].put(addingCafeBillDrink.getDrinkId(), addingCafeBillDrink);
                                }
                                orderViewModel.setDrinksInOrder(orderDrinks[0]);
                                holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                            }
                        });

                        holder.btnMenuDrinkRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(drinkAmountCounter[0] > 0) {
                                    drinkAmountCounter[0]--;
                                    if(drinkAmountCounter[0] != 0) {
                                        CafeBillDrink addingCafeBillDrink = orderDrinks[0].get(categoryDrinkSnapshot.getKey());
                                        addingCafeBillDrink.setDrinkAmount(drinkAmountCounter[0]);
                                        addingCafeBillDrink.setDrinkTotalPrice(
                                            roundDecimal((float) (addingCafeBillDrink.getDrinkPrice() * drinkAmountCounter[0]),
                                            AppConstValue.variableConstValue.DRINK_PRICE_ROUND_DECIMAL_PLACE));
                                        orderDrinks[0].put(categoryDrinkSnapshot.getKey(), addingCafeBillDrink);
                                        orderViewModel.setDrinksInOrder(orderDrinks[0]);
                                    }
                                    else {
                                        orderDrinks[0].remove(categoryDrinkSnapshot.getKey());
                                        orderViewModel.setDrinksInOrder(orderDrinks[0]);
                                    }
                                    holder.tvMenuDrinkAmount.setText(String.valueOf(drinkAmountCounter[0]));
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
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

            @NonNull
            @Override
            public MenuDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.menu_drink_item, parent, false);
                return new MenuDrinkViewHolder(view);
            }
        };
        rvMenuCategoryDrinks.setAdapter(adapterDrinks);
        adapterDrinks.startListening();
    }

    //for setting 2 decimals for Float numbers
    public static float roundDecimal(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void updateOrderDrinks(HashMap<String, CafeBillDrink> currentOrderDrinks) {
        orderViewModel.setDrinksInOrder(currentOrderDrinks);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(Objects.nonNull(adapterCategories)) {
            adapterCategories.stopListening();
        }
        if(Objects.nonNull(adapterDrinks)) {
            adapterDrinks.stopListening();
        }
    }
}