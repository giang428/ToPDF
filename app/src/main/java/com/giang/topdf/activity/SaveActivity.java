package com.giang.topdf.activity;

import static com.giang.topdf.utils.Constant.ACTIVITY_CONVERT;
import static com.giang.topdf.utils.Constant.ACTIVITY_CREATE;
import static com.giang.topdf.utils.Constant.DOCUMENTS_FOLDER;
import static com.giang.topdf.utils.Constant.IMAGE_LIST_URI;
import static com.giang.topdf.utils.Constant.PAGE_SIZE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import lib.folderpicker.FolderPicker;

public class SaveActivity extends AppCompatActivity {
    ArrayList<Uri> mImagesList;
    Button mSaveButton, mView, mShare;
    String mSavefileName, mSavefilePath, mfinalPath;
    EditText mFileName, mFilePath;
    TextView mtxt1,mtxt2;
    RadioGroup mQualityPick;
    Spinner paperSize;
    int paperSizeSelected;
    boolean mIsCreatePDF, mIsCreated;
    SharedPreferences sharedPreferences;

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        mIsCreated = false;
        mImagesList = getIntent().getParcelableArrayListExtra(IMAGE_LIST_URI);
        mSaveButton = findViewById(R.id.save_button);
        mView = findViewById(R.id.view_file_button);
        mShare = findViewById(R.id.share_file_button);
        if (!mIsCreated) {
            mView.setEnabled(false);
            mShare.setEnabled(false);
        }
        //Create spinner
        paperSize = findViewById(R.id.paperSize);
        ArrayAdapter<String> paperSizeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.page_size_dropdown));
        paperSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paperSize.setAdapter(paperSizeAdapter);
        paperSizeSelected = PAGE_SIZE.get(PageSize.A4);
        paperSize.setSelection(paperSizeSelected);

        mQualityPick = findViewById(R.id.quality_pick);
        mQualityPick.check(R.id.radioButton_o);

        mFileName = this.findViewById(R.id.save_file_name_edit);
        mFilePath = this.findViewById(R.id.save_file_path_edit);

        mtxt1 = findViewById(R.id.quality_txt);
        mtxt2 = findViewById(R.id.page_size_txt);
        //Set default folder for saving pdf file
        mFilePath.setText(sharedPreferences.getString("defaultLocation", DOCUMENTS_FOLDER));
        //get intent
        Intent t = getIntent();
        if (t.hasExtra(ACTIVITY_CREATE)) {
            mFileName.setText(t.getExtras().getString("fileName"));
            mIsCreatePDF = true;
        } else if (t.hasExtra(ACTIVITY_CONVERT)) {
            String mFilePathReceivedFromIntent = t.getExtras().getString("filePath");
            mFileName.setText(FileUtils.getFileNameWithoutExtension(mFilePathReceivedFromIntent));
            mQualityPick.setVisibility(View.GONE);
            paperSize.setVisibility(View.GONE);
            mtxt1.setVisibility(View.GONE);
            mtxt2.setVisibility(View.GONE);
        }
        //Choosing another folder to save
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
                    String mFileOutputPath = mFilePath.getText().toString() + "/" + mFileName.getText().toString() + ".pdf";
                    if (FileUtils.fileExists(mFileOutputPath)) {

                        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
                        dialogBuilder.setTitle(R.string.file_already_exist)
                                .setMessage(getString(R.string.m1) + FileUtils.getFileName(mFileOutputPath) + " " + getString(R.string.m3))
                                .setPositiveButton(R.string.action_rename, (dialog, which) -> dialog.dismiss())
                                .setNegativeButton(R.string.action_overwrite, (dialog, which) -> convertPdf(mFilePathReceivedFromIntent, mFileOutputPath))
                                .show();

                    } else convertPdf(mFilePathReceivedFromIntent, mFileOutputPath);
                }
            } catch (Exception e) {
                mIsCreated = false;
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });

        mView.setOnClickListener(v -> {
            try {
                FileUtils.viewFile(this, mfinalPath);
            } catch (Exception e) {
                Toast.makeText(this, "Error! " + e, Toast.LENGTH_SHORT).show();
            }
        });
        mShare.setOnClickListener(v -> {
            try {
                FileUtils.shareFile(this, mfinalPath);
            } catch (Exception e) {
                Toast.makeText(this, "Error! " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void convertPdf(String filePath, String outputPath) {
        try {
            PDFDoc pdfdoc = new PDFDoc();
            Convert.officeToPdf(pdfdoc, filePath, null);
            pdfdoc.save(outputPath, SDFDoc.SaveMode.INCREMENTAL, null);
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setMessage(R.string.convert_success_msg)
                    .setPositiveButton(R.string.action_view, (successDialog, which) -> FileUtils.viewFile(this, outputPath))
                    .setNegativeButton(R.string.action_close, (successDialog, which) -> successDialog.cancel())
                    .setNeutralButton(R.string.action_share, (successDialog, which) -> FileUtils.shareFile(this, outputPath))
                    .show();
            mIsCreated = true;
            if (mIsCreated) {
                mfinalPath = outputPath;
                mView.setEnabled(true);
                mShare.setEnabled(true);
            }
        } catch (PDFNetException e) {
            Toast.makeText(this, "Error when converting file", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdf(ArrayList<Uri> mImagesList) {
        mSavefileName = mFileName.getText().toString();
        mSavefilePath = mFilePath.getText().toString() + "/";
        Rectangle pageSize = new Rectangle(getPageSize(paperSize.getSelectedItemPosition()));
        pageSize.setBackgroundColor(BaseColor.WHITE);
        Document document = new Document(pageSize,
                0, 0, 0, 0);
        Rectangle documentRect = document.getPageSize();
        try {
            File pdfFile = new File(mSavefilePath, mSavefileName + ".pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            for (int i = 0; i < mImagesList.size(); i++) {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImagesList.get(i));
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, getQuality(), bytes);
                Image image = Image.getInstance(bytes.toByteArray());
                image.setBorder(Rectangle.BOX);
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();
                image.scaleToFit(pageWidth, pageHeight);
                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);
                document.add(image);
                document.newPage();
            }
            document.close();

            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setMessage(R.string.convert_success_msg)
                    .setPositiveButton(R.string.action_view,
                            (successDialog, which)
                                    -> FileUtils.viewFile(this, mSavefilePath + mSavefileName + ".pdf"))
                    .setNegativeButton(R.string.action_close,
                            (successDialog, which)
                                    -> successDialog.cancel())
                    .setNeutralButton(R.string.action_share,
                            (successDialog, which)
                                    -> FileUtils.shareFile(this, mSavefilePath + mSavefileName + ".pdf"))
                    .show();
            mIsCreated = true;
            if (mIsCreated) {
                mfinalPath = mSavefilePath + mSavefileName + ".pdf";
                mView.setEnabled(true);
                mShare.setEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Path: " + mSavefilePath + mSavefileName + ".pdf" + " Error! " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private Rectangle getPageSize(int paperSizeSelected) {
        for(Map.Entry<Rectangle,Integer> entry : PAGE_SIZE.entrySet()){
            if(Objects.equals(paperSizeSelected,entry.getValue())) return entry.getKey();
        }
        return PageSize.A4;
    }

    @SuppressLint("NonConstantResourceId")
    private int getQuality() {
        int quality;
        switch (mQualityPick.getCheckedRadioButtonId()) {
            case R.id.radioButton_o:
                quality = 100;
                break;
            case R.id.radioButton_h:
                quality = 75;
                break;
            case R.id.radioButton_m:
                quality = 50;
                break;
            case R.id.radioButton_l:
                quality = 25;
                break;
            default:
                quality = 30;
                break;
        }
        return quality;
    }
}