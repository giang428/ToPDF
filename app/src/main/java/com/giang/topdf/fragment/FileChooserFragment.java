package com.giang.topdf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

public class FileChooserFragment extends Fragment {
    String[] mimetype = new String[]{
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"

    };
    Button mBrowseButton;
    EditText meditText_path;
    ActivityResultLauncher<Intent> sfilechooser = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null) {
                        Uri fileUri = data.getData();
                        String filePath = null;
                        try {
                            filePath = FileUtils.getPath(this.getContext(), fileUri);
                            Toast.makeText(getContext(),"Success add" + FileUtils.getFileName(filePath),Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(this.getContext(), "An error has occurred!", Toast.LENGTH_SHORT).show();
                        }
                        this.meditText_path.setText(filePath);
                    }
                }
            }
    );
    public String getPath()  {
        return this.meditText_path.getText().toString();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View root = inflater.inflate(R.layout.fragment_file_chooser, container, false);
        mBrowseButton = root.findViewById(R.id.browse_button);
        meditText_path = root.findViewById(R.id.file_path);
        mBrowseButton.setOnClickListener(view ->{
            String folderPath = Environment.getExternalStorageDirectory() + "/";
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            Uri myUri = Uri.parse(folderPath);
            intent.setDataAndType(myUri, "*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
            intent = Intent.createChooser(intent, "Choose a file");
            sfilechooser.launch(intent);
        });
        return root;
    }
}