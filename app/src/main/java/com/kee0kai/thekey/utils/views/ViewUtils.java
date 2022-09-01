package com.kee0kai.thekey.utils.views;

import android.app.slice.Slice;
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

    public static void changeSliderValueIfNeed(Slider slider, float value) {
        if (slider.getValue() != value)
            slider.setValue(value);
    }

    public static void changeCheckedIfNeed(CompoundButton compoundButton, boolean checked) {
        if (compoundButton.isChecked() != checked)
            compoundButton.setChecked(checked);
    }

}
