package com.example.onlinewaiter.Functions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessage;
import com.example.onlinewaiter.Other.ToastMessage;
import com.example.onlinewaiter.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BugReportDialog {

    public static void bugReport(Context context, int sender, String cafeId, String phoneNumber, ToastMessage toastMessage) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bugReportView = inflater.inflate(R.layout.dialog_bug_report, null);
        EditText etBugReport = bugReportView.findViewById(R.id.etBugReport);
        Button btnBugReportConfirm = bugReportView.findViewById(R.id.btnBugReportConfirm);
        ImageButton ibCloseBugReport = bugReportView.findViewById(R.id.ibCloseBugReport);

        final AlertDialog bugReportDialog = new AlertDialog.Builder(context)
                .setView(bugReportView)
                .create();
        bugReportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bugReportDialog.setCanceledOnTouchOutside(false);
        bugReportDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                etBugReport.requestFocus();
                btnBugReportConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etBugReport.getText().toString().length() < 10) {
                            TextInputLayout ftfBugReport = bugReportView.findViewById(R.id.ftfBugReport);
                            ftfBugReport.setError(context.getResources().getString(R.string.act_employee_report_bug_condition));
                        }
                        else {
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
                            String currentDateTime = simpleDateFormat.format(new Date());
                            AppError appError = new AppError(
                                    cafeId,
                                    phoneNumber,
                                    AppErrorMessage.Title.USER_BUG_REPORT,
                                    etBugReport.getText().toString(),
                                    currentDateTime,
                                    AppConstValue.errorSender.USER_OWNER
                            );
                            appError.sendError(appError);
                            bugReportDialog.dismiss();
                            toastMessage.showToast(context.getResources().getString(R.string.act_emplyoee_report_bug_successful), 0);
                        }
                    }
                });
                ibCloseBugReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bugReportDialog.dismiss();
                    }
                });
            }
        });
        bugReportDialog.show();
    }
}
