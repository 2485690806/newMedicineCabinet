package com.ycmachine.smartdevice.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ycmachine.smartdevice.R;

import java.io.File;
import java.util.List;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mImagePaths;

    public GridImageAdapter(Context context, List<String> imagePaths) {
        mContext = context;
        mImagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = mImagePaths.get(position);
        
        // 使用Glide加载本地图片
        Glide.with(mContext)
                .load(new File(imagePath)) // 从文件路径加载
                .into(holder.ivGrid);
    }

    @Override
    public int getItemCount() {
        return mImagePaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGrid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGrid = itemView.findViewById(R.id.iv_grid);
        }
    }
}