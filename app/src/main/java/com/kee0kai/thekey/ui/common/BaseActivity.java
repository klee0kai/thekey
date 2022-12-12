package com.kee0kai.thekey.ui.common;

import static com.kee0kai.thekey.App.DI;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.klee0kai.stone.AndroidStone;
import com.kee0kai.thekey.managers.ActivitySecureManager;

import javax.inject.Inject;

public class BaseActivity extends AppCompatActivity {

    @Inject
    public ActivitySecureManager activitySecureManager ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DI.inject(this, AndroidStone.lifeCycleOwner(getLifecycle()));
        if (getSecType() == SecureType.SECURE)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        activitySecureManager.addActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public SecureType getSecType() {
        return SecureType.SECURE;
    }

    public enum SecureType {
        SECURE,
        PUBLIC
    }

}
