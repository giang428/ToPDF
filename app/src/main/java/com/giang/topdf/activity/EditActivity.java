package com.giang.topdf.activity;

import static com.giang.topdf.utils.Constant.IMAGE_LIST_URI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.giang.topdf.R;
import com.giang.topdf.adapter.EditAdapter;
import com.giang.topdf.adapter.EditImageOptionListAdapter;
import com.giang.topdf.model.EditItem;
import com.giang.topdf.utils.Constant;
import com.giang.topdf.utils.DepthPageTransformer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.type.ButtonGravity;
import gun0912.tedimagepicker.builder.type.MediaType;

public class EditActivity extends AppCompatActivity implements EditImageOptionListAdapter.OnItemClickListener {
    Button mBack, mConfirm;
    ViewPager mViewPager;
    EditAdapter mEditAdapter;
    EditImageOptionListAdapter mEditImageOptionListAdapter;
    ArrayList<Uri> mGetUri, mReturnUri;
    ActivityResultLauncher<Intent> cropActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() != null && result.getResultCode() == 727) {
                    Uri cropped = Uri.parse(result.getData().getStringExtra("h"));
                    //Toast.makeText(this, cropped.toString(), Toast.LENGTH_SHORT).show();
                    int p = result.getData().getIntExtra("position", 1);
                    try {
                        mReturnUri.set(p, cropped);
                        mEditAdapter.notifyDataSetChanged();
                        mViewPager.setAdapter(mEditAdapter);
                        mViewPager.setCurrentItem(p);
                    } catch (Exception e) {
                        Toast.makeText(this, "a " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_edit);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //get arrayList Uri from preview activity
        mGetUri = getIntent().getParcelableArrayListExtra(IMAGE_LIST_URI);
        mReturnUri = mGetUri;

        mBack = findViewById(R.id.backbtn);
        mConfirm = findViewById(R.id.confirmbtn);

        //init viewpager
        initViewPager(mGetUri);
        //init option menu
        showOptionMenu();
        //on Back pressed, dismiss all changes
        mBack.setOnClickListener(v -> {
            //if (mGetUri.equals(mReturnUri)) super.finish();
            //else {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder
                    .setTitle(R.string.confirm_back_title)
                    .setMessage(R.string.confirm_back_msg)
                    .setPositiveButton(R.string.no_str, (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(R.string.yes_str, (dialog, which) -> super.finish())
                    .show();
            // }
        });
        //on Confirm pressed, return the edited Uri
        mConfirm.setOnClickListener(v -> {
            Intent i = getIntent();
            i.putExtra(IMAGE_LIST_URI, mReturnUri);
            setResult(Constant.RESULT_EDIT, i);
            finish();
        });
    }

    private void showOptionMenu() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView mRecyclerView = this.findViewById(R.id.recyclerView1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mEditImageOptionListAdapter = new EditImageOptionListAdapter(this, getOptions(),
                getApplicationContext());
        mRecyclerView.setAdapter(mEditImageOptionListAdapter);
    }

    private ArrayList<EditItem> getOptions() {
        ArrayList<EditItem> itemArrayList = new ArrayList<>();
        itemArrayList.add(new EditItem(R.drawable.ic_edit_add_image, getString(R.string.item_add)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_crop_image, getString(R.string.item_crop)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_rotate_image, getString(R.string.item_rotate)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_filter_image, getString(R.string.item_filter)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_rearrange_image, getString(R.string.item_rearrange)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_remove_image, getString(R.string.item_remove)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_draw_image, getString(R.string.item_draw)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_add_text_image, getString(R.string.item_add_text)));
        itemArrayList.add(new EditItem(R.drawable.ic_edit_watermark_image, getString(R.string.item_watermark)));

        return itemArrayList;
    }

    private void initViewPager(ArrayList<Uri> images) {
        mViewPager = findViewById(R.id.viewpager);
        mEditAdapter = new EditAdapter(this, images);
        mViewPager.setAdapter(mEditAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                addImages();
                break;
            case 1:
                cropImage(mViewPager.getCurrentItem());
                break;
            case 2:
                rotateImage(mViewPager.getCurrentItem());

                break;
            case 3:
                applyFilter();
                break;
            case 4:
                rearrangeImages();
                break;
            case 5:
                removeImage(mViewPager.getCurrentItem());
                break;
            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void rotateImage(int pos) {
        try {
            Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), mReturnUri.get(pos));
            Matrix x = new Matrix();
            x.postRotate(90);
            Bitmap z = Bitmap.createBitmap(b, 0, 0,
                    b.getWidth(),
                    b.getHeight(), x, false);
            OutputStream os = getContentResolver().openOutputStream(mReturnUri.get(pos));
            z.compress(Bitmap.CompressFormat.PNG, 100, os);
            Toast.makeText(this, "rotated", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "error " + e, Toast.LENGTH_SHORT).show();
        }
        mEditAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mEditAdapter);
        mViewPager.setCurrentItem(pos);
    }

    private void removeImage(int pos) {
        if (mReturnUri.size() == 1)
            Toast.makeText(this, R.string.delete_error_msg, Toast.LENGTH_SHORT).show();
        else {
            mReturnUri.remove(pos);
            mEditAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mEditAdapter);
            mViewPager.setCurrentItem(pos);
        }
    }

    private void rearrangeImages() {
    }

    private void applyFilter() {
    }

    private void cropImage(int pos) {
        Intent i = new Intent(this, CropActivity.class);
        i.putExtra("h", mReturnUri.get(mViewPager.getCurrentItem()));
        i.putExtra("position", pos);
        cropActivity.launch(i);
    }

    private void addImages() {
        TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .buttonGravity(ButtonGravity.BOTTOM)
                .startMultiImage(uriList -> {
                            mReturnUri.addAll(uriList);
                            mEditAdapter.notifyDataSetChanged();
                            mViewPager.setAdapter(mEditAdapter);
                            Toast.makeText(this, uriList.size() + getString(R.string.number_of_image_added), Toast.LENGTH_SHORT).show();
                        }
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}