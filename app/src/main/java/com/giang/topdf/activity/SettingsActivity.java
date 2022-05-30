package com.giang.topdf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.giang.topdf.R;
import com.giang.topdf.utils.Constant;

import lib.folderpicker.FolderPicker;

public class SettingsActivity extends AppCompatActivity {
    private static Preference mDefaultStorage;
    private static SharedPreferences mSharedPreferences;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {


        ActivityResultLauncher<Intent> mPickFile = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String folderLocation = null;
                        if (result.getData() != null) {
                            folderLocation = result.getData().getStringExtra("data");
                            mSharedPreferences.edit().putString("defaultLocation", folderLocation).commit();
                            mDefaultStorage.setSummary(folderLocation);
                        }
                    }
                });

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            mDefaultStorage = findPreference("defaultLocation");
            if (mDefaultStorage != null) {
                mDefaultStorage.setSummary(mSharedPreferences.getString("defaultLocation", Constant.DOCUMENTS_FOLDER));
                mDefaultStorage.setOnPreferenceClickListener(
                        preference -> {
                            Intent i = new Intent(getContext(), FolderPicker.class);
                            mPickFile.launch(i);
                            return false;
                        }
                );
            }
        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
        }
    }
}