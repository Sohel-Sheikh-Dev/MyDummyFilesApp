package com.example.thefilesapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView cateRV;
    MainActivityAdapter mainActivityAdapter;
    LinearLayoutManager linearLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkStorageAccess();

//        getallapps();

        mainActivityAdapter = new MainActivityAdapter(getApplicationContext());

        cateRV = findViewById(R.id.categories);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        cateRV.setLayoutManager(linearLayoutManager);
        cateRV.setAdapter(mainActivityAdapter);


    }



    public void getallapps() {
        // get list of all the apps installed
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        String[] apps = new String[packList.size()];
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            apps[i] = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            Log.d("Apps", "App name "+apps);
            Log.d("Apps", "No. of apps "+packList.size());
        }

    }
        // set all the apps name in list view
//        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, apps));
        // write total count of apps available.
//        text.setText(infos.size() + " Apps are installed");



    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void checkStorageAccess() {
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Button bt2 = findViewById(R.id.button2);
            bt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, ListFiles.class);


//                    ListFiles.getAllDir(Environment.getExternalStorageDirectory());

                    startActivity(intent);
                }
            });
        }
    }


}