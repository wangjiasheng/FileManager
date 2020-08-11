package com.example.filemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private File[] files;
    private File currentFilePath=null;
    private FileAdapter mFileAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       if( ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},3000);
       }else {
           doSomeThing();
       }
    }
    public void doSomeThing(){
        File file=new File("/sdcard/");
        files=file.listFiles();
        recyclerView=findViewById(R.id.recyclerView);
        FocusFixedLinearLayoutManager manager=new FocusFixedLinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mFileAdapter=new FileAdapter();
        recyclerView.setAdapter(mFileAdapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            doSomeThing();
        }
    }

    class FileAdapter extends RecyclerView.Adapter<Holder>{

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
           View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.file_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            holder.mName.setText(files[position].getName());
            if(files[position]==null){
                return ;
            }
            if(files[position].isDirectory()){
                holder.mIcon.setImageResource(R.drawable.dir);
            }else if(files[position].isFile()){
               if(files[position].getAbsolutePath().endsWith(".mp4")){
                   Glide.with( MainActivity.this )
                           .load( Uri.fromFile( new File( files[position].getAbsolutePath() ) ) )
                           .into( holder.mIcon);
               }else{
                   holder.mIcon.setImageResource(R.drawable.re);
               }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if(files[position].isDirectory()){
                      currentFilePath=files[position];
                      files=currentFilePath.listFiles();
                      notifyDataSetChanged();
                  }else if(files[position].isFile()){
                      Toast.makeText(MainActivity.this,files[position].getAbsolutePath(),Toast.LENGTH_SHORT).show();
                  }
                }
            });
        }

        @Override
        public int getItemCount() {
            return files==null?0:files.length;
        }
    }


    @Override
    public void onBackPressed() {
        if(currentFilePath==null||currentFilePath.getAbsolutePath().equalsIgnoreCase("/sdcard")){
            super.onBackPressed();
        }else{
            currentFilePath=currentFilePath.getParentFile();
            files=currentFilePath.listFiles();
            mFileAdapter.notifyDataSetChanged();
        }
    }
}