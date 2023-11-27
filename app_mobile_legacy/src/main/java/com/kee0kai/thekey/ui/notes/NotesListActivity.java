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

    private ActivityNotesBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.vpFragmentsContainer.setAdapter(new FragmentsAdapter(getSupportFragmentManager(), 0));
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
