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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.R;
import com.giang.topdf.utils.PermissionsUtils;

import info.hannes.liveedgedetection.activity.ScanActivity;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 6;


    public static final String[] PERMISSONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    Button create,convert;
    TextView about,setting;

    //Permission variables
    private void checkAndAskForStoragePermission() {
        if (!PermissionsUtils.getInstance().checkRuntimePermissions(this, PERMISSONS)) {
            getRuntimePermissions();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    askStorageManagerPermission();
            }
        }
    }
    private void askStorageManagerPermission() {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.one_more_thing_text)
                        .setMessage(R.string.storage_manager_permission_rationale)
                        .setCancelable(false)
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
                PERMISSONS,
                REQUEST_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        checkAndAskForStoragePermission();
        create = MainActivity.this.findViewById(R.id.create_button);
        convert = MainActivity.this.findViewById(R.id.convert_button);
        about = MainActivity.this.findViewById(R.id.about);
        setting = MainActivity.this.findViewById(R.id.setting);

        create.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(intent);
        });

        convert.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,ConvertToPDFActivity.class);
            startActivity(intent);
        });

        about.setOnClickListener(view -> {
        Intent intent = new Intent(MainActivity.this,AboutUs.class);
        startActivity(intent);
      });
    }

}
