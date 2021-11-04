package com.example.thefilesapp;

import static com.example.thefilesapp.ListAdapter.gettingName;
import static com.example.thefilesapp.ListAdapter.gettingName2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ListFiles extends AppCompatActivity {

    RecyclerView allRV;
    ListAdapter listAdapter;
    LinearLayoutManager linearLayoutManager;

    public static File[] files;
    public static String path;
    public static int valuesSize;
    List values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

//        innerFiles = MainActivity.files;

        path = Environment.getExternalStorageDirectory().toString();

//        path = "/storage/emulated/0/Telegram/Telegram Documents";

        if(ListAdapter.isClicked){
            path = gettingName2.toString();
            Log.d("TAG", "onCreate: "+gettingName2.toString());
//            listAdapter.notifyDataSetChanged();
        }else{
//            path = "/storage/emulated/0/Telegram/Telegram Documents";
            path = Environment.getExternalStorageDirectory().toString();
        }

/*
        if(getIntent().hasExtra("path")){
            path += getIntent().getStringExtra("path");
        }else{
            path = path1;
        }
*/


        values = new ArrayList();
        File directory = new File(path);

        files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
//                if (!file.startsWith(".")) {
                    values.add(file);
//                }
            }
            valuesSize = values.size();
//            Log.d("File Size::", "onCreate: "+files.length);
            Log.d("Size::", "onCreate: "+values.size());
        }

//        File directory = new File(path);
//        files = directory.listFiles();

        listAdapter = new ListAdapter(getApplicationContext(),values);
//        Log.d("Files", "Size: "+ files.length);

//        Log.d("TAG", "File Size"+(getFiles.length));

        allRV = findViewById(R.id.allRV);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        allRV.setLayoutManager(linearLayoutManager);
        allRV.setAdapter(listAdapter);

    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(getApplicationContext(), ListFiles.class);
        finish();
        ListAdapter.isClicked = false;
//        startActivity(intent);
    }

    public void getFiles(){
        String path = Environment.getExternalStorageDirectory().toString();

        File directory = new File(Environment.getExternalStorageDirectory().toString());
        File[] files = directory.listFiles();

        Log.d("Files", "Size: "+ files.length);
        Log.d("Files", "Path: " + path);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

}