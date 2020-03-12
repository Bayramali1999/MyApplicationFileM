package com.example.myapplication.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.BaseModel;
import com.example.myapplication.listener.FileItemClickListener;

public abstract class BaseVH extends RecyclerView.ViewHolder {

    public BaseVH(@NonNull View itemView) {
        super(itemView);
    }

    abstract void onBind(BaseModel baseModel, boolean selected);

}
