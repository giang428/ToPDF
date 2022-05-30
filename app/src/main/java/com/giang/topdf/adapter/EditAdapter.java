package com.giang.topdf.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.giang.topdf.R;

import java.util.ArrayList;

public class EditAdapter extends PagerAdapter {
    private final Context mContext;
    private final ArrayList<Uri> mEditImages;
    private final LayoutInflater mLayoutInflater;

    public EditAdapter(Context mContext, ArrayList<Uri> mEditImages) {
        this.mContext = mContext;
        this.mEditImages = mEditImages;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    public EditAdapter(Context mContext, ArrayList<Uri> mEditImages, LayoutInflater mLayoutInflater) {
        this.mContext = mContext;
        this.mEditImages = mEditImages;
        this.mLayoutInflater = mLayoutInflater;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        View editImageView = mLayoutInflater.inflate(R.layout.edit_image_layout, view, false);
        ImageView imageView = editImageView.findViewById(R.id.edit_img);
        Glide.with(mContext)
                .load(mEditImages.get(position))
                .into(imageView);
        view.addView(editImageView, 0);
        return editImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return String.format("%d / %d", position + 1, getCount());
    }

    @Override
    public int getCount() {
        return mEditImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}