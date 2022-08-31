package com.kee0kai.thekey.utils.adapter;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.List;

public class CompositeAdapter {

    public static ListDelegationAdapter<List<Object>> create(AdapterDelegate<List<Object>>... adapterDelegates) {
        AdapterDelegatesManager<List<Object>> manager = new AdapterDelegatesManager<>();
        for (AdapterDelegate<List<Object>> adapterDelegate : adapterDelegates)
            manager.addDelegate(adapterDelegate);
        return new ListDelegationAdapter<>(manager);
    }

}
