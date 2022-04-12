package com.giang.topdf.activity;

import static com.giang.topdf.R.id.convert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;


import com.giang.topdf.BuildConfig;
import com.giang.topdf.R;
import com.giang.topdf.fragment.FileChooserFragment;
import com.google.android.material.snackbar.Snackbar;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.*;
import com.pdftron.sdf.SDFDoc;

import java.io.File;

public class ConvertToPDFActivity extends AppCompatActivity {

    Button mConvertbtn;
    View mView;
    private FileChooserFragment fileChooserFragment;
    ActivityResultLauncher<Intent> viewfilelaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result->{
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();}
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_to_pdfactivity);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.fileChooserFragment = (FileChooserFragment) fragmentManager.findFragmentById(R.id.filechooserFragment);
        mView = findViewById(convert);
        mConvertbtn = this.findViewById(R.id.convert_and_save_button);
        mConvertbtn.setOnClickListener(view ->{
            String inputpath = this.fileChooserFragment.getPath();
            String outputpath = inputpath.substring(0, inputpath.lastIndexOf('.')) + ".pdf";
            convertFile(inputpath,outputpath);
        });
    }
    private void convertFile(String filePath, String outputPath) {
        try {
            PDFDoc pdfdoc = new PDFDoc();
            Convert.officeToPdf(pdfdoc,filePath, null);
            pdfdoc.save(outputPath, SDFDoc.SaveMode.INCREMENTAL, null);
            Snackbar sucess = Snackbar
                    .make(mView,"Convert successfully!",Snackbar.LENGTH_LONG)
                    .setAction("View", view -> {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                       Uri oFile_uri =  FileProvider.getUriForFile(this,
                                BuildConfig.APPLICATION_ID + ".provider",new File(outputPath));
                        i.setDataAndType(oFile_uri,"application/pdf");
                        startActivity(i);
                        /*try {
                            viewfilelaunch.launch(i);
                        }catch (ActivityNotFoundException e){
                                Snackbar.make(mView,"Can't open file",Snackbar.LENGTH_SHORT).show();
                            }*/
                    });
            sucess.show();
        } catch (PDFNetException e) {
            Toast.makeText(this,"Error when converting file",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.finish();
    }
}