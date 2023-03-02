package com.kee0kai.thekey.ui.fileprovider;

import static com.kee0kai.thekey.App.DI;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.github.klee0kai.hummus.adapterdelegates.delegate.simple.ClassAdapterDelegate;
import com.github.klee0kai.hummus.adapterdelegates.delegate.simple.SimpleViewHolder;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ItemFileBinding;
import com.kee0kai.thekey.ui.fileprovider.model.FileItem;

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
    protected void onBindViewHolder(FileItem it, int i, @NonNull SimpleViewHolder vh) {
        ItemFileBinding binding = ItemFileBinding.bind(vh.itemView);
        binding.ivIcon.setImageDrawable(it.isFile ? null : folderDrawable);
        binding.tvGroupHeader.setText(it.name);
        vh.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemSelected(it);
        });
    }
}
