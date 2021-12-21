package com.example.thefilesapp.MainComponents;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thefilesapp.BuildConfig;
import com.example.thefilesapp.NoPreview;
import com.example.thefilesapp.R;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class MainCompAdapter extends RecyclerView.Adapter<MainCompAdapter.ViewHolder> {

    Context context;
    ArrayList<String> pathList;
    ArrayList<PackageInfo> packages;

    public MainCompAdapter(Context context, ArrayList<String> pathList, ArrayList<PackageInfo> packages) {
        this.context = context;
        this.pathList = pathList;
        this.packages = packages;
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

        if (packages != null) {

            long updateTimeInMilliseconds = new File(packages.get(pos).applicationInfo.sourceDir).lastModified();

            DateFormat sdf = new SimpleDateFormat("MMM d");

            holder.lastDatem.setText(sdf.format(updateTimeInMilliseconds));

            Log.d("Maggy", "TTonCreate: " + "package:" + packages.get(pos).packageName);
            holder.fileName.setText(packages.get(pos).applicationInfo.loadLabel(context.getPackageManager()).toString());

            Drawable icons = packages.get(pos).applicationInfo.loadIcon(context.getApplicationContext().getPackageManager());
            Glide.with(context.getApplicationContext()).load(icons).into(holder.iconIVFile);

            holder.folderItemFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String packageName = packages.get(pos).packageName;

                    Log.d("Maggy", "TonCreate: " + packageName);

                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse("package:" + packageName));
                    context.startActivity(i);

                }
            });


        } else {
            String pathListPos = pathList.get(pos);

            File fileDate = new File(pathListPos);
            Date lastMod = new Date(fileDate.lastModified());
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

            holder.lastDatem.setText(sdf.format(lastMod));

            Path pathName = Paths.get(pathListPos).getFileName();
            String extension = FilenameUtils.getExtension(pathName.toString());
            holder.fileName.setText(pathName.toString());


            holder.folderItemFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Toast.makeText(context.getApplicationContext(), "CC "+packages.get(0), Toast.LENGTH_SHORT).show();

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

            String[] apkLists = {"apk", "xapk", "apks", "apkm"};

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

            if (extension.equals("zip")) {
                Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(holder.iconIVFile);
            }


            String[] docLists = {"doc", "docx", "txt", "csv", "rtf", "odt", "md", "pdf"};

            for (String accDoc : docLists) {
                if (extension.equals(accDoc)) {
//                    File fp = new File(pathListPos);
//                    Toast.makeText(context.getApplicationContext(), "" + fp, Toast.LENGTH_SHORT).show();
//                    Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(holder.iconIVFile);

                    try {
                        PDDocument pdfReader = PDDocument.load(new File(pathListPos));
                        if (pdfReader.isEncrypted()) {
                            Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(holder.iconIVFile);
                        }
                        if (!(pdfReader.isEncrypted())) {
                            ParcelFileDescriptor input = ParcelFileDescriptor.open(new File(pathListPos), ParcelFileDescriptor.MODE_READ_ONLY);
                            PdfRenderer renderer = new PdfRenderer(input);
//                        Log.d("PDFy", "onBindViewHolder M: " + renderer);
                            PdfRenderer.Page page = renderer.openPage(0);
                            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                            Glide.with(context.getApplicationContext()).load(bitmap).into(holder.iconIVFile);
                            page.close();
                            renderer.close();
                        } else {
                            Glide.with(context.getApplicationContext()).load(R.drawable.file_open).into(holder.iconIVFile);
                        }
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

    }

    @Override
    public int getItemCount() {
        if (packages != null) {
            return packages.size();
        } else {
            return pathList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fileName;
        CardView folderItemFile;
        ImageView iconIVFile;
        TextView lastDatem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileNameFile);
            folderItemFile = itemView.findViewById(R.id.folderItemFile);
            iconIVFile = itemView.findViewById(R.id.iconIVFile);
            lastDatem = itemView.findViewById(R.id.fileLastDateFile);
        }
    }
}
