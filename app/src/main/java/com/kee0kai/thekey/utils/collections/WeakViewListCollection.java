package com.kee0kai.thekey.utils.collections;

import com.kee0kai.thekey.utils.arch.IRefreshView;
import com.kee0kai.thekey.utils.arch.Threads;

public class WeakViewListCollection extends WeakListCollection<IRefreshView>{

    public void refreshAllViews() {
        Threads.runMain(() -> {
            for (IRefreshView v : toList())
                v.refreshUI();
        });
    }

    /**
     * уведомить view слушателей с задержкой, для корректной проверки isLoading
     */
    public void refreshAllViews(int delay) {
        Threads.runMainDelayed(() -> {
            for (IRefreshView v : toList())
                v.refreshUI();
        }, delay);
    }

}
