package com.giang.topdf.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.giang.topdf.R;
import com.giang.topdf.model.EditItem;

import java.util.ArrayList;

public class EditImageOptionListAdapter extends RecyclerView.Adapter<EditImageOptionListAdapter.ViewHolder> {
    private final ArrayList<EditItem> mOptions;
    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;

    public EditImageOptionListAdapter(OnItemClickListener onItemClickListener,
                                      ArrayList<EditItem> optionItems, Context context) {
        mOnItemClickListener = onItemClickListener;
        mOptions = optionItems;
        mContext = context;
    }

    @NonNull
    @Override
    public EditImageOptionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon,
                parent, false);
        return new EditImageOptionListAdapter.ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull EditImageOptionListAdapter.ViewHolder holder, int position) {
        int imageId = mOptions.get(position).getItemId();
        holder.imageView.setImageDrawable(AppCompatResources.getDrawable(mContext, imageId));
        holder.textView.setText(mOptions.get(position).getItemName());
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgToolIcon);
            textView = itemView.findViewById(R.id.txtTool);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(getBindingAdapterPosition());
        }
    }
}
