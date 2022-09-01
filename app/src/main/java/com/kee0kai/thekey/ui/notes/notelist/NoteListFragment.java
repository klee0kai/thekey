package com.kee0kai.thekey.ui.notes.notelist;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.FragmentNotesBinding;
import com.kee0kai.thekey.navig.InnerNavigator;
import com.kee0kai.thekey.ui.notes.model.NoteItem;
import com.kee0kai.thekey.utils.adapter.CompositeAdapter;
import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.SimpleDiffResult;
import com.kee0kai.thekey.utils.arch.IRefreshView;

public class NoteListFragment extends Fragment implements IRefreshView, View.OnClickListener, NoteAdapterDelegate.INoteListener {

    private final NoteListPresenter presenter = DI.presenter().noteListPresenter();
    private final InnerNavigator navigator = DI.control().innerNavigator();

    private final CompositeAdapter adapter = CompositeAdapter.create(
            new NoteAdapterDelegate(R.layout.item_account_note, this)
    );
    private FragmentNotesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        binding.rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotes.setAdapter(adapter);
        presenter.subscribe(this);
        presenter.init(savedInstanceState == null);
        binding.fdCreateEntry.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unsubscribe(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.fdCreateEntry) {
            startActivity(navigator.note(0));
        }
    }


    @Override
    public void onClicked(NoteItem noteItem) {
        startActivity(navigator.note(noteItem.id));
    }

    @Override
    public void onDeleteClicked(NoteItem noteItem) {
        presenter.delNote(noteItem.id);
    }

    @Override
    public void refreshUI() {
        presenter.popFlatListChanges().applyTo(adapter);
    }


}
