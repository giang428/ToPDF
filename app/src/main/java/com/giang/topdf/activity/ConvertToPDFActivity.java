package com.giang.topdf.activity;

import static com.giang.topdf.R.id.convert;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.R;
import com.giang.topdf.utils.Constant;
import com.giang.topdf.utils.FileUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.sdf.SDFDoc;

import java.io.File;

import lib.folderpicker.FolderPicker;

public class ConvertToPDFActivity extends AppCompatActivity {
    Button mConvertButton, mBrowserButton;
    View mView;
    EditText mFilePath;
    TextView mStatus, mFileName, mFileSize;
    ImageView mIcon;
    ActivityResultLauncher<Intent> mPickFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String fileLocation;
                    if (result.getData() != null) {

                        fileLocation = result.getData().getStringExtra("data");
                        String mFileType = FileUtils.getFileExtension(fileLocation);

                        if (Constant.MS_FILE_TYPE.contains(mFileType)) {
                            mFilePath.setText(fileLocation);
                            Toast.makeText(this,
                                    getString(R.string.add_file_successfully) + " " + FileUtils.getFileName(fileLocation),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(this,
                                    getString(R.string.unsupport_type) + mFileType,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            });

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_to_pdfactivity);
        mView = findViewById(convert);
        mConvertButton = this.findViewById(R.id.save_button);
        mBrowserButton = this.findViewById(R.id.browse_button);
        mStatus = this.findViewById(R.id.file_chosen_status);
        mFileName = this.findViewById(R.id.file_chosen_name);
        mFileSize = this.findViewById(R.id.file_chosen_size);
        mFilePath = this.findViewById(R.id.file_path);
        mIcon = this.findViewById(R.id.file_icon);

        mBrowserButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, FolderPicker.class);
            intent.putExtra("pickFiles", true);
            intent.putExtra("title", "Select a document file to convert");
            mPickFile.launch(intent);
        });

        mConvertButton.setOnClickListener(view -> {
            try {
                Intent i = new Intent(this,SaveActivity.class);
                i.putExtra("activity_convert",true);
                i.putExtra("filePath",mFilePath.getText().toString());
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(this, "No file selected!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void convertFile(String filePath, String outputPath) {
        try {
            PDFDoc pdfdoc = new PDFDoc();
            Convert.officeToPdf(pdfdoc, filePath, null);
            pdfdoc.save(outputPath, SDFDoc.SaveMode.INCREMENTAL, null);
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setMessage(R.string.convert_success_msg);
            dialog.setPositiveButton(R.string.action_view, (successDialog, which) -> FileUtils.viewFile(this, outputPath));
            dialog.setNegativeButton(R.string.action_close, (successDialog, which) -> successDialog.cancel());
            dialog.setNeutralButton(R.string.action_share, (successDialog, which) -> FileUtils.shareFile(this, outputPath));
            dialog.show();
        } catch (PDFNetException e) {
            Toast.makeText(this, "Error when converting file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }
}