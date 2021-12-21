package com.example.thefilesapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RecyclerView cateRV;
    MainActivityAdapter mainActivityAdapter;
    LinearLayoutManager linearLayoutManager;
    UsbManager mUsbManager;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    CardView externalStrCard;
    CardView massStrCard;

    TextView strIntr,strExtr,strMass;

    File[] externalStorages;

    HashMap<String, UsbDevice> devices;

    ArrayList<String> compSizes = new ArrayList<>();
    long totalS = 0;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        externalStrCard = findViewById(R.id.extrStr);
        massStrCard = findViewById(R.id.massStr);

        strIntr = findViewById(R.id.storageIntr);
        strExtr = findViewById(R.id.storageExtr);
        strMass = findViewById(R.id.storageMass);

        strIntr.setText(getTotalSize(Environment.getExternalStorageDirectory()));

        externalStorages = getExternalFilesDirs(null);

        devices = mUsbManager.getDeviceList();

        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        setSupportActionBar(toolbar);

        collapsingToolbarLayout.setTitle("App Name");
        collapsingToolbarLayout.setTitleEnabled(false);

        //For moving files
//        from.renameTo(to);


/*
        //For copying files
        try {

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
            Toast.makeText(getApplicationContext(), "PP"+e.toString(), Toast.LENGTH_LONG).show();
        }

        Log.v("TAG", "Copy file successful.");
*/

//        try {
//            copyDirectoryOneLocationToAnotherLocation(new File(),new File("/storage/emulated/0/Audiobooks"));
//        } catch (IOException e) {
//            Toast.makeText(getApplicationContext(),"PP",Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }


        checkStorageAccess();

        externalMemoryAvailable(MainActivity.this);

//        checkOTGAvailable();

//        getallapps();

        /*
        CardView bt2 = findViewById(R.id.interalStr);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  /storage/emulated/0/Audiobooks
                // /storage/emulated/0/Download/norm.jpg
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                startActivity(intent);
            }
        });
*/
        Uri uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriVideos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uriAudio = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uriDocs;


        Uri[] lUri = {uriDownloads, uriImages, uriVideos, uriAudio};
        String[] UriSize = {MediaStore.Downloads.SIZE, MediaStore.Images.Media.SIZE, MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.SIZE};
        String[] UriData = {MediaStore.Downloads.DATA, MediaStore.Images.Media.DATA, MediaStore.Video.Media.DATA, MediaStore.Audio.Media.DATA};

        String[] contents = {"Images", "Videos", "Audio", "Documents & other", "Apps"};

        uriDownloadsSize();
//        for (int i = 0; i < lUri.length; i++) {
//            uriSizes(lUri[i], UriSize[i], UriData[i]);
//        }
        for (int i = 0; i < contents.length; i++) {
            loadContents(contents[i]);
        }
        compSizes.add("Installed Application Size");


        for (int i = 0; i < compSizes.size(); i++) {
            Log.d("CompSize", compSizes.get(i));
        }
