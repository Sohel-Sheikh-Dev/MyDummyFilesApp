package com.example.thefilesapp;


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
    File directory;
//    public static int valuesSize;
//    List values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

//        if(getIntent().hasExtra("clickedPath")){
//            path = getIntent().getStringExtra("clickedPath");
//            Log.d("Intent Path ", "onCreate: "+path);
//        }
//        else {
            path = Environment.getExternalStorageDirectory().toString()+"/Books";
//            Log.d("Class Path ", "onCreate: "+path);
//        }
        getFiles(path);

//        if(files != null) {
            listAdapter = new ListAdapter(getApplicationContext(),files);

            allRV = findViewById(R.id.allRV);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            allRV.setLayoutManager(linearLayoutManager);
            allRV.setAdapter(listAdapter);
//        }
//        else{
//            files.length = 0;
//        }

        /*
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
            Log.d("Size::", "onCreate: "+values.size());
        }

        listAdapter = new ListAdapter(getApplicationContext(),values);

        allRV = findViewById(R.id.allRV);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        allRV.setLayoutManager(linearLayoutManager);
        allRV.setAdapter(listAdapter);
*/
    }

/*
        @Override
        public void onBackPressed() {
            path = "";
            Log.d("Path", "onCreate: "+path);
            finish();
        }
*/
    public void getFiles(String path) {

        directory = new File(path);
        files = directory.listFiles();
//        int length = files.length;
        if(files != null) {
            Log.d("FileDetails", "Size: " + files.length);
            Log.d("FileDetails", "Path: " + path);
            for (int i = 0; i < files.length; i++) {
                Log.d("FileDetails", "FileName:" + files[i].getName());
            }
        }
    }

}