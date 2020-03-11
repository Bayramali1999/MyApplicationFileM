package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.FileModel;
import com.example.myapplication.listener.FileItemClickListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileVH> {
    private List<FileModel> files;
    private boolean showExtension;
    private FileItemClickListener listener;
    private SelectionTracker<FileModel> selectionTracker;

    public FileAdapter(List<FileModel> files, boolean b, FileItemClickListener clickListener) {
        this.files = files;
        this.showExtension = b;
        this.listener = clickListener;
    }

    @NonNull
    @Override
    public FileVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FileVH(view, showExtension, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FileVH holder, int position) {
        FileModel file = files.get(position);
        holder.onBind(file, selectionTracker.isSelected(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateData(List<FileModel> myFiles) {
        files.clear();
        files.addAll(myFiles);
        sortByName();
    }

    public void sortByName() {
        Collections.sort(files, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel o1, FileModel o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        notifyDataSetChanged();
    }

    public void sortByDate() {
        Collections.sort(files, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel o1, FileModel o2) {
                return (String.valueOf(o1.getLastModified() / 1024))
                        .compareTo(String.valueOf(o2.getLastModified() / 1024));
            }
        });
        notifyDataSetChanged();
    }

    public void setSelectionTracker(SelectionTracker<FileModel> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

}
