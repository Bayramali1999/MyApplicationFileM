package com.example.myapplication.tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.example.myapplication.data.BaseModel;
import com.example.myapplication.data.FileModel;

import java.util.List;

public class MyFileKeyProvider extends ItemKeyProvider<BaseModel> {
    private final List<BaseModel> list;

    public MyFileKeyProvider(int scope, List<BaseModel> list) {
        super(scope);
        this.list = list;
    }

    @Nullable
    @Override
    public BaseModel getKey(int position) {
        return list.get(position);
    }

    @Override
    public int getPosition(@NonNull BaseModel key) {
        return list.indexOf(key);
    }
}
