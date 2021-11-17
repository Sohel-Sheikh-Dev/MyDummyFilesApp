package com.example.thefilesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    Context context;
    String [] categoryItemList = {"Downloads","Images","Videos","Audio","Documents & other","Apps"};
    String [] categoryItemListSizes = {"315 MB","193 MB","6.6 GB","45 kB","112 MB","13 GB"};
    int[] images = {R.drawable.download_icon,R.drawable.images_icon,R.drawable.videos_icon,R.drawable.ic_baseline_audiotrack_24,R.drawable.document,R.drawable.option};
    
    
    public MainActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.main_icon_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.ViewHolder holder, int position) {
        holder.iconName.setText(categoryItemList[position]);
        holder.iconStorage.setText(categoryItemListSizes[position]);
        Glide.with(context.getApplicationContext()).load(images[position]).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return categoryItemList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView iconName, iconStorage;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconName = itemView.findViewById(R.id.icon_name);
            iconStorage = itemView.findViewById(R.id.storage);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
