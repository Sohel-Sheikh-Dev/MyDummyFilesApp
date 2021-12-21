package com.example.thefilesapp;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thefilesapp.MainComponents.MainCompFiles;
import com.example.thefilesapp.MainComponents.MainCompModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

    Context context;
    long totalImageSize, totalVideoSize, totalAudioSize, totalApkSize, totalDocSize;
    String[] categoryItemList = {"Downloads", "Images", "Videos", "Audio", "Documents & other", "Apk files","Apps"};
    int[] images = {R.drawable.download_icon, R.drawable.images_icon, R.drawable.videos_icon, R.drawable.ic_baseline_audiotrack_24, R.drawable.document, R.drawable.ic_baseline_android_24, R.drawable.option};

    File[] f;

    ArrayList<String> valPaths = new ArrayList<>();
    ArrayList<PackageInfo> packageInfos = new ArrayList<>();

    List<MainCompModel> mainCompModelList = new ArrayList<>();

    ArrayList<String> componentSizes = new ArrayList<>();


    public MainActivityAdapter(Context context, ArrayList<String> componentSizes) {
        this.context = context;
        this.componentSizes = componentSizes;
    }

    @NonNull
    @Override
    public MainActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.main_icon_item, parent, false));
    }


    private void getAllDir(File dir, String type) {
        File[] mainListFiles = dir.listFiles();
        if (mainListFiles != null) {
            for (int i = 0; i < mainListFiles.length; i++) {


                if (mainListFiles[i].isDirectory()) {
                    getAllDir(mainListFiles[i], type);
                } else {

                    if (type.equals("Images")) {

                        if (mainListFiles[i].getName().endsWith(".bmp") ||
                                mainListFiles[i].getName().endsWith(".gif") ||
                                mainListFiles[i].getName().endsWith(".jpg") ||
                                mainListFiles[i].getName().endsWith(".jpeg") ||
                                mainListFiles[i].getName().endsWith(".png") ||
                                mainListFiles[i].getName().endsWith(".webp") ||
                                mainListFiles[i].getName().endsWith(".heif")) {
//                            Log.d("Filu", "FileName:" + mainListFiles[i].getName());

                            Intent intent = new Intent(context.getApplicationContext(), ListFiles.class);
                            intent.putExtra("provFile", mainListFiles);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                            totalImageSize += ListAdapter.getFileSize(mainListFiles[i]);
//                        Log.d("Filu", "FileName:" + listFile[i].getName() + "\t Size: " + Formatter.formatFileSize(getApplicationContext(), ListAdapter.getFileSize(listFile[i])));
                        }
                        f = mainListFiles;
                    }


                    if (type.equals("Videos")) {

                        if (mainListFiles[i].getName().endsWith(".3gp") ||
                                mainListFiles[i].getName().endsWith(".mp4") ||
                                mainListFiles[i].getName().endsWith(".mkv") ||
                                mainListFiles[i].getName().endsWith(".ts") ||
                                mainListFiles[i].getName().endsWith(".webm")) {
                            Log.d("Filu", "FileName:" + mainListFiles[i].getName());
                            totalVideoSize += ListAdapter.getFileSize(mainListFiles[i]);
//                        Log.d("Filu", "FileName:" + listFile[i].getName() + "\t Size: " + Formatter.formatFileSize(getApplicationContext(), ListAdapter.getFileSize(listFile[i])));
                        }
                    }


                    if (type.equals("Audio")) {

                        if (mainListFiles[i].getName().endsWith(".3gp") ||
                                mainListFiles[i].getName().endsWith(".aac") ||
                                mainListFiles[i].getName().endsWith(".opus") ||
                                mainListFiles[i].getName().endsWith(".flac") ||
                                mainListFiles[i].getName().endsWith(".ts") ||
                                mainListFiles[i].getName().endsWith(".mid") ||
                                mainListFiles[i].getName().endsWith(".xmf") ||
                                mainListFiles[i].getName().endsWith(".mxmf") ||
                                mainListFiles[i].getName().endsWith(".rtttx") ||
                                mainListFiles[i].getName().endsWith(".rtx") ||
                                mainListFiles[i].getName().endsWith(".ota") ||
                                mainListFiles[i].getName().endsWith(".imy") ||
                                mainListFiles[i].getName().endsWith(".ogg") ||
                                mainListFiles[i].getName().endsWith(".wav") ||
                                mainListFiles[i].getName().endsWith(".mp3")) {
                            Log.d("Filu", "FileName:" + mainListFiles[i].getName());
                            totalAudioSize += ListAdapter.getFileSize(mainListFiles[i]);
//                        Log.d("Filu", "FileName:" + listFile[i].getName() + "\t Size: " + Formatter.formatFileSize(getApplicationContext(), ListAdapter.getFileSize(listFile[i])));
                        }
                    }


                    if (type.equals("Documents & other")) {
                        //Have to implement an zip file extension
                        if (mainListFiles[i].getName().endsWith(".txt") ||
                                mainListFiles[i].getName().endsWith(".pdf") ||
                                mainListFiles[i].getName().endsWith(".zip") ||
                                mainListFiles[i].getName().endsWith(".csv") ||
                                mainListFiles[i].getName().endsWith(".rtf") ||
                                mainListFiles[i].getName().endsWith(".otd") ||
                                mainListFiles[i].getName().endsWith(".md")) {
                            Log.d("Filu", "FileName:" + mainListFiles[i].getName());
                            totalDocSize += ListAdapter.getFileSize(mainListFiles[i]);
//                        Log.d("Filu", "FileName:" + listFile[i].getName() + "\t Size: " + Formatter.formatFileSize(getApplicationContext(), ListAdapter.getFileSize(listFile[i])));
                        }
                    }


/*
                    if (type.equals("Apps")) {

                        List<ApplicationInfo> apps = context.getApplicationContext().getPackageManager().getInstalledApplications(0);
                        for(ApplicationInfo app : apps) {
                            if((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
                                // It is a system app
                                Log.d("Filu", "System apps: " + app);
                            } else {
                                // It is installed by the user
                                Log.d("Filu", "Downloaded apps: " + app);
                            }
                        }
                    }
*/

                }


            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBindViewHolder(@NonNull MainActivityAdapter.ViewHolder holder, int position) {

        int positionCopy = position;

        Toast.makeText(context.getApplicationContext(), componentSizes.size() + ": " + categoryItemList.length, Toast.LENGTH_SHORT).show();

        holder.iconName.setText(categoryItemList[position]);
        holder.iconStorage.setText(componentSizes.get(position));
        Glide.with(context.getApplicationContext()).load(images[position]).into(holder.icon);

        holder.folderItemMain.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {

                valPaths.clear();

                String clickedPath = categoryItemList[positionCopy];


                Intent i = new Intent(context.getApplicationContext(), MainCompFiles.class);


                if (clickedPath.equals("Downloads")) {
                    explodeC();
                }

                if (clickedPath.equals("Images")) {
                    loadContents("Images");
                }


                if (clickedPath.equals("Videos")) {
                    loadContents("Videos");
                }

                if (clickedPath.equals("Audio")) {
                    loadContents("Audio");
                }


                if (clickedPath.equals("Documents & other")) {
                    loadContents("Documents & other");
                }

                if (clickedPath.equals("Apps")) {
                    loadContents("Apps");
                }

                if (clickedPath.equals("Apk files")) {
                    installedApps();
                    i.putExtra("PackageInfos",packageInfos);
                }

                i.putExtra("ValPath", valPaths);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
/*
                if (clickedPath.equals("Apps")) {
                    Toast.makeText(context.getApplicationContext(), "" + clickedPath, Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    getAllDir(Environment.getStorageDirectory(), "Apps");
                    }
                    Toast.makeText(context.getApplicationContext(), "Total Size: " + Formatter.formatFileSize(context.getApplicationContext(), totalApkSize), Toast.LENGTH_SHORT).show();
                    //                    loadContents(clickedPath);
                }
*/


            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void installedApps() {
        List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);



            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();

//                Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

//                PackageInfo bnb = packInfo.packageName;

//                p = appName;
//                packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                packageInfos.add(packInfo);
//                mainCompModelList.get(i).getPackageInfoList().add(packInfo);


                final PackageManager pm = context.getPackageManager();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                for (ApplicationInfo packageInfo : packages) {
                        getPackageSizeInfo(context.getApplicationContext(),packageInfo.packageName);
                }

                Log.e("App № " + Integer.toString(i), appName + " " + i);
            }
        }
    }

    private void getPackageSizeInfo(final Context context, String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
            final StorageManager storageManager = (StorageManager)  context.getSystemService(Context.STORAGE_SERVICE);
            try {

                ApplicationInfo ai = context.getApplicationContext().getPackageManager().getApplicationInfo(packageName, 0);
                StorageStats storageStats = storageStatsManager.queryStatsForUid(ai.storageUuid, context.getApplicationContext().getApplicationInfo().uid);
                long cacheSize = storageStats.getCacheBytes();
                long dataSize = storageStats.getDataBytes();
                long apkSize = storageStats.getAppBytes();

                PackageStats packageStats = new PackageStats(packageName);
                packageStats.cacheSize = cacheSize;
                packageStats.dataSize = dataSize;
                packageStats.codeSize = apkSize;
                Log.e("LAG" ,""+apkSize);
//                observer.onGetStatsCompleted(packageStats, true);
                //long size += info.cacheSize;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else  {
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = context.getPackageManager().getClass()
                        .getMethod("getPackageSizeInfo",
                                String.class, Class.forName("android.content.pm.IPackageStatsObserver"));

                getPackageSizeInfo.invoke(context.getPackageManager(), packageName,
                        new IPackageStatsObserver.Stub() { //error

                            public void onGetStatsCompleted(
                                    PackageStats pStats, boolean succeeded)
                                    throws RemoteException {

                                //totalSize = totalSize + pStats.cacheSize;
                                //Log.d("size", totalSize+"");
                                Toast.makeText( context.getApplicationContext(), "size"+pStats.cacheSize + ",," + pStats.dataSize + ",," + pStats.codeSize, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } catch (Exception e) {
                try {
                    getPackageSizeInfo = context.getPackageManager().getClass()
                            .getMethod("getPackageSizeInfo",
                                    String.class, Class.forName("android.content.pm.IPackageStatsObserver"));

                    getPackageSizeInfo.invoke(context.getPackageManager(), packageName,
                            new IPackageStatsObserver.Stub() { //error

                                public void onGetStatsCompleted(
                                        PackageStats pStats, boolean succeeded)
                                        throws RemoteException {
                                    Toast.makeText( context.getApplicationContext(), "size"+pStats.cacheSize, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                } catch (Exception ee) {
                    Log.d("eeeeeeeeeee", "error");
                    ee.printStackTrace();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void explodeC() {
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        Uri uriDownloads = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uriDownloads, null, null, null, null);

        long daaa = 0;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String data = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Downloads.DATA))));
                String dataSize = cursor.getString(Integer.parseInt(String.valueOf(cursor.getColumnIndex(MediaStore.Downloads.SIZE))));
//                long dataSizes = Long.parseLong(dataSize);

                File dFiles = new File(data);
                if (!(dFiles.isDirectory())) {
                    daaa += Long.parseLong(dataSize);
//                    Log.d("MainIntentPath ", "Data " + data);
                    valPaths.add(data);
                }
//                Log.d("Intent Path ", "Data " + Formatter.formatFileSize(getApplicationContext(), sizee));
            }

            Log.d("MMIntent Path ", "Data " + Formatter.formatFileSize(context.getApplicationContext(), daaa));
            cursor.close();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadContents(String contentTitles) {

        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();

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
        if(contentTitles.equals("Apps")){
            fileExtensions = new String[]{"apk" , "xapk" , "apks" , "apkm"};
        }

        Toast.makeText(context.getApplicationContext(), "Uri: " + uri, Toast.LENGTH_SHORT).show();

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        for (int i = 0; i < fileExtensions.length; i++) {
            String[] selectionArgsPdf = new String[0];
            Log.d("Intent Path ", "Pata: " + fileExtensions[i]);
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensions[i]);
            Log.d("Intent Path ", "Sata: " + mimeType);
            if (mimeType == null) {
                i++;
//                selectionArgsPdf = new String[]{"image/png"};
            } else {
                selectionArgsPdf = new String[]{mimeType};
            }
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE};
            String sortOrder = null;

            Cursor cursor = contentResolver.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);

            long sizee = 0;

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    String dataSizes = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
                    Log.d("MainIntentPath ", "Data " + data);
//                    File [] datas = data
                    valPaths.add(data);
//                    mainCompModelList.get(i).setPathList(va);
//                    valPaths = new String[]{data};
//                    Log.d("Intent Path ", "Vall" + cursor.getString(cursor.getColumnIndexOrThrow(projection[0])));
                    sizee += Long.parseLong(dataSizes);
                }
//                Log.d("Intent Path ", "Vall" + cursor.getString(cursor.getColumnName(0));
                Log.d("MMIntent Path ", "Data " + Formatter.formatFileSize(context.getApplicationContext(), sizee));
                cursor.close();
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryItemList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView iconName, iconStorage;
        ImageView icon;
        CardView folderItemMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconName = itemView.findViewById(R.id.icon_name);
            iconStorage = itemView.findViewById(R.id.storage);
            icon = itemView.findViewById(R.id.icon);
            folderItemMain = itemView.findViewById(R.id.folderItemMain);
        }
    }
}
