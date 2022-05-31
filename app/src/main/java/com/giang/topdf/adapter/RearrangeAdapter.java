package com.giang.topdf.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.giang.topdf.R;

import java.util.ArrayList;

public class RearrangeAdapter extends RecyclerView.Adapter<RearrangeAdapter.RearrangeViewHolder> {
    private final RearrangeAdapter.onClickListener mOnClickListener;
    private ArrayList<Uri> mImageList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public RearrangeAdapter(RearrangeAdapter.onClickListener onClickListener, ArrayList<Uri> mImageList, Context mContext) {
        this.mOnClickListener = onClickListener;
        this.mImageList = mImageList;
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RearrangeAdapter.RearrangeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rearrange_layout, parent, false);
        return new RearrangeAdapter.RearrangeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RearrangeAdapter.RearrangeViewHolder holder, int position) {
        Uri img = mImageList.get(position);
        Glide.with(mContext)
                .load(img)
                .into(holder.mImageView);
        if (position == 0) {
            holder.mUpButton.setVisibility(View.GONE);
        } else {
            holder.mUpButton.setVisibility(View.VISIBLE);
        }
        if (position == getItemCount() - 1) {
            holder.mDownButton.setVisibility(View.GONE);
        } else {
            holder.mDownButton.setVisibility(View.VISIBLE);
        }
        holder.mNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public void positionChanged(ArrayList<Uri> images) {
        mImageList = images;
        notifyDataSetChanged();
    }

    public interface onClickListener {
        void onUpButtonClicked(int position);

        void onDownButtonClicked(int position);
    }

    public class RearrangeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;
        TextView mNumber;
        ImageButton mUpButton, mDownButton;

        public RearrangeViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.rearrangeImageView);
            mNumber = itemView.findViewById(R.id.pageNumber1);
            mUpButton = itemView.findViewById(R.id.buttonUp);
            mDownButton = itemView.findViewById(R.id.buttonDown);
            mUpButton.setOnClickListener(this);
            mDownButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.buttonUp) {
                mOnClickListener.onUpButtonClicked(getAbsoluteAdapterPosition());
            } else if (v.getId() == R.id.buttonDown) {
                mOnClickListener.onDownButtonClicked(getAbsoluteAdapterPosition());
            }
        }
    }
}
