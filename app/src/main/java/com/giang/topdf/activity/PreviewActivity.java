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

import java.util.ArrayList;


public class PreviewActivity extends AppCompatActivity {
    private ArrayList<Uri> mListImages;
    private PreviewAdapter mPreviewAdapter;
    Button mSave,mEdit;
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
}