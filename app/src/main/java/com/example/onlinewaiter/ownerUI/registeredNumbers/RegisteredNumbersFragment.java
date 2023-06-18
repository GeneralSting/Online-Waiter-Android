package com.example.onlinewaiter.ownerUI.registeredNumbers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.RegisteredNumber;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Services.MailService;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.RegisteredNumberViewHolder;
import com.example.onlinewaiter.databinding.FragmentRegisteredNumbersBinding;
import com.example.onlinewaiter.ownerUI.main.MainViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegisteredNumbersFragment extends Fragment {

    //global variables/objects
    private FragmentRegisteredNumbersBinding binding;
    private MainViewModel mainViewModel;
    private ToastMessage toastMessage;
    RegisteredNumbersViewModel registeredNumbersViewModel;
    FirebaseRefPaths firebaseRefPaths;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
    private AppError appError;

    //fragment views
    RecyclerView rvRegisteredNumbers;
    RecyclerView.LayoutManager rvRegisteredNumbersManager;
    FloatingActionButton fabRegisterNumber;
    SwitchCompat scRegisteredNumber;

    //firebase
    FirebaseRecyclerAdapter<RegisteredNumber, RegisteredNumberViewHolder> adapterRegisteredNumbers;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisteredNumbersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fabRegisterNumber = binding.fabRegisterNumbers;
        rvRegisteredNumbers = binding.rvRegisteredNumbers;
        scRegisteredNumber = binding.scRegisteredNumber;
        rvRegisteredNumbersManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRegisteredNumbers.setLayoutManager(rvRegisteredNumbersManager);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        toastMessage = new ToastMessage(requireActivity());
        registeredNumbersViewModel = new ViewModelProvider(requireActivity()).get(RegisteredNumbersViewModel.class);
        firebaseRefPaths = new FirebaseRefPaths();

        switchPressed();
        fabAddAction();
        populateRvRegisteredNumbers(false);
        return root;
    }

    private void switchPressed() {
        scRegisteredNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                stopAdapterListening();
                populateRvRegisteredNumbers(b);
            }
        });
    }

    private void fabAddAction() {
        fabRegisterNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View registerNumberView = getLayoutInflater().inflate(R.layout.dialog_register_number, null);
                TextView tvRegisterNumberIncorrect = (TextView) registerNumberView.findViewById(R.id.tvRegisterNumberIncorrect);
                EditText etRegisterNumber = (EditText) registerNumberView.findViewById(R.id.etRegisterNumber);
                Button btnRegisterNumber = (Button) registerNumberView.findViewById(R.id.btnRegisterNumber);
                ImageButton ibCloseNewNumber = (ImageButton) registerNumberView.findViewById(R.id.ibCloseNewNumber);

                final AlertDialog registerNumberDialog = new AlertDialog.Builder(getActivity())
                        .setView(registerNumberView)
                        .create();
                registerNumberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                registerNumberDialog.setCanceledOnTouchOutside(false);
                registerNumberDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        btnRegisterNumber.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String phoneNumberValidator = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
                                if ((etRegisterNumber.getText().toString().matches(phoneNumberValidator))) {
                                    RegisteredNumber registeredNumber = new RegisteredNumber(
                                            firebaseRefPaths.getRefRegisteredNumberWaiter(),
                                            AppConstValue.registeredNumberConstValues.NUMBER_ALLOWED
                                    );
                                    DatabaseReference newRegisteredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredCafe(
                                            mainViewModel.getOwnerCafeId().getValue()
                                    ));
                                    newRegisteredNumberRef.child(etRegisterNumber.getText().toString()).setValue(registeredNumber);
                                    registerNumberDialog.dismiss();
                                }
                                else {
                                    tvRegisterNumberIncorrect.setText(getResources().getString(R.string.act_login_phone_number_incorrect));
                                }
                            }
                        });

                        ibCloseNewNumber.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                registerNumberDialog.dismiss();
                            }
                        });
                    }
                });
                registerNumberDialog.show();
            }
        });
    }

    private void populateRvRegisteredNumbers(boolean showOwner) {
        DatabaseReference cafeRegisteredNumbersRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredCafe(mainViewModel.getOwnerCafeId().getValue()));
        Query query = null;
        if(showOwner) {
            query = cafeRegisteredNumbersRef.orderByKey().equalTo(firebaseRefPaths.getRefRegisteredNumberChild(mainViewModel.getOwnerPhoneNumber().getValue()));
        }
        else {
            query = cafeRegisteredNumbersRef.orderByChild(firebaseRefPaths.getRefRegisteredNumberRoleChild()).equalTo(
                    firebaseRefPaths.getRefRegisteredNumberWaiter()
            );
        }
        FirebaseRecyclerOptions<RegisteredNumber> options = new FirebaseRecyclerOptions
                .Builder<RegisteredNumber>()
                .setQuery(query, RegisteredNumber.class)
                .build();
        adapterRegisteredNumbers = new FirebaseRecyclerAdapter<RegisteredNumber, RegisteredNumberViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RegisteredNumberViewHolder holder, int position, @NonNull RegisteredNumber model) {
                DatabaseReference registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredNumber(
                        mainViewModel.getOwnerCafeId().getValue(), getRef(position).getKey()));
                registeredNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot registeredNumberSnapshot) {
                        if(!registeredNumberSnapshot.exists()) {
                            return;
                        }
                        RegisteredNumber registeredNumber = registeredNumberSnapshot.getValue(RegisteredNumber.class);
                        holder.tvRegisteredNumber.setText(registeredNumberSnapshot.getKey());
                        if(registeredNumber.getRole().equals(AppConstValue.registeredNumberConstValues.NUMBER_ROLE_WAITER)) {
                            if(registeredNumber.isAllowed()) {
                                holder.btnRegisteredNumberDisable.setText(getResources().getString(R.string.registered_numbers_btn_disable));
                                holder.clRegisteredNumber.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_blue_overlay));
                            }
                            else {
                                holder.btnRegisteredNumberDisable.setText(getResources().getString(R.string.registered_numbers_btn_enable));
                                holder.clRegisteredNumber.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_purple_overlay));
                            }
                            holder.btnRegisteredNumberDisable.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    disableEnableAction(registeredNumber, registeredNumberSnapshot.getKey());
                                }
                            });

                            final boolean[] acceptRemoval = {false};
                            holder.btnRegisteredNumberRemove.setText(getResources().getString(R.string.registered_numbers_btn_remove));
                            holder.btnRegisteredNumberRemove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(acceptRemoval[0]) {
                                        removePhoneNumber(registeredNumberSnapshot.getKey());
                                    }
                                    else {
                                        holder.btnRegisteredNumberRemove.setText(getResources().getString(R.string.accept));
                                        acceptRemoval[0] = true;
                                    }
                                }
                            });
                        }
                        else if(Objects.equals(registeredNumberSnapshot.getKey(), mainViewModel.getOwnerPhoneNumber().getValue()) && showOwner) {
                            holder.btnRegisteredNumberDisable.setVisibility(View.GONE);
                            holder.btnRegisteredNumberRemove.setVisibility(View.GONE);
                            holder.clRegisteredNumber.setBackgroundColor(getResources().getColor(R.color.cv_cafe_update_green_overlay));
                            if(Boolean.FALSE.equals(registeredNumbersViewModel.getPhoneNumberChanged().getValue())) {
                                holder.btnRegisteredNumberOwner.setVisibility(View.VISIBLE);
                            }
                            else {
                                holder.btnRegisteredNumberOwner.setVisibility(View.VISIBLE);
                                holder.btnRegisteredNumberOwner.setEnabled(false);
                            }
                            holder.btnRegisteredNumberOwner.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    registeredNumberOwner(registeredNumber);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                        serverAlertDialog.makeAlertDialog();
                        simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_NORMAL, Locale.CANADA);
                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                mainViewModel.getOwnerCafeId().getValue(),
                                mainViewModel.getOwnerPhoneNumber().getValue(),
                                AppErrorMessages.Messages.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                                error.getMessage().toString(),
                                currentDateTime
                        );
                        appError.sendError(appError);
                    }
                });
            }

            @NonNull
            @Override
            public RegisteredNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.registered_number_item, parent, false);
                return new RegisteredNumberViewHolder(view);
            }
        };
        rvRegisteredNumbers.setAdapter(adapterRegisteredNumbers);
        adapterRegisteredNumbers.startListening();
    }

    private void disableEnableAction(RegisteredNumber registeredNumber, String registeredNumberId) {
        DatabaseReference registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredNumberAllowed(
                mainViewModel.getOwnerCafeId().getValue(), registeredNumberId));
        registeredNumberRef.setValue(!registeredNumber.isAllowed());
    }

    private void removePhoneNumber(String registeredNumberId) {
        DatabaseReference registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredNumber(
                mainViewModel.getOwnerCafeId().getValue(), registeredNumberId));
        registeredNumberRef.removeValue();
    }

    private void registeredNumberOwner(RegisteredNumber registeredNumber) {
        MailService mailService = new MailService(getContext(), getActivity(), getResources().getString(R.string.email_subject),
                getResources().getString(R.string.email_body), getResources().getString(R.string.email_body_info));

        View newOwnerNumberView = getLayoutInflater().inflate(R.layout.dialog_owner_phone_number, null);
        TextView tvDialogOwnerNumberDescription = (TextView) newOwnerNumberView.findViewById(R.id.tvDialogOwnerNumberDescription);
        TextView tvDialogOwnerNumberTitle = (TextView) newOwnerNumberView.findViewById(R.id.tvDialogOwnerNumberTitle);
        TextView tvDialogOwnerMainIncorrect = (TextView) newOwnerNumberView.findViewById(R.id.tvDialogOwnerMainIncorrect);
        TextView tvDialogOwnerPart = (TextView) newOwnerNumberView.findViewById(R.id.tvDialogOwnerPart);
        EditText etDialogOwnerNumberMain = (EditText) newOwnerNumberView.findViewById(R.id.etDialogOwnerNumberMain);
        Button btnDialogOwnerNumber = (Button) newOwnerNumberView.findViewById(R.id.btnDialogOwnerNumber);
        ImageView ivOwnerNumberIcon = (ImageView) newOwnerNumberView.findViewById(R.id.ivOwnerNumberIcon);
        LinearLayoutCompat llOwnerNumberContainer = (LinearLayoutCompat) newOwnerNumberView.findViewById(R.id.llOwnerNumberContainer);
        ImageButton ibCloseChangeNumber = (ImageButton) newOwnerNumberView.findViewById(R.id.ibCloseChangeNumber);

        final AlertDialog newOwnerNumberDialog = new AlertDialog.Builder(getActivity())
                .setView(newOwnerNumberView)
                .create();
        newOwnerNumberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newOwnerNumberDialog.setCanceledOnTouchOutside(false);

        final boolean[] codeEntered = {false};
        newOwnerNumberDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                btnDialogOwnerNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(codeEntered[0]) {
                            String phoneNumberValidator = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
                            if ((etDialogOwnerNumberMain.getText().toString().matches(phoneNumberValidator))) {
                                registeredNumbersViewModel.setPhoneNumberChanged(true);
                                changeOwnerNumber(registeredNumber, etDialogOwnerNumberMain.getText().toString());
                                newOwnerNumberDialog.dismiss();
                            }
                            else {
                                tvDialogOwnerMainIncorrect.setText(getResources().getString(R.string.act_login_phone_number_incorrect));
                            }
                        }
                        else {
                            if(!etDialogOwnerNumberMain.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE) &&
                                    Integer.parseInt(etDialogOwnerNumberMain.getText().toString()) ==
                                            registeredNumbersViewModel.getEmailRandomCode().getValue()) {
                                codeEntered[0] = true;
                                etDialogOwnerNumberMain.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                                int containerPL = llOwnerNumberContainer.getPaddingLeft();
                                int containerPT = llOwnerNumberContainer.getPaddingTop();
                                int containerPR = llOwnerNumberContainer.getPaddingRight();
                                int containerPB = llOwnerNumberContainer.getPaddingBottom();
                                llOwnerNumberContainer.setBackgroundResource(R.drawable.action_dialog_warning_bg);
                                llOwnerNumberContainer.setPadding(containerPL, containerPT, containerPR, containerPB);

                                int iconPL = ivOwnerNumberIcon.getPaddingLeft();
                                int iconPT = ivOwnerNumberIcon.getPaddingTop();
                                int iconPR = ivOwnerNumberIcon.getPaddingRight();
                                int iconPB = ivOwnerNumberIcon.getPaddingBottom();
                                ivOwnerNumberIcon.setBackgroundResource(R.drawable.action_dialog_warning_bg);
                                ivOwnerNumberIcon.setPadding(iconPL, iconPT, iconPR, iconPB);

                                etDialogOwnerNumberMain.setFilters(new InputFilter[] {new InputFilter.LengthFilter(13)});
                                etDialogOwnerNumberMain.setInputType(InputType.TYPE_CLASS_PHONE);
                                etDialogOwnerNumberMain.setHint(getResources().getString(R.string.registered_numbers_dialog_owner_number));
                                tvDialogOwnerNumberTitle.setText(getResources().getString(R.string.registered_numbers_dialog_second_title));
                                tvDialogOwnerPart.setText(getResources().getString(R.string.registered_numbers_dialog_second_part));
                                tvDialogOwnerNumberDescription.setText(getResources().getString(R.string.registered_numbers_dialog_second_description));
                                tvDialogOwnerMainIncorrect.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                            }
                            else {
                                tvDialogOwnerMainIncorrect.setText(getResources().getString(R.string.registered_numbers_wrong_email_code));
                            }
                        }
                    }
                });
                ibCloseChangeNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newOwnerNumberDialog.dismiss();
                    }
                });
            }
        });
        newOwnerNumberDialog.show();
    }

    private void changeOwnerNumber(RegisteredNumber registeredNumber, String newPhoneNumber) {
        DatabaseReference ownerRegisteredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredNumber(
                mainViewModel.getOwnerCafeId().getValue(), mainViewModel.getOwnerPhoneNumber().getValue()));
        ownerRegisteredNumberRef.removeValue();
        ownerRegisteredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRefRegisteredCafe(mainViewModel.getOwnerCafeId().getValue()));
        mainViewModel.setOwnerPhoneNumber(newPhoneNumber);
        ownerRegisteredNumberRef.child(newPhoneNumber).setValue(registeredNumber);
        stopAdapterListening();
        populateRvRegisteredNumbers(true);
    }

    private void stopAdapterListening() {
        if(Objects.nonNull(adapterRegisteredNumbers)) {
            adapterRegisteredNumbers.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        stopAdapterListening();
    }
}