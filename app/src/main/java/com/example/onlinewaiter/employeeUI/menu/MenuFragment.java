package com.example.onlinewaiter.employeeUI.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CafeDrinksCategory;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.MenuCategoryViewHolder;
import com.example.onlinewaiter.ViewHolder.MenuDrinkViewHolder;
import com.example.onlinewaiter.databinding.FragmentMenuBinding;
import com.example.onlinewaiter.employeeUI.order.OrderViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

public class MenuFragment extends Fragment {

    //fragment views
    RecyclerView rvMenuCategories, rvMenuCategoryDrinks;
    RecyclerView.LayoutManager rvCategoriesLayoutManager, rvDrinksLayoutManager;

    //global variables/obejcts
    private OrderViewModel orderViewModel;
    private MenuViewModel menuViewModel;
    Boolean emptyOrder;
    ToastMessage toastMessage;
    private FragmentMenuBinding binding;

    //firebase
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference menuCategoriesRef, menuCategoryRef, menuCategoryDrinksRef, menuCategoryDrinkRef;
    ValueEventListener menuCategoriesListener, menuCategoryDrinksListener;
    FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder> adapterCategories;
    FirebaseRecyclerAdapter<CategoryDrink, MenuDrinkViewHolder> adapterDrinks;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        toastMessage = new ToastMessage(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();

        rvMenuCategories = (RecyclerView) binding.rvMenuCategories;
        rvMenuCategoryDrinks = (RecyclerView) binding.rvMenuCategoryDrinks;
        rvCategoriesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMenuCategories.setLayoutManager(rvCategoriesLayoutManager);
        rvDrinksLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvMenuCategoryDrinks.setLayoutManager(rvDrinksLayoutManager);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        menuViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(MenuViewModel.class);
        orderViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(OrderViewModel.class);
        insertCafeCategories();
    }

