package com.example.thefilesapp.MainComponents;

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
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thefilesapp.BuildConfig;
import com.example.thefilesapp.MainActivityAdapter;
import com.example.thefilesapp.NoPreview;
import com.example.thefilesapp.R;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainCompAdapter extends RecyclerView.Adapter<MainCompAdapter.ViewHolder> {

    Context context;
    ArrayList<String> pathList;


    public MainCompAdapter(Context context, ArrayList<String> pathList) {
        this.context = context;
        this.pathList = pathList;
    }

    @NonNull
    @Override
    public MainCompAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.files_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MainCompAdapter.ViewHolder holder, int position) {
        int pos = position;

        String pathListPos = pathList.get(pos);

        Path pathName = Paths.get(pathListPos).getFileName();
        String extension = FilenameUtils.getExtension(pathName.toString());
        holder.fileName.setText(pathName.toString());


        holder.folderItemFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context.getApplicationContext(), extension, Toast.LENGTH_SHORT).show();
                Intent promptInstall = new Intent(Intent.ACTION_GET_CONTENT);
                promptInstall.setAction(Intent.ACTION_VIEW);
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(pathListPos));
                promptInstall.setDataAndType(uriForFile, mime.getExtensionFromMimeType(com.google.common.io.Files.getFileExtension(new File(pathListPos).toString())));
                promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri returnUri = promptInstall.getData();

                File videoFile = new File(String.valueOf(new File(pathListPos)));

                Log.i("TAG", Uri.fromFile(videoFile).toString());

                MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{videoFile.getAbsolutePath()}, null, (path, uri) -> Log.i("TAG", uri.toString()));

//                    Log.d("TAG", "Uri: " + Uri.fromFile(new File(String.valueOf(getFiles[pos]))));

//                Log.d("Datai", "Diff days: " + diffDays);


                try {
                    context.startActivity(promptInstall);
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage() + "Heyy", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(), NoPreview.class);
                    String filePath = String.valueOf(new File(pathListPos));
                    intent.putExtra("FilePath", filePath);
//                    intent.putExtra("ModDate", formattedModDate);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });

        String[] imgLists = {"bmp", "gif", "jpg", "png", "webp", "heif"};

        for (String accImg : imgLists) {
            if (extension.equals(accImg)) {
                Glide.with(context.getApplicationContext()).load(pathListPos).into(holder.iconIVFile);
            }
        }

        String[] vidLists = {"3gp", "mp4", "mkv", "ts", "webm"};

        for (String accVid : vidLists) {
            if (extension.equals(accVid)) {
                RequestOptions requestOptions = new RequestOptions();

                Glide.with(context.getApplicationContext())
                        .load(pathListPos).apply(requestOptions)
                        .thumbnail(Glide.with(context.getApplicationContext())
                                .load(pathListPos))
                        .into(holder.iconIVFile);
            }
        }

        String[] apkLists = {"apk" , "xapk" , "apks" , "apkm"};

        for (String accApk : apkLists) {
            if (extension.equals(accApk)) {
                String APKFilePath = pathListPos; //For example...
                Toast.makeText(context.getApplicationContext(), APKFilePath, Toast.LENGTH_SHORT).show();

                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(APKFilePath, 0);

                pi.applicationInfo.sourceDir = APKFilePath;
                pi.applicationInfo.publicSourceDir = APKFilePath;


                Drawable APKicon = pi.applicationInfo.loadIcon(pm);
                Glide.with(context.getApplicationContext()).load(APKicon).into(holder.iconIVFile);

            }
        }

//        if (extensionWithDot.startsWith(".")) {
//            Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(holder.iconIVFile);
//        }

        String[] docLists = {"doc", "docx", "txt", "csv", "rtf", "odt", "md", "zip", "pdf"};

        for (String accDoc : docLists) {
            if (extension.equals(accDoc)) {
                try {
                    ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(pathListPos), ParcelFileDescriptor.MODE_READ_ONLY);
                    Log.d("PDFy", "onBindViewHolder M: " + pathListPos);
                    PdfRenderer renderer = new PdfRenderer(input);
                    PdfRenderer.Page page = renderer.openPage(0);
                    Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    Glide.with(context.getApplicationContext()).load(bitmap).into(holder.iconIVFile);
                    page.close();
                    renderer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        String[] audLists = {"aac", "opus", "flac", "ts", "mid", "xmf", "mxmf", "rtttx", "rtx", "ota", "imy", "ogg", "wav", "mp3"};

        for (String accAud : audLists) {
            if (extension.equals(accAud)) {
                Log.d("PDFy", "onBindViewHolder M: " + pathListPos);
                Glide.with(context.getApplicationContext()).load(R.drawable.ic_baseline_audiotrack_24).into(holder.iconIVFile);
            }

        }
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName;
        CardView folderItemFile;
        ImageView iconIVFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileNameFile);
            folderItemFile = itemView.findViewById(R.id.folderItemFile);
            iconIVFile = itemView.findViewById(R.id.iconIVFile);
        }
    }
}
