package com.example.myapplication.fragments;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.FileAdapter;
import com.example.myapplication.listener.FileItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {
    private static final int READ_REQ_CODE = 313;
    private RecyclerView rv;
    private FileAdapter fileAdapter;
    private List<File> list;
    private FileItemClickListener clickListener;
    private ActionMode.Callback actionCallback;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_file, container, false);
        setUpActionCallback();
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_sort);
        MainActivity activity = (MainActivity) getActivity();
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(drawable);
        setUpListener();
        list = new ArrayList<>();
        fileAdapter = new FileAdapter(list, true, clickListener);
        rv = view.findViewById(R.id.rv_list);
        return view;
    }

    private void setUpListener() {
        clickListener = new FileItemClickListener() {
            @Override
            public void fileIsClicked(File file) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                builder.setTitle("Send '" + file.getName() + "' to ... ?");
                builder.create();
                builder.show();
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setArrowButtonOnToolbar();
        setHasOptionsMenu(true);
        rv.setAdapter(fileAdapter);
        givePermission();
    }

    private void setArrowButtonOnToolbar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpActionCallback() {
        actionCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
                mode.setTitle("Selected");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                //.. click item
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        };
    }

    private void givePermission() {
        String[] permissions = new String[1];
        permissions[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission
                    (getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                readDataFromStorage();
            } else {
                requestPermissions(permissions, READ_REQ_CODE);
            }
        } else {
            readDataFromStorage();
        }
    }


    private void readDataFromStorage() {
        final List<File> myFiles = new ArrayList<>();
        String download = Environment.getExternalStorageDirectory().toString() + "/Download";
        String document = Environment.getExternalStorageDirectory().toString() + "/Documents";
        String root = Environment.getExternalStorageDirectory().toString();
        File fDown = new File(download);
        File fDoc = new File(document);
        File fRoot = new File(root);

        myFiles.addAll(readFileList(fDown));
        myFiles.addAll(myFiles.size(), readFileList(fDoc));
        myFiles.addAll(myFiles.size(), readFileList(fRoot));

        fileAdapter.updateData(myFiles);
    }

    private List<File> readFileList(File fDown) {
        List<File> myFiles = new ArrayList<>();

        if (fDown.exists()) {
            File[] files = fDown.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    myFiles.add(file);
                }
            }
        }
        return myFiles;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ_CODE && grantResults.length != 0) {
            readDataFromStorage();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.sort_by_name);
        item.setChecked(true);
        MenuItem itemSearch = menu.findItem(R.id.search_text);
        SearchView sv;
        sv = (SearchView) itemSearch.getActionView();
        searchFileByName(sv);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_text) {
            return true;
        } else if (id == R.id.sort_by_name) {
            fileAdapter.sortByName();
            item.setChecked(true);
            return true;
        } else if (id == R.id.sort_by_date) {
            fileAdapter.sortByDate();
            item.setChecked(true);
            return true;
        }
        return false;
    }

    private void searchFileByName(SearchView searchView) {
        List<File> originList = new ArrayList<>();
        originList.addAll(list);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list.clear();
                if (newText.isEmpty()) {
                    list.addAll(originList);
                    fileAdapter.notifyDataSetChanged();
                } else {
                    for (File file : originList) {
                        String name = file.getName().toLowerCase();
                        String text = newText.toLowerCase();
                        if (name.contains(text)) {
                            list.add(file);
                        }
                    }
                    fileAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }
}

