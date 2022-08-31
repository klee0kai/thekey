package com.kee0kai.thekey.ui.storage;

import androidx.annotation.NonNull;

import com.kee0kai.thekey.databinding.ItemStorageBinding;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.utils.adapter.ClassAdapterDelegate;
import com.kee0kai.thekey.utils.adapter.SimpleViewHolder;

import java.util.List;

public class StorageAdapterDelegate extends ClassAdapterDelegate<Storage> {

    public interface IStorageListener {
        void onStorageSelected(Storage selectedStorage);
    }

    private final IStorageListener listener;

    public StorageAdapterDelegate(int layoutRes, IStorageListener listener) {
        super(Storage.class, layoutRes);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(Storage it, int pos, @NonNull SimpleViewHolder vh, @NonNull List<Object> allObjects, @NonNull List<Object> allOldlist) {
        ItemStorageBinding binding = ItemStorageBinding.bind(vh.itemView);


    }
}
