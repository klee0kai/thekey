package com.kee0kai.thekey.ui.common.views;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class InstantAutoCompleteTextView extends MaterialAutoCompleteTextView implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int SHOW_AUTOCOMPLETE_DELAY = 100;

    public InstantAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public InstantAutoCompleteTextView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        init();
    }

    public InstantAutoCompleteTextView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init();
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused && getAdapter() != null) {
            int sel = this.getSelectionEnd();
            setText(getText());
            this.setSelection(sel);
        }
    }


    @Override
    public void onClick(View v) {
        if (isFocused() && getAdapter() != null) {
            int sel = this.getSelectionEnd();
            setText(getText());
            this.setSelection(sel);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isFocused() && getAdapter() != null) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    setText(getText());
                    InstantAutoCompleteTextView.this.setSelection(getText().length());
                }
            }, SHOW_AUTOCOMPLETE_DELAY);

        }
    }

    private void init() {
        setOnClickListener(this);
        setOnItemClickListener(this);
    }

}
