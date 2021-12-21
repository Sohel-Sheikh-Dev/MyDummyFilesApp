package com.example.thefilesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class DirAdapter extends RecyclerView.Adapter<DirAdapter.ViewHolder> {

    Context context;
    File[] dirFiles;
    public static String clickedPath;

    public DirAdapter(Context context, File[] dirFiles) {
        this.context = context;
        this.dirFiles = dirFiles;
    }

    @NonNull
    @Override
    public DirAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.folder_item, parent, false));
    }



    @Override
    public void onBindViewHolder(@NonNull DirAdapter.ViewHolder holder, int position) {

        int pos = position;

        holder.fileName.setText(dirFiles[pos].getName());
//        Log.d("Diry", "Directories: " + dirFiles[position]);


        holder.folderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "It is a directory", Toast.LENGTH_SHORT).show();


                CopyMoveToScreen activity = (CopyMoveToScreen) context;
                CMFrag cmFrag = new CMFrag();
                clickedPath = dirFiles[pos].toString();

                Bundle bundle = new Bundle();
                bundle.putString("message", clickedPath);
//                CMFrag cmFrag = new CMFrag();
                cmFrag.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.copyMoveScreen,cmFrag).addToBackStack(null).commit();


//                Intent intent = new Intent(context.getApplicationContext(), ListDirectories.class);

//                intent.putExtra("clickedPath", clickedPath);
                Log.d("AdapterFileName", "onClick: " + clickedPath);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dirFiles.length;
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
