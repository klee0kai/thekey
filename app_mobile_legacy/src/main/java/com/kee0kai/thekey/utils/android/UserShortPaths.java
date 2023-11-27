package com.kee0kai.thekey.utils.android;

import static com.kee0kai.thekey.App.DI;

import android.content.Context;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.utils.views.EmptyTextWatcher;

import java.io.File;
import java.io.IOException;

public class UserShortPaths {


    private static final String APPDATA = "appdata";
    private static final String PHONE_STORAGE = "phoneStorage";


    public static SpannableString shortPathName(String p) {
        String path = p;
        try {
            path = new File(p).getCanonicalPath();
        } catch (IOException ignore) {
        }
        Context context = DI.app().application();
        String appData = context.getApplicationInfo().dataDir;
        String phoneStorage = Environment.getExternalStorageDirectory().getAbsolutePath();

        SpannableString userPath = null;
        if (path.startsWith(appData) || p.startsWith(appData)) {
            String pp = path.startsWith(appData) ? path : p;
            userPath = new SpannableString(APPDATA + pp.substring(appData.length()));
            userPath.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, APPDATA.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return userPath;
        }
        if (path.startsWith(phoneStorage) || p.startsWith(phoneStorage)) {
            String pp = path.startsWith(phoneStorage) ? path : p;
            userPath = new SpannableString(PHONE_STORAGE + pp.substring(phoneStorage.length()));
            userPath.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, PHONE_STORAGE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return userPath;
        }


        return new SpannableString(path);
    }


    public static String absolutePath(String userShortPath) {
        if (userShortPath == null) return null;
        if (userShortPath.toLowerCase().startsWith(APPDATA.toLowerCase())) {
            return DI.app().application().getApplicationInfo().dataDir + userShortPath.substring(APPDATA.length());
        }
        if (userShortPath.toLowerCase().startsWith(PHONE_STORAGE.toLowerCase())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + userShortPath.substring(PHONE_STORAGE.length());
        }
        return userShortPath;
    }

    public static String[] getRootPaths(boolean showAsUsers) {
        Context context = DI.app().application();
        String roots[] = new String[]{
                DI.app().application().getApplicationInfo().dataDir,
                Environment.getExternalStorageDirectory().getAbsolutePath()
        };
        for (int i = 0; i < roots.length; i++) {
            roots[i] = showAsUsers ? shortPathName(roots[i]).toString() : roots[i];
        }
        return roots;

    }


    public static class ColoringUserPath extends EmptyTextWatcher {

        private Context context = DI.app().application();

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().toLowerCase().startsWith(APPDATA.toLowerCase())) {
                s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, APPDATA.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return;
            }
            if (s.toString().toLowerCase().startsWith(PHONE_STORAGE.toLowerCase())) {
                s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, PHONE_STORAGE.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return;
            }
            for (Object span : s.getSpans(0, s.length(), ForegroundColorSpan.class)) {
                s.removeSpan(span);
            }

        }
    }

}

