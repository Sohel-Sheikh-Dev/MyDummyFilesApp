package com.example.thefilesapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    //    public static File[] getFiles;
    List nvalues;
    Context context;
    public static boolean isClicked = false;
    public static String gettingName;
    public static File gettingName2;

    public ListAdapter(Context context, List nvalues) {
        this.nvalues = nvalues;
        this.context = context;
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {

        int p = position;
        holder.fileName.setText(ListFiles.files[position].getName());

//        Log.d("Size=", "onCreate: " + nvalues.size());

        holder.folderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (new File(String.valueOf(ListFiles.files[p])).isDirectory()) {
                Intent intent = new Intent(context.getApplicationContext(), ListFiles.class);
//                ListFiles.path += "/Download";
//                gettingName = ListFiles.files[p];
                gettingName2 = ListFiles.files[p];
                Log.d("AdapterFileName", "onClick: " + gettingName2);

                intent.putExtra("path", gettingName);
                isClicked = true;
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


//                } else {
//                    Toast.makeText(context.getApplicationContext(), gettingName + " is not a directory", Toast.LENGTH_LONG).show();
//                }


            }
        });

    }


    @Override
    public int getItemCount() {
        return nvalues.size();
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
