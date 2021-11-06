package com.example.thefilesapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.io.Files;

import java.io.File;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    File[] getFiles;
    Context context;

    public ListAdapter(Context context, File[] getFiles) {
        this.getFiles = getFiles;
        this.context = context;
    }


    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {

        int pos = position;
        holder.fileName.setText(getFiles[pos].getName());

        holder.folderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (getFiles[pos].isDirectory()) {
//                    Toast.makeText(context.getApplicationContext(), "It is a directory", Toast.LENGTH_SHORT).show();
//                }


                    Intent promptInstall = new Intent(Intent.ACTION_GET_CONTENT);
                    promptInstall.setAction(Intent.ACTION_VIEW);
                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    promptInstall.setDataAndType(uri,mime.getExtensionFromMimeType(Files.getFileExtension(getFiles[pos].toString())) );
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        context.startActivity(promptInstall);
                    }catch (Exception e){
                        Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
/*
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    Log.d("TAG", "openPDF: intent with uri: " + uri);
                    */

//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(uri, "application/pdf");
//                    context.startActivity(Intent.createChooser(intent, "Open with..."));

                    /*
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    Intent testIntent = new Intent(Intent.ACTION_VIEW);
                    testIntent.setType("application/pdf");
//                    List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(getFiles[pos]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/pdf");
                    context.startActivity(intent);


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    Log.d("TAG", "openPDF: intent with uri: " + uri);
                    intent.setDataAndType(uri, "application/pdf");
                    context.startActivity(Intent.createChooser(intent, "Open with..."));
*/
                }
//                else {
//                    Toast.makeText(context.getApplicationContext(), "It is a " + Files.getFileExtension(getFiles[pos].toString()) + " type file" + "\nFile: " + (getFiles[pos].toString()), Toast.LENGTH_SHORT).show();
//                }
/*
                Intent intent = new Intent(context.getApplicationContext(), ListFiles.class);
                String clickedPath = getFiles[pos].toString();
                intent.putExtra("clickedPath", clickedPath);
                Log.d("AdapterFileName", "onClick: " + clickedPath);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

 */
            
        });


    }


    @Override
    public int getItemCount() {
        if (getFiles != null) {
            return getFiles.length;
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName;
        CardView folderItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileName);
            folderItem = itemView.findViewById(R.id.folderItem);
        }
    }
}
