package com.example.thefilesapp;

import static com.example.thefilesapp.ListAdapter.copyMove;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.HashMap;

public class CopyMoveToScreen extends AppCompatActivity {

    HashMap<String, UsbDevice> devices;
    UsbManager mUsbManager;
    File[] externalStorages;
    TextView strIntr, strExtr, strMass, strDevices;

    CardView internalStrCard, externalStrCard, massStrCard;
    public static String copyMovePath;
    RelativeLayout copyMoveDownScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_move_to_screen);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        internalStrCard = findViewById(R.id.interalStr);
        externalStrCard = findViewById(R.id.extrStr);
        massStrCard = findViewById(R.id.massStr);
        copyMoveDownScreen = findViewById(R.id.copyMoveDownScreen);

        devices = mUsbManager.getDeviceList();

        strIntr = findViewById(R.id.storageIntr);
        strExtr = findViewById(R.id.storageExtr);
        strMass = findViewById(R.id.storageMass);
        strDevices = findViewById(R.id.strDevices);

        strIntr.setText(getTotalSize(Environment.getExternalStorageDirectory()));


        externalStorages = getExternalFilesDirs(null);

        copyMoveTo(CopyMoveToScreen.this);

        if (copyMove.equals("copyFile")) {
            strDevices.setText("Copy to");
        }

        if (copyMove.equals("moveFile")) {
            strDevices.setText("Move to");
        }

    }

    public String getTotalSize(File path) {
//        File path = new File("/storage/emulated");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long multi = availableBlocks * blockSize;
        return Formatter.formatFileSize(getApplicationContext(), multi) + " free";
    }

    public void copyMoveTo(Activity context) {

        internalStrCard.setOnClickListener(v -> {
            CopyMoveToScreen activity = (CopyMoveToScreen) context;
            CMFrag cmFrag = new CMFrag();
            copyMovePath = Environment.getExternalStorageDirectory().toString();
//            copyMoveDownScreen.setVisibility(View.GONE);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.copyMoveScreen, cmFrag).addToBackStack(null).commit();
        });

        File[] storages = ContextCompat.getExternalFilesDirs(context, null);

        String prodName = "";

        for (String key : devices.keySet()) {
            Log.d("TAGGI", "Device Name: " + devices.get(key).getDeviceName());
            Log.d("TAGGI", "Manufacturer Name: " + devices.get(key).getManufacturerName());
            Log.d("TAGGI", "Product Name: " + devices.get(key).getProductName());
            prodName = devices.get(key).getProductName();
        }

        if (storages.length > 1 && (!(devices.isEmpty()))) {
            if (prodName.equals("Mass Storage Device")) {
                Toast.makeText(CopyMoveToScreen.this, "USB Devices Currently Connected", Toast.LENGTH_SHORT).show();

                String[] massStr = externalStorages[1].toString().split("/");
                String massPath = "/storage/" + massStr[2];
                Log.d("Filuu", "Path: " + massStr[2]);

                strMass.setText(getTotalSize(new File(massPath)));

                massStrCard.setOnClickListener(v -> {
                    CopyMoveToScreen activity = (CopyMoveToScreen) context;
                    CMFrag cmFrag = new CMFrag();
                    copyMovePath = massPath;
//                    copyMoveDownScreen.setVisibility(View.GONE);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.copyMoveScreen, cmFrag).addToBackStack(null).commit();
                });
            }
        }

        if (devices.isEmpty() && storages[1] != null) {
            String[] sdStr = externalStorages[1].toString().split("/");

            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            strExtr.setText(getTotalSize(new File(sdPath)));

            externalStrCard.setOnClickListener(v -> {
                CopyMoveToScreen activity = (CopyMoveToScreen) context;
                CMFrag cmFrag = new CMFrag();
                copyMovePath = sdPath;
//                copyMoveDownScreen.setVisibility(View.GONE);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.copyMoveScreen, cmFrag).addToBackStack(null).commit();
            });
        }

        if (storages.length > 2 && (!(devices.isEmpty())) && storages[2] != null) {

            String[] sdStr = externalStorages[2].toString().split("/");

            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            strExtr.setText(getTotalSize(new File(sdPath)));

            externalStrCard.setOnClickListener(v -> {
                ListFiles activity = (ListFiles) context;
                CMFrag cmFrag = new CMFrag();
                copyMovePath = sdPath;
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.full, cmFrag).addToBackStack(null).commit();
            });


        } else {
            if ((!(devices.isEmpty())) && storages[0] != null && storages[1] != null) {
                Toast.makeText(CopyMoveToScreen.this, "SD Card not present", Toast.LENGTH_SHORT).show();
                externalStrCard.setVisibility(View.GONE);
            }
            if (devices.isEmpty()) {
                Toast.makeText(CopyMoveToScreen.this, "No Devices Currently Connected", Toast.LENGTH_SHORT).show();
                massStrCard.setVisibility(View.GONE);
            }
        }

    }

}