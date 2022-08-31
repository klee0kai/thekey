package com.kee0kai.thekey.utils.views;

import android.widget.TextView;

import java.util.Objects;

public class ViewUtils {

    public static void changeTextIfNeed(TextView textView, String txt) {
        if (Objects.equals(textView.getText().toString(), txt))
            return;
        textView.setText(txt);
    }

}
