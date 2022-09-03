package com.kee0kai.thekey.ui.storage;

import static com.kee0kai.thekey.App.DI;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityStoragesBinding;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.navig.activity_contracts.EditStorageActivityContract;
import com.kee0kai.thekey.providers.StorageFileProvider;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.ui.editstorage.EditStoragePresenter;
import com.kee0kai.thekey.utils.adapter.CompositeAdapter;
import com.kee0kai.thekey.utils.arch.IRefreshView;

import java.io.File;
import java.util.List;

public class StoragesActivity extends BaseActivity implements IRefreshView, StorageAdapterDelegate.IStorageListener, View.OnClickListener {

    private final StoragesPresenter presenter = DI.presenter().storagesPresenter();

    private final ListDelegationAdapter<List<Object>> adapter = CompositeAdapter.create(
            new StorageAdapterDelegate(R.layout.item_storage, this)
    );

    private ActivityResultLauncher<EditStorageActivityContract.EditStorageTask> editStorageLauncher;

    private ActivityStoragesBinding binding;

    @Override
    public SecureType getSecType() {
        return SecureType.PUBLIC;
    }

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

        editStorageLauncher = registerForActivityResult(new EditStorageActivityContract(), result -> presenter.refreshData(false));
        binding.fdCreateStorage.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.refreshData(false);
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
        MenuItem searchItem = menu.findItem(R.id.search_storage);
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
        return true;
    }

    @Override
    public void onStorageSelected(Storage selectedStorage) {
        Intent resultIntent = new Intent();
        resultIntent.setData(Uri.fromFile(new File(selectedStorage.path)));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onStorageDetailsClicked(Storage storage) {
        editStorageLauncher.launch(new EditStorageActivityContract.EditStorageTask(storage.path, EditStoragePresenter.ChangeStorageMode.DETAILS));
    }

    @Override
    public void onStorageShareClicked(Storage storage) {
        File stFile = new File(storage.path);
        Uri fileUri = FileProvider.getUriForFile(this, StorageFileProvider.AUTHORITIES, stFile.getAbsoluteFile());
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setStream(fileUri)
                .setType("application/octet-stream")
                .getIntent()
                .setAction(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setClipData(ClipData.newUri(getContentResolver(), null, fileUri));
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_storage)));
    }

    @Override
    public void onStorageEditClicked(Storage storage) {
        editStorageLauncher.launch(new EditStorageActivityContract.EditStorageTask(storage.path, EditStoragePresenter.ChangeStorageMode.EDIT));
    }

    @Override
    public void onStorageCopyClicked(Storage storage) {
        editStorageLauncher.launch(new EditStorageActivityContract.EditStorageTask(storage.path, EditStoragePresenter.ChangeStorageMode.COPY));
    }

    @Override
    public void onStorageDeleteClicked(Storage storage) {
        presenter.delStorage(storage);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.fdCreateStorage) {
            editStorageLauncher.launch(null);
        }
    }

    @Override
    public void refreshUI() {
        binding.pbUpdatestorageProgress.setVisibility(presenter.refreshDateFuture.isInProcess() ? View.VISIBLE : View.GONE);
        presenter.popFlatListChanges().applyTo(adapter);
    }


}
