package com.example.thefilesapp;

import static com.example.thefilesapp.ListAdapter.actualPath;
import static com.example.thefilesapp.ListAdapter.copyMove;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ListDirectories extends AppCompatActivity {

    File[] Dirfiles;

    RecyclerView dirRV;
    DirAdapter dirAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    String path;
    RelativeLayout copyMoveLayout;
    Button copyMoveBtn;
    String actualFilePath;
    String from, to;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_directories);


        copyMoveLayout = findViewById(R.id.copyMoveLayout);
        copyMoveBtn = findViewById(R.id.copyMoveButton);
        actualFilePath = actualPath;



        /*
                if(getIntent().hasExtra("moveFile")){
            copyMoveBtn.setVisibility(View.VISIBLE);
        }
         */


//        if(getIntent().hasExtra("actualFilePath")){
//            actualFilePath = getIntent().getStringExtra("actualFilePath");
        Toast.makeText(getApplicationContext(), "Actual File Path: " + actualFilePath, Toast.LENGTH_SHORT).show();
//        }
        if (getIntent().hasExtra("clickedPath")) {
            path = getIntent().getStringExtra("clickedPath");
            Log.d("Intent Path ", "onCreate: " + path);
            listDir(path);
        } else {
            path = Environment.getExternalStorageDirectory().toString();
            Log.d("Class Path ", "onCreate: " + path);
            listDir(path);
        }

        copyMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Actual File Path is: " + actualFilePath + "\nCurrent path is: " + path, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Which one? " + copyMove, Toast.LENGTH_SHORT).show();
                if (copyMove.equals("copyFile")) {
                    copyMoveBtn.setText("Copy here");
                    copyFiles(actualFilePath, path);
                }
                if (copyMove.equals("moveFile")) {
                    copyMoveBtn.setText("Move here");
                    moveFiles(actualFilePath, path);
                }
            }
        });

//        listDir(Environment.getExternalStorageDirectory().toString());

        if (Dirfiles != null) {
            dirRV = findViewById(R.id.dirRV);
            dirAdapter = new DirAdapter(getApplicationContext(), Dirfiles);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            dirRV.setLayoutManager(linearLayoutManager);
            dirRV.setAdapter(dirAdapter);
        }

    }

    private void copyFiles(String fromFile, String toFile) {
        try {
            File from = new File(fromFile);
            File to = new File(toFile + "/" + from.getName());

            Toast.makeText(getApplicationContext(), "From: " + from, Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "To: " + to, Toast.LENGTH_SHORT).show();

            InputStream in = new FileInputStream(from);
            OutputStream out = new FileOutputStream(to);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;


            while (true) {
                if (!((len = in.read(buf)) > 0)) break;

                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "PP", Toast.LENGTH_SHORT).show();
        }
    }


    private void moveFiles(String fromFile, String toFile) {
        File from = new File(fromFile);
        File to = new File(toFile + "/" + from.getName());
        //For moving files
        from.renameTo(to);
    }

    private void listDir(String path) {
        File f = new File(path);
        File[] directories = f.listFiles(File::isDirectory);
        Dirfiles = directories;

        for (int i = 0; i < directories.length; i++)
        {
            Log.d("Files", "FileName:" + directories[i].getName());
        }

    }

}