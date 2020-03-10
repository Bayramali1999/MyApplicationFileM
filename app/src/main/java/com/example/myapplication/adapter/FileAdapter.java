package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.listener.FileItemClickListener;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileVH> {
    private List<File> files;
    private boolean showExtension;
    private FileItemClickListener listener;

    public FileAdapter(List<File> files, boolean b, FileItemClickListener clickListener) {
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
        File file = files.get(position);
        holder.onBind(file);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void updateData(List<File> myFiles) {
        files.clear();
        files.addAll(myFiles);
        sortByName();
    }

    public void sortByName() {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        notifyDataSetChanged();
    }

    public void sortByDate() {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (String.valueOf(o1.lastModified() / 1024))
                        .compareTo(String.valueOf(o2.lastModified() / 1024));
            }
        });
        notifyDataSetChanged();
    }

}
