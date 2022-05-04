package com.giang.topdf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.giang.topdf.R;

import lib.folderpicker.FolderPicker;

public class ConvertOptionsFragment extends Fragment {
    EditText mFileName;
    EditText mSaveFilePath;
    String mFileNamestr,mSaveFilePathstr;
    RadioGroup mQuality;
    public ConvertOptionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    ActivityResultLauncher<Intent> mPickFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result ->{
        if(result.getResultCode() == Activity.RESULT_OK){
            String folderLocation = null;
            if(result.getData() != null) {
                folderLocation = result.getData().getStringExtra("data");
            }
            setmSaveFilePath(folderLocation);
        }
    });
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_convert_opions, container, false);
        mFileName = root.findViewById(R.id.save_file_name_edit);
        if(getArguments() != null) {
            mFileName.setText(getArguments().getString("fileName"));
           // mSaveFilePath.setText(getArguments().getString("defaultSavePath"));
        }
        mSaveFilePath = root.findViewById(R.id.save_file_path_edit);

        mFileNamestr = this.mFileName.getText().toString();
        mSaveFilePathstr = this.mSaveFilePath.getText().toString();

        mQuality = root.findViewById(R.id.quality_pick);


        mQuality.check(R.id.radioButton_o);

        mSaveFilePath.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (mSaveFilePath.getRight() - mSaveFilePath.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Intent i = new Intent(this.getContext(), FolderPicker.class);
                    mPickFile.launch(i);
                    return true;
                }
            }
            return false;
        });
        return root;
    }

    public void setmSaveFilePath(String mSaveFilePath) {
        this.mSaveFilePath.setText(mSaveFilePath);
    }

    public String getmFileName() {
        return mFileNamestr;
    }
    public String getmFilePath(){
        return mSaveFilePathstr;
    }
}
