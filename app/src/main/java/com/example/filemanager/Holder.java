package com.example.filemanager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder{
        public mTextView mName;
        public ImageView mIcon;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mIcon=itemView.findViewById(R.id.mIcon);
            mName=itemView.findViewById(R.id.mName);
            mIcon.setRotation(270);
            mIcon.setRotation(270);
           // mName.setRotation(270);
        }
    }