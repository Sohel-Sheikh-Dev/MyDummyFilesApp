package com.example.thefilesapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    File[] getFiles;


    Context context;

    File dirrs;

    public static String actualPath;
    public static String copyMove;

    private static final int ITEM_TYPE_FOLDER = 1;
    private static final int ITEM_TYPE_FILE = 2;


    public ListAdapter(Context context, File[] getFiles, File dirss) {
        this.getFiles = getFiles;
        this.context = context;
        this.dirrs = dirss;
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


//    void generateImageFromPdf(Uri pdfUri) {
//        int pageNumber = 0;
//        PdfiumCore pdfiumCore = new PdfiumCore(context.getApplicationContext());
//        try {
//            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
//            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
//            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
//            pdfiumCore.openPage(pdfDocument, pageNumber);
//            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
//            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
//            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
//            saveImage(bmp);
//            pdfiumCore.closeDocument(pdfDocument); // important!
//        } catch (Exception e) {
//            //todo with exception
//        }
//    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";

    public static void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder, "PDF.png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }

    private void scanFilesIns(File file, int poss) {

        File[] fileArray = file.listFiles();
        if (fileArray[poss].isDirectory()) {
            Log.d("TAG", "Images: " + fileArray[poss]);
//            scanFilesIns(file,poss);
            File[] insideFileArray = fileArray[poss].listFiles();
            Log.d("TAG", "Images Inside: " + insideFileArray[0]);
        }
//        if(file.isFile() && (file.getPath().endsWith(".jpg"))){
//        }
    }


    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return formatSize(totalBlocks * blockSize);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int pos = position;


//        File pathIns = new File("/storage/emulated/0/Pictures");
//
//        MainActivity.scanFiles(pathIns);

        /*
        File[] fileArray = pathIns.listFiles();
        if (fileArray[pos].isDirectory()) {
            File[] insideFileArray = pathIns.listFiles();
            Log.d("TAG", "Images: " + insideFileArray[pos]);
        }
        */

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

                            actualPath = getFiles[pos].getPath();

                            switch (item.toString()) {

                                case "copy":

                                    Intent intent = new Intent(context.getApplicationContext(),CopyMoveToScreen.class);
                                    copyMove = "copyFile";
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);

                                    /*
                                    Intent intent = new Intent(context.getApplicationContext(),ListDirectories.class);
                                    copyMove = "copyFile";
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                     */
/*

                                    ListFiles activity = (ListFiles) context;
                                    CMFrag cmFrag = new CMFrag();
                                    copyMove = "copyFile";
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("actPath", "getFiles[pos].getPath().toString()");
//                                    cmFrag.setArguments(bundle);
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.full, cmFrag).addToBackStack(null).commit();
*/
                                    break;

                                case "move":


                                    Intent mintent = new Intent(context.getApplicationContext(),CopyMoveToScreen.class);
                                    copyMove = "moveFile";
                                    mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(mintent);

/*
                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    Intent moveIntent = new Intent(context.getApplicationContext(),ListDirectories.class);
                                    copyMove = "moveFile";
                                    moveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(moveIntent);
 */
/*

                                    ListFiles moveActivity = (ListFiles) context;
                                    CMFrag moveCmFrag = new CMFrag();
                                    copyMove = "moveFile";
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("actPath", "getFiles[pos].getPath().toString()");
//                                    cmFrag.setArguments(bundle);
                                    moveActivity.getSupportFragmentManager().beginTransaction().replace(R.id.full, moveCmFrag).addToBackStack(null).commit();
 */
                                    break;

                                case "delete":
                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(context.getApplicationContext(), "File name: " + getFiles[pos].getName(), Toast.LENGTH_SHORT).show();
                                    removeItem(pos);
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
                    File dir = new File("/storage/emulated/0");
                    String sizeString = Formatter.formatFileSize(context.getApplicationContext(), getFileSize(dir));
                    Toast.makeText(context.getApplicationContext(), "Space: " + getTotalInternalMemorySize(), Toast.LENGTH_SHORT).show();
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

            ((FileViewHolder) holder).eclipcesFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), ((FileViewHolder) holder).eclipcesFile);

                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();

                            actualPath = getFiles[pos].getPath();

                            Log.d("TAG", "Images: " + actualPath);

                            switch (item.toString()) {

                                case "copy":

                                    Intent intent = new Intent(context.getApplicationContext(),CopyMoveToScreen.class);
                                    copyMove = "copyFile";
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);

                                    /*
                                    Intent intent = new Intent(context.getApplicationContext(),ListDirectories.class);
                                    copyMove = "copyFile";
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                     */


//                                    ListFiles activity = (ListFiles) context;
//                                    CMFrag cmFrag = new CMFrag();
//                                    copyMove = "copyFile";
////                                    Bundle bundle = new Bundle();
////                                    bundle.putString("actPath", "getFiles[pos].getPath().toString()");
////                                    cmFrag.setArguments(bundle);
//                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.full, cmFrag).addToBackStack(null).commit();

                                    break;

                                case "move":


                                    Intent mintent = new Intent(context.getApplicationContext(),CopyMoveToScreen.class);
                                    copyMove = "moveFile";
                                    mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(mintent);

