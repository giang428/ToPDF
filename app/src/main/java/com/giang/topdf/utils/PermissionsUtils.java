package com.giang.topdf.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.giang.topdf.R;

public class PermissionsUtils {
    public static final int REQUEST_CODE_FOR_WRITE_PERMISSION = 4;
    public static final int REQUEST_CODE_FOR_READ_PERMISSION = 5;


    public static final String[] WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] READ_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static PermissionsUtils getInstance() {
        return PermissionsUtils.SingletonHolder.INSTANCE;
    }

    /**
     * checkRuntimePermissions takes in an Object instance(can be of type Activity or Fragment),
     * an array of permission and checks for if all the permissions are granted or not
     *
     * @param context     can be of type Activity or Fragment
     * @param permissions string array of permissions
     * @return true if all permissions are granted, otherwise false
     */
    public boolean checkRuntimePermissions(Object context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if ((ContextCompat.checkSelfPermission(retrieveContext(context),
                        permission)
                        != PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * requestRuntimePermissions takes in an Object instance(can be of type Activity or Fragment),
     * a String array of permissions and
     * a permission request code and requests for the permission
     *
     * @param context     can be of type Activity or Fragment
     * @param permissions string array of permissions
     * @param requestCode permission request code
     */
    public void requestRuntimePermissions(Object context, String[] permissions,
                                          int requestCode) {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    permissions, requestCode);
        } else if (context instanceof Fragment) {
            ((Fragment) context).requestPermissions(permissions, requestCode);
        }
    }

    /**
     * retrieves context of passed in non-null object, context can be of type
     * AppCompatActivity or Fragment
     *
     * @param context can be of type AppCompatActivity or Fragment
     */
    private Context retrieveContext(@NonNull Object context) {
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getApplicationContext();
        } else {
            return ((Fragment) context).requireActivity();
        }
    }

    /**
     * Handle a RequestPermissionResult by checking if the first permission is granted
     * and executing a Runnable when permission is granted
     *
     * @param grantResults    the GrantResults Array
     * @param requestCode
     * @param expectedRequest
     * @param whenSuccessful  the Runnable to call when permission is granted
     */
    public void handleRequestPermissionsResult(Activity context, @NonNull int[] grantResults,
                                               int requestCode, int expectedRequest, @NonNull Runnable whenSuccessful) {

        if (requestCode == expectedRequest && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                whenSuccessful.run();
            } else {
                showPermissionDenyDialog(context, requestCode);
            }
        }
    }

    private void showPermissionDenyDialog(Activity activity, int requestCode) {
        String[] permission;
        if (requestCode == REQUEST_CODE_FOR_WRITE_PERMISSION) {
            permission = WRITE_PERMISSIONS;
        } else {
            permission = READ_PERMISSIONS;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permission_denied_text)
                    .setMessage(R.string.storage_need_rationale_description)
                    .setPositiveButton(R.string.ask_again_text, (dialog, which) -> {
                        requestRuntimePermissions(activity, permission, REQUEST_CODE_FOR_WRITE_PERMISSION);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss()).show();
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permission_denied_text)
                    .setMessage(R.string.storage_need_rationale_for_not_ask_again_flag)
                    .setPositiveButton(R.string.enable_from_settings_text, (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        activity.startActivity(intent);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel_text, (dialog, which) -> dialog.dismiss()).show();
        }
    }

    private static class SingletonHolder {
        static final PermissionsUtils INSTANCE = new PermissionsUtils();
    }
}