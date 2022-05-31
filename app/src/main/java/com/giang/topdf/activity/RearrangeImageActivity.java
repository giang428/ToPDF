package com.giang.topdf.activity;

import static com.giang.topdf.utils.Constant.IMAGE_LIST_URI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.giang.topdf.R;
import com.giang.topdf.adapter.RearrangeAdapter;
import com.giang.topdf.utils.Constant;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class RearrangeImageActivity extends AppCompatActivity implements RearrangeAdapter.onClickListener {
    RecyclerView mRecycleView;
    private ArrayList<Uri> mImageList;
    private RearrangeAdapter mRearrangeAdapter;
    Button mConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rearrange_image);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Rearrange images");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent i = getIntent();
        mImageList = i.getParcelableArrayListExtra(IMAGE_LIST_URI);
        initRecycleView(mImageList);
        mConfirm = findViewById(R.id.rearrangeConfirmBtn);
        mConfirm.setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.putParcelableArrayListExtra(IMAGE_LIST_URI,mImageList);
            setResult(1337,intent);
            finish();
        });
    }

    private void initRecycleView(ArrayList<Uri> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecycleView = findViewById(R.id.rearrangeView);
        mRecycleView.setLayoutManager(layoutManager);
        mRearrangeAdapter = new RearrangeAdapter(this,list, this);
        mRecycleView.setAdapter(mRearrangeAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setTitle(R.string.confirm_back_title)
                .setMessage(R.string.confirm_back_msg)
                .setPositiveButton(R.string.no_str, (dialog,which) -> dialog.dismiss())
                .setNegativeButton(R.string.yes_str, (dialog,which) -> onBackPressed())
                .show();
        return false;
    }

    @Override
    public void onUpButtonClicked(int position) {
        mImageList.add(position - 1, mImageList.remove(position));
        mRearrangeAdapter.positionChanged(mImageList);
    }

    @Override
    public void onDownButtonClicked(int position) {
        mImageList.add(position + 1, mImageList.remove(position));
        mRearrangeAdapter.positionChanged(mImageList);
    }
}