package com.example.thefilesapp;


import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.comparator.NameFileComparator;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.comparator.SizeFileComparator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListFiles extends AppCompatActivity {

    RecyclerView allRV;
    ListAdapter listAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;

    public static File[] files;
    public static String path;
    File directory;
    ImageView ivNothing,sampleTest;
//    public static int valuesSize;
//    List values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        //copy(new File("/storage/emulated/0/-4941010891231570339_120.jpg"), new File("/storage/emulated/0/Download"));

//        moveFile("/storage/emulated/0/-4941010891231570339_120.jpg","-4941010891231570339_120.jpg","/storage/emulated/0/Download");

//        File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kaic1/imagem.jpg");
//        File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kaic2/imagem.jpg");
//        from.renameTo(to);


        ivNothing = findViewById(R.id.imageViewNothing);

        sampleTest = findViewById(R.id.sampleTest);






        if(getIntent().hasExtra("clickedPath")){
            path = getIntent().getStringExtra("clickedPath");
            Log.d("Intent Path ", "onCreate: "+path);
        }
        else {
            path = Environment.getExternalStorageDirectory().toString();
            Log.d("Class Path ", "onCreate: "+path);
        }
        getFiles(path);

        if(files != null) {
            listAdapter = new ListAdapter(getApplicationContext(),files);

            allRV = findViewById(R.id.allRV);
            gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            allRV.setLayoutManager(linearLayoutManager);
            allRV.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }
        if(files == null || files.length == 0){
            ivNothing.setVisibility(View.VISIBLE);
        }
//        else{
//            ivNothing.setVisibility(View.VISIBLE);
//        }
//
//        Log.d("Check Files", "onCreate: "+files.length);


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


    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

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
            Arrays.sort(files, SizeFileComparator.SIZE_COMPARATOR);

            Log.d("FileDetails", "Size: " + files.length);
            Log.d("FileDetails", "Path: " + path);
/*
            String APKFilePath = files[37].getPath(); //For example...
            Toast.makeText(getApplicationContext(),APKFilePath,Toast.LENGTH_SHORT).show();

            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

            // the secret are these two lines....
            pi.applicationInfo.sourceDir       = APKFilePath;
            pi.applicationInfo.publicSourceDir = APKFilePath;
            //

            Drawable APKicon = pi.applicationInfo.loadIcon(pm);
            String   AppName = (String)pi.applicationInfo.loadLabel(pm);
            Glide.with(getApplicationContext())
                    .load(APKicon)
                    .into(sampleTest);
            */
/*
            String vidPath = files[34].getPath();
            Log.d("Intent Path ", "My path: "+vidPath);

            RequestOptions requestOptions = new RequestOptions();

            Glide.with(getApplicationContext())
                    .load(vidPath).apply(requestOptions)
                    .thumbnail(Glide.with(getApplicationContext())
                            .load(vidPath))
                    .into(sampleTest);
*/
            for (int i = 0; i < files.length; i++) {

                Log.d("FileDetails", "FileName:" + files[i].getName());

            }
        }
    }





}