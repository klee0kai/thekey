package com.kee0kai.thekey.ui.storage;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ItemStorageBinding;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.utils.adapter.ClassAdapterDelegate;
import com.kee0kai.thekey.utils.adapter.SimpleViewHolder;
import com.kee0kai.thekey.utils.android.UserShortPaths;

import java.io.File;
import java.util.List;

public class StorageAdapterDelegate extends ClassAdapterDelegate<Storage> {

    public interface IStorageListener {
        void onStorageSelected(Storage selectedStorage);

        void onStorageDetailsClicked(Storage storage);

        void onStorageShareClicked(Storage storage);

        void onStorageEditClicked(Storage storage);

        void onStorageCopyClicked(Storage storage);

        void onStorageDeleteClicked(Storage storage);
    }

    private final IStorageListener listener;

    public StorageAdapterDelegate(int layoutRes, IStorageListener listener) {
        super(Storage.class, layoutRes);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(Storage storage, int pos, @NonNull SimpleViewHolder vh, @NonNull List<Object> allObjects, @NonNull List<Object> allOldlist) {
        ItemStorageBinding binding = ItemStorageBinding.bind(vh.itemView);

        binding.tvFilepath.setText(UserShortPaths.shortPathName(storage.path));
        boolean isExist = new File(storage.path).exists();
        binding.tvFilepath.setTextColor(vh.itemView.getResources().getColor(isExist ? android.R.color.primary_text_light : R.color.red));
        binding.tvStorageName.setText(storage.name);
        binding.tvStorageName.setVisibility(storage.name != null && !storage.name.isEmpty() ? View.VISIBLE : View.GONE);

        vh.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onStorageSelected(storage);
        });
        vh.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(vh.itemView.getContext(), vh.itemView);
            popupMenu.inflate(R.menu.popup_storages);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupMenu.setGravity(Gravity.RIGHT);
            }
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.popup_details) {
                    if (listener != null)
                        listener.onStorageDetailsClicked(storage);
                } else if (menuItem.getItemId() == R.id.popup_share) {
                    if (listener != null)
                        listener.onStorageShareClicked(storage);
                } else if (menuItem.getItemId() == R.id.popup_change) {
                    if (listener != null)
                        listener.onStorageEditClicked(storage);
                } else if (menuItem.getItemId() == R.id.popup_copy) {
                    if (listener != null)
                        listener.onStorageCopyClicked(storage);
                } else if (menuItem.getItemId() == R.id.popup_delete) {
                    if (listener != null)
                        listener.onStorageDeleteClicked(storage);
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
    }
}
