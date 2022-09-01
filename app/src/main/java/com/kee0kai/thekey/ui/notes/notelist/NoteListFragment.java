package com.kee0kai.thekey.ui.notes.notelist;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kee0kai.thekey.databinding.FragmentNotesBinding;
import com.kee0kai.thekey.navig.InnerNavigator;

public class NoteListFragment extends Fragment {

    private final NoteListPresenter presenter = DI.presenter().noteListPresenter();
    private final InnerNavigator navigator = DI.control().innerNavigator();

    private FragmentNotesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


}
