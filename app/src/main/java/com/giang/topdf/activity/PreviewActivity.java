package com.giang.topdf.activity;

import static com.giang.topdf.utils.Constant.ACTIVITY_CREATE;
import static com.giang.topdf.utils.Constant.IMAGE_LIST_URI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giang.topdf.R;
import com.giang.topdf.adapter.PreviewAdapter;
import com.giang.topdf.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PreviewActivity extends AppCompatActivity {
    Button mSave, mEdit;
    private ArrayList<Uri> mListImages;
    private PreviewAdapter mPreviewAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.preview_images);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        mSave = findViewById(R.id.save_btn);
        mEdit = findViewById(R.id.edit_btn);
        mListImages = getIntent().getParcelableArrayListExtra(IMAGE_LIST_URI);
        Toast.makeText(this, mListImages.size() + getString(R.string.number_of_image_added), Toast.LENGTH_SHORT).show();

        ActivityResultLauncher<Intent> mGetUriFromEditActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Constant.RESULT_EDIT && result.getData() != null) {
                        mListImages = result.getData().getParcelableArrayListExtra(IMAGE_LIST_URI);
                        mPreviewAdapter.setData(mListImages);
                        recyclerView.setAdapter(mPreviewAdapter);
                    }
                }
        );
        initRecycleView(mListImages);
        mSave.setOnClickListener(v -> {
            Intent intent = new Intent(this, SaveActivity.class);
            intent.putParcelableArrayListExtra(IMAGE_LIST_URI, mListImages);
            String mFileNameCurrentDateTime = getCurrentDateTime();
            intent.putExtra(ACTIVITY_CREATE, true);
            intent.putExtra("fileName", mFileNameCurrentDateTime);
            startActivity(intent);
        });
        mEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putParcelableArrayListExtra(IMAGE_LIST_URI, mListImages);
            mGetUriFromEditActivity.launch(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreviewAdapter.notifyDataSetChanged();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initRecycleView(ArrayList<Uri> mListImagesUri) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        mPreviewAdapter = new PreviewAdapter(this, mListImagesUri);
        recyclerView.setAdapter(mPreviewAdapter);

    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
