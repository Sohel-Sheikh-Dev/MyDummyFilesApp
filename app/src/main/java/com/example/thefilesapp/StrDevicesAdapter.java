package com.example.thefilesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StrDevicesAdapter extends RecyclerView.Adapter<StrDevicesAdapter.ViewHolder> {


    Context context;
    long totalImageSize, totalVideoSize, totalAudioSize, totalApkSize, totalDocSize;
    String[] categoryItemList = {"Downloads", "Images", "Videos", "Audio", "Documents & other", "Apps","Apk files"};
    String[] categoryItemListSizes = {"315 MB", "193 MB", "6.6 GB", "45 kB", "112 MB", "13 GB","11 GB"};
    int[] images = {R.drawable.download_icon, R.drawable.images_icon, R.drawable.videos_icon, R.drawable.ic_baseline_audiotrack_24, R.drawable.document, R.drawable.option, R.drawable.file_open};


    public StrDevicesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public StrDevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StrDevicesAdapter.ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.main_icon_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull StrDevicesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
