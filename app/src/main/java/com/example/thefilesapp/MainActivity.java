package com.example.thefilesapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    public static File[] files;
//    public static String path;

//    RecyclerView allRV;
//    ListAdapter listAdapter;
//    LinearLayoutManager linearLayoutManager;

    RecyclerView allRV;
    ListAdapter listAdapter;
    LinearLayoutManager linearLayoutManager;

    public static File[] files;
    public static String path;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        path = Environment.getExternalStorageDirectory().toString();

        if(ListAdapter.isClicked){
            path += "/Download";
//            listAdapter.notifyDataSetChanged();
        }

        if(getIntent().hasExtra("path")){
            path += getIntent().getStringExtra("path");
        }else{
            path = path1;
        }
        Log.d("TAG", "onCreate: "+path);

        File directory = new File(path);
        files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);


        listAdapter = new ListAdapter(getApplicationContext(),files);

        allRV = findViewById(R.id.allRV);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        allRV.setLayoutManager(linearLayoutManager);
        allRV.setAdapter(listAdapter);
*/
/*
        path = Environment.getExternalStorageDirectory().toString();
        File directory = new File(path);
        files = directory.listFiles();

        listAdapter = new ListAdapter(getApplicationContext(),files);

        allRV = findViewById(R.id.allRV);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        allRV.setLayoutManager(linearLayoutManager);
        allRV.setAdapter(listAdapter);
*/

        if (!Environment.isExternalStorageManager()){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }else{
            Button bt2 = findViewById(R.id.button2);
            bt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*

                    path = Environment.getExternalStorageDirectory().toString();
                    File directory = new File(path);
                    files = directory.listFiles();
                    Log.d("Files", "Size: "+ files.length);
                    for (int i = 0; i < files.length; i++)
                    {
                        Log.d("Files", "FileName:" + files[i].getName());
                    }

*/


                    Intent intent = new Intent(MainActivity.this,ListFiles.class);
                    startActivity(intent);
                }
            });
        }



    }
}