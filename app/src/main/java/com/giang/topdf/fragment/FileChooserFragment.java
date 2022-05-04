package com.giang.topdf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.giang.topdf.R;
import com.giang.topdf.utils.FileUtils;

import java.util.ArrayList;

import lib.folderpicker.FolderPicker;

public class FileChooserFragment extends Fragment {
    private final String[] mimeType = new String[]{
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"

    };
    private final ArrayList<String> fileType = new ArrayList<String>() {
        {
            add(".doc");
            add(".docx");
            add(".xls");
            add(".xlsx");
            add(".ppt");
            add(".pptx");
        }
    };
    Button mBrowseButton;
    EditText meditText_path;
    ActivityResultLauncher<Intent> mPickFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String fileLocation;
                    if (result.getData() != null) {
                        fileLocation = result.getData().getStringExtra("data");
                        String type = FileUtils.getFileExtension(fileLocation);
                        if (fileType.contains(type)) {
                            meditText_path.setText(fileLocation);
                            Toast.makeText(getContext(), getString(R.string.add_file_successfully) + " " + FileUtils.getFileName(fileLocation), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this.getContext(), getString(R.string.unsupport_type) + type, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    public String getPath() {
        return this.meditText_path.getText().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_file_chooser, container, false);
        mBrowseButton = root.findViewById(R.id.browse_button);
        meditText_path = root.findViewById(R.id.file_path);
        mBrowseButton.setOnClickListener(view -> {
            Intent intent = new Intent(this.getContext(), FolderPicker.class);
            intent.putExtra("pickFiles", true);
            mPickFile.launch(intent);
        });
        return root;
    }
}