package com.kee0kai.thekey.utils.views;

import android.app.Activity;
import android.text.SpannableString;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

import java.util.Objects;

public class ViewUtils {

    public static void changeTextIfNeed(TextView textView, String txt) {
        if (Objects.equals(textView.getText().toString(), txt))
            return;
        textView.setText(txt);
    }

    public static void changeTextIfNeed(TextView textView, SpannableString txt) {
        if (Objects.equals(textView.getText().toString(), txt.toString()))
            return;
        textView.setText(txt);
    }

    public static void changeSliderValueIfNeed(Slider slider, float value) {
        if (slider.getValue() != value)
            slider.setValue(value);
    }

    public static void changeCheckedIfNeed(CompoundButton compoundButton, boolean checked) {
        if (compoundButton.isChecked() != checked)
            compoundButton.setChecked(checked);
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
