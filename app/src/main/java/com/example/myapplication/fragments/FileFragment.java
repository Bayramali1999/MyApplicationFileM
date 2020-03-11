package com.example.myapplication.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.FileAdapter;
import com.example.myapplication.data.FileModel;
import com.example.myapplication.listener.FileItemClickListener;
import com.example.myapplication.tracker.MyFileKeyProvider;
import com.example.myapplication.tracker.MyFileLookup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment implements ActionMode.Callback {
    private static final int READ_REQ_CODE = 313;
    private RecyclerView rv;
    private FileAdapter fileAdapter;
    private List<FileModel> list;
    private FileItemClickListener clickListener;
    private SelectionTracker<FileModel> selectionTracker;
    private ActionMode actionMode = null;
    private List<FileModel> sendFiles = new ArrayList<>();
    private List<FileModel> originList = new ArrayList<>();
    public SearchView sv = null;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_file, container, false);
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
            public void fileIsClicked(ArrayList<FileModel> file) {
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

                builder.setTitle("Send '" + file.get(0).getName() + "' to ... ?");
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
        setUpSelectionTracker();
        fileAdapter.setSelectionTracker(selectionTracker);
        trackerAddObserver();
        givePermission();
    }

    private void trackerAddObserver() {
        selectionTracker.addObserver(
                new SelectionTracker.SelectionObserver<FileModel>() {
                    @Override
                    public void onSelectionChanged() {
                        super.onSelectionChanged();
                        if (selectionTracker.hasSelection() && actionMode == null) {
                            MainActivity activity = (MainActivity) getActivity();
                            actionMode = activity.startSupportActionMode(FileFragment.this);
                        } else if (!selectionTracker.hasSelection() && actionMode != null) {
                            actionMode.finish();
                            actionMode = null;
                        }
                        if (actionMode != null) {
                            actionMode.setTitle(selectionTracker.getSelection().size() + " selected");
                        }
                    }

                    @Override
                    public void onItemStateChanged(@NonNull FileModel key, boolean selected) {
                        super.onItemStateChanged(key, selected);
                        if (selected) {
                            sendFiles.add(key);
                        } else {
                            sendFiles.remove(key);
                        }
                    }
                }
        );
    }


    private void setUpSelectionTracker() {
        selectionTracker = new SelectionTracker.Builder<FileModel>(
                "my-selection-id",
                rv,
                new MyFileKeyProvider(1, list),
                new MyFileLookup(rv),
                StorageStrategy.createParcelableStorage(FileModel.class)
        ).withOnItemActivatedListener(new OnItemActivatedListener<FileModel>() {
            @Override
            public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<FileModel> item, @NonNull MotionEvent e) {
                return true;
            }
        }).withOnDragInitiatedListener(new OnDragInitiatedListener() {
            @Override
            public boolean onDragInitiated(@NonNull MotionEvent e) {
                return true;
            }

        }).build();
    }

    private void setArrowButtonOnToolbar() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        final List<FileModel> myFiles = new ArrayList<>();
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
        originList.addAll(myFiles);
    }

    private List<FileModel> readFileList(File fDown) {
        List<FileModel> myFiles = new ArrayList<>();

        if (fDown.exists()) {
            File[] files = fDown.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    myFiles.add(new FileModel(
                            file.getName(),
                            file.getPath(),
                            file.lastModified(),
                            file.length()));
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
        sv = (SearchView) itemSearch.getActionView();
        sv.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
                    for (FileModel file : originList) {
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

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.send) {
            actionMode.finish();
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionTracker.clearSelection();
    }

}

