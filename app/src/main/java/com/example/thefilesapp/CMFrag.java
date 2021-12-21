package com.example.thefilesapp;

import static com.example.thefilesapp.CopyMoveToScreen.copyMovePath;
import static com.example.thefilesapp.ListAdapter.actualPath;
import static com.example.thefilesapp.ListAdapter.copyMove;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CMFrag extends Fragment {

    View view;
    Button copyMoveBtn;

    File[] Dirfiles;

    RecyclerView dirRV;
    DirAdapter dirAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    String path;
    RelativeLayout copyMoveLayout;
    String actualFilePath;
    TextView copyMoveTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_c_m, container, false);

        copyMoveBtn = view.findViewById(R.id.copyMoveBtn);
        copyMoveLayout = view.findViewById(R.id.copyMoveLayout);
        copyMoveTV = view.findViewById(R.id.copyMoveText);

        actualFilePath = actualPath;

        Toast.makeText(getContext(), "Actual File Path: " + actualFilePath, Toast.LENGTH_SHORT).show();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            path = bundle.getString("message");
            Log.d("Intent Path ", "onCreate: " + path);
            listDir(path);
        } else {
            path = copyMovePath;
            Log.d("Class Path ", "onCreate: " + path);
            listDir(path);
        }

        if (copyMove.equals("copyFile")) {
            copyMoveTV.setText("Copy here");
        }
        if (copyMove.equals("moveFile")) {
            copyMoveTV.setText("Move here");
        }


        copyMoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Actual File Path is: " + actualFilePath + "\nCurrent path is: " + path, Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Which one? " + copyMove, Toast.LENGTH_SHORT).show();
                if (copyMove.equals("copyFile")) {
                    copyFiles(actualFilePath,path);
                }
                if (copyMove.equals("moveFile")) {
                    mFiles(actualFilePath,path);
//                    moveFiles(actualFilePath,path);
                }
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

//        listDir(Environment.getExternalStorageDirectory().toString());

        if (Dirfiles != null) {
            dirRV = view.findViewById(R.id.dirRV);
            dirAdapter = new DirAdapter(getContext(), Dirfiles);
            linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            dirRV.setLayoutManager(linearLayoutManager);
            dirRV.setAdapter(dirAdapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void copyFiles(String fromFile, String toFile) {
        try {
            File from = new File(fromFile);
            File to = new File(toFile + "/" + from.getName());

            Log.d("Lagg","From: " +from);
            Log.d("Lagg","To: "+to);
//            Log.d("Lagg",);
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
            Toast.makeText(getContext(), "PP", Toast.LENGTH_SHORT).show();
        }
    }

    private void mFiles(String fromFile, String toFile) {
        try {
            File from = new File(fromFile);
            File to = new File(toFile + "/" + from.getName());

            Toast.makeText(getContext(), "From: " + from, Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "To: " + to, Toast.LENGTH_SHORT).show();

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
            in = null;
            out.flush();
            out.close();
            out = null;

            from.delete();
            dirAdapter.notifyDataSetChanged();

            Log.d("mFiles", "mFiles: "+from);

            Toast.makeText(getContext(), "TTTTTT"+from+"/"+from.getName(), Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "PP", Toast.LENGTH_SHORT).show();
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

//        for (int i = 0; i < directories.length; i++) {
//            Log.d("Files", "FileName:" + directories[i].getName());
//        }

    }

}