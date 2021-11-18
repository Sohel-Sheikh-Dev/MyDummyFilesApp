package com.example.thefilesapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    File[] getFiles;
    Context context;

    private static final int ITEM_TYPE_FOLDER = 1;
    private static final int ITEM_TYPE_FILE = 2;


    public ListAdapter(Context context, File[] getFiles) {
        this.getFiles = getFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOLDER) {
            return new FolderViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.folder_item, parent, false));
        } else {
            return new FileViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.files_item, parent, false));
        }
    }

    public void copy(File src, File dst) throws IOException {

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);


        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
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

    public static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int pos = position;

        Date lastMod = new Date(getFiles[pos].lastModified());
        LocalDateTime now = LocalDateTime.now();

        DateFormat sdf = new SimpleDateFormat("MMM d");

        DateFormat FMD = new SimpleDateFormat("MMMM d, h:mm a");

        DateFormat dateF1 = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter dateF2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate d1 = LocalDate.parse(dateF1.format(lastMod), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate d2 = LocalDate.parse(dateF2.format(now), DateTimeFormatter.ISO_LOCAL_DATE);


        Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
        long diffDays = diff.toDays();

        String formattedModDate = FMD.format(lastMod);

        if (getFiles[pos].isDirectory()) {
            ((FolderViewHolder) holder).fileName.setText(getFiles[pos].getName());

            ((FolderViewHolder) holder).fileLastDate.setText(sdf.format(lastMod));

            ((FolderViewHolder) holder).eclipces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), ((FolderViewHolder) holder).eclipces);

                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