//        loadDocs();

        mainActivityAdapter = new MainActivityAdapter(getApplicationContext(), compSizes);

        cateRV = findViewById(R.id.categoriesRV);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        cateRV.setLayoutManager(linearLayoutManager);
        cateRV.setAdapter(mainActivityAdapter);


    }


    public String getTotalSize(File path) {
//        File path = new File("/storage/emulated");
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long multi = availableBlocks * blockSize;
        return Formatter.formatFileSize(getApplicationContext(), multi)+" free";
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadContents(String contentTitles) {

        long sizee = 0;
        String formattedSize = null;

        ContentResolver contentResolver = getApplicationContext().getContentResolver();

        String mimeType;
        String[] fileExtensions = new String[0];
        Uri uri = MediaStore.Files.getContentUri("external");

//        if (contentTitles.equals("Downloads")) {
//            Uri uriDownloads = ;
//            fileExtensions = new String[]{"bmp", "gif", "jpg", "jpeg", "png", "webp", "heif"};
//            uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
//        }

        if (contentTitles.equals("Images")) {
            fileExtensions = new String[]{"bmp", "gif", "jpeg", "png", "webp", "heif"};
        }
        if (contentTitles.equals("Videos")) {
            fileExtensions = new String[]{"3gp", "mp4", "mkv", "ts", "webm"};
        }
        if (contentTitles.equals("Audio")) {
            fileExtensions = new String[]{"aac", "opus", "flac", "ts", "mid", "xmf", "mxmf", "rtttx", "rtx", "ota", "imy", "ogg", "wav", "mp3"};
        }
        if (contentTitles.equals("Documents & other")) {
            fileExtensions = new String[]{"doc", "docx", "txt", "csv", "rtf", "odt", "md", "zip", "pdf"};
        }
        if (contentTitles.equals("Apps")) {
            fileExtensions = new String[]{"apk", "xapk", "apks", "apkm"};
        }

//        Toast.makeText(getApplicationContext(), "Uri: " + uri, Toast.LENGTH_SHORT).show();

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        for (int i = 0; i < fileExtensions.length; i++) {
            String[] selectionArgs = new String[0];
            Log.d("Intent Path ", "Pata: " + fileExtensions[i]);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensions[i]);
            Log.d("Intent Path ", "Sata: " + mimeType);
            if (mimeType == null) {
                i++;
//                selectionArgsPdf = new String[]{"image/png"};
            } else {
                selectionArgs = new String[]{mimeType};
            }
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE};
            String sortOrder = null;

            Cursor cursor = contentResolver.query(uri, projection, selectionMimeType, selectionArgs, sortOrder);



            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String data, dataSizes;

                    data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    if (contentTitles.equals("Images")) {
                        dataSizes = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                    } else {
                        dataSizes = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
                    }
                    Log.d("MainIntentPath ", "Data " + data);
//                    File [] datas = data
//                    mainCompModelList.get(i).setPathList(va);
//                    valPaths = new String[]{data};
//                    Log.d("Intent Path ", "Vall" + cursor.getString(cursor.getColumnIndexOrThrow(projection[0])));
                    sizee += Long.parseLong(dataSizes);
                    formattedSize = Formatter.formatFileSize(getApplicationContext(), sizee);
                }
//                Log.d("Intent Path ", "Vall" + cursor.getString(cursor.getColumnName(0));
                cursor.close();
            }
        }
        compSizes.add(formattedSize);
        Log.d("MainDSize", "Size of " + contentTitles + ": " + Formatter.formatFileSize(getApplicationContext(), sizee));
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void uriDownloadsSize() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uriDownloads, null, null, null, null);

        long daaa = 0;
        String formattedSizeD = null;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String data, dataSize;

//                if(uris.equals("MediaStore.Downloads.EXTERNAL_CONTENT_URI")) {
                data = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Downloads.DATA))));
                dataSize = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Downloads.SIZE))));
//                }
//                else{
//                    data = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))));
//                    dataSize = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))));
//                }
                File dFiles = new File(data);
                if (!(dFiles.isDirectory())) {
                    daaa += Long.parseLong(dataSize);
                    formattedSizeD = Formatter.formatFileSize(getApplicationContext(), daaa);
                }
            }
            compSizes.add(0, formattedSizeD);
            Log.d("MainDSize", "Download Size: " + Formatter.formatFileSize(getApplicationContext(), daaa));
            cursor.close();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void uriSizes(Uri uriiii, String uriiiiSize, String uriiiiData) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        String formattedSizeC = null;
        Cursor cursor = contentResolver.query(uriiii, null, null, null, null);
        long sizee = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String data, dataSize;
                data = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(uriiiiData))));
                dataSize = cursor.getString(cursor.getColumnIndexOrThrow(uriiiiSize));

                File dFiles = new File(data);
                if (!(dFiles.isDirectory())) {
                    sizee += Long.parseLong(dataSize);
                    formattedSizeC = Formatter.formatFileSize(getApplicationContext(), sizee);
                }
            }
            compSizes.add(formattedSizeC);
            Log.d("MainCSize", "Data " + Formatter.formatFileSize(getApplicationContext(), sizee));
            cursor.close();
        }
    }


    public void getallapps() {
        // get list of all the apps installed
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        String[] apps = new String[packList.size()];
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            apps[i] = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            Log.d("Apps", "App name " + apps);
            Log.d("Apps", "No. of apps " + packList.size());
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

    public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

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

            CardView internalStrCard = findViewById(R.id.interalStr);

            internalStrCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                intent.putExtra("intrPath", "intrPath");
                startActivity(intent);
            });

        }
    }

    public void externalMemoryAvailable(Activity context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);

        String prodName = "";

        for (String key : devices.keySet()) {
            Log.d("TAGGI", "Device Name: " + devices.get(key).getDeviceName());
            Log.d("TAGGI", "Manufacturer Name: " + devices.get(key).getManufacturerName());
            Log.d("TAGGI", "Product Name: " + devices.get(key).getProductName());
            prodName = devices.get(key).getProductName();
        }

