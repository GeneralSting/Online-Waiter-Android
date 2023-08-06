package com.example.onlinewaiter.ownerUI.registeredNumbers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinewaiter.Functions.ViewMargins;
import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Models.RegisteredNumber;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.Other.FirebaseRefPaths;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.Services.MailService;
import com.example.onlinewaiter.Other.ServerAlertDialog;
import com.example.onlinewaiter.R;
import com.example.onlinewaiter.ViewHolder.RegisteredNumberViewHolder;
import com.example.onlinewaiter.databinding.FragmentRegisteredNumbersBinding;
import com.example.onlinewaiter.ownerUI.GlobalViewModel.OwnerViewModel;
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
    //fragment views
    private TextView tvSwitchDescription;
    private RecyclerView rvRegisteredNumbers;
    private FloatingActionButton fabRegisterNumber;
    private SwitchCompat scRegisteredNumber;

    //global variables/objects
    private FragmentRegisteredNumbersBinding binding;
    private MainViewModel mainViewModel;
    private RegisteredNumbersViewModel registeredNumbersViewModel;
    private OwnerViewModel ownerViewModel;
    private FirebaseRefPaths firebaseRefPaths;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
    private AppError appError;
    private ToastMessage toastMessage;

    //firebase
    private FirebaseRecyclerAdapter<RegisteredNumber, RegisteredNumberViewHolder> adapterRegisteredNumbers;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisteredNumbersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        tvSwitchDescription = binding.tvRNSwitch;
        fabRegisterNumber = binding.fabRegisterNumbers;
        rvRegisteredNumbers = binding.rvRegisteredNumbers;
        scRegisteredNumber = binding.scRegisteredNumber;

        toastMessage = new ToastMessage(getActivity());
        RecyclerView.LayoutManager rvRegisteredNumbersManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRegisteredNumbers.setLayoutManager(rvRegisteredNumbersManager);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        registeredNumbersViewModel = new ViewModelProvider(requireActivity()).get(RegisteredNumbersViewModel.class);
        ownerViewModel = new ViewModelProvider(requireActivity()).get(OwnerViewModel.class);
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
                if(b) {
                    tvSwitchDescription.setText(getResources().getString(R.string.registered_numbers_switch_owner));
                }
                else {
                    tvSwitchDescription.setText(getResources().getString(R.string.registered_numbers_switch_emplyoees));
                }
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
                TextView tvMemoryIncorrect = (TextView) registerNumberView.findViewById(R.id.tvRegisterNumberMemoryIncorrect);
                EditText etRegisterNumber = (EditText) registerNumberView.findViewById(R.id.etRegisterNumber);
                EditText etNumberMemory = (EditText) registerNumberView.findViewById(R.id.etRegisterNumberMemory);
                Button btnRegisterNumber = (Button) registerNumberView.findViewById(R.id.btnRegisterNumber);
                ImageButton ibCloseNewNumber = (ImageButton) registerNumberView.findViewById(R.id.ibCloseNewNumber);

                etRegisterNumber.setText(ownerViewModel.getCafeCountryStandards().getValue().getCountryNumberCode());

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
                                String phoneNumberValidator = AppConstValue.regex.PHONE_NUMBER_VALIDATOR;
                                if (!(etRegisterNumber.getText().toString().matches(phoneNumberValidator))) {
                                    tvRegisterNumberIncorrect.setText(getResources().getString(R.string.act_login_phone_number_incorrect));
                                }
                                else {
                                    if(etNumberMemory.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                                        tvMemoryIncorrect.setText(getResources().getString(R.string.registered_numbers_memory_incorrect));
                                    }
                                    else {
                                        checkExistingWord(etNumberMemory.getText().toString(),
                                                etRegisterNumber.getText().toString(), registerNumberDialog, tvMemoryIncorrect);
                                    }
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

    private void checkExistingWord(String memoryWord, String phoneNumber, Dialog registerNumberDialog, TextView tvMemoryIncorrect) {
        final boolean[] wordMatched = {false};
        DatabaseReference registeredNumbersRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(mainViewModel.getOwnerCafeId().getValue()));
        registeredNumbersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot registeredNumbersSnapshot) {
                for(DataSnapshot registeredNumberSnapshot : registeredNumbersSnapshot.getChildren()) {
                    RegisteredNumber registeredNumber = registeredNumberSnapshot.getValue(RegisteredNumber.class);
                    if(registeredNumber.getRole().equals(AppConstValue.registeredNumbersRole.WAITER) && registeredNumber.getMemoryWord().equals(memoryWord)) {
                        wordMatched[0] = true;
                        tvMemoryIncorrect.setText(getResources().getString(R.string.registered_numbers_memory_existing));
                        return;
                    }
                }
                if(!wordMatched[0]) {
                    RegisteredNumber registeredNumber = new RegisteredNumber(
                            AppConstValue.registeredNumbersRole.WAITER,
                            memoryWord,
                            AppConstValue.registeredNumberConstValue.NUMBER_ALLOWED,
                            AppConstValue.variableConstValue.WEB_APP_REGISTERED_DEFAULT
                    );
                    DatabaseReference newRegisteredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(
                            mainViewModel.getOwnerCafeId().getValue()));
                    newRegisteredNumberRef.child(phoneNumber).setValue(registeredNumber);
                    registerNumberDialog.dismiss();

                    toastMessage.showToast(getResources().getString(R.string.registered_numbers_registered_successfully), 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                registerNumberDialog.dismiss();
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
                simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void checkExistingEditedWord(String oldPhoneNumber, String memoryWord, String phoneNumber, Dialog registerNumberDialog,
                                         TextView tvEditNumberIncorrect, TextView tvMemoryIncorrect) {
        final boolean[] wordMatched = {false};
        final boolean[] numberMatched = {false};
        DatabaseReference registeredNumbersRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(mainViewModel.getOwnerCafeId().getValue()));
        registeredNumbersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot registeredNumbersSnapshot) {
                for(DataSnapshot registeredNumberSnapshot : registeredNumbersSnapshot.getChildren()) {
                    if(Objects.equals(registeredNumberSnapshot.getKey(), phoneNumber)) {
                        numberMatched[0] = true;
                        tvEditNumberIncorrect.setText(getResources().getString(R.string.registered_numbers_number_existing));
                        return;
                    }

                    RegisteredNumber registeredNumber = registeredNumberSnapshot.getValue(RegisteredNumber.class);
                    if(registeredNumber.getRole().equals(AppConstValue.registeredNumbersRole.WAITER) && registeredNumber.getMemoryWord().equals(memoryWord) &&
                    !oldPhoneNumber.equals(registeredNumberSnapshot.getKey())) {
                        wordMatched[0] = true;
                        tvMemoryIncorrect.setText(getResources().getString(R.string.registered_numbers_memory_existing));
                        return;
                    }
                }
                if(!wordMatched[0] && !numberMatched[0]) {
                    RegisteredNumber registeredNumber = new RegisteredNumber(
                            AppConstValue.registeredNumbersRole.WAITER,
                            memoryWord,
                            AppConstValue.registeredNumberConstValue.NUMBER_ALLOWED,
                            AppConstValue.variableConstValue.WEB_APP_REGISTERED_DEFAULT
                    );
                    DatabaseReference editedNumberRef;
                    if(!phoneNumber.equals(oldPhoneNumber)) {
                        editedNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredNumber(
                                mainViewModel.getOwnerCafeId().getValue(), oldPhoneNumber));
                        editedNumberRef.removeValue();
                    }
                    editedNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(
                            mainViewModel.getOwnerCafeId().getValue()));
                    editedNumberRef.child(phoneNumber).setValue(registeredNumber);
                    registerNumberDialog.dismiss();
                    toastMessage.showToast(getResources().getString(R.string.registered_numbers_edit_successful), 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                registerNumberDialog.dismiss();
                ServerAlertDialog serverAlertDialog = new ServerAlertDialog(getActivity());
                serverAlertDialog.makeAlertDialog();
                simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
                String currentDateTime = simpleDateFormat.format(new Date());
                appError = new AppError(
                        mainViewModel.getOwnerCafeId().getValue(),
                        mainViewModel.getOwnerPhoneNumber().getValue(),
                        AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
                        error.getMessage().toString(),
                        currentDateTime
                );
                appError.sendError(appError);
            }
        });
    }

    private void populateRvRegisteredNumbers(boolean showOwner) {
        DatabaseReference cafeRegisteredNumbersRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(mainViewModel.getOwnerCafeId().getValue()));
        Query query = null;
        if(showOwner) {
            query = cafeRegisteredNumbersRef.orderByKey().equalTo(firebaseRefPaths.getRegisteredNumberChild(mainViewModel.getOwnerPhoneNumber().getValue()));
        }
        else {
            query = cafeRegisteredNumbersRef.orderByChild(firebaseRefPaths.getRegisteredNumberRoleChild()).equalTo(
                    AppConstValue.registeredNumbersRole.WAITER
            );
        }
        FirebaseRecyclerOptions<RegisteredNumber> options = new FirebaseRecyclerOptions
                .Builder<RegisteredNumber>()
                .setQuery(query, RegisteredNumber.class)
                .build();
        adapterRegisteredNumbers = new FirebaseRecyclerAdapter<RegisteredNumber, RegisteredNumberViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RegisteredNumberViewHolder holder, int position, @NonNull RegisteredNumber model) {
                DatabaseReference registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredNumber(
                        mainViewModel.getOwnerCafeId().getValue(), getRef(position).getKey()));
                registeredNumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot registeredNumberSnapshot) {
                        if(!registeredNumberSnapshot.exists()) {
                            return;
                        }
                        RegisteredNumber registeredNumber = registeredNumberSnapshot.getValue(RegisteredNumber.class);
                        holder.tvRegisteredNumber.setText(registeredNumberSnapshot.getKey());
                        if(registeredNumber.getRole().equals(AppConstValue.registeredNumberConstValue.NUMBER_ROLE_WAITER)) {
                            holder.tvRegisteredNumberAlias.setText(registeredNumber.getMemoryWord());
                            holder.ivEditRegisteredNumber.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    editRegisteredNumber(registeredNumber, registeredNumberSnapshot.getKey());
                                }
                            });
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
                            holder.ivEditRegisteredNumber.setVisibility(View.GONE);
                            holder.tvRegisteredNumber.setTextSize(24);
                            ViewMargins.setMargins(holder.cvRegisteredNumber, 86, 32, 86,86);
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
                        simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
                        String currentDateTime = simpleDateFormat.format(new Date());
                        appError = new AppError(
                                mainViewModel.getOwnerCafeId().getValue(),
                                mainViewModel.getOwnerPhoneNumber().getValue(),
                                AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED_OWNER,
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
                        .inflate(R.layout.item_registered_number, parent, false);
                return new RegisteredNumberViewHolder(view);
            }
        };
        rvRegisteredNumbers.setAdapter(adapterRegisteredNumbers);
        adapterRegisteredNumbers.startListening();
    }

    private void editRegisteredNumber(RegisteredNumber registeredNumber, String phoneNumber) {
        View editNumberView = getLayoutInflater().inflate(R.layout.dialog_edit_number, null);
        TextView tvEditNumberTitle = (TextView) editNumberView.findViewById(R.id.tvEditNumberTitle);
        TextView tvEditNumberIncorrect = (TextView) editNumberView.findViewById(R.id.tvEditNumberIncorrect);
        TextView tvEditNumberMemoryIncorrect = (TextView) editNumberView.findViewById(R.id.tvEditNumberMemoryIncorrect);
        EditText etEditNumber = (EditText) editNumberView.findViewById(R.id.etEditNumber);
        EditText etEditNumberMemory = (EditText) editNumberView.findViewById(R.id.etEditNumberMemory);
        Button btnEditNumber = (Button) editNumberView.findViewById(R.id.btnEditNumber);
        ImageButton ibCloseEditNumber = (ImageButton) editNumberView.findViewById(R.id.ibCloseEditNumber);

        tvEditNumberTitle.setText(getResources().getString(R.string.registered_numbers_edit_title) + AppConstValue.characterConstValue.CHARACTER_SPACING + phoneNumber);
        etEditNumber.setText(phoneNumber);
        etEditNumberMemory.setText(registeredNumber.getMemoryWord());

        final AlertDialog editNumberDialog = new AlertDialog.Builder(getActivity())
                .setView(editNumberView)
                .create();
        editNumberDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editNumberDialog.setCanceledOnTouchOutside(false);
        editNumberDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                btnEditNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEditNumberMemoryIncorrect.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                        tvEditNumberIncorrect.setText(AppConstValue.variableConstValue.EMPTY_VALUE);
                        boolean fulfilledConditions = true;
                        String phoneNumberValidator = AppConstValue.regex.PHONE_NUMBER_VALIDATOR;
                        if (!etEditNumber.getText().toString().matches(phoneNumberValidator)) {
                            fulfilledConditions = false;
                            tvEditNumberIncorrect.setText(getResources().getString(R.string.act_login_phone_number_incorrect));
                        }
                        if(etEditNumberMemory.getText().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE)) {
                            fulfilledConditions = false;
                            tvEditNumberMemoryIncorrect.setText(getResources().getString(R.string.registered_numbers_memory_incorrect));
                        }
                        if(etEditNumber.getText().toString().equals(phoneNumber) && etEditNumberMemory.getText().toString().equals(registeredNumber.getMemoryWord())) {
                            fulfilledConditions = false;
                            tvEditNumberMemoryIncorrect.setText(getResources().getString(R.string.registered_numbers_edit_no_change));
                            tvEditNumberIncorrect.setText(getResources().getString(R.string.registered_numbers_edit_no_change));
                        }
                        if(fulfilledConditions) {
                            checkExistingEditedWord(phoneNumber, etEditNumberMemory.getText().toString(),
                                    etEditNumber.getText().toString(), editNumberDialog, tvEditNumberIncorrect, tvEditNumberMemoryIncorrect);
                        }
                    }
                });

                ibCloseEditNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editNumberDialog.dismiss();
                    }
                });
            }
        });
        editNumberDialog.show();
    }

    private void disableEnableAction(RegisteredNumber registeredNumber, String registeredNumberId) {
        DatabaseReference registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredNumberAllowed(
                mainViewModel.getOwnerCafeId().getValue(), registeredNumberId));
        registeredNumberRef.setValue(!registeredNumber.isAllowed());
    }

    private void removePhoneNumber(String registeredNumberId) {
        DatabaseReference registeredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredNumber(
                mainViewModel.getOwnerCafeId().getValue(), registeredNumberId));
        registeredNumberRef.removeValue();
    }

    private void registeredNumberOwner(RegisteredNumber registeredNumber) {
        MailService mailService = new MailService(getContext(), getActivity());
        mailService.appSendMail();

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
                            String phoneNumberValidator = AppConstValue.regex.PHONE_NUMBER_VALIDATOR;
                            if ((etDialogOwnerNumberMain.getText().toString().matches(phoneNumberValidator))) {
                                if(!etDialogOwnerNumberMain.getText().toString().equals(mainViewModel.getOwnerPhoneNumber().getValue())) {
                                    registeredNumbersViewModel.setPhoneNumberChanged(true);
                                    changeOwnerNumber(registeredNumber, etDialogOwnerNumberMain.getText().toString());
                                    newOwnerNumberDialog.dismiss();
                                }
                                else {
                                    tvDialogOwnerMainIncorrect.setText(getResources().getString(R.string.act_login_phone_number_same));
                                }
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
        DatabaseReference ownerRegisteredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getRegisteredNumber(
                mainViewModel.getOwnerCafeId().getValue(), mainViewModel.getOwnerPhoneNumber().getValue()));
        ownerRegisteredNumberRef.removeValue();
        ownerRegisteredNumberRef = firebaseDatabase.getReference(firebaseRefPaths.getCafeRegisteredNumbers(mainViewModel.getOwnerCafeId().getValue()));
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