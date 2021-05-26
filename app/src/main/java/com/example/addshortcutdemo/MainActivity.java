package com.example.addshortcutdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addShortcut = findViewById(R.id.add_btn);

        addShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortcutManage.addShortcut(getApplicationContext(), R.mipmap.ic_launcher, Settings.ACTION_DATA_ROAMING_SETTINGS, "腾讯乘车码");
                //gotoAppDetailIntent();
            }
        });
    }



}