/*
                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    Intent moveIntent = new Intent(context.getApplicationContext(),ListDirectories.class);
                                    copyMove = "moveFile";
                                    moveIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(moveIntent);
 */
/*

                                    ListFiles moveActivity = (ListFiles) context;
                                    CMFrag moveCmFrag = new CMFrag();
                                    copyMove = "moveFile";
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("actPath", "getFiles[pos].getPath().toString()");
//                                    cmFrag.setArguments(bundle);
                                    moveActivity.getSupportFragmentManager().beginTransaction().replace(R.id.full, moveCmFrag).addToBackStack(null).commit();
*/
                                    break;

                                case "delete":
                                    Toast.makeText(context.getApplicationContext(), "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    Toast.makeText(context.getApplicationContext(), "File name: " + getFiles[pos].getName(), Toast.LENGTH_SHORT).show();
                                    removeItem(pos);
                                    break;

                            }
                            return true;
                        }
                    });
                    popupMenu.show();

                }
            });

            ((FileViewHolder) holder).fileNameFile.setText(getFiles[pos].getName());


            String extension = FilenameUtils.getExtension(getFiles[pos].getName());
            Toast.makeText(context.getApplicationContext(), "" + getFiles[pos].getName(), Toast.LENGTH_SHORT).show();
            String extensionWithDot = getFiles[pos].getName();


            ((FileViewHolder) holder).folderItemFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context.getApplicationContext(), "Hey ui"+getFiles[pos], Toast.LENGTH_SHORT).show();
                    Toast.makeText(context.getApplicationContext(), extension, Toast.LENGTH_SHORT).show();
                    Intent promptInstall = new Intent(Intent.ACTION_GET_CONTENT);
                    promptInstall.setAction(Intent.ACTION_VIEW);
                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", getFiles[pos]);
                    promptInstall.setDataAndType(uriForFile, mime.getExtensionFromMimeType(com.google.common.io.Files.getFileExtension(getFiles[pos].toString())));
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Uri returnUri = promptInstall.getData();
                    String mimeType = context.getContentResolver().getType(returnUri);

                    File videoFile = new File(String.valueOf(getFiles[pos]));

                    Log.i("TAG", Uri.fromFile(videoFile).toString());

                    MediaScannerConnection.scanFile(context.getApplicationContext(), new String[] { videoFile.getAbsolutePath() }, null, (path, uri) -> Log.i("TAG", uri.toString()));

//                    Log.d("TAG", "Uri: " + Uri.fromFile(new File(String.valueOf(getFiles[pos]))));

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


            if (extension.equals("jpg") || extension.equals("png")) {
                Glide.with(context.getApplicationContext()).load(getFiles[pos]).into(((FileViewHolder) holder).iconIVFile);
            }
            if (extension.equals("mp4")) {
                RequestOptions requestOptions = new RequestOptions();

                Glide.with(context.getApplicationContext())
                        .load(getFiles[pos]).apply(requestOptions)
                        .thumbnail(Glide.with(context.getApplicationContext())
                                .load(getFiles[pos]))
                        .into(((FileViewHolder) holder).iconIVFile);
            }


            if (extension.equals("apk")) {
                String APKFilePath = getFiles[pos].getPath(); //For example...
                Toast.makeText(context.getApplicationContext(), APKFilePath, Toast.LENGTH_SHORT).show();

                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

                pi.applicationInfo.sourceDir = APKFilePath;
                pi.applicationInfo.publicSourceDir = APKFilePath;


                Drawable APKicon = pi.applicationInfo.loadIcon(pm);
                Glide.with(context.getApplicationContext()).load(APKicon).into(((FileViewHolder) holder).iconIVFile);

            }

            if (extensionWithDot.startsWith(".")) {
                Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(((FileViewHolder) holder).iconIVFile);
            }


            if (extension.equals("pdf")) {

                PDFBoxResourceLoader.init(context.getApplicationContext());

                try {
                    PDDocument pdfReader = PDDocument.load(new File(getFiles[pos].getPath()));
                    if (pdfReader.isEncrypted()) {
                        Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(((FileViewHolder) holder).iconIVFile);
                    } else {
                        ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(getFiles[pos].getPath()), ParcelFileDescriptor.MODE_READ_ONLY);
                        Log.d("PDF", "onBindViewHolder: " + new File(getFiles[pos].getPath()));
                        PdfRenderer renderer = new PdfRenderer(input);
                        PdfRenderer.Page page = renderer.openPage(0);
                        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        Glide.with(context.getApplicationContext()).load(bitmap).into(((FileViewHolder) holder).iconIVFile);
                        page.close();
                        renderer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (extension.equals("opus")) {
                Glide.with(context.getApplicationContext()).load(R.drawable.ic_baseline_audiotrack_24).into(((FileViewHolder) holder).iconIVFile);
            }


        }


    }

    public void removeItem(int position) {
        getFiles[position].delete();
        notifyItemRemoved(position);
    }

    private void deleteFile(String inputPath, String inputFile) {
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
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
