package com.example.thefilesapp.MainComponents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.thefilesapp.R;

import java.util.ArrayList;

public class MainCompFiles extends AppCompatActivity {

    ArrayList<String> pathVals;
    ArrayList<PackageInfo> packageInfos;

    RecyclerView pathRv;
    LinearLayoutManager pathLLM;
    MainCompAdapter mainCompAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_comp_files);

        if (getIntent().hasExtra("PackageInfos")) {
            packageInfos = (ArrayList<PackageInfo>) getIntent().getSerializableExtra("PackageInfos");
            Log.d("Maggy", "onCreate: " + packageInfos.get(0));
        }

        if (getIntent().hasExtra("ValPath")) {
            pathVals = (ArrayList<String>) getIntent().getSerializableExtra("ValPath");
        }


        if (pathVals != null) {
            pathRv = findViewById(R.id.mainCompList);
            mainCompAdapter = new MainCompAdapter(getApplicationContext(), pathVals, packageInfos);
            pathLLM = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            pathRv.setLayoutManager(pathLLM);
            pathRv.setAdapter(mainCompAdapter);

        }


//        getAllPaths();

    }

    private void getAllPaths() {
        for (int i = 0; i < packageInfos.size(); i++) {
            Log.d("Maggy", "onCreate: " + packageInfos.get(i));
        }
    }

}