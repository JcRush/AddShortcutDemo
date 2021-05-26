package com.example.addshortcutdemo;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.SHORTCUT_SERVICE;

public class ShortcutManage {
    public static void addShortcut(Context context, int drawableId, String route, String name) {
        if (hasShortcut(context, name) || hasShortCut1(context, name) || hasShortcutO(context, name)) {
            Toast.makeText(context,"已经有桌面快捷方式了", Toast.LENGTH_SHORT).show();
            return;
        }

        //Android o以上的版本用ShortcutManager来管理快捷方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager scm = (ShortcutManager) context.getSystemService(SHORTCUT_SERVICE);
            //设置路由（关联程序）
            Intent launcherIntent = new Intent(route);
            ShortcutInfo si = new ShortcutInfo.Builder(context, "tecentmap")
                    .setIcon(Icon.createWithResource(context, drawableId))
                    .setShortLabel(name)
                    .setIntent(launcherIntent)
                    .build();
            assert scm != null;
            boolean addSuccess = scm.requestPinShortcut(si, null);
            if(addSuccess) return;
        } else {
            //"com.android.launcher.action.INSTALL_SHORTCUT"
            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 不允许重复创建
            addShortcutIntent.putExtra("tecentmap", false);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            // 图标
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(context, drawableId));
            // 设置关联程序
            Intent launcherIntent = new Intent(route);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

            // 发送广播
            context.sendBroadcast(addShortcutIntent);
        }

    }

    public static boolean hasShortcut(Context context, String name) {
        boolean result = false;
        try {
            final String uriStr;
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) {
                uriStr = "content://com.android.launcher.settings/favorites?notify=true";
            } else if (sdkInt < 19) {
                uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
            } else {
                uriStr = "content://com.android.launcher3.settings/favorites?notify=true";
            }
            final Uri CONTENT_URI = Uri.parse(uriStr);
            final Cursor cursor = context.getContentResolver().query(CONTENT_URI, null,
                    "title=?", new String[]{name}, null);
            if (cursor != null && cursor.getCount() > 0) {
                result = true;
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;

    }

    private static boolean hasShortCut1(Context context, String name) {    //判断快捷键是否存在 方法一
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Uri
                                .parse("content://com.android.launcher.settings/favorites?notify=true"),
                        null, "title=?", new String[] {name}, null);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        return false;
    }

    protected static String getAuthorityFromPermission(Context context, String permission) {
        // 先得到默认的Launcher
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager mPackageManager = context.getPackageManager();
        ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
        if (resolveInfo == null) {
            return null;
        }
        @SuppressLint("WrongConstant") List<ProviderInfo> info = mPackageManager.queryContentProviders(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.applicationInfo.uid, PackageManager.GET_PROVIDERS);
        if (info != null) {
            for (int j = 0; j < info.size(); j++) {
                ProviderInfo provider = info.get(j);
                if (provider.readPermission == null) {
                    continue;
                }
                if (Pattern.matches(".*launcher.*READ_SETTINGS", provider.readPermission)) {
                    return provider.authority;
                }
            }
        }
        return null;

    }

    public static boolean hasShortcutO(Context context, String appName) {
        long start = System.currentTimeMillis();
        String authority = getAuthorityFromPermission(context, appName);
        if (authority == null) {
            return false;
        }
        long end = System.currentTimeMillis() - start;
        String url = "content://" + authority + "/favorites?notify=true";
        try {
            Uri CONTENT_URI = Uri.parse(url);
            Cursor c = context.getContentResolver().query(CONTENT_URI, null, " title= ? ", new String[]{appName}, null);
            if (c != null && c.moveToFirst()) {
                c.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

}
