package com.example.thefilesapp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.comparator.SizeFileComparator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ListFiles extends AppCompatActivity {

    private static String type;
    RecyclerView allRV;
    ListAdapter listAdapter;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;

    public static File[] files;
    public static String path;
    File directory;
    ImageView ivNothing, sampleTest;

//    public static int valuesSize;
//    List values;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

//        getAllDir(Environment.getExternalStorageDirectory());

//        scanFiles(new File("/storage/emulated/0/"));


        //copy(new File("/storage/emulated/0/-4941010891231570339_120.jpg"), new File("/storage/emulated/0/Download"));

//        moveFile("/storage/emulated/0/-4941010891231570339_120.jpg","-4941010891231570339_120.jpg","/storage/emulated/0/Download");

//        File from = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kaic1/imagem.jpg");
//        File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kaic2/imagem.jpg");
//        from.renameTo(to);


        ivNothing = findViewById(R.id.imageViewNothing);
        sampleTest = findViewById(R.id.sampleTest);

        if (getIntent().hasExtra("clickedPath")) {
            path = getIntent().getStringExtra("clickedPath");
            Log.d("Intent Path ", "onCreate: " + path);
            try {
                getFiles(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            path = Environment.getExternalStorageDirectory().toString();
            Log.d("Class Path ", "onCreate: " + path);
            try {
                getFiles(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (files != null) {
            listAdapter = new ListAdapter(ListFiles.this, files);

            allRV = findViewById(R.id.allRV);
            allRV.hasFixedSize();
            allRV.setNestedScrollingEnabled(false);
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
            allRV.setLayoutManager(linearLayoutManager);
            allRV.setAdapter(listAdapter);
//            listAdapter.notifyDataSetChanged();   


        }
        if (files == null || files.length == 0) {
            ivNothing.setVisibility(View.VISIBLE);
        }

        /*

        File f = new File("/storage/emulated/0");

        getMimeType(String.valueOf(f));
        Log.d("TAG", " m Images: " + type);

        File[] jpgFiles = f.listFiles(file -> (file.getPath().endsWith(".jpg")||file.getPath().endsWith(".jpeg")));
        for (File imgFiles : jpgFiles) {
            Log.d("TAG", " m Images: " + imgFiles);
        }
*/
//        scanFiles(path);
/*
        File[] insideFiles = directory.listFiles();
        for (File f : insideFiles) {
            if (f.isDirectory()) {
//                for (int i = 0; i < insideFiles.length; i++) {
                    if (f != null) {
                        Log.d("TAG", " m Images: " + f);
                        path = new File(String.valueOf(insideFiles[5]));
                    }
//                }
            }
        }
*/

//                    scanFiles(path);

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
            File dir = new File(outputPath);
            if (!dir.exists()) {
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


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
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

    public static String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void scanFiles(File file) {
        File[] fileArray = file.listFiles();
        if (fileArray != null) {
            for (File f : fileArray) {
                if (f.isDirectory())
                    scanFiles(f);
                if (f.isFile() && (f.getPath().endsWith(".jpg"))) {
                    Log.d("TAG", "Images: " + f.getName());
                }

            }
        } else {
            Toast.makeText(ListFiles.this, "Laura", Toast.LENGTH_SHORT).show();
        }
    }

    public void getFiles(String path) throws IOException {

        directory = new File(path);
        files = directory.listFiles();
//        int length = files.length;
        if (files != null) {
            Arrays.sort(files, SizeFileComparator.SIZE_COMPARATOR);

            Log.d("FileDetails", "Size: " + files.length);
            Log.d("FileDetails", "Path: " + path);
/*
            String APKFilePath = files[38].getPath(); //For example...


            ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(APKFilePath), ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(input);
            PdfRenderer.Page page = renderer.openPage(0);
            Bitmap bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            Glide.with(getApplicationContext()).load(bitmap).into(sampleTest);
            page.close();
            renderer.close();




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


    /*
        @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadAudio() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();

//        Uri o = ApplicationInfo.FLAG_EXTERNAL_STORAGE;
//        Uri i = MediaStore.Files.FileColumns.MIME_TYPE;
//        Uri p = MediaStore.Files.getContentUri("external");
        Uri uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriVideos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uriAudio = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //        String selection = MediaStore.Images.Media.Is + "!= 0";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

//        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(uriDownloads, null, null, null, null);

        long sizee = 0;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
//                String data = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
//                String title = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
//                String album = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
//                String artist = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
//                String duration = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                sizee += Long.parseLong(data);

//                Song song = Song(data, title, album, artist);
//                if (duration != null) {
//                    song.setDuration(duration)
//                }
//                audioList.add(song)
            }
            Log.d("Intent Path ", "Data " + Formatter.formatFileSize(getApplicationContext(), sizee));
            cursor.close();
        }
    }    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadAudio() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();

//        Uri o = ApplicationInfo.FLAG_EXTERNAL_STORAGE;
//        Uri i = MediaStore.Files.FileColumns.MIME_TYPE;
//        Uri p = MediaStore.Files.getContentUri("external");
        Uri uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriVideos = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri uriAudio = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //        String selection = MediaStore.Images.Media.Is + "!= 0";
//        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

//        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(uriDownloads, null, null, null, null);

        long sizee = 0;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
//                String data = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
//                String title = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
//                String album = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))));
//                String artist = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
//                String duration = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                sizee += Long.parseLong(data);

//                Song song = Song(data, title, album, artist);
//                if (duration != null) {
//                    song.setDuration(duration)
//                }
//                audioList.add(song)
            }
            Log.d("Intent Path ", "Data " + Formatter.formatFileSize(getApplicationContext(), sizee));
            cursor.close();
        }
    }
     */


}