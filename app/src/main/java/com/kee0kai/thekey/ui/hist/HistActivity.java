package com.kee0kai.thekey.ui.hist;

import static com.kee0kai.thekey.App.DI;
import static com.kee0kai.thekey.ui.note.NoteActivity.NOTE_PTR_EXTRA;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityHistoryBinding;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.utils.adapter.CompositeAdapter;
import com.kee0kai.thekey.utils.arch.IRefreshView;

import java.util.List;


public class HistActivity extends BaseActivity implements IRefreshView {


    private final HistPresenter presenter = DI.presenter().histPresenter();

    private final ListDelegationAdapter<List<Object>> adapter = CompositeAdapter.create(
            new PasswAdapterDelegate(R.layout.item_passw)
    );

    private ActivityHistoryBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.rvPasswds.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPasswds.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.rvPasswds.setAdapter(adapter);

        presenter.subscribe(this);
        presenter.init(getIntent().getLongExtra(NOTE_PTR_EXTRA, 0),
                savedInstanceState == null);
        presenter.refreshData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }

    @Override
    public void refreshUI() {
        presenter.popFlatListChanges().applyTo(adapter);
    }
}