    private void insertCafeCategories() {
        menuCategoriesRef = firebaseDatabase.getReference("cafes/" + menuViewModel.getCafeId().getValue() + "/cafeDrinksCategories");
        Query query = menuCategoriesRef;
        FirebaseRecyclerOptions<CafeDrinksCategory> options = new FirebaseRecyclerOptions
                .Builder<CafeDrinksCategory>()
                .setQuery(query, CafeDrinksCategory.class)
                .build();
        adapterCategories = new FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position, @NonNull CafeDrinksCategory model) {
                menuCategoryRef = firebaseDatabase.getReference(
                        "cafes/" + menuViewModel.getCafeId().getValue() + "/cafeDrinksCategories/" + getRef(position).getKey());
                menuCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                        if(!categorySnapshot.exists()) {
                            return;
                        }
                        String categoryImage, categoryName;
                        categoryImage = Objects.requireNonNull(categorySnapshot.child("image").getValue()).toString();
                        categoryName = Objects.requireNonNull(categorySnapshot.child("name").getValue()).toString();
                        holder.tvMenuCategory.setText(categoryName);
                        Glide.with(Objects.requireNonNull(getActivity())).load(categoryImage).into(holder.ivMenuCategory);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                rvMenuCategories.setVisibility(View.GONE);
                                insertCategoryDrinks(categorySnapshot.getKey());
                                rvMenuCategoryDrinks.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();
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
        //displaying ceratin recyclerView (categories/drinks of category)
        final Observer<Boolean> observingCurrentRv = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    rvMenuCategoryDrinks.setVisibility(View.GONE);
                    rvMenuCategories.setVisibility(View.VISIBLE);
                }
            }
        };
        menuViewModel.getDisplayingCategories().observe(requireActivity(), observingCurrentRv);

        //cart/order for drinks
        final HashMap<String, CafeBillDrink>[] orderDrinks = new HashMap[]{new HashMap<>()};
        emptyOrder = false;
        HashMap<String, CafeBillDrink> addedOrderDrinks = orderViewModel.getDrinksInOrder().getValue();
        if(addedOrderDrinks == null || addedOrderDrinks.isEmpty()) {
            emptyOrder = true;
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        menuCategoryDrinksRef = firebaseDatabase.getReference(
                "cafes/" + menuViewModel.getCafeId().getValue() + "/cafeDrinksCategories/" + clickedCategoryId + "/categoryDrinks");
        FirebaseRecyclerOptions<CategoryDrink> options = new FirebaseRecyclerOptions
                .Builder<CategoryDrink>()
                .setQuery(menuCategoryDrinksRef, CategoryDrink.class)
                .build();
        adapterDrinks = new FirebaseRecyclerAdapter<CategoryDrink, MenuDrinkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuDrinkViewHolder holder, int position, @NonNull CategoryDrink model) {
                menuCategoryDrinkRef = firebaseDatabase.getReference(
                        "cafes/" + menuViewModel.getCafeId().getValue() + "/cafeDrinksCategories/" +
                                clickedCategoryId + "/categoryDrinks/" + getRef(position).getKey());
                menuCategoryDrinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categoryDrinkSnapshot) {
                        if(!categoryDrinkSnapshot.exists()) {
                            return;
                        }
                        CategoryDrink categoryDrink = categoryDrinkSnapshot.getValue(CategoryDrink.class);
                        holder.tvMenuDrinkName.setText(categoryDrink.getCategoryDrinkName());
                        holder.tvMenuDrinkPrice.setText(decimalFormat.format(categoryDrink.getCategoryDrinkPrice()) +
                                getResources().getString(R.string.country_currency));
                        Glide.with(getActivity()).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivMenuDrink);

                        if(categoryDrink.getCategoryDrinkDescription().length() > 45) {
                            holder.tvMenuDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription().substring(0, 42) +
                                    getResources().getString(R.string.etc_dots));
                            holder.tvMenuDrinkDescription.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    TextView txtFullDrinkDescription = new TextView(getActivity());
                                    txtFullDrinkDescription.setTextSize(24);
                                    final AlertDialog fullDrinkDescriptionDialog = new AlertDialog.Builder(getActivity()).create();
                                    fullDrinkDescriptionDialog.setView(
                                            txtFullDrinkDescription, 90, 120, 130, 140
                                    );
                                    fullDrinkDescriptionDialog.setTitle(
                                            getResources().getString(R.string.menu_drink_description_dialog_title) + " " + categoryDrink.getCategoryDrinkName()
                                    );
                                    fullDrinkDescriptionDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            txtFullDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription());
                                        }
                                    });
                                    fullDrinkDescriptionDialog.show();
                                }
                            });
                        }
                        else {
                            holder.tvMenuDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription());
                        }

                        final int[] drinkAmountCounter = {0};
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
                                if(drinkAmountCounter[0] > 1) {
                                    CafeBillDrink addingCafeBillDrink = orderDrinks[0].get(categoryDrinkSnapshot.getKey());
                                    addingCafeBillDrink.setDrinkAmount(drinkAmountCounter[0]);
                                    addingCafeBillDrink.setDrinkTotalPrice(
                                            roundDecimal((float) (addingCafeBillDrink.getDrinkPrice() * drinkAmountCounter[0]), 2)
                                    );
                                    orderDrinks[0].put(categoryDrinkSnapshot.getKey(), addingCafeBillDrink);
                                    orderViewModel.setDrinksInOrder(orderDrinks[0]);
                                }
                                else {
                                    CafeBillDrink addingCafeBillDrink = new CafeBillDrink(
                                        categoryDrinkSnapshot.getKey(),
                                            categoryDrink.getCategoryDrinkName(),
                                            categoryDrink.getCategoryDrinkImage(),
                                            categoryDrink.getCategoryDrinkPrice(),
                                            categoryDrink.getCategoryDrinkPrice(),
                                            drinkAmountCounter[0]
                                    );
                                    orderDrinks[0].put(addingCafeBillDrink.getDrinkId(), addingCafeBillDrink);
                                    orderViewModel.setDrinksInOrder(orderDrinks[0]);
                                }
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
                                                roundDecimal((float) (addingCafeBillDrink.getDrinkPrice() * drinkAmountCounter[0]), 2)
                                        );
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
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapterCategories.stopListening();
        if(Boolean.FALSE.equals(menuViewModel.getDisplayingCategories().getValue())) {
            adapterDrinks.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}