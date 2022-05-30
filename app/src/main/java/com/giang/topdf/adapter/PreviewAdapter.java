package com.giang.topdf.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.giang.topdf.R;

import java.util.ArrayList;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewViewHolder> {
    private final Context mContext;
    private final ArrayList<Uri> mPreviewImages;
    private final LayoutInflater mLayoutInflater;

    public PreviewAdapter(Context context, ArrayList<Uri> previewItems) {
        this.mContext = context;
        this.mPreviewImages = previewItems;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public PreviewAdapter(Context mContext, ArrayList<Uri> mPreviewImages, LayoutInflater mLayoutInflater) {
        this.mContext = mContext;
        this.mPreviewImages = mPreviewImages;
        this.mLayoutInflater = mLayoutInflater;
    }

    @NonNull
    @Override
    public PreviewAdapter.PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_view_layout, parent, false);
        return new PreviewAdapter.PreviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewAdapter.PreviewViewHolder holder, int position) {
        Uri path = mPreviewImages.get(position);
        Glide.with(mContext)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(holder.imageView);
        holder.pageNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mPreviewImages.size();
    }

    public void setData(ArrayList<Uri> images) {
        mPreviewImages.clear();
        mPreviewImages.addAll(images);
    }

    public class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView pageNumber;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_layout);
            pageNumber = itemView.findViewById(R.id.pageNumber);
        }
    }
}
