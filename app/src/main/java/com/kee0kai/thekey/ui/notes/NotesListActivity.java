package com.kee0kai.thekey.ui.notes;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityNotesBinding;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.navig.activity_contracts.EditStorageActivityContract;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.ui.editstorage.EditStoragePresenter;
import com.kee0kai.thekey.ui.notes.gen.GenPasswFragment;
import com.kee0kai.thekey.ui.notes.notelist.NoteListFragment;


public class NotesListActivity extends BaseActivity {

    private final CryptStorageEngine engine = DI.engine().cryptEngine();
    private ActivityResultLauncher<EditStorageActivityContract.EditStorageTask> editStorageLauncher;

    private ActivityNotesBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editStorageLauncher = registerForActivityResult(new EditStorageActivityContract(), res -> {
        });

        binding.vpFragmentsContainer.setAdapter(new FragmentsAdapter(getSupportFragmentManager(), 0));
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notes, menu);
        return true;
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
        } else if (item.getItemId() == R.id.about_app) {

        }
        return super.onOptionsItemSelected(item);
    }

    public static class FragmentsAdapter extends FragmentPagerAdapter {

        private final String accountsPage, generationPage;

        public FragmentsAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            accountsPage = DI.app().application().getString(R.string.accounts);
            generationPage = DI.app().application().getString(R.string.generation);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return new NoteListFragment();
                }
                case 1: {
                    return new GenPasswFragment();
                }
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    return accountsPage;
                }
                case 1: {
                    return generationPage;
                }
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
