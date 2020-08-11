package com.example.filemanager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Holder extends RecyclerView.ViewHolder{
        public TextView mName;
        public ImageView mIcon;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mIcon=itemView.findViewById(R.id.mIcon);
            mName=itemView.findViewById(R.id.mName);
        }
    }