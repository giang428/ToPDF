package com.giang.topdf.activity;

import static com.giang.topdf.R.id.convert;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.giang.topdf.R;
import com.giang.topdf.fragment.ConvertOptionsFragment;
import com.giang.topdf.fragment.FileChooserFragment;
import com.giang.topdf.utils.FileUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.sdf.SDFDoc;

public class ConvertToPDFActivity extends AppCompatActivity {
    Button mConvertButton,mViewButton,mShareButton;
    View mView;
    String inputPath =" ",outputPath =" ";
    private FileChooserFragment fileChooserFragment;
    private ConvertOptionsFragment convertOptionsFragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_to_pdfactivity);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.fileChooserFragment = (FileChooserFragment) fragmentManager.findFragmentById(R.id.filechooserFragment);
        this.convertOptionsFragment = (ConvertOptionsFragment) fragmentManager.findFragmentById(R.id.convertOptionsFragment);

        mView = findViewById(convert);

        mConvertButton = this.findViewById(R.id.convert_and_save_button);
        mShareButton = this.findViewById(R.id.share_file_button);
        mViewButton = this.findViewById(R.id.view_file_button);

        mShareButton.setEnabled(false);
        mViewButton.setEnabled(false);

        mConvertButton.setOnClickListener(view ->{
            try{
                    inputPath = this.fileChooserFragment.getPath();
                    /*outputPath = convertOptionsFragment.getmSaveFilePath()
                            + convertOptionsFragment.getmFileName()
                            + ".pdf";*/
                    outputPath = FileUtils.getFilePath(inputPath) + FileUtils.getFileNameWithoutExtension(inputPath) + ".pdf";
                    Toast.makeText(this, outputPath, Toast.LENGTH_SHORT).show();
                    if (FileUtils.fileExists(outputPath)) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.file_already_exist)
                                .setMessage(getString(R.string.m1) + FileUtils.getFileName(outputPath) + getString(R.string.m2) + FileUtils.getFilePath(outputPath) + getString(R.string.m3))
                                .setPositiveButton(R.string.action_rename, ((dialog, which) -> {
                                }))
                                .setNegativeButton(R.string.action_overwrite, ((dialog, which) -> convertFile(inputPath, outputPath)))
                                .setNeutralButton(R.string.action_close, ((dialog, which) -> dialog.cancel()))
                                .show();

                    } else convertFile(inputPath, outputPath);
                }

                //}
            catch (NullPointerException e){
                Toast.makeText(this,"No file selected!",Toast.LENGTH_LONG).show();
            }
        });

        mShareButton.setOnClickListener(v -> FileUtils.shareFile(this,outputPath));
        mViewButton.setOnClickListener(v -> FileUtils.viewFile(this,outputPath));
    }
    private void convertFile(String filePath, String outputPath) {
        try {
            PDFDoc pdfdoc = new PDFDoc();
            Convert.officeToPdf(pdfdoc,filePath, null);
            pdfdoc.save(outputPath, SDFDoc.SaveMode.INCREMENTAL, null);
            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
            dialog.setMessage(R.string.convert_success_msg);
            dialog.setPositiveButton(R.string.action_view, (successDialog, which) -> FileUtils.viewFile(this,outputPath));
            dialog.setNegativeButton(R.string.action_close, (successDialog, which) -> successDialog.cancel());
            dialog.setNeutralButton(R.string.action_share, (successDialog, which) -> FileUtils.shareFile(this,outputPath));
            dialog.show();
            mShareButton.setEnabled(true);
            mViewButton.setEnabled(true);
        } catch (PDFNetException e) {
            Toast.makeText(this,"Error when converting file",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed(){
        super.finish();
    }
}