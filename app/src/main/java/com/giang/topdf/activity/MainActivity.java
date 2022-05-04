package com.giang.topdf.activity;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.R;
import com.giang.topdf.utils.PermissionsUtils;

import java.util.ArrayList;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.type.ButtonGravity;
import gun0912.tedimagepicker.builder.type.MediaType;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    Button mCreateBtn,mConvertBtn;
    TextView mAbout,mSetting;


    private void checkAndAskForStoragePermission() {
        if (!PermissionsUtils.getInstance().checkRuntimePermissions(this, PERMISSIONS)) {
            getRuntimePermissions();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if (!Environment.isExternalStorageManager()) {
                    askStorageManagerPermission();
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void askStorageManagerPermission() {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.one_more_thing_text)
                        .setMessage(R.string.storage_manager_permission_rationale)
                        .setPositiveButton(R.string.allow_text, (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            dialog.dismiss();
                        }).setNegativeButton(R.string.close_app_text, ((dialog, which) -> finishAndRemoveTask()))
                        .show();
    }
    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                PERMISSIONS,
                REQUEST_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        checkAndAskForStoragePermission();
        mCreateBtn = MainActivity.this.findViewById(R.id.create_button);
        mConvertBtn = MainActivity.this.findViewById(R.id.convert_button);
        mAbout = MainActivity.this.findViewById(R.id.about);
        mSetting = MainActivity.this.findViewById(R.id.setting);
        mCreateBtn.setOnClickListener(view ->
                        TedImagePicker.with(this)
                        .mediaType(MediaType.IMAGE)
                        .buttonGravity(ButtonGravity.BOTTOM)
                        .startMultiImage(uriList -> {
                            Intent i = new Intent(this,PreviewActivity.class);
                            ArrayList<Uri> mArrUri = new ArrayList<>(uriList);
                            i.putParcelableArrayListExtra("uriList", mArrUri);
                            startActivity(i);
                        }
                )
        );
        mConvertBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,ConvertToPDFActivity.class);
            startActivity(intent);
        });

        mAbout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,AboutUs.class);
            startActivity(intent);
      });
    }
}