//                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//                        int position = info.position;

                            switch (item.toString()) {
                                case "copy":
                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    break;


                                case "move":
//                                File sourceLocation = new File(getFiles[pos].getAbsolutePath());
////                                sourceLocation.renameTo(new File("/storage/emulated/0/Download"));
//                                Log.d("Loggy", "" + sourceLocation);
//
//                                Log.d("Hagy", "onCreate: " + (Environment.getExternalStorageDirectory().getAbsolutePath() + "/-4941010891231570339_120.jpg"));
//
//                                File from = new File("file://"+Environment.getExternalStorageDirectory().getAbsolutePath() + "/-4941010891231570339_120.jpg");
//                                File to = new File("file://"+Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
//                                from.renameTo(to);

                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    break;


                                case "delete":
                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(context.getApplicationContext(), "File name: " + getFiles[pos].getName(), Toast.LENGTH_SHORT).show();
                                    getFiles[pos].delete();
//                              notifyItemRemoved(position);
//                              Toast.makeText(context.getApplicationContext(),"File name: "+getFiles[35].getName(),Toast.LENGTH_SHORT).show();
//                              getFiles[35].delete();
                                    break;

                            }

                            return true;
                        }
                    });
                    popupMenu.show();

                }
            });

            ((FolderViewHolder) holder).iconIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File dir = new File(String.valueOf(getFiles[pos]));
                    String sizeString = Formatter.formatFileSize(context.getApplicationContext(), getFileSize(dir));
                    Toast.makeText(context.getApplicationContext(), "Space: " + sizeString, Toast.LENGTH_SHORT).show();
                }
            });

            ((FolderViewHolder) holder).folderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (getFiles[pos].isDirectory()) {
                    Toast.makeText(context.getApplicationContext(), "It is a directory", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(), ListFiles.class);
                    String clickedPath = getFiles[pos].toString();
                    intent.putExtra("clickedPath", clickedPath);
                    Log.d("AdapterFileName", "onClick: " + clickedPath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                        /*
                    } else {
                        Intent promptInstall = new Intent(Intent.ACTION_GET_CONTENT);
                        promptInstall.setAction(Intent.ACTION_VIEW);
                        final MimeTypeMap mime = MimeTypeMap.getSingleton();
                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                        promptInstall.setDataAndType(uri, mime.getExtensionFromMimeType(com.google.common.io.Files.getFileExtension(getFiles[pos].toString())));
                        promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Log.d("Datai", "Diff days: " + diffDays);


                        try {
                            context.startActivity(promptInstall);
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage() + "Heyy", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context.getApplicationContext(), NoPreview.class);
                            String filePath = String.valueOf(getFiles[pos]);
                            intent.putExtra("FilePath", filePath);
                            intent.putExtra("ModDate", formattedModDate);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                    */

                }


            });
        } else {
            ((FileViewHolder) holder).fileNameFile.setText(getFiles[pos].getName());

            String extension = FilenameUtils.getExtension(getFiles[pos].getName());
            String extensionWithDot = getFiles[pos].getName();


            ((FileViewHolder) holder).folderItemFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context.getApplicationContext(),extension,Toast.LENGTH_SHORT).show();
                    Intent promptInstall = new Intent(Intent.ACTION_GET_CONTENT);
                    promptInstall.setAction(Intent.ACTION_VIEW);
                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    promptInstall.setDataAndType(uri, mime.getExtensionFromMimeType(com.google.common.io.Files.getFileExtension(getFiles[pos].toString())));
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Log.d("Datai", "Diff days: " + diffDays);


                    try {
                        context.startActivity(promptInstall);
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage() + "Heyy", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context.getApplicationContext(), NoPreview.class);
                        String filePath = String.valueOf(getFiles[pos]);
                        intent.putExtra("FilePath", filePath);
                        intent.putExtra("ModDate", formattedModDate);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            });

            if(extension.equals("jpg")){
                Glide.with(context.getApplicationContext()).load(getFiles[pos]).into(((FileViewHolder) holder).iconIVFile);
            }
            if(extension.equals("mp4")){
                RequestOptions requestOptions = new RequestOptions();

                Glide.with(context.getApplicationContext())
                        .load(getFiles[pos]).apply(requestOptions)
                        .thumbnail(Glide.with(context.getApplicationContext())
                                .load(getFiles[pos]))
                        .into(((FileViewHolder) holder).iconIVFile);
            }
            if(extension.equals("apk")){
                String APKFilePath = getFiles[pos].getPath(); //For example...
                Toast.makeText(context.getApplicationContext(),APKFilePath,Toast.LENGTH_SHORT).show();

                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

                pi.applicationInfo.sourceDir       = APKFilePath;
                pi.applicationInfo.publicSourceDir = APKFilePath;


                Drawable APKicon = pi.applicationInfo.loadIcon(pm);
                Glide.with(context.getApplicationContext()).load(APKicon).into(((FileViewHolder) holder).iconIVFile);

            }
            if(extensionWithDot.startsWith(".")){
                Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(((FileViewHolder) holder).iconIVFile);
            }
            if(extension.equals("pdf")){
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(String.valueOf(getFiles[pos]), MediaStore.Images.Thumbnails.MINI_KIND);
                Glide.with(context.getApplicationContext()).load(thumb).into(((FileViewHolder) holder).iconIVFile);
            }
            if(extension.equals("opus")){
                Glide.with(context.getApplicationContext()).load(R.drawable.ic_baseline_audiotrack_24).into(((FileViewHolder) holder).iconIVFile);
            }


        }


    }


    @Override
    public int getItemViewType(int position) {
        if (getFiles[position].isDirectory()) {
            return ITEM_TYPE_FOLDER;
        } else {
            return ITEM_TYPE_FILE;
        }
    }


    @Override
    public int getItemCount() {
        if (getFiles != null) {
            return getFiles.length;
        } else {
            return 0;
        }
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {

        TextView fileName, fileLastDate;
        CardView folderItem;
        ImageView eclipces, iconIV;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileName);
            fileLastDate = itemView.findViewById(R.id.fileLastDate);
            folderItem = itemView.findViewById(R.id.folderItem);
            eclipces = itemView.findViewById(R.id.threeDots);
            iconIV = itemView.findViewById(R.id.iconIV);
        }
    }


    public static class FileViewHolder extends RecyclerView.ViewHolder {

        TextView fileNameFile, fileLastDateFile;
        CardView folderItemFile;
        ImageView eclipcesFile, iconIVFile;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            fileNameFile = itemView.findViewById(R.id.fileNameFile);
            fileLastDateFile = itemView.findViewById(R.id.fileLastDateFile);
            folderItemFile = itemView.findViewById(R.id.folderItemFile);
            eclipcesFile = itemView.findViewById(R.id.threeDotsFile);
            iconIVFile = itemView.findViewById(R.id.iconIVFile);
        }
    }


}
