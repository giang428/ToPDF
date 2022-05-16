package com.giang.topdf.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.R;
import com.giang.topdf.utils.FileUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.sdf.SDFDoc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import lib.folderpicker.FolderPicker;

public class SaveActivity extends AppCompatActivity {
    ArrayList<Uri> mImagesList;
    Button mSaveButton;
    String mSavefileName, mSavefilePath;
    EditText mFileName, mFilePath;
    boolean mIsCreatePDF;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        ActivityResultLauncher<Intent> mPickFile = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String folderLocation = null;
                        if (result.getData() != null) {
                            folderLocation = result.getData().getStringExtra("data");
                        }
                        mFilePath.setText(folderLocation);
                    }
                });

        mImagesList = getIntent().getParcelableArrayListExtra("uriList");
        mSaveButton = findViewById(R.id.save_button);
        //Create spinner
        Spinner paperSize = (Spinner) findViewById(R.id.paperSize);
        ArrayAdapter<String> paperSizeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.page_size_dropdown));
        paperSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paperSize.setAdapter(paperSizeAdapter);
        paperSize.setSelection(6);

        mFileName = this.findViewById(R.id.save_file_name_edit);
        mFilePath = this.findViewById(R.id.save_file_path_edit);
        mFilePath.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath());
        Intent t = getIntent();
        if (t.hasExtra("activity_create")) {
            mFileName.setText(t.getExtras().getString("fileName"));
            mIsCreatePDF = true;
        } else if (t.hasExtra("activity_convert")) {
            String mFilePathReceivedFromIntent = t.getExtras().getString("filePath");
            mFileName.setText(FileUtils.getFileNameWithoutExtension(mFilePathReceivedFromIntent));
        }
        mFilePath.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (mFilePath.getRight() - mFilePath.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Intent i = new Intent(this, FolderPicker.class);
                    mPickFile.launch(i);
                    return true;
                }
            }
            return false;
        });

        mSaveButton.setOnClickListener(v -> {
            try {
                if (mIsCreatePDF)
                    createPdf(mImagesList);
                else {
                    String mFilePathReceivedFromIntent = t.getExtras().getString("filePath");
                    String mFileOutputPath = mFilePath.getText().toString() + mFileName.getText().toString() + ".pdf";
                    convertPdf(mFilePathReceivedFromIntent, mFileOutputPath);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void convertPdf(String filePath, String outputPath) {
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

    private void createPdf(ArrayList<Uri> mImagesList) {
        mSavefileName = mFileName.getText().toString();
        mSavefilePath = mFilePath.getText().toString() + "/";
        Rectangle pageSize = new Rectangle(PageSize.A4);
        pageSize.setBackgroundColor(BaseColor.WHITE);
        Document document = new Document(pageSize,
                0, 0, 0, 0);
        Rectangle documentRect = document.getPageSize();
        try {
            File pdfFile = new File(mSavefilePath, mSavefileName + ".pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            for (int i = 0; i < mImagesList.size(); i++) {
                Image image = Image.getInstance(FileUtils.getPath(this, mImagesList.get(i)));
                image.setBorder(Rectangle.BOX);
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();
                image.scaleAbsolute(pageWidth, pageHeight);
                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);
                document.add(image);
                document.newPage();
            }
            document.close();
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setMessage(R.string.convert_success_msg);
            dialog.setPositiveButton(R.string.action_view, (successDialog, which) -> FileUtils.viewFile(this, mSavefilePath + mSavefileName + ".pdf"));
            dialog.setNegativeButton(R.string.action_close, (successDialog, which) -> successDialog.cancel());
            dialog.setNeutralButton(R.string.action_share, (successDialog, which) -> FileUtils.shareFile(this, mSavefilePath + mSavefileName + ".pdf"));
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Path: " + mSavefilePath + mSavefileName + ".pdf" + " Error! " + e, Toast.LENGTH_SHORT).show();
        }
    }
}