package com.kee0kai.thekey.ui.notes.notelist;

import android.os.Build;
import android.view.Gravity;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ItemAccountNoteBinding;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.ui.notes.model.NoteItem;
import com.kee0kai.thekey.utils.adapter.ClassAdapterDelegate;
import com.kee0kai.thekey.utils.adapter.SimpleViewHolder;

import java.util.List;

public class NoteAdapterDelegate extends ClassAdapterDelegate<NoteItem> {

    public interface INoteListener {
        void onClicked(NoteItem noteItem);

        void onDeleteClicked(NoteItem noteItem);
    }

    private final INoteListener listener;

    public NoteAdapterDelegate(int layoutRes, INoteListener listener) {
        super(NoteItem.class, layoutRes);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(NoteItem it, int pos, @NonNull SimpleViewHolder vh, @NonNull List<Object> allObjects, @NonNull List<Object> allOldlist) {
        ItemAccountNoteBinding binding = ItemAccountNoteBinding.bind(vh.itemView);
        binding.tvSite.setText(it.decryptedNote.site);
        binding.tvLogin.setText(it.decryptedNote.login);
        binding.tvDescription.setText(it.decryptedNote.desc);

        vh.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClicked(it);
        });
        vh.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(vh.itemView.getContext(), vh.itemView);
            popupMenu.inflate(R.menu.popup_notes);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                popupMenu.setGravity(Gravity.RIGHT);
            popupMenu.setOnMenuItemClickListener(m -> {
                if (m.getItemId() == R.id.popup_delete) {
                    if (listener != null)
                        listener.onDeleteClicked(it);
                    return true;
                }
                return false;
            });

            popupMenu.show();
            return true;
        });
    }
}
