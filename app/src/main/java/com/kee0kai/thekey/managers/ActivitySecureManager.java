package com.kee0kai.thekey.managers;

import android.app.Activity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.utils.collections.WeakListCollection;

public class ActivitySecureManager implements LifecycleObserver {

    private final WeakListCollection<BaseActivity> openedActivities = new WeakListCollection<>();


    public ActivitySecureManager() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void addActivity(BaseActivity activity) {
        openedActivities.add(activity);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        for (BaseActivity activity : openedActivities.toList()) {
            if (activity != null && activity.getSecType() == BaseActivity.SecureType.SECURE) {
                activity.setResult(Activity.RESULT_CANCELED);
                activity.finish();
            }
        }
        openedActivities.clearNulls(null);
    }


}
