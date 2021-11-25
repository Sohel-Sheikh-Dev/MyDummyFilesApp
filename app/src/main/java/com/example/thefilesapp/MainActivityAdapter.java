package com.example.thefilesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    Context context;
    String[] categoryItemList = {"Downloads", "Images", "Videos", "Audio", "Documents & other", "Apps"};
    String[] categoryItemListSizes = {"315 MB", "193 MB", "6.6 GB", "45 kB", "112 MB", "13 GB"};
    int[] images = {R.drawable.download_icon, R.drawable.images_icon, R.drawable.videos_icon, R.drawable.ic_baseline_audiotrack_24, R.drawable.document, R.drawable.option};


    public MainActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.main_icon_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.ViewHolder holder, int position) {

        int positionCopy = position;

        holder.iconName.setText(categoryItemList[position]);
        holder.iconStorage.setText(categoryItemListSizes[position]);
        Glide.with(context.getApplicationContext()).load(images[position]).into(holder.icon);

        holder.folderItemMain.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {

                String clickedPath = categoryItemList[positionCopy];
                Toast.makeText(context.getApplicationContext(), "" + clickedPath, Toast.LENGTH_SHORT).show();

//                if (clickedPath.equals("Downloads") || clickedPath.equals("Images") || clickedPath.equals("Videos") || clickedPath.equals("Audio")) {
//                    loadContents(clickedPath);
//                }

                /*
                Intent intent = new Intent(context.getApplicationContext(), ListFiles.class);
                intent.putExtra("clickedPath", "/storage/emulated/0/Download");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            */
            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadContents(String contentTitles) {
        String queryUri = "";

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();

        if (contentTitles.equals("Downloads")) {
            Uri uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            queryUri = uriDownloads.toString();
        }
        if (contentTitles.equals("Images")) {
            Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            queryUri = uriImages.toString();
        }
        if (contentTitles.equals("Videos")) {
            Uri uriVideos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            queryUri = uriVideos.toString();
        }
        if (contentTitles.equals("Audio")) {
            Uri uriAudio = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            queryUri = uriAudio.toString();
        }

        Cursor cursor = contentResolver.query(Uri.parse(queryUri), null, null, null, null);

//        long sizee = 0;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                Log.d("Intent Path ", "Data " + data);
//                sizee += Long.parseLong(data);
            }
//            Log.d("Intent Path ", "Data " + Formatter.formatFileSize(context.getApplicationContext(), sizee));
            cursor.close();
        }
    }

    @Override
    public int getItemCount() {
        return categoryItemList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView iconName, iconStorage;
        ImageView icon;
        CardView folderItemMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconName = itemView.findViewById(R.id.icon_name);
            iconStorage = itemView.findViewById(R.id.storage);
            icon = itemView.findViewById(R.id.icon);
            folderItemMain = itemView.findViewById(R.id.folderItemMain);
        }
    }
}
