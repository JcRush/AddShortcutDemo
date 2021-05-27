package com.example.addshortcutdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tvPermission;
    private static final int PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button addShortcut = findViewById(R.id.add_btn);
        addShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShortcutManage.shortcutHigh(MainActivity.this, "腾讯乘车码") || ShortcutManage.hasShortcutLow(MainActivity.this, "腾讯乘车码")) {
                    //Toast.makeText(getApplicationContext(),"已经有桌面快捷方式了", Toast.LENGTH_SHORT).show();
                    dialogShow("已经有桌面快捷方式了", "通知");
                    return;
                }

                String[] permissions = {Manifest.permission.INSTALL_SHORTCUT};
                if(ContextCompat.checkSelfPermission(MainActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_REQUEST);
                }

                ShortcutManage.addShortcut(getApplicationContext(), R.mipmap.ic_launcher, Settings.ACTION_DATA_ROAMING_SETTINGS, "腾讯乘车码");
                Toast.makeText(getApplicationContext(),"尝试添加桌面快捷方式", Toast.LENGTH_SHORT).show();
            }
        });

        tvPermission = findViewById(R.id.tv_qualify);
        tvPermission.setText(getPermission());

        Button gotoermisson = findViewById(R.id.goto_btn);
        gotoermisson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuntimeSettingPage runtimeSettingPage = new RuntimeSettingPage(MainActivity.this);
                runtimeSettingPage.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvPermission.setText(getPermission());
    }

    private String getPermission() {
        int check = ShortcutManage.check(this);
        String state = "未知";
        switch (check) {
            case ShortcutManage.PERMISSION_DENIED:
                state = "已禁止";
                break;
            case ShortcutManage.PERMISSION_GRANTED:
                state = "已同意";
                break;
            case ShortcutManage.PERMISSION_ASK:
                state = "询问";
                break;
            case ShortcutManage.PERMISSION_UNKNOWN:
                state = "未知";
                break;
        }
        return state;
    }

    private void dialogShow(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setTitle(title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }



}