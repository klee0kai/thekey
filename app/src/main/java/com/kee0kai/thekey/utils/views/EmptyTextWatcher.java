package com.kee0kai.thekey.utils.views;

import android.text.Editable;
import android.text.TextWatcher;

public class EmptyTextWatcher implements TextWatcher {

    public boolean ignoreChanges = false;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
