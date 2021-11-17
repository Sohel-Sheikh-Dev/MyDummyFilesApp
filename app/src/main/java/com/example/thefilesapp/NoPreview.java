package com.example.thefilesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class NoPreview extends AppCompatActivity {

    TextView noPrevFileName,noPrevPath,noPrevFileSize,noPrevFileDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_preview);

        init();

        if(getIntent().hasExtra("FilePath")){
            File path = new File(getIntent().getStringExtra("FilePath"));
            Toast.makeText(NoPreview.this,"The path is: "+(path),Toast.LENGTH_LONG).show();
            noPrevFileName.setText(path.getName());
            noPrevPath.setText(path.toString());
            String sizeString = Formatter.formatFileSize(NoPreview.this, ListAdapter.getFileSize(path));
            noPrevFileSize.setText(sizeString);
        }

        if(getIntent().hasExtra("ModDate")) {
            String modDate = getIntent().getStringExtra("ModDate");
            noPrevFileDate.setText("Modified "+modDate);
        }

        }

    private void init(){
        noPrevFileName = findViewById(R.id.noPrevFileName);
        noPrevPath = findViewById(R.id.noPrevPath);
        noPrevFileSize = findViewById(R.id.noPrevFileSize);
        noPrevFileDate = findViewById(R.id.noPrevFileDate);
    }

}