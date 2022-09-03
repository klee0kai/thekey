package com.kee0kai.thekey.ui.fileprovider;

import static com.kee0kai.thekey.App.DI;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityFileproviderBinding;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.ui.fileprovider.model.FileItem;
import com.kee0kai.thekey.utils.adapter.CompositeAdapter;
import com.kee0kai.thekey.utils.android.UserShortPaths;
import com.kee0kai.thekey.utils.arch.IRefreshView;
import com.kee0kai.thekey.utils.views.ViewUtils;

import java.io.File;
import java.lang.ref.WeakReference;


public class FileProviderActivity extends BaseActivity implements IRefreshView, View.OnClickListener, FileAdapterDelegate.IFileAdapterListener {

    public static final String WORK_MODE_EXTRA = "md";

    private final FileProviderPresenter presenter = DI.presenter().fileProviderPresenter();
    private final CompositeAdapter adapter = CompositeAdapter.create(
            new FileAdapterDelegate(R.layout.item_file, this)
    );

    private WeakReference<View> dlgView = null;

    private ActivityFileproviderBinding binding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFileproviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.rvFilesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvFilesList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), RecyclerView.VERTICAL));
        binding.rvFilesList.setAdapter(adapter);
        presenter.subscribe(this);

        FileProviderPresenter.WorkMode workMode = FileProviderPresenter.WorkMode.values()[getIntent().getIntExtra(WORK_MODE_EXTRA, FileProviderPresenter.WorkMode.CREATE_STORAGE.ordinal())];
        presenter.init(workMode, savedInstanceState == null);

        binding.fdCreateStorage.setOnClickListener(this);
        binding.tvCurPath.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_only, menu);
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
    public void onBackPressed() {
        if (presenter.toUp()) return;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.fdCreateStorage) {
            new AlertDialog.Builder(this)
                    .setTitle(UserShortPaths.shortPathName(presenter.getCurPath().getAbsolutePath() + "/"))
                    .setView((dlgView = new WeakReference<>(LayoutInflater.from(this).inflate(R.layout.dlg_create_storage, null))).get())
                    .setPositiveButton(R.string.create, (dialog, which) -> {
                        View v1 = dlgView != null ? dlgView.get() : null;
                        if (v1 == null) return;
                        EditText edStorageName = v1.findViewById(R.id.ed_storage_name);
                        String storageName = edStorageName.getText().toString();
                        if (storageName.isEmpty()) {
                            Toast.makeText(FileProviderActivity.this, R.string.err_input_path, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent();
                        File stFile = new File(presenter.getCurPath().getAbsolutePath(), storageName);
                        intent.putExtra("path", stFile.getAbsolutePath());
                        intent.setData(Uri.fromFile(stFile));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show();

        } else if (v == binding.tvCurPath) {
            presenter.toUp();
        }
    }

    @Override
    public void onItemSelected(FileItem fileItem) {
        if (!fileItem.isFile)
            presenter.openDir(fileItem.name);
    }

    @Override
    public void refreshUI() {
        ViewUtils.changeTextIfNeed(binding.tvCurPath, UserShortPaths.shortPathName(presenter.getCurPath().getAbsolutePath()));
        presenter.popFlatListChanges().applyTo(adapter);
    }


}
