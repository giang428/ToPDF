package com.giang.topdf.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.R;
import com.giang.topdf.fragment.ConvertOptionsFragment;
import com.giang.topdf.utils.FileUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SaveActivity extends AppCompatActivity {
    ArrayList<Uri> mImagesList;
    Button mSaveButton;
    String mSavefileName, mSavefilePath;
    ConvertOptionsFragment mConvertOptionsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        mSaveButton = findViewById(R.id.save_pdf_btn);
        mConvertOptionsFragment = (ConvertOptionsFragment) getSupportFragmentManager().findFragmentById(R.id.saveOptionsFragment);

        Bundle b = new Bundle();
        //b.putString("defaultSavePath",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
        b.putString("fileName",getCurrentDateTime());
        mConvertOptionsFragment.setArguments(b);

        mImagesList = getIntent().getParcelableArrayListExtra("uriList");
        mSavefileName = this.mConvertOptionsFragment.getmFileName();
        mSavefilePath = this.mConvertOptionsFragment.getmFilePath();
        mSaveButton.setOnClickListener(v -> createPdf(mImagesList));
    }
    private void createPdf(ArrayList<Uri> mImagesList) {
        //String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        //Toast.makeText(this, mPath, Toast.LENGTH_SHORT).show();
        Rectangle pageSize = new Rectangle(PageSize.A4);
        pageSize.setBackgroundColor(BaseColor.WHITE);
        Document document = new Document(pageSize,
                0, 0, 0, 0);
        Rectangle documentRect = document.getPageSize();
        try{
            File pdfFile = new File(mSavefilePath,mSavefileName + ".pdf");
            PdfWriter pdfWriter = PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
            document.open();
            for (int i = 0; i < mImagesList.size(); i++) {
                Image image = Image.getInstance(FileUtils.getPath(this,mImagesList.get(i)));
                image.setBorder(Rectangle.BOX);
                float pageWidth = document.getPageSize().getWidth();
                float pageHeight = document.getPageSize().getHeight();
                image.scaleAbsolute(pageWidth,pageHeight);
                image.setAbsolutePosition(
                        (documentRect.getWidth() - image.getScaledWidth()) / 2,
                        (documentRect.getHeight() - image.getScaledHeight()) / 2);
                document.add(image);
                document.newPage();
            }
            document.close();
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setMessage(R.string.convert_success_msg);
            dialog.setPositiveButton(R.string.action_view, (successDialog, which) -> FileUtils.viewFile(this,mSavefilePath + mSavefileName + ".pdf"));
            dialog.setNegativeButton(R.string.action_close, (successDialog, which) -> successDialog.cancel());
            dialog.setNeutralButton(R.string.action_share, (successDialog, which) -> FileUtils.shareFile(this,mSavefilePath + mSavefileName + ".pdf"));
            dialog.show();
        }catch (Exception e){
            Toast.makeText(this, "Path: " +mSavefilePath+mSavefileName + ".pdf" + "Error!" + e, Toast.LENGTH_SHORT).show();
        }
    }
    private String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }
}