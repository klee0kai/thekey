package com.kee0kai.thekey.ui.notes.notelist;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.klee0kai.stone.AndroidStone;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.FragmentNotesBinding;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.navig.InnerNavigator;
import com.kee0kai.thekey.navig.activity_contracts.EditStorageActivityContract;
import com.kee0kai.thekey.ui.dialogs.AcceptDialogFragment;
import com.kee0kai.thekey.ui.editstorage.EditStoragePresenter;
import com.kee0kai.thekey.ui.notes.model.NoteItem;
import com.kee0kai.thekey.utils.adapter.CompositeAdapter;
import com.kee0kai.thekey.utils.arch.IRefreshView;

import javax.inject.Inject;

public class NoteListFragment extends Fragment implements IRefreshView, View.OnClickListener, NoteAdapterDelegate.INoteListener, AcceptDialogFragment.IAcceptListener {

    private static final String DEL_ACCEPT_DLG_TAG = "del_note_dlg";

    @Inject
    public NoteListPresenter presenter;
    @Inject
    public InnerNavigator navigator;
    @Inject
    public CryptStorageEngine engine;


    private final CompositeAdapter adapter = CompositeAdapter.create(
            new NoteAdapterDelegate(R.layout.item_account_note, this)
    );
    private ActivityResultLauncher<EditStorageActivityContract.EditStorageTask> editStorageLauncher;

    private FragmentNotesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        DI.inject(this, AndroidStone.lifeCycleOwner(getLifecycle()));
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        binding.rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotes.setAdapter(adapter);
        presenter.subscribe(this);
        presenter.init(savedInstanceState == null);
        binding.fdCreateEntry.setOnClickListener(this);
        editStorageLauncher = registerForActivityResult(new EditStorageActivityContract(), res -> {
        });
        Fragment fr = getChildFragmentManager().findFragmentByTag(DEL_ACCEPT_DLG_TAG);
        if (fr instanceof AcceptDialogFragment)
            ((AcceptDialogFragment) fr).setListener(this);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notes, menu);
        MenuItem searchItem = menu.findItem(R.id.search_note);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (!TextUtils.isEmpty(presenter.getSearchQuery())) {
            searchItem.expandActionView();
            searchView.setQuery(presenter.getSearchQuery(), true);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.search(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about_storage) {
            editStorageLauncher.launch(new EditStorageActivityContract.EditStorageTask(
                    engine.getLoggedStoragePath(), EditStoragePresenter.ChangeStorageMode.DETAILS
            ));
        } else if (item.getItemId() == R.id.edit_storage) {
            editStorageLauncher.launch(new EditStorageActivityContract.EditStorageTask(
                    engine.getLoggedStoragePath(), EditStoragePresenter.ChangeStorageMode.EDIT_LOGGED_STORAGE
            ));
        }
        return super.onOptionsItemSelected(item);
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
        presenter.setDeletingPtNote(noteItem.id);
        AcceptDialogFragment dlg = AcceptDialogFragment.newInstance(new AcceptDialogFragment.AcceptDialogArgs(
                getString(R.string.del_note_title),
                getString(R.string.del_note_message)
        ));
        dlg.setListener(this);
        dlg.show(getChildFragmentManager(), DEL_ACCEPT_DLG_TAG);
    }

    @Override
    public void onAcceptDlgDone(AcceptDialogFragment dlg, boolean accept) {
        presenter.delNote(accept);
    }

    @Override
    public void refreshUI() {
        presenter.popFlatListChanges().applyTo(adapter);
    }


}
