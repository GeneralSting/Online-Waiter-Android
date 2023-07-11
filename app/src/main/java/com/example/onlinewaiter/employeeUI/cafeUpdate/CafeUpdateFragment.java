package com.example.onlinewaiter.employeeUI.cafeUpdate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinewaiter.Filters.DecimalPriceInputFilter;
import com.example.onlinewaiter.Interfaces.ItemClickListener;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.CafeBillDrink;
import com.example.onlinewaiter.Models.CafeDrinksCategory;
import com.example.onlinewaiter.Models.CategoryDrink;
import com.example.onlinewaiter.Models.DrinksCategory;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.MenuCategoryViewHolder;
import com.example.onlinewaiter.ViewHolder.UpdateDrinkViewHolder;
import com.example.onlinewaiter.databinding.FragmentCafeUpdateBinding;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.example.onlinewaiter.employeeUI.order.OrderViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class CafeUpdateFragment extends Fragment {
    //permissions


    //global variables/objects
    private FragmentCafeUpdateBinding binding;
    boolean checkNewDrinkName, checkNewDrinkDescription, checkNewDrinkPrice, newDrinkPriceSecondConfirm, newDrinkImageSecondConfirm,
            newDrinkImageSelected, addingNewDrink;
    ToastMessage toastMessage;
    OrderViewModel orderViewModel;
    MenuViewModel menuViewModel;
    CafeUpdateViewModel cafeUpdateViewModel;
    ProgressDialog progressDialog;
    DecimalFormat decimalFormat;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);

    //fragment views
    LinearLayoutCompat linearLayoutContainer;
    Button btnCafeUpdateMenu, btnCafeUpdateNewDrink, btnCafeUpdateTables, btnNewDrinkDialogAccept;
    TextView tvCafeUpdateNewDrink, tvCafeUpdateMenu, tvCafeUpdateTables;
    ImageView ivDrinkGlobalContainer;
    RecyclerView rvCafeUpdateCategories, rvCafeUpdateCategoryDrinks;
    RecyclerView.LayoutManager layoutManagerCategories, layoutManagerCategoryDrinks;
    EditText etGlobalNewDrinkPrice;
    ImageView ivGlobalImageNotAdded;

    //firebase
    private FirebaseRefPaths firebaseRefPaths;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference menuCategoriesRef, menuCategoryRef, menuCategoryDrinksRef, menuCategoryDrinkRef,
            removeDrinkRef, deleteCafeCategoryRef, categoryDrinkRef, drinksCategoriesRef, drinksCategoryRef, cafeTablesRef;
    private FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder> adapterCategories;
    private FirebaseRecyclerAdapter<CategoryDrink, UpdateDrinkViewHolder> adapterCategoryDrinks;
    private FirebaseRecyclerAdapter<DrinksCategory, MenuCategoryViewHolder> adapterDrinksCategory;
    StorageReference storageRef, storageDeleteImageRef;
    private final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private AppError appError;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCafeUpdateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        cafeUpdateViewModel = new ViewModelProvider(requireActivity()).get(CafeUpdateViewModel.class);
        toastMessage = new ToastMessage(getActivity());
        firebaseRefPaths = new FirebaseRefPaths(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        decimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_WITH_ZERO);
        addingNewDrink = false;

        linearLayoutContainer = binding.llCafeUpdateContainer;
        btnCafeUpdateMenu = binding.btnCafeUpdateMenu;
        btnCafeUpdateNewDrink = binding.btnCafeUpdateNewDrink;
        btnCafeUpdateTables = binding.btnCafeUpdateTables;
        tvCafeUpdateMenu = binding.tvCafeUpdateMenu;
        tvCafeUpdateNewDrink = binding.tvCafeUpdateNewDrink;
        tvCafeUpdateTables = binding.tvCafeUpdateTables;
        rvCafeUpdateCategories = binding.rvCafeUpdateCategories;
        rvCafeUpdateCategoryDrinks = binding.rvCafeUpdateCategoryDrinks;
        layoutManagerCategories = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManagerCategoryDrinks = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCafeUpdateCategories.setLayoutManager(layoutManagerCategories);
        rvCafeUpdateCategoryDrinks.setLayoutManager(layoutManagerCategoryDrinks);

        final Observer<Integer> viewDisplayedObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer viewDisplayedDepth) {
                if(Boolean.TRUE.equals(cafeUpdateViewModel.getCafeUpdateDisplayChange().getValue())) {
                    switch (viewDisplayedDepth) {
                        case 0:
                            rvCafeUpdateCategories.setVisibility(View.GONE);
                            linearLayoutContainer.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            rvCafeUpdateCategoryDrinks.setVisibility(View.GONE);
                            rvCafeUpdateCategories.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        };
        cafeUpdateViewModel.getCafeUpdateRvDispalyed().observe(requireActivity(), viewDisplayedObserver);
        cafeUpdateViewModel.setCafeUpdateDisplayChange(false);
        cafeUpdateViewModel.setCafeUpdateRvDisplayed(0);
        setBtnsAction();

        return root;
    }

    private void setBtnsAction() {
        btnCafeUpdateMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafeUpdateViewModel.setCafeUpdateDisplayChange(false);
                cafeUpdateViewModel.setCafeUpdateRvDisplayed(1);
                addingNewDrink = false;
                insertCafeCategories();
                tvCafeUpdateMenu.setVisibility(View.GONE);
                linearLayoutContainer.setVisibility(View.GONE);
                rvCafeUpdateCategories.setVisibility(View.VISIBLE);
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tvCafeUpdateMenu.setVisibility(View.VISIBLE);
                            }
                        },
                        400);
            }
        });

        btnCafeUpdateNewDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafeUpdateViewModel.setCafeUpdateDisplayChange(false);
                cafeUpdateViewModel.setCafeUpdateRvDisplayed(1);
                linearLayoutContainer.setVisibility(View.GONE);
                addingNewDrink = true;
                insertAllCategories();
                tvCafeUpdateNewDrink.setVisibility(View.GONE);
                rvCafeUpdateCategories.setVisibility(View.VISIBLE);
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                tvCafeUpdateNewDrink.setVisibility(View.VISIBLE);
                            }
                        },
                        400);
            }
        });

        btnCafeUpdateTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTables();
            }
        });
    }

    private void insertCafeCategories() {
        menuCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategories());
        Query query = menuCategoriesRef;
        FirebaseRecyclerOptions<CafeDrinksCategory> options = new FirebaseRecyclerOptions.Builder<CafeDrinksCategory>()
                .setQuery(query, CafeDrinksCategory.class)
                .build();
        adapterCategories = new FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position, @NonNull CafeDrinksCategory model) {
                menuCategoryRef = firebaseDatabase.getReference(
                        firebaseRefPaths.getRefCafeCategory(getRef(position).getKey()));
                menuCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                        if(!categorySnapshot.exists()) {
                            return;
                        }
                        String categoryImage, categoryName;
                        categoryImage = Objects.requireNonNull(categorySnapshot.child(firebaseRefPaths.getRefSingleCafeCategoryImage()).getValue()).toString();
                        categoryName = Objects.requireNonNull(categorySnapshot.child(firebaseRefPaths.getRefSingleCafeCategoryName()).getValue()).toString();
                        holder.tvMenuCategory.setText(categoryName);
                        Glide.with(requireActivity()).load(categoryImage).into(holder.ivMenuCategory);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                insertCategoryDrinks(getRef(position).getKey());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        closeProgressDialog();
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
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

            @NonNull
            @Override
            public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_category_item, parent, false);
                return new MenuCategoryViewHolder(view);
            }
        };
        rvCafeUpdateCategories.setAdapter(adapterCategories);
        adapterCategories.startListening();
    }

    private void insertCategoryDrinks(String cafeCategoryId) {
        cafeUpdateViewModel.setCafeUpdateRvDisplayed(2);
        rvCafeUpdateCategories.setVisibility(View.GONE);
        rvCafeUpdateCategoryDrinks.setVisibility(View.VISIBLE);
        menuCategoryDrinksRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrinks(cafeCategoryId));
        FirebaseRecyclerOptions<CategoryDrink> options = new FirebaseRecyclerOptions.Builder<CategoryDrink>()
                .setQuery(menuCategoryDrinksRef, CategoryDrink.class)
                .build();
        FirebaseRecyclerAdapter<CategoryDrink, UpdateDrinkViewHolder> adapterCategoryDrinks = new FirebaseRecyclerAdapter<CategoryDrink, UpdateDrinkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UpdateDrinkViewHolder holder, int position, @NonNull CategoryDrink model) {
                menuCategoryDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrink(cafeCategoryId, getRef(position).getKey()));
                menuCategoryDrinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categoryDrinkSnapshot) {
                        if(!categoryDrinkSnapshot.exists()) {
                            return;
                        }
                        if(isAdded()) {
                            CategoryDrink categoryDrink = categoryDrinkSnapshot.getValue(CategoryDrink.class);

                            holder.etUpdateDrinkName.setText(categoryDrink.getCategoryDrinkName());
                            holder.etUpdateDrinkDescription.setText(categoryDrink.getCategoryDrinkDescription());
                            holder.etUpdateDrinkPrice.setFilters(new InputFilter[] {new DecimalPriceInputFilter(
                                    6,2, 1000000)});
                            holder.etUpdateDrinkPrice.setText(decimalFormat.format(categoryDrink.getCategoryDrinkPrice()).replace(
                                    AppConstValue.variableConstValue.COMMA, AppConstValue.variableConstValue.DOT));
                            Glide.with(requireActivity()).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivUpdateDrink);
                            Glide.with(requireActivity()).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivUpdateDrinkComparison);
                            holder.btnUpdateDrinkAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    boolean imageSame = areImagesSame(holder.ivUpdateDrinkComparison, holder.ivUpdateDrink);

                                    if(holder.etUpdateDrinkPrice.getText().toString().equals(AppConstValue.variableConstValue.DOT) ||
                                            holder.etUpdateDrinkPrice.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                        holder.etUpdateDrinkPrice.setText(AppConstValue.variableConstValue.ZERO_VALUE);
                                    }

                                    CategoryDrink updatedCategoryDrink = new CategoryDrink(
                                            holder.etUpdateDrinkDescription.getText().toString(),
                                            categoryDrink.getCategoryDrinkImage(),
                                            holder.etUpdateDrinkName.getText().toString(),
                                            Float.parseFloat(holder.etUpdateDrinkPrice.getText().toString())
                                    );


                                    if(categoryDrink.getCategoryDrinkName().equals(updatedCategoryDrink.getCategoryDrinkName()) &&
                                    categoryDrink.getCategoryDrinkDescription().equals(updatedCategoryDrink.getCategoryDrinkDescription()) &&
                                    priceToTextConverter(categoryDrink.getCategoryDrinkPrice()).equals(priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice()))) {
                                        if(!imageSame) {
                                            updateDrinkImage(cafeCategoryId, categoryDrinkSnapshot.getKey(), updatedCategoryDrink, holder.ivUpdateDrink);
                                            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_img_update_successful), 0);
                                        }
                                        else {
                                            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_no_change), 0);
                                        }
                                    }
                                    else {
                                        if(!imageSame) {
                                            if(updateDrinkCheck(updatedCategoryDrink)) {
                                                updateDrinkInfo(cafeCategoryId, categoryDrinkSnapshot.getKey(), updatedCategoryDrink);
                                                updateDrinkImage(cafeCategoryId, categoryDrinkSnapshot.getKey(), updatedCategoryDrink, holder.ivUpdateDrink);
                                                toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_update_successful), 0);
                                            }
                                        }
                                        else {
                                            if(updateDrinkCheck(updatedCategoryDrink)) {
                                                updateDrinkInfo(cafeCategoryId, categoryDrinkSnapshot.getKey(), updatedCategoryDrink);
                                                toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_info_update_successful), 0);
                                            }
                                        }
                                    }
                                }
                            });

                            holder.btnUpdateDrinkRemove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    removeDrink(cafeCategoryId, categoryDrinkSnapshot.getKey(), categoryDrink);
                                }
                            });

                            holder.ivUpdateDrink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ivDrinkGlobalContainer = holder.ivUpdateDrink;
                                    bottomImagePicker();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        closeProgressDialog();
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
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

            @NonNull
            @Override
            public UpdateDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_drink_item, parent, false);
                return new UpdateDrinkViewHolder(view);
            }
        };
        rvCafeUpdateCategoryDrinks.setAdapter(adapterCategoryDrinks);
        adapterCategoryDrinks.startListening();
    }

    private void bottomImagePicker() {
        View imagePickerView = getLayoutInflater().inflate(R.layout.dialog_bottom_image_picker, null);
        final BottomSheetDialog dialogImagePicker = new BottomSheetDialog(getActivity());
        dialogImagePicker.setContentView(imagePickerView);
        ImageView ivDrinkCamera = imagePickerView.findViewById(R.id.ivDrinkImageCamera);
        ImageView ivDrinkGallery = imagePickerView.findViewById(R.id.ivDrinkImageGallery);

        Glide.with(imagePickerView)
                .load(getResources().getDrawable(R.drawable.cafe_update_drink_camera))
                .transform(new RoundedCorners(350))
                .into(ivDrinkCamera);

        Glide.with(imagePickerView)
                .load(getResources().getDrawable(R.drawable.cafe_update_drink_gallery))
                .transform(new RoundedCorners(350))
                .into(ivDrinkGallery);

        dialogImagePicker.show();
        ivDrinkCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });
        ivDrinkGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDeviceGallery();
            }
        });
    }

    private void insertAllCategories() {
        drinksCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getRefDrinksCategories());
        Query query = drinksCategoriesRef;
        FirebaseRecyclerOptions<DrinksCategory> drinksCategoryOptions = new FirebaseRecyclerOptions.Builder<DrinksCategory>()
                .setQuery(query, DrinksCategory.class)
                .build();

        FirebaseRecyclerAdapter<DrinksCategory, MenuCategoryViewHolder> adapterDrinksCategory =
                new FirebaseRecyclerAdapter<DrinksCategory, MenuCategoryViewHolder>(drinksCategoryOptions) {
            @Override
            protected void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position, @NonNull DrinksCategory model) {
                drinksCategoriesRef.child(firebaseRefPaths.getRefDrinksCategoriesChild(getRef(position).getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot categoryDrinkSnapshot) {
                        if(!categoryDrinkSnapshot.exists()) {
                            return;
                        }
                        DrinksCategory drinksCategory = categoryDrinkSnapshot.getValue(DrinksCategory.class);
                        holder.tvMenuCategory.setText(drinksCategory.getCategoryName());
                        Glide.with(getActivity()).load(drinksCategory.getCategoryImage()).into(holder.ivMenuCategory);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                createNewDrink(getRef(position).getKey(), drinksCategory.getCategoryName());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        closeProgressDialog();
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
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

            @NonNull
            @Override
            public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_category_item, parent, false);
                return new MenuCategoryViewHolder(view);
            }
        };
        rvCafeUpdateCategories.setAdapter(adapterDrinksCategory);
        adapterDrinksCategory.startListening();
    }

    private void createNewDrink(String categoryId, String categoryName) {
        checkNewDrinkName = checkNewDrinkDescription = checkNewDrinkPrice = false;
        newDrinkPriceSecondConfirm = newDrinkImageSecondConfirm = newDrinkImageSelected = true;

        View newDrinkView = getLayoutInflater().inflate(R.layout.dialog_new_drink, null);
        EditText etNewDrinkName = newDrinkView.findViewById(R.id.etNewDrinkName);
        EditText etNewDrinkDescription = newDrinkView.findViewById(R.id.etNewDrinkDescription);
        EditText etNewDrinkPrice = newDrinkView.findViewById(R.id.etNewDrinkPrice);
        etGlobalNewDrinkPrice = etNewDrinkPrice;
        TextView tvNewDrinkTitle = newDrinkView.findViewById(R.id.tvNewDrinkTitle);
        etNewDrinkPrice.setFilters(new InputFilter[] {new DecimalPriceInputFilter(6, 2, 1000000)});

        ivDrinkGlobalContainer = newDrinkView.findViewById(R.id.ivNewDrink);
        ImageView ivImageNotAdded = newDrinkView.findViewById(R.id.ivImageNotAdded);
        ivGlobalImageNotAdded = ivImageNotAdded;
        btnNewDrinkDialogAccept = newDrinkView.findViewById(R.id.newDrinkDialogAccept);
        ImageButton ibCloseNewDrink = newDrinkView.findViewById(R.id.ibCloseNewDrink);

        tvNewDrinkTitle.setText(getResources().getString(R.string.cafe_update_new_drink_dialog_title) +
                AppConstValue.characterConstValue.CHARACTER_SPACING + categoryName);
        final AlertDialog newDrinkDialog = new AlertDialog.Builder(getActivity())
                .setView(newDrinkView)
                .create();
        newDrinkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newDrinkDialog.setCanceledOnTouchOutside(false);
        newDrinkDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                etNewDrinkName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newDrinkPriceSecondConfirm = newDrinkImageSecondConfirm = true;
                        etNewDrinkPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                        ivImageNotAdded.setVisibility(View.GONE);
                        if(etNewDrinkName.getText().length() >= 25) {
                            checkNewDrinkName = true;
                            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_name_condition_failed), 0);
                            etNewDrinkName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_negative)));
                        }
                        else if(checkNewDrinkName) {
                            etNewDrinkName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tumbleweed)));
                            checkNewDrinkName = false;
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                etNewDrinkDescription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newDrinkPriceSecondConfirm = newDrinkImageSecondConfirm = true;
                        etNewDrinkPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                        ivImageNotAdded.setVisibility(View.GONE);
                        if(etNewDrinkDescription.getText().length() >= 60) {
                            checkNewDrinkDescription = true;
                            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_description_condition_failed), 0);
                            etNewDrinkDescription.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_negative)));
                        }
                        else if(checkNewDrinkDescription) {
                            etNewDrinkDescription.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tumbleweed)));
                            checkNewDrinkDescription = false;
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                etNewDrinkPrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newDrinkPriceSecondConfirm = newDrinkImageSecondConfirm = true;
                        etNewDrinkPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                        ivImageNotAdded.setVisibility(View.GONE);
                        if(etNewDrinkPrice.getText().length() == 2 &&
                                etNewDrinkPrice.getText().charAt(0) == AppConstValue.variableConstValue.CHAR_ZERO_VALUE &&
                                etNewDrinkPrice.getText().charAt(1) != AppConstValue.variableConstValue.CHAR_DOT ||
                                etNewDrinkPrice.getText().length() == 1 &&
                                        etNewDrinkPrice.getText().charAt(0) == AppConstValue.variableConstValue.CHAR_DOT) {
                            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_incorrect), 0);
                            etNewDrinkPrice.setText("");
                        }
                        if(etNewDrinkPrice.getText().length() >= 9) {
                            checkNewDrinkPrice = true;
                            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_condition_failed), 0);
                            etNewDrinkPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_negative)));
                        }
                        else if(checkNewDrinkPrice) {
                            etNewDrinkPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tumbleweed)));
                            checkNewDrinkPrice = false;
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                ivDrinkGlobalContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomImagePicker();
                    }
                });

                btnNewDrinkDialogAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etNewDrinkName.getText().length() > 0 && etNewDrinkDescription.getText().length() > 0 && etNewDrinkPrice.getText().length() > 0) {
                            DecimalFormat decimalFormat = new DecimalFormat(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_FORMAT_NO_ZERO);
                            double drinkPrice = Double.parseDouble(etNewDrinkPrice.getText().toString());
                            if(decimalFormat.format(drinkPrice).toString().equals(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_COMMA_NO_ZERO)
                            || decimalFormat.format(drinkPrice).toString().equals(AppConstValue.decimalFormatConstValue.PRICE_DECIMAL_DOT_NO_ZERO)) {
                                if(!newDrinkPriceSecondConfirm) {
                                    newDrinkPriceSecondConfirm = true;
                                }
                                else {
                                    newDrinkPriceSecondConfirm = false;
                                    etNewDrinkPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_baseline_danger_16, 0, 0, 0);
                                }
                            }
                            if(ivDrinkGlobalContainer.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.item_no_image).getConstantState()) {
                                if(!newDrinkImageSecondConfirm) {
                                    newDrinkImageSecondConfirm = true;
                                    newDrinkImageSelected = false;
                                }
                                else {
                                    newDrinkImageSecondConfirm = false;
                                    ivImageNotAdded.setVisibility(View.VISIBLE);
                                }
                            }
                            if(newDrinkPriceSecondConfirm && newDrinkImageSecondConfirm) {
                                newDrinkDialog.dismiss();
                                //cafeDrinkImage is empty string, image will be added after
                                //if we chose not to pick image, drink will get default no_image
                                CategoryDrink newCategoryDrink = new CategoryDrink(
                                        etNewDrinkDescription.getText().toString(),
                                        AppConstValue.variableConstValue.EMPTY_VALUE,
                                        etNewDrinkName.getText().toString(),
                                        Float.parseFloat(etNewDrinkPrice.getText().toString()));
                                if(newDrinkImageSelected) {
                                    uploadNewDrinkImage(categoryId, newCategoryDrink);
                                }
                                else {
                                    checkNewCafeCategory(categoryId, newCategoryDrink, false);
                                }
                            }
                        }
                    }
                });
                ibCloseNewDrink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newDrinkDialog.dismiss();
                    }
                });
            }
        });
        newDrinkDialog.show();
    }

    private void uploadNewDrinkImage(String categoryId, CategoryDrink newCategoryDrink) {
        setProgressDialog(getResources().getString(R.string.cafe_update_drink_new_image_uploading));
        Date currentDate = new Date();
        String imageName = menuViewModel.getCafeId().getValue() + AppConstValue.characterConstValue.IMAGE_NAME_UNDERLINE +
                newCategoryDrink.getCategoryDrinkName() + AppConstValue.characterConstValue.IMAGE_NAME_UNDERLINE + simpleDateFormat.format(currentDate);
        Bitmap bitmap = ((BitmapDrawable) ivDrinkGlobalContainer.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteData = byteArrayOutputStream.toByteArray();
        storageRef = firebaseStorage.getReference(firebaseRefPaths.getStorageCategoryDrinks() + imageName);
        UploadTask uploadTask = storageRef.putBytes(byteData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadImageUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        closeProgressDialog();
                        newCategoryDrink.setCategoryDrinkImage(downloadImageUri.getResult().toString());
                        checkNewCafeCategory(categoryId, newCategoryDrink, true);
                    }
                });
                downloadImageUri.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        closeProgressDialog();
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();
                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                menuViewModel.getCafeId().getValue(),
                                menuViewModel.getPhoneNumber().getValue(),
                                AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                                e.toString(),
                                currentDateTime
                        );
                        appError.sendError(appError);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closeProgressDialog();
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Messages.IMAGE_UPLOAD_TASK_FAILED,
                        e.toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void checkNewCafeCategory(String categoryId, CategoryDrink newCategoryDrink, boolean hasImage) {
        menuCategoryRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategory(categoryId));
        menuCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cafeCategorySnapshot) {
                if(cafeCategorySnapshot.exists()) {
                    addNewDrink(categoryId, newCategoryDrink, hasImage);
                }
                else {
                    drinksCategoryRef = firebaseDatabase.getReference(firebaseRefPaths.getRefDrinksCategory(categoryId));
                    drinksCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot drinksCategorySnapshot) {
                            DrinksCategory drinksCategory = drinksCategorySnapshot.getValue(DrinksCategory.class);
                            CafeDrinksCategory newCafeDrinksCategory = new CafeDrinksCategory(
                                drinksCategory.getCategoryDescription().toString(),
                                drinksCategory.getCategoryImage().toString(),
                                drinksCategory.getCategoryName().toString(),
                                null
                            );
                            menuCategoriesRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategories());
                            menuCategoriesRef.child(categoryId).setValue(newCafeDrinksCategory);

                            addNewDrink(categoryId, newCategoryDrink, hasImage);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            closeProgressDialog();
                            ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                            serverAlertDialog.makeAlertDialog();

                            String currentDateTime = simpleDateFormat.format(new Date());
                            appError = new AppError(
                                    menuViewModel.getCafeId().getValue(),
                                    menuViewModel.getPhoneNumber().getValue(),
                                    AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                                    error.getMessage().toString(),
                                    currentDateTime
                            );
                            appError.sendError(appError);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                closeProgressDialog();
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void addNewDrink(String categoryId, CategoryDrink newCategoryDrink, boolean hasImage) {
        menuCategoryDrinksRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrinks(categoryId));
        setProgressDialog(getResources().getString(R.string.cafe_update_new_drink_adding));
        if(!hasImage) {
            newCategoryDrink.setCategoryDrinkImage(firebaseRefPaths.getStorageCategoryDrinksNoImage());
        }
        String dbKey = menuCategoryDrinksRef.push().getKey();
        menuCategoryDrinksRef.child(dbKey).setValue(newCategoryDrink);
        closeProgressDialog();
        toastMessage.showToast(getResources().getString(R.string.cafe_update_new_drink_successfully_added), 0);
    }

    private void updateDrinkImage(String cafeCategoryId, String categoryDrinkId, CategoryDrink updatedCategoryDrink, ImageView ivUpdatedDrink) {
        setProgressDialog(getResources().getString(R.string.cafe_update_drink_image_uploading));
        Date currentDate = new Date();
        String imageName = menuViewModel.getCafeId().getValue() + AppConstValue.characterConstValue.IMAGE_NAME_UNDERLINE +
                updatedCategoryDrink.getCategoryDrinkName() + AppConstValue.characterConstValue.IMAGE_NAME_UNDERLINE + simpleDateFormat.format(currentDate);
        Bitmap bitmap = ((BitmapDrawable) ivUpdatedDrink.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteData = byteArrayOutputStream.toByteArray();
        storageRef = firebaseStorage.getReference(firebaseRefPaths.getStorageCategoryDrinks() + imageName);
        UploadTask uploadTask = storageRef.putBytes(byteData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadImageUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        categoryDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getCategoryDrinkImage(cafeCategoryId, categoryDrinkId));
                        categoryDrinkRef.setValue(downloadImageUri.getResult().toString());

                        if(!updatedCategoryDrink.getCategoryDrinkImage().equals(firebaseRefPaths.getStorageCategoryDrinksNoImage())) {
                            storageDeleteImageRef = firebaseStorage.getReferenceFromUrl(updatedCategoryDrink.getCategoryDrinkImage());
                            storageDeleteImageRef.delete();
                        }
                        //insertCategoryDrinks(cafeCategoryId);
                        closeProgressDialog();
                    }
                });
                downloadImageUri.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        closeProgressDialog();
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();
                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                menuViewModel.getCafeId().getValue(),
                                menuViewModel.getPhoneNumber().getValue(),
                                AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                                e.toString(),
                                currentDateTime
                        );
                        appError.sendError(appError);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closeProgressDialog();
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Messages.IMAGE_UPLOAD_TASK_FAILED,
                        e.toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private boolean updateDrinkCheck(CategoryDrink updatedCategoryDrink) {
        if(updatedCategoryDrink.getCategoryDrinkName().length() > 25 || updatedCategoryDrink.getCategoryDrinkName().length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_name_condition_failed), 0);
            return false;
        }
        if(updatedCategoryDrink.getCategoryDrinkDescription().length() > 60 || updatedCategoryDrink.getCategoryDrinkDescription().length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_description_condition_failed), 0);
            return false;
        }
        //here is max length 10 because france locale text number looks like: 123 456,89 -> additional space
        if(priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice()).length() > 10 ||
                priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice()).length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_condition_failed), 0);
            return false;
        }
        String updatedDrinkTextPrice = priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice());
        if (updatedDrinkTextPrice.length() == 2 && updatedDrinkTextPrice.charAt(0) == AppConstValue.variableConstValue.CHAR_ZERO_VALUE &&
                (updatedDrinkTextPrice.charAt(1) == AppConstValue.variableConstValue.CHAR_COMMA ||
                updatedDrinkTextPrice.charAt(1) == AppConstValue.variableConstValue.CHAR_DOT)) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_input_failed), 0);
            return false;
        }
        if(updatedDrinkTextPrice.length() == 1 && updatedDrinkTextPrice.charAt(0) == AppConstValue.variableConstValue.CHAR_DOT
                || updatedDrinkTextPrice.charAt(0) == AppConstValue.variableConstValue.CHAR_COMMA) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_input_failed), 0);
        }
        return true;
    }

    private void updateDrinkInfo(String cafeCategoryId, String categoryDrinkId, CategoryDrink updatedCategoryDrink) {
        setProgressDialog(getResources().getString(R.string.cafe_update_drink_info_uploading));
        categoryDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrink(cafeCategoryId, categoryDrinkId));
        categoryDrinkRef.setValue(updatedCategoryDrink);
        //insertCategoryDrinks(cafeCategoryId);
        closeProgressDialog();
    }

    private void removeDrink(String cafeCategoryId, String categoryDrinkId, CategoryDrink categoryDrink) {
        boolean drinkInCurrentOrder = false;
        HashMap<String, CafeBillDrink> orderDrinks = orderViewModel.getDrinksInOrder().getValue();
        if(orderDrinks != null && !orderDrinks.isEmpty()) {
            for (String key : orderDrinks.keySet()) {
                if(key.equals(categoryDrinkId)) {
                    drinkInCurrentOrder = true;
                }
            }
        }

        View removeDrinkView = getLayoutInflater().inflate(R.layout.dialog_remove_drink, null);
        TextView tvRemoveDrinkName = (TextView) removeDrinkView.findViewById(R.id.tvRemoveDrinkName);
        TextView tvRemoveDrinkCategory = (TextView) removeDrinkView.findViewById(R.id.tvRemoveDrinkCategory);
        ImageView ivRemoveDrink = (ImageView) removeDrinkView.findViewById(R.id.ivRemoveDrink);
        Button btnRemoveDrinkAccept = (Button) removeDrinkView.findViewById(R.id.btnRemoveDrinkAccept);
        ImageButton ibCloseRemoveDrink = (ImageButton) removeDrinkView.findViewById(R.id.ibCloseRemoveDrink);

        if(drinkInCurrentOrder) {
            TextView tvRemoveDrinkTitle = (TextView) removeDrinkView.findViewById(R.id.tvRemoveDrinkTitle);
            LinearLayoutCompat llRemoveDrinkContainer = (LinearLayoutCompat) removeDrinkView.findViewById(R.id.llRemoveDrinkContainer);
            int containerPL = llRemoveDrinkContainer.getPaddingLeft();
            int containerPT = llRemoveDrinkContainer.getPaddingTop();
            int containerPR = llRemoveDrinkContainer.getPaddingRight();
            int containerPB = llRemoveDrinkContainer.getPaddingBottom();
            llRemoveDrinkContainer.setBackgroundResource(R.drawable.action_dialog_danger_bg);
            llRemoveDrinkContainer.setPadding(containerPL, containerPT, containerPR, containerPB);

            ImageView ivRemoveDrinkIcon = (ImageView) removeDrinkView.findViewById(R.id.ivRemoveDrinkIcon);
            int iconPL = ivRemoveDrinkIcon.getPaddingLeft();
            int iconPT = ivRemoveDrinkIcon.getPaddingTop();
            int iconPR = ivRemoveDrinkIcon.getPaddingRight();
            int iconPB = ivRemoveDrinkIcon.getPaddingBottom();
            ivRemoveDrinkIcon.setBackgroundResource(R.drawable.action_dialog_danger_bg);
            ivRemoveDrinkIcon.setPadding(iconPL, iconPT, iconPR, iconPB);

            tvRemoveDrinkTitle.setText(getResources().getString(R.string.cafe_update_drink_remove_dialog_order_title));
        }

        removeDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategoryName(cafeCategoryId));
        removeDrinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoryNameSnapshot) {
                tvRemoveDrinkCategory.setText(Objects.requireNonNull(categoryNameSnapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                closeProgressDialog();
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });


        AlertDialog dialogRemoveDrink;
        dialogRemoveDrink = new AlertDialog.Builder(getActivity())
                .setView(removeDrinkView)
                .create();
        dialogRemoveDrink.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvRemoveDrinkName.setText(categoryDrink.getCategoryDrinkName());
        Glide.with(getActivity()).load(categoryDrink.getCategoryDrinkImage()).into(ivRemoveDrink);

        boolean finalDrinkInCurrentOrder = drinkInCurrentOrder;
        dialogRemoveDrink.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                ibCloseRemoveDrink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogRemoveDrink.dismiss();
                    }
                });

                btnRemoveDrinkAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setProgressDialog(getResources().getString(R.string.cafe_update_drink_deletion));
                        if(finalDrinkInCurrentOrder) {
                            orderDrinks.remove(categoryDrinkId);
                            orderViewModel.setDrinksInOrder(orderDrinks);
                        }

                        if(!categoryDrink.getCategoryDrinkImage().equals(firebaseRefPaths.getStorageCategoryDrinksNoImage())) {
                            storageDeleteImageRef = firebaseStorage.getReferenceFromUrl(categoryDrink.getCategoryDrinkImage());
                            storageDeleteImageRef.delete();
                        }
                        checkDeletedDrinkCategory(cafeCategoryId, categoryDrinkId);
                        dialogRemoveDrink.dismiss();
                        closeProgressDialog();
                    }
                });
            }
        });
        dialogRemoveDrink.show();
    }

    private void checkDeletedDrinkCategory(String cafeCategoryId, String categoryDrinkId) {
        removeDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrink(cafeCategoryId, categoryDrinkId));
        removeDrinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    deleteCafeCategoryRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrinks(cafeCategoryId));
                    deleteCafeCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists() || snapshot.getChildrenCount() == 1) {
                                firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategory(cafeCategoryId)).removeValue();
                            }
                            else {
                                removeDrinkRef.removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            closeProgressDialog();
                            ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                            serverAlertDialog.makeAlertDialog();

                            String currentDateTime = simpleDateFormat.format(new Date());
                            appError = new AppError(
                                    menuViewModel.getCafeId().getValue(),
                                    menuViewModel.getPhoneNumber().getValue(),
                                    AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                                    error.getMessage().toString(),
                                    currentDateTime
                            );
                            appError.sendError(appError);
                        }
                    });
                }
                else {
                    toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_already_deleted), 0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void updateTables() {
        View tablesStateView = getLayoutInflater().inflate(R.layout.dialog_update_tables, null);
        EditText etCurrentTablesState = (EditText) tablesStateView.findViewById(R.id.etTablesCurrentState);
        EditText etNewTablesState = (EditText) tablesStateView.findViewById(R.id.etTablesNewState);
        ImageButton ibCloseUpdateTables = (ImageButton) tablesStateView.findViewById(R.id.ibCloseUpdateTables);
        Button btnUpdateTablesAccept = (Button) tablesStateView.findViewById(R.id.btnUpdateTablessAccept);

        etCurrentTablesState.setText(String.valueOf(orderViewModel.getCafeTablesNumber().getValue()));
        final AlertDialog tablesUpdateDialog = new AlertDialog.Builder(getActivity())
                .setView(tablesStateView)
                .create();
        tablesUpdateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tablesUpdateDialog.setCanceledOnTouchOutside(false);
        tablesUpdateDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                etNewTablesState.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(editable.length() == 1 && editable.toString().equals(AppConstValue.variableConstValue.ZERO_VALUE)) {
                            etNewTablesState.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                            toastMessage.showToast(getResources().getString(R.string.cafe_update_tables_state_zero), 0);
                        }
                    }
                });
                btnUpdateTablesAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etNewTablesState.length() > 0) {
                            if(Integer.parseInt(String.valueOf(etNewTablesState.getText())) == orderViewModel.getCafeTablesNumber().getValue()) {
                                toastMessage.showToast(getResources().getString(R.string.cafe_update_tables_state_same), 0);
                            }
                            else {
                                if(Integer.parseInt(String.valueOf(etNewTablesState.getText())) > 0) {
                                    Integer cafeTables = Integer.parseInt(String.valueOf(etNewTablesState.getText()));
                                    cafeTablesRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeTables());
                                    cafeTablesRef.setValue(cafeTables);

                                    etCurrentTablesState.setText(String.valueOf(cafeTables));
                                    etNewTablesState.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                    etNewTablesState.setHint(AppConstValue.variableConstValue.EMPTY_VALUE);
                                    orderViewModel.setCafeTablesNumber(cafeTables);
                                    toastMessage.showToast(getResources().getString(R.string.cafe_update_tables_state_updated), 0);
                                }
                                else {
                                    etNewTablesState.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                    toastMessage.showToast(getResources().getString(R.string.cafe_update_tables_state_zero), 0);
                                }
                            }
                        }
                        else {
                            toastMessage.showToast(getResources().getString(R.string.cafe_update_table_state_empty), 0);
                        }
                    }
                });
                ibCloseUpdateTables.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tablesUpdateDialog.dismiss();
                    }
                });
            }
        });
        tablesUpdateDialog.show();
    }

    private void setProgressDialog(String title) {
        progressDialog.setTitle(title);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean areImagesSame(ImageView firstImage, ImageView secondImage) {
        Bitmap bitmap1 = ((BitmapDrawable) firstImage.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) secondImage.getDrawable()).getBitmap();
        return bitmap1.sameAs(bitmap2);
    }

    private String priceToTextConverter(float number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(number);
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, AppConstValue.permissionConstValue.CAMERA_PERMISSION_CODE);
        }
        else {
            openDeviceCamera();
        }
    }

    private void openDeviceCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, AppConstValue.permissionConstValue.CAMERA_REQUEST_CODE);
    }

    private void openDeviceGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, AppConstValue.permissionConstValue.GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == AppConstValue.permissionConstValue.CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openDeviceCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        newDrinkPriceSecondConfirm = newDrinkImageSecondConfirm = true;
        if(addingNewDrink) {
            etGlobalNewDrinkPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            ivGlobalImageNotAdded.setVisibility(View.GONE);
        }

        switch (requestCode) {
            case AppConstValue.permissionConstValue.GALLERY_REQUEST_CODE:
                if(data != null && data.getData() != null) {
                    ivDrinkGlobalContainer.setImageURI(data.getData());
                }
                break;
            case AppConstValue.permissionConstValue.CAMERA_REQUEST_CODE:
                if(data != null) {
                    ivDrinkGlobalContainer.setImageBitmap((Bitmap) data.getExtras().get(AppConstValue.permissionConstValue.CAMERA_EXTRAS_DATA));
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(Objects.nonNull(adapterCategories)) {
            adapterCategories.stopListening();
        }
        if(Objects.nonNull(adapterCategoryDrinks)) {
            adapterCategoryDrinks.stopListening();
        }
        if(Objects.nonNull(adapterDrinksCategory)) {
            adapterDrinksCategory.stopListening();
        }
    }
}