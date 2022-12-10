package com.kee0kai.thekey.managers;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.kee0kai.thekey.App;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.utils.collections.WeakListCollection;

public class ActivitySecureManager implements LifecycleObserver {

    private final WeakListCollection<BaseActivity> openedActivities = new WeakListCollection<>();


    public ActivitySecureManager() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onPause(owner);
                for (BaseActivity activity : openedActivities.toList()) {
                    if (activity != null && activity.getSecType() == BaseActivity.SecureType.SECURE) {
                        activity.setResult(Activity.RESULT_CANCELED);
                        activity.finish();
                    }
                }
                openedActivities.clearNulls(null);
                App.DI.gcAllSoftRefs();
            }
        });
    }

    public void addActivity(BaseActivity activity) {
        openedActivities.add(activity);
    }


}
