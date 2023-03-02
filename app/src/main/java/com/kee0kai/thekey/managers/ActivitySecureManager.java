package com.kee0kai.thekey.managers;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.github.klee0kai.hummus.collections.weaklist.WeakList;
import com.kee0kai.thekey.App;
import com.kee0kai.thekey.ui.common.BaseActivity;

public class ActivitySecureManager implements LifecycleObserver {

    private final WeakList<BaseActivity> openedActivities = new WeakList<>();


    public ActivitySecureManager() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onPause(owner);
                for (BaseActivity activity : openedActivities) {
                    if (activity != null && activity.getSecType() == BaseActivity.SecureType.SECURE) {
                        activity.setResult(Activity.RESULT_CANCELED);
                        activity.finish();
                    }
                }
                openedActivities.clearNulls();
                App.DI.gcAllSoftRefs();
            }
        });
    }

    public void addActivity(BaseActivity activity) {
        openedActivities.add(activity);
    }


}
