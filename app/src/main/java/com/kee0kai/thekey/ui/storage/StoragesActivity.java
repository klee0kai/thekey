package com.kee0kai.thekey.ui.storage;

import static com.kee0kai.thekey.App.DI;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityStoragesBinding;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.navig.activity_contracts.CreateStorageActivityContract;
import com.kee0kai.thekey.utils.adapter.CompositeAdapter;
import com.kee0kai.thekey.utils.arch.IRefreshView;

import java.util.List;

public class StoragesActivity extends AppCompatActivity implements IRefreshView, StorageAdapterDelegate.IStorageListener, View.OnClickListener {

    private final StoragesPresenter presenter = DI.presenter().storagesPresenter();

    private final ListDelegationAdapter<List<Object>> adapter = CompositeAdapter.create(
            new StorageAdapterDelegate(R.layout.activity_storages, this)
    );

    private ActivityResultLauncher<CreateStorageActivityContract.CreateStorageTask> createStorageLauncher;

    private ActivityStoragesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoragesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.rvStorages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStorages.setAdapter(adapter);
        presenter.subscribe(this);

        createStorageLauncher = registerForActivityResult(new CreateStorageActivityContract(), result -> presenter.refreshData());
        binding.fdCreateStorage.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_storages, menu);
        return true;
    }

    @Override
    public void onStorageSelected(Storage selectedStorage) {

    }

    @Override
    public void onClick(View view) {
        if (view == binding.fdCreateStorage) {
            createStorageLauncher.launch(null);
        }
    }

    @Override
    public void refreshUI() {

    }


}
