package com.example.onlinewaiter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.onlinewaiter.Models.AppError;
import com.example.onlinewaiter.Other.AppConstValue;
import com.example.onlinewaiter.Other.AppErrorMessages;
import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ImageCropperActivity extends AppCompatActivity {

    //global objects/variables
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        readIntent();
        croppingImage();
    }

    private void readIntent() {
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            String result = intent.getStringExtra(AppConstValue.bundleConstValue.BUNDLE_CROPPER_IMAGE_DATA);
            fileUri = Uri.parse(result);
        }
    }

    private void croppingImage() {
        String destUri = UUID.randomUUID().toString() + AppConstValue.variableConstValue.IMAGE_FORMAT_JPG;
        UCrop.Options cropOptions = new UCrop.Options();
        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), destUri)))
                .withOptions(cropOptions)
                .withAspectRatio(0,0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000, 2000)
                .start(ImageCropperActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(AppConstValue.bundleConstValue.BUNDLE_CROPPER_IMAGE_RESULT, resultUri + AppConstValue.variableConstValue.EMPTY_VALUE);
            setResult(AppConstValue.permissionConstValue.GALLERY_RESULT_CODE, returnIntent);
        }
        else if(resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            String errorMessage = AppErrorMessages.Message.IMAGE_CROPPER_RESULT_ERROR;
            if(!cropError.getMessage().toString().equals(AppConstValue.variableConstValue.EMPTY_VALUE) && cropError.getMessage() != null) {
                errorMessage = cropError.getMessage().toString();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(AppConstValue.dateConstValue.DATE_TIME_FORMAT_DEFAULT, Locale.CANADA);
            MenuViewModel menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
            String currentDateTime = simpleDateFormat.format(new Date());
            AppError appError = new AppError(
                    menuViewModel.getCafeId().getValue(),
                    menuViewModel.getPhoneNumber().getValue(),
                    AppErrorMessages.Message.RETRIEVING_FIREBASE_DATA_FAILED,
                    errorMessage,
                    currentDateTime
            );
            appError.sendError(appError);
        }
        finish();
    }
}