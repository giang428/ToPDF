package com.giang.topdf.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giang.topdf.R;
import com.giang.topdf.adapter.PreviewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PreviewActivity extends AppCompatActivity {
    Button mSave,mEdit;
    private ArrayList<Uri> mListImages;
    private PreviewAdapter mPreviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setTitle("Preview PDF");
        mSave = findViewById(R.id.save_btn);
        mEdit = findViewById(R.id.edit_btn);
        mListImages = getIntent().getParcelableArrayListExtra("uriList");
        Toast.makeText(this, mListImages.size() + " images added", Toast.LENGTH_SHORT).show();
        initRecycleView(mListImages);

        mSave.setOnClickListener(v -> {
            Intent intent = new Intent(this,SaveActivity.class);
            intent.putParcelableArrayListExtra("uriList",mListImages);
            String mFileNameCurrentDateTime = getCurrentDateTime();
            intent.putExtra("activity_create",true);
            intent.putExtra("fileName",mFileNameCurrentDateTime);
            startActivity(intent);
        });
    }
    private void initRecycleView(ArrayList<Uri> mListImagesUri) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        mPreviewAdapter = new PreviewAdapter(this,mListImagesUri);
        recyclerView.setAdapter(mPreviewAdapter);
    }
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
