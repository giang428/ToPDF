package com.giang.topdf.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.giang.topdf.R;
import com.labters.documentscanner.DocumentScannerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CropActivity extends AppCompatActivity {
    DocumentScannerView documentScannerView;
    Button mCrop, mBack, mConfirm;
    ImageView mImageView, mRotate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_crop);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Uri i = getIntent().getParcelableExtra("h");
        documentScannerView = findViewById(R.id.document_scanner);
        mImageView = findViewById(R.id.result_image);
        mCrop = findViewById(R.id.btnImageCrop);
        mBack = findViewById(R.id.crop_backBtn);
        mBack.setVisibility(View.INVISIBLE);
        mConfirm = findViewById(R.id.crop_confirmBtn);
        mConfirm.setVisibility(View.INVISIBLE);
        mRotate = findViewById(R.id.rotateBtn);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), i);
            documentScannerView.setImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCrop.setOnClickListener(v -> {
            Bitmap images = documentScannerView.getCroppedImage();
            mImageView.setVisibility(View.VISIBLE);
            documentScannerView.setVisibility(View.INVISIBLE);
            mConfirm.setVisibility(View.VISIBLE);
            mBack.setVisibility(View.VISIBLE);
            mCrop.setVisibility(View.INVISIBLE);
            mImageView.setImageBitmap(images);
            //Intent intent = getIntent();
            // intent.putExtra("h",bitmap);
        });
        mBack.setOnClickListener(v -> {
            mImageView.setVisibility(View.INVISIBLE);
            documentScannerView.setVisibility(View.VISIBLE);
            mCrop.setVisibility(View.VISIBLE);
            mConfirm.setVisibility(View.INVISIBLE);
            mBack.setVisibility(View.INVISIBLE);
        });
        mConfirm.setOnClickListener(v -> {
            Bitmap e = documentScannerView.getCroppedImage();
            Intent k = getIntent();
            Uri img = getImageUri(e);
            k.putExtra("h", img.toString());
            k.putExtra("position", getIntent().getIntExtra("position", 1));
            setResult(727, k);
            finish();
        });
        Bitmap finalBitmap = bitmap;
        mRotate.setOnClickListener(v -> {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            if (finalBitmap != null) {
                Bitmap.createBitmap(finalBitmap, 0, 0,
                        finalBitmap.getWidth(),
                        finalBitmap.getHeight(), matrix, false);
                documentScannerView.setImage(finalBitmap);
            }
        });
    }

    private Uri getImageUri(Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        /*File f = new File(getCacheDir(),"tmp");
        try {
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "tmp", null);
        return Uri.parse(path);
    }
}
