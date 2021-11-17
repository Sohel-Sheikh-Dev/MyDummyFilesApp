package com.example.thefilesapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StatFs;
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    File[] getFiles;
    Context context;

    public ListAdapter(Context context, File[] getFiles) {
        this.getFiles = getFiles;
        this.context = context;
    }


    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.folder_item, parent, false));
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
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {


        int pos = position;

/*
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(getFiles[34]);
        AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

        try {
            grabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            e.printStackTrace();
        }

        Mat img = new Mat();
        
        for(int frameCount = 0;frameCount < grabber.getLengthInVideoFrames();frameCount++){
            try {
                Frame nthFrame = grabber.grabImage();
                Bitmap bitmap = converterToBitmap.convert(nthFrame);
                Mat mat = converterToMat.convertToOrgOpenCvCoreMat(nthFrame);

//                img = context.getApplicationContext()



            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }


        }
*/
//        Log.d("TAG", "Fileeeeee: " + getFiles[34].getName());



        holder.fileName.setText(getFiles[pos].getName());

        Date lastMod = new Date(getFiles[pos].lastModified());
        LocalDateTime now = LocalDateTime.now();

        DateFormat sdf = new SimpleDateFormat("MMM d");

        DateFormat FMD = new SimpleDateFormat("MMMM d, h:mm a");

        String formattedModDate = FMD.format(lastMod);

        holder.fileLastDate.setText(sdf.format(lastMod));


        DateFormat dateF1 = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter dateF2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate d1 = LocalDate.parse(dateF1.format(lastMod), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate d2 = LocalDate.parse(dateF2.format(now), DateTimeFormatter.ISO_LOCAL_DATE);


        Duration diff = Duration.between(d1.atStartOfDay(), d2.atStartOfDay());
        long diffDays = diff.toDays();



        /*

        DateFormat hour = new SimpleDateFormat("HH");
        DateFormat min = new SimpleDateFormat("mm");

        DateTimeFormatter currHour = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter currMin = DateTimeFormatter.ofPattern("mm");

        int h = Integer.parseInt(currHour.format(now));
        int m = Integer.parseInt(currMin.format(now));

        int modHr = Integer.parseInt(hour.format(lastMod));
        int modMin = Integer.parseInt(min.format(lastMod));


        int diffHr = h - modHr;
        int diffMin = m - modMin;

        LocalTime time = LocalTime.of(h, m);
        time = time.minusHours(modHr);




        if (diffDays > 7) {
            holder.fileLastDate.setText(sdf.format(lastMod));
        }
        if (diffDays <= 7 && diffDays > 0) {
            holder.fileLastDate.setText(diffDays + " days ago");
        }
        if (diffDays <= 1) {
            holder.fileLastDate.setText(sdf.format(lastMod));
            //Only hours and minutes ago of modified time are left
            //holder.fileLastDate.setText("Today");
            //holder.fileLastDate.setText(diffHr+" hours ago");
        }
        */

//        if (diffDays <= 7 && diffDays > 1) {
//            holder.fileLastDate.setText(diffDays + " days ago");
////            Log.d("Datai", "Diffrence between dates is : " + diffDays + "days");
//        }
//        if (diffDays <= 1) {
//            holder.fileLastDate.setText(diffHr + " hours ago");
//        }


        holder.eclipces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), holder.eclipces);

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
                                break;

                        }

                        return true;
                    }
                });
                popupMenu.show();
//                notifyItemRemoved(position);
//                Toast.makeText(context.getApplicationContext(),"File name: "+getFiles[35].getName(),Toast.LENGTH_SHORT).show();
//                getFiles[35].delete();
            }
        });


        holder.iconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(String.valueOf(getFiles[pos]));
                String sizeString = Formatter.formatFileSize(context.getApplicationContext(), getFileSize(dir));
                Toast.makeText(context.getApplicationContext(), "Space: " + sizeString, Toast.LENGTH_SHORT).show();
            }
        });


        holder.folderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getFiles[pos].isDirectory()) {
                    Toast.makeText(context.getApplicationContext(), "It is a directory", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(), ListFiles.class);
                    String clickedPath = getFiles[pos].toString();
                    intent.putExtra("clickedPath", clickedPath);
                    Log.d("AdapterFileName", "onClick: " + clickedPath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    

/*
                    if (diffDays == 0) {
                        if (diffHr == 0) {
                            Log.d("Datai", "Diff min: " + diffMin);
                        } else {
                            Log.d("Datai", "Diff hour: " + diffHr);
                        }
                    }
*/

                    context.startActivity(intent);
                } else {
                    Intent promptInstall = new Intent(Intent.ACTION_GET_CONTENT);
                    promptInstall.setAction(Intent.ACTION_VIEW);
                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    promptInstall.setDataAndType(uri, mime.getExtensionFromMimeType(com.google.common.io.Files.getFileExtension(getFiles[pos].toString())));
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Log.d("Datai", "Diff days: " + diffDays);

//                    Log.d("Position", "File Pos: " + getFiles[34].getName());


/*
                    if (diffDays == 0) {
                        Log.d("Datai", "Current hour: " + h + "\tCurrent minute: " + m);
                        Log.d("Datai", "Modified hour: " + modHr + "\tModified minute: " + modMin);
//                        if (diffHr == 0) {
//                            Log.d("Datai", "Diff min: " + diffMin);
//                        } else {
//                            Log.d("Datai", "Diff hour: " + diffHr);
//                        }

//                        Log.d("Datai", "Diff min: " + diffMin);
                        Log.d("Datai", "Diff hour: " + finalTime);

                    }
*/

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



/*
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    Log.d("TAG", "openPDF: intent with uri: " + uri);
                    */

//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(uri, "application/pdf");
//                    context.startActivity(Intent.createChooser(intent, "Open with..."));

                    /*
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(FileProvider.getUriForFile(context.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    Intent testIntent = new Intent(Intent.ACTION_VIEW);
                    testIntent.setType("application/pdf");
//                    List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(getFiles[pos]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/pdf");
                    context.startActivity(intent);


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    Log.d("TAG", "openPDF: intent with uri: " + uri);
                    intent.setDataAndType(uri, "application/pdf");
                    context.startActivity(Intent.createChooser(intent, "Open with..."));
*/
            }
//                else {
//                    Toast.makeText(context.getApplicationContext(), "It is a " + Files.getFileExtension(getFiles[pos].toString()) + " type file" + "\nFile: " + (getFiles[pos].toString()), Toast.LENGTH_SHORT).show();
//                }


        });


    }


    @Override
    public int getItemCount() {
        if (getFiles != null) {
            return getFiles.length;
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName, fileLastDate;
        CardView folderItem;
        ImageView eclipces, iconIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileName);
            fileLastDate = itemView.findViewById(R.id.fileLastDate);
            folderItem = itemView.findViewById(R.id.folderItem);
            eclipces = itemView.findViewById(R.id.threeDots);
            iconIV = itemView.findViewById(R.id.iconIV);
        }
    }
}
