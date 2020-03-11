package com.example.myapplication.listener;

import com.example.myapplication.data.FileModel;

import java.io.File;
import java.util.ArrayList;

public interface FileItemClickListener {
    void fileIsClicked(ArrayList<FileModel> file);
}