//        Toast.makeText(MainActivity.this, "" + storages[0], Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this, "" + storages[1], Toast.LENGTH_SHORT).show();
/*
        if (storages.length > 1 && storages[0] != null && !(devices.isEmpty()) && storages[2] != null) {
            Toast.makeText(MainActivity.this, "SD Card present", Toast.LENGTH_SHORT).show();

            String[] sdStr = externalStorages[2].toString().split("/");
            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            externalStrCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                intent.putExtra("sdPath",sdPath);
                startActivity(intent);
            });


            for (String key : devices.keySet()) {
                Log.d("TAGGI", "Device Name: " + devices.get(key).getDeviceName());
                Log.d("TAGGI", "Manufacturer Name: " + devices.get(key).getManufacturerName());
                Log.d("TAGGI", "Product Name: " + devices.get(key).getProductName());
                prodName = devices.get(key).getProductName();
            }

            if (prodName.equals("Mass Storage Device")) {
                Toast.makeText(MainActivity.this, "USB Devices Currently Connected", Toast.LENGTH_SHORT).show();

                String[] massStr = externalStorages[1].toString().split("/");
                String massPath = "/storage/" + massStr[2];
                Log.d("Filuu", "Path: " + massStr[2]);

                massStrCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ListFiles.class);
                    intent.putExtra("massPath", massPath);
                    startActivity(intent);
                });

            }

//            String[] sdStr = null;
//            if(devices.isEmpty()) {
//                sdStr = externalStorages[1].toString().split("/");
//            }
//            if(!(devices.isEmpty())){
//                sdStr = externalStorages[2].toString().split("/");
//            }
//                String sdPath = "/storage/" + sdStr[2];
//                Log.d("Filuu", "Path: " + sdStr[2]);


        }
        if (storages[0] != null && devices.isEmpty() && storages[1] != null) {
            Toast.makeText(MainActivity.this, "SD Card present", Toast.LENGTH_SHORT).show();
            String[] sdStr = externalStorages[2].toString().split("/");
            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            externalStrCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                intent.putExtra("sdPath",sdPath);
                startActivity(intent);
            });
        }
        if (storages[0] != null && !devices.isEmpty() && storages[2] == null) {
            if (prodName.equals("Mass Storage Device")) {
                Toast.makeText(MainActivity.this, "USB Devices Currently Connected", Toast.LENGTH_SHORT).show();

                String [] massStr = externalStorages[1].toString().split("/");
                String massPath = "/storage/"+massStr[2];
                Log.d("Filuu", "Path: " + massStr[2]);

                massStrCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ListFiles.class);
                    intent.putExtra("massPath",massPath);
                    startActivity(intent);
                });
            }
        }
        */
//        if (storages[0] != null && devices.isEmpty() && storages[2] == null) {
//            Toast.makeText(MainActivity.this, "SD Card not present", Toast.LENGTH_SHORT).show();
//            externalStrCard.setVisibility(View.GONE);
//            Toast.makeText(MainActivity.this, "No Devices Currently Connected", Toast.LENGTH_SHORT).show();
//            massStrCard.setVisibility(View.GONE);
//        }

        if (storages.length > 1 && (!(devices.isEmpty()))) {
            if (prodName.equals("Mass Storage Device")) {
                Toast.makeText(MainActivity.this, "USB Devices Currently Connected", Toast.LENGTH_SHORT).show();

                String[] massStr = externalStorages[1].toString().split("/");
                String massPath = "/storage/" + massStr[2];
                Log.d("Filuu", "Path: " + massStr[2]);

                strMass.setText(getTotalSize(new File(massPath)));

                massStrCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ListFiles.class);
                    intent.putExtra("massPath", massPath);
                    startActivity(intent);
                });
            }
        }

        if (devices.isEmpty() && storages[1] != null) {
            String[] sdStr = externalStorages[1].toString().split("/");

            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            strExtr.setText(getTotalSize(new File(sdPath)));

            externalStrCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                intent.putExtra("sdPath", sdPath);
                startActivity(intent);
            });
        }

        if (storages.length > 2 && (!(devices.isEmpty())) && storages[2] != null) {

            String[] sdStr = externalStorages[2].toString().split("/");

            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            strExtr.setText(getTotalSize(new File(sdPath)));

            externalStrCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                intent.putExtra("sdPath", sdPath);
                startActivity(intent);
            });


        } else {
            if ((!(devices.isEmpty())) && storages[0] != null && storages[1] != null) {
                Toast.makeText(MainActivity.this, "SD Card not present", Toast.LENGTH_SHORT).show();
                externalStrCard.setVisibility(View.GONE);
            }
            if (devices.isEmpty()) {
                Toast.makeText(MainActivity.this, "No Devices Currently Connected", Toast.LENGTH_SHORT).show();
                massStrCard.setVisibility(View.GONE);
            }
        }


        /*



        if (devices.isEmpty()) {
            Toast.makeText(MainActivity.this, "No Devices Currently Connected", Toast.LENGTH_SHORT).show();
            massStrCard.setVisibility(View.GONE);
        }

        if ((!(devices.isEmpty())) && (storages[2] == null)) {
            Toast.makeText(MainActivity.this, "SD Card not present", Toast.LENGTH_SHORT).show();
            externalStrCard.setVisibility(View.GONE);
        }

        if ((devices.isEmpty()) && storages.length == 1) {
            Toast.makeText(MainActivity.this, "SD Card not present", Toast.LENGTH_SHORT).show();
            externalStrCard.setVisibility(View.GONE);
        }

//            Toast.makeText(MainActivity.this, ""+Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();

        else {
            Toast.makeText(MainActivity.this, "SD Card present", Toast.LENGTH_SHORT).show();
            
            String[] sdStr = null;
            
            if(devices.isEmpty()) {
                sdStr = externalStorages[1].toString().split("/");
            }
            if(!(devices.isEmpty())){
                sdStr = externalStorages[2].toString().split("/");
            }
            String sdPath = "/storage/" + sdStr[2];
            Log.d("Filuu", "Path: " + sdStr[2]);

            externalStrCard.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListFiles.class);
                intent.putExtra("sdPath",sdPath);
                startActivity(intent);
            });

            if (prodName.equals("Mass Storage Device")) {
                Toast.makeText(MainActivity.this, "USB Devices Currently Connected", Toast.LENGTH_SHORT).show();

                String[] massStr = externalStorages[1].toString().split("/");
                String massPath = "/storage/" + massStr[2];
                Log.d("Filuu", "Path: " + massStr[2]);

                massStrCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ListFiles.class);
                    intent.putExtra("massPath", massPath);
                    startActivity(intent);
                });
            }
        }
         */


    }
/*
    private void checkOTGAvailable() {

        String prodName = "";

        for (String key : devices.keySet()) {
            Log.d("TAGGI", "Device Name: " + devices.get(key).getDeviceName());
            Log.d("TAGGI", "Manufacturer Name: " + devices.get(key).getManufacturerName());
            Log.d("TAGGI", "Product Name: " + devices.get(key).getProductName());
            prodName = devices.get(key).getProductName();
        }

        if (devices.isEmpty()) {
            Toast.makeText(MainActivity.this, "No Devices Currently Connected", Toast.LENGTH_SHORT).show();
            massStrCard.setVisibility(View.GONE);
        } else {
            if (prodName.equals("Mass Storage Device")) {
                Toast.makeText(MainActivity.this, "USB Devices Currently Connected", Toast.LENGTH_SHORT).show();

                String[] massStr = externalStorages[1].toString().split("/");
                String massPath = "/storage/" + massStr[2];
                Log.d("Filuu", "Path: " + massStr[2]);

                massStrCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ListFiles.class);
                    intent.putExtra("massPath", massPath);
                    startActivity(intent);
                });
            }
        }
    }
*/
}