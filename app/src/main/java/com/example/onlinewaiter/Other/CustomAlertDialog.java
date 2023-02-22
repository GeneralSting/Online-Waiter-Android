package com.example.onlinewaiter.Other;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinewaiter.MainActivity;
import com.example.onlinewaiter.R;

public class CustomAlertDialog {

    private Context context;
    private String dialogHeader, dialogMessage;
    private Drawable dialogImage;
    private Integer btnOkAction;

    public CustomAlertDialog() {}

    public CustomAlertDialog(Context context, String dialogHeader, String dialogMessage, Drawable dialogImage) {
        this.context = context;
        this.dialogHeader = dialogHeader;
        this.dialogMessage = dialogMessage;
        this.dialogImage = dialogImage;
    }

    public CustomAlertDialog(Context context, String dialogHeader, String dialogMessage, Drawable dialogImage, Integer btnOkAction) {
        this.context = context;
        this.dialogHeader = dialogHeader;
        this.dialogMessage = dialogMessage;
        this.dialogImage = dialogImage;
        this.btnOkAction = btnOkAction;
    }

    public void makeAlertDialog() {
        if(this.btnOkAction == null) {
            actionNotRecived();
        }
        else {
            actionRecived();
        }
    }

    private void actionRecived() {
        Dialog alertDialog;
        alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.dialog_alert);
        alertDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.bg_dialog_alert));
        alertDialog.setCancelable(false);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAlertAnimation;

        ImageView ivDialogImage = alertDialog.findViewById(R.id.ivDialogAlertImage);
        ivDialogImage.setImageDrawable(dialogImage);
        TextView txtDialogHeader = alertDialog.findViewById(R.id.txtDialogAlertHeader);
        txtDialogHeader.setText(dialogHeader);
        TextView txtDialogMessage = alertDialog.findViewById(R.id.txtDialogAlertDescription);
        txtDialogMessage.setText(dialogMessage);
        Button btnOk = alertDialog.findViewById(R.id.btnDialogAlertOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (btnOkAction) {
                    case 0: {
                        MainActivity.getInstance().proba();
                    }
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void actionNotRecived() {
        Dialog alertDialog;
        alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.dialog_alert);
        alertDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.bg_dialog_alert));
        alertDialog.setCancelable(false);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAlertAnimation;

        ImageView ivDialogImage = alertDialog.findViewById(R.id.ivDialogAlertImage);
        ivDialogImage.setImageDrawable(dialogImage);
        TextView txtDialogHeader = alertDialog.findViewById(R.id.txtDialogAlertHeader);
        txtDialogHeader.setText(dialogHeader);
        TextView txtDialogMessage = alertDialog.findViewById(R.id.txtDialogAlertDescription);
        txtDialogMessage.setText(dialogMessage);
        Button btnOk = alertDialog.findViewById(R.id.btnDialogAlertOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public int alertingAction() {
        return 0;
    }
}
