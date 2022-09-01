package com.kee0kai.thekey.ui.fileprovider;

import static com.kee0kai.thekey.App.DI;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ItemFileBinding;
import com.kee0kai.thekey.ui.fileprovider.model.FileItem;
import com.kee0kai.thekey.utils.adapter.ClassAdapterDelegate;
import com.kee0kai.thekey.utils.adapter.SimpleViewHolder;

import java.util.List;

public class FileAdapterDelegate extends ClassAdapterDelegate<FileItem> {

    interface IFileAdapterListener {
        void onItemSelected(FileItem fileItem);
    }

    private final IFileAdapterListener listener;
    private final Drawable folderDrawable;

    public FileAdapterDelegate(int layoutRes, IFileAdapterListener listener) {
        super(FileItem.class, layoutRes);
        this.listener = listener;
        folderDrawable = DI.app().application().getResources().getDrawable(R.drawable.ic_folder_24px);
    }

    @Override
    protected void onBindViewHolder(FileItem it, int pos, @NonNull SimpleViewHolder vh, @NonNull List<Object> allObjects, @NonNull List<Object> allOldlist) {
        ItemFileBinding binding = ItemFileBinding.bind(vh.itemView);
        binding.ivIcon.setImageDrawable(it.isFile ? null : folderDrawable);
        binding.tvGroupHeader.setText(it.name);
        vh.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemSelected(it);
        });
    }
}
