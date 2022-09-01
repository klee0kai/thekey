package com.kee0kai.thekey.utils.adapter;

import androidx.annotation.NonNull;

import com.hannesdorfmann.adapterdelegates3.AdapterDelegate;
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.List;

public class CompositeAdapter extends ListDelegationAdapter<List<Object>> {

    public CompositeAdapter() {
    }

    public CompositeAdapter(@NonNull AdapterDelegatesManager<List<Object>> delegatesManager) {
        super(delegatesManager);
    }

    public static CompositeAdapter create(AdapterDelegate<List<Object>>... adapterDelegates) {
        AdapterDelegatesManager<List<Object>> manager = new AdapterDelegatesManager<>();
        for (AdapterDelegate<List<Object>> adapterDelegate : adapterDelegates)
            manager.addDelegate(adapterDelegate);
        return new CompositeAdapter(manager);
    }

}
