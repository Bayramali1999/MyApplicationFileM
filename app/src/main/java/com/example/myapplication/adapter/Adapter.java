package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.BaseModel;
import com.example.myapplication.data.FileModel;
import com.example.myapplication.data.OtherDocModel;
import com.example.myapplication.listener.FileItemClickListener;
import com.example.myapplication.listener.OtherDocClickListener;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<BaseVH> {
    private List<BaseModel> list;
    private boolean showExtension;
    private FileItemClickListener listener;
    private SelectionTracker<BaseModel> selectionTracker;
    private static final int OTHER_DOC = 0;
    private static final int FILE = 1;
    private OtherDocClickListener otherDocClickListener;

    public Adapter(List<BaseModel> files, boolean b,
                   FileItemClickListener clickListener,
                   OtherDocClickListener otherDocClickListener) {
        this.list = files;
        this.showExtension = b;
        this.listener = clickListener;
        this.otherDocClickListener = otherDocClickListener;
    }

    @NonNull
    @Override
    public BaseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FILE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
            return new FileVH(view, showExtension, listener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_doc_item, parent, false);
            return new OtherDocVH(view, otherDocClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseModel item = list.get(position);
        if (item instanceof OtherDocModel) {
            return OTHER_DOC;
        } else {
            return FILE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseVH holder, int position) {
        BaseModel file = list.get(position);
        if (file instanceof FileModel) {
            boolean isSelected = selectionTracker.isSelected((FileModel) file);
            holder.onBind(file, isSelected);

        } else {
            holder.onBind(file, true);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setSelectionTracker(SelectionTracker<BaseModel> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }
}
