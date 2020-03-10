package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.fragments.FileFragment;

public class MainActivity extends AppCompatActivity {
    private static final int READ_REQ_CODE = 313;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FileFragment fileFragment = new FileFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root, fileFragment)
                .commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        return true;
    }
}
