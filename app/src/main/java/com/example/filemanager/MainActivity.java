package com.example.filemanager;

import androidx.annotation.IntegerRes;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import droidninja.filepicker.FilePickerBuilder;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private File[] files;
    private File currentFilePath=null;
    private FileAdapter mFileAdapter;
    private int lastPosition=-1;
    private int lastOffset=-1;
    private int currentFocusItem=-1;
    private int lastFocusItem=-1;
    private Stack<Integer> stack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stack=new Stack<>();
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
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        mFileAdapter=new FileAdapter();
        recyclerView.setAdapter(mFileAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.getLayoutManager() != null) {
                    getPositionAndOffset();
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
    /**
     * 记录RecyclerView当前位置
     */
    private void getPositionAndOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if(topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = layoutManager.getPosition(topView);
        }
    }
    private void scrollToPosition() {
        if(recyclerView.getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            doSomeThing();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_DOWN:
                try {
                    Field mKeyCodeUpField=KeyEvent.class.getDeclaredField("mKeyCode");
                    mKeyCodeUpField.setAccessible(true);
                    mKeyCodeUpField.set(event,KeyEvent.KEYCODE_DPAD_RIGHT);
                    return super.dispatchKeyEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                try {
                    Field mKeyCodeUpField=KeyEvent.class.getDeclaredField("mKeyCode");
                    mKeyCodeUpField.setAccessible(true);
                    mKeyCodeUpField.set(event,KeyEvent.KEYCODE_DPAD_LEFT);
                    return super.dispatchKeyEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(event.getAction()==KeyEvent.ACTION_UP){
                    exitFileOrDir();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(event.getAction()==KeyEvent.ACTION_UP){
                    openFileOrDir();
                }
                return true;
        }
        return super.dispatchKeyEvent(event);
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
            holder.itemView.setTag(position);
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
                    openFileOrDir(position);
                }
            });
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        currentFocusItem=position;
                    }
                }
            });
            if(lastFocusItem!=-1&&lastFocusItem==position){
                holder.itemView.requestFocus();
            }
        }

        @Override
        public int getItemCount() {
            return files==null?0:files.length;
        }
    }


    @Override
    public void onBackPressed() {
        if(exitFileOrDir()){
           return;
        }
        super.onBackPressed();
    }
    public void openFileOrDir(int position){
        stack.push(position);
        if(files[position].isDirectory()){
            currentFilePath=files[position];
            files=currentFilePath.listFiles();
            currentFocusItem=-1;
            mFileAdapter.notifyDataSetChanged();
        }else if(files[position].isFile()){
            Toast.makeText(MainActivity.this,files[position].getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
    }
    public void openFileOrDir(){
        if(currentFocusItem>=0){//当下一级目录没有东西的时候index为默认-1，按右键会触发此方法,会发生空指针错误
            openFileOrDir(currentFocusItem);
        }
    }
    public boolean exitFileOrDir(){
        if(currentFilePath!=null&&!currentFilePath.getAbsolutePath().equalsIgnoreCase("/sdcard")){
            currentFilePath=currentFilePath.getParentFile();
            files=currentFilePath.listFiles();
            mFileAdapter.notifyDataSetChanged();
            scrollToPosition();
            lastFocusItem=stack.pop();
            return true;
        }
        return false;
    }
}