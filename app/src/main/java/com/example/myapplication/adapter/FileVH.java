package com.example.myapplication.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.listener.FileItemClickListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class FileVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView tvName;
    private TextView tvSpace;
    private TextView tvCreatedDate;
    private ImageView fileIc;
    private boolean showExtension;
    private View v;
    private TextView tvExtension;
    private File file;
    private FileItemClickListener listener;

    FileVH(@NonNull View itemView, boolean showExtension, FileItemClickListener listener) {
        super(itemView);
        this.listener = listener;
        this.tvName = itemView.findViewById(R.id.tv_file_name);
        this.tvSpace = itemView.findViewById(R.id.tv_file_size);
        this.tvCreatedDate = itemView.findViewById(R.id.tv_file_date);
        this.fileIc = itemView.findViewById(R.id.iv_file);
        this.showExtension = showExtension;
        this.v = itemView.findViewById(R.id.lv_another);
        this.tvExtension = itemView.findViewById(R.id.tv_extension);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.fileIsClicked(file);
    }

    void onBind(File file) {
        this.file = file;
        String name = file.getName();

        setFileSpace(file);
        setFileCreatedDate(file);
        setFileImage(name);
    }

    private void setFileImage(String name) {
        int dot = name.lastIndexOf(".");
        String fExt = name.substring(dot + 1);
        fileIc.setVisibility(View.VISIBLE);
        v.setVisibility(View.INVISIBLE);
        fileIc.setImageResource(R.drawable.ic_pdf);
//
//        if (fExt.length() > 4) {
//            tvExtension.setText("");
//        } else {
//            tvExtension.setText(fExt);
//        }

        if (showExtension) {
            bindDataWithExtension(file);
        } else {
            bindData(file);
        }
    }

    private void setFileCreatedDate(File file) {
        SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyy");
        Date date = new Date(file.lastModified());
        String modifiedDate = sd.format(date);
        tvCreatedDate.setText(modifiedDate);
    }

    private void setFileSpace(File file) {
        long space = Long.parseLong(String.valueOf(file.length() / 1024));
        if (space >= 1024) {
            long mbSpace = space / 1024;
            tvSpace.setText(mbSpace + " MB");
        } else {
            tvSpace.setText(space + " KB");
        }
    }

    private void bindData(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf(".");
        String fileName = name.substring(0, dot + 1);
        tvName.setText(fileName);
    }

    private void bindDataWithExtension(File file) {
        String fileName;
        String name = file.getName();
        if (name.length() > 40) {
            fileName = name.substring(0, 40) + "...";
        } else {
            fileName = file.getName();
        }
        tvName.setText(fileName);
    }

    private int setUpFileIc(String name) {
        String[] fileExtensions = new String[]{
                "doc", "docx", "ppt",
                "pptx", "xls", "xlsx", "pdf"
        };
        if (name.length() > 0) {
            return getFileExtensionsIndex(name, fileExtensions);
        } else {
            return R.drawable.ic_search;
        }
    }

    private int getFileExtensionsIndex(String extension, String[] fileExtensions) {
        return R.drawable.ic_pdf;
    }

}