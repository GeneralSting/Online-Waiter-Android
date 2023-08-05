package com.example.onlinewaiter.Other;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.onlinewaiter.R;

public class ActionDialog {
    public static void showInfoDialog(Context context, String title, String content) {
        View viewDialog = LayoutInflater.from(context).inflate(R.layout.dialog_action_info, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(viewDialog);

        TextView tvInfoDialogTitle = viewDialog.findViewById(R.id.tvInfoDialogTitle);
        TextView tvInfoDialogContent = viewDialog.findViewById(R.id.tvInfoDialogContent);
        tvInfoDialogTitle.setText(title);
        tvInfoDialogContent.setText(content);
        ImageButton ibCloseDialog = viewDialog.findViewById(R.id.ibCloseInfoDialog);

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ibCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
}
