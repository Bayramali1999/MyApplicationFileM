package com.example.myapplication.tracker;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.FileVH;
import com.example.myapplication.data.FileModel;

public class MyFileLookup extends ItemDetailsLookup<FileModel> {

    private final RecyclerView recyclerView;

    public MyFileLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<FileModel> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof FileVH) {
                return ((FileVH) viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
