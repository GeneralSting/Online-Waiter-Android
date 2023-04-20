package com.example.onlinewaiter.employeeUI.cafeUpdate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 105;

    //global variables/objects
    private FragmentCafeUpdateBinding binding;
    boolean checkNewDrinkName, checkNewDrinkDescription, checkNewDrinkPrice, newDrinkSecondConfirm, newDrinkImageSelected,
            firstDrinkNewCategory;
    ToastMessage toastMessage;
    OrderViewModel orderViewModel;
    MenuViewModel menuViewModel;
    CafeUpdateViewModel cafeUpdateViewModel;
    ProgressDialog progressDialog;
    DecimalFormat decimalFormat;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);

    //fragment views
    LinearLayoutCompat linearLayoutContainer;
    Button btnCafeUpdateMenu, btnCafeUpdateNewDrink, btnCafeUpdateTables, btnUpdateDialogAccept;
    TextView tvCafeUpdateNewDrink, tvCafeUpdateMenu, tvCafeUpdateTables;
    ImageView ivDrinkGlobalContainer;
    RecyclerView rvCafeUpdateCategories, rvCafeUpdateCategoryDrinks;
    RecyclerView.LayoutManager layoutManagerCategories, layoutManagerCategoryDrinks;

    //firebase
    private FirebaseRefPaths firebaseRefPaths;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference menuCategoriesRef, menuCategoryRef, menuCategoryDrinksRef, menuCategoryDrinkRef, removeDrinkRef, deleteCafeCategoryRef, categoryDrinkRef, appErrorRef;
    private FirebaseRecyclerAdapter<CafeDrinksCategory, MenuCategoryViewHolder> adapterCategories;
    private FirebaseRecyclerAdapter<CategoryDrink, UpdateDrinkViewHolder> adapterCategoryDrinks;
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
        appErrorRef = firebaseDatabase.getReference(firebaseRefPaths.getRefAppErrors());
        decimalFormat = new DecimalFormat("0.00");

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
        setBtnActions();

        return root;
    }

    private void setBtnActions() {
        btnCafeUpdateMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cafeUpdateViewModel.setCafeUpdateDisplayChange(false);
                cafeUpdateViewModel.setCafeUpdateRvDisplayed(1);
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
                                rvCafeUpdateCategories.setVisibility(View.GONE);
                                insertCategoryDrinks(getRef(position).getKey());
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
                                AppErrorMessages.Messages.IMAGE_UPLOAD_TASK_FAILED,
                                currentDateTime
                        );

                        String dbKey = appErrorRef.push().getKey();
                        appErrorRef.child(dbKey).setValue(appError);
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
                            holder.etUpdateDrinkPrice.setText(decimalFormat.format(categoryDrink.getCategoryDrinkPrice()).replace(",", "."));
                            Glide.with(requireActivity()).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivUpdateDrink);
                            Glide.with(requireActivity()).load(categoryDrink.getCategoryDrinkImage()).into(holder.ivUpdateDrinkComparison);

                            holder.btnUpdateDrinkAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    boolean imageSame = areImagesSame(holder.ivUpdateDrinkComparison, holder.ivUpdateDrink);

                                    if(holder.etUpdateDrinkPrice.getText().toString().equals(".") ||
                                            holder.etUpdateDrinkPrice.getText().toString().equals("")) {
                                        holder.etUpdateDrinkPrice.setText("0");
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
                                            }
                                        }
                                        else {
                                            if(updateDrinkCheck(updatedCategoryDrink)) {
                                                updateDrinkInfo(cafeCategoryId, categoryDrinkSnapshot.getKey(), updatedCategoryDrink);
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
                            });
                        }
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
            public UpdateDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.update_drink_item, parent, false);
                return new UpdateDrinkViewHolder(view);
            }
        };
        rvCafeUpdateCategoryDrinks.setAdapter(adapterCategoryDrinks);
        adapterCategoryDrinks.startListening();
    }

    private void insertAllCategories() {

    }
    private void updateTables() {

    }

    private void updateDrinkImage(String cafeCategoryId, String categoryDrinkId, CategoryDrink updatedCategoryDrink, ImageView ivUpdatedDrink) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getResources().getString(R.string.cafe_update_drink_image_uploading));
        progressDialog.show();
        Date currentDate = new Date();
        String imageName = menuViewModel.getCafeId().getValue() + "_" + updatedCategoryDrink.getCategoryDrinkName() + "_" + simpleDateFormat.format(currentDate);
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
                        insertCategoryDrinks(cafeCategoryId);
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
                downloadImageUri.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();
                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                menuViewModel.getCafeId().getValue(),
                                menuViewModel.getPhoneNumber().getValue(),
                                AppErrorMessages.Messages.DOWNLOAD_IMAGE_URI_FAILED,
                                currentDateTime
                        );

                        String dbKey = appErrorRef.push().getKey();
                        appErrorRef.child(dbKey).setValue(appError);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();

                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        menuViewModel.getCafeId().getValue(),
                        menuViewModel.getPhoneNumber().getValue(),
                        AppErrorMessages.Messages.IMAGE_UPLOAD_TASK_FAILED,
                        currentDateTime
                );
                String dbKey = appErrorRef.push().getKey();
                appErrorRef.child(dbKey).setValue(appError);
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
        if(priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice()).length() > 9 ||
                priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice()).length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_condition_failed), 0);
            return false;
        }
        String updatedDrinkTextPrice = priceToTextConverter(updatedCategoryDrink.getCategoryDrinkPrice());
        if (updatedDrinkTextPrice.length() == 2 && updatedDrinkTextPrice.charAt(0) == '0' &&
                (updatedDrinkTextPrice.charAt(1) == ',' || updatedDrinkTextPrice.charAt(1) == '.')) {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_input_failed), 0);
            return false;
        }
        if(updatedDrinkTextPrice.length() == 1 && updatedDrinkTextPrice.charAt(0) == '.' || updatedDrinkTextPrice.charAt(0) == ',') {
            toastMessage.showToast(getResources().getString(R.string.cafe_update_drink_price_input_failed), 0);
        }
        return true;
    }

    private void updateDrinkInfo(String cafeCategoryId, String categoryDrinkId, CategoryDrink updatedCategoryDrink) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getResources().getString(R.string.cafe_update_drink_info_uploading));
        categoryDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrink(cafeCategoryId, categoryDrinkId));
        categoryDrinkRef.setValue(updatedCategoryDrink);
        insertCategoryDrinks(cafeCategoryId);
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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
        Button btnRemoveDrinkCancel = (Button) removeDrinkView.findViewById(R.id.btnRemoveDrinkCancel);
        Button btnRemoveDrinkAccept = (Button) removeDrinkView.findViewById(R.id.btnRemoveDrinkAccept);

        removeDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategoryName(cafeCategoryId));
        removeDrinkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoryNameSnapshot) {
                tvRemoveDrinkCategory.setText(Objects.requireNonNull(categoryNameSnapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
            }
        });


        AlertDialog dialogRemoveDrink;
        if(drinkInCurrentOrder) {
            TextView tvDialogTitleView = new TextView(getActivity());
            tvDialogTitleView.setText(getResources().getString(R.string.cafe_update_drink_remove_dialog_order_title));
            tvDialogTitleView.setTextSize(20F);
            tvDialogTitleView.setPadding(20, 30, 20, 30);
            tvDialogTitleView.setBackgroundColor(getResources().getColor(R.color.danger_overlay));
            tvDialogTitleView.setTextColor(Color.WHITE);
            dialogRemoveDrink = new AlertDialog.Builder(getActivity())
                    .setView(removeDrinkView)
                    .setCustomTitle(tvDialogTitleView)
                    .create();
        }
        else {
            dialogRemoveDrink = new AlertDialog.Builder(getActivity())
                    .setView(removeDrinkView)
                    .setTitle(getResources().getString(R.string.cafe_update_drink_remove_dialog_title))
                    .create();
        }
        tvRemoveDrinkName.setText(categoryDrink.getCategoryDrinkName());
        Glide.with(getActivity()).load(categoryDrink.getCategoryDrinkImage()).into(ivRemoveDrink);

        boolean finalDrinkInCurrentOrder = drinkInCurrentOrder;
        dialogRemoveDrink.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                btnRemoveDrinkCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogRemoveDrink.dismiss();
                    }
                });

                btnRemoveDrinkAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setTitle(getResources().getString(R.string.cafe_update_drink_deletion));
                        if(finalDrinkInCurrentOrder) {
                            orderDrinks.remove(categoryDrinkId);
                            orderViewModel.setDrinksInOrder(orderDrinks);
                        }
                        removeDrinkRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrink(cafeCategoryId, categoryDrinkId));
                        removeDrinkRef.removeValue();

                        if(!categoryDrink.getCategoryDrinkImage().equals(firebaseRefPaths.getStorageCategoryDrinksNoImage())) {
                            storageDeleteImageRef = firebaseStorage.getReferenceFromUrl(categoryDrink.getCategoryDrinkImage());
                            storageDeleteImageRef.delete();
                        }
                        checkDeletedDrinkCategory(cafeCategoryId);
                        dialogRemoveDrink.dismiss();
                        insertCategoryDrinks(categoryDrinkId);
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
        dialogRemoveDrink.show();
    }

    private void checkDeletedDrinkCategory(String cafeCategoryId) {
        deleteCafeCategoryRef = firebaseDatabase.getReference(firebaseRefPaths.getRefCategoryDrinks(cafeCategoryId));
        deleteCafeCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    firebaseDatabase.getReference(firebaseRefPaths.getRefCafeCategory(cafeCategoryId)).removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
            }
        });
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
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else {
            openDeviceCamera();
        }
    }

    private void openDeviceCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openDeviceGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openDeviceCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if(data != null && data.getData() != null) {
                    ivDrinkGlobalContainer.setImageURI(data.getData());
                }
                break;
            case CAMERA_REQUEST_CODE:
                if(data != null) {
                    ivDrinkGlobalContainer.setImageBitmap((Bitmap) data.getExtras().get("data"));
                }
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(Objects.nonNull(adapterCategories)) {
            adapterCategories.stopListening();
            Log.d("PROBA123", "onStop: ");
        }
        if(Objects.nonNull(adapterCategoryDrinks)) {
            adapterCategoryDrinks.stopListening();
            Log.d("PROBA123", "onStop: 2");
        }
    }
}