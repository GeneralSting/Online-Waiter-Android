package com.example.onlinewaiter.Other;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.example.onlinewaiter.MainActivity;
import com.example.onlinewaiter.R;

public class ServerAlertDialog {

    private Context context;

    public ServerAlertDialog(Context context) {
        this.context = context;
    }

    public void makeAlertDialog() {
        Dialog alertDialog;
        alertDialog = new Dialog(context);
        alertDialog.setContentView(R.layout.dialog_alert);
        alertDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.bg_dialog_alert));
        alertDialog.setCancelable(false);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAlertAnimation;

        Button btnOk = alertDialog.findViewById(R.id.btnDialogAlertOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
