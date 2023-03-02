package com.kee0kai.thekey.ui.fileprovider;

import static com.kee0kai.thekey.App.DI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.klee0kai.hummus.adapterdelegates.CompositeAdapter;
import com.github.klee0kai.stone.AndroidStone;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityFileproviderBinding;
import com.kee0kai.thekey.databinding.DlgCreateStorageBinding;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.ui.dialogs.CreateFileDialogFragment;
import com.kee0kai.thekey.ui.fileprovider.model.FileItem;
import com.kee0kai.thekey.utils.android.UserShortPaths;
import com.kee0kai.thekey.utils.arch.IRefreshView;
import com.kee0kai.thekey.utils.views.ViewUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import javax.inject.Inject;


public class FileProviderActivity extends BaseActivity implements IRefreshView, View.OnClickListener, FileAdapterDelegate.IFileAdapterListener, CreateFileDialogFragment.ICreateFileListener {

    public static final String WORK_MODE_EXTRA = "md";
    private static final String INPUT_FLNAME_DLG_TAG = "flname_dlg";

    @Inject
    public FileProviderPresenter presenter;

    private final CompositeAdapter adapter = CompositeAdapter.create(
            new FileAdapterDelegate(R.layout.item_file, this)
    );

    private WeakReference<DlgCreateStorageBinding> dlgBinding = null;
    private ActivityFileproviderBinding binding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DI.inject(this, AndroidStone.lifeCycleOwner(getLifecycle()));
        binding = ActivityFileproviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.rvFilesList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvFilesList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), RecyclerView.VERTICAL));
        binding.rvFilesList.setAdapter(adapter);
        presenter.subscribe(this);

        String fileType = getIntent().getType();
        FileProviderPresenter.WorkMode workMode = FileProviderPresenter.WorkMode.values()[getIntent().getIntExtra(WORK_MODE_EXTRA, FileProviderPresenter.WorkMode.CREATE_FILE.ordinal())];
        presenter.init(workMode, fileType, savedInstanceState == null);

        binding.fdCreateStorage.setOnClickListener(this);
        binding.tvCurPath.setOnClickListener(this);
        Fragment fr = getSupportFragmentManager().findFragmentByTag(INPUT_FLNAME_DLG_TAG);
        if (fr instanceof CreateFileDialogFragment)
            ((CreateFileDialogFragment) fr).setListener(this);
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
            dlgBinding = new WeakReference<>(DlgCreateStorageBinding.inflate(LayoutInflater.from(this)));
            dlgBinding.get().tvTkeyFormat.setText(presenter.getFileType());
            CreateFileDialogFragment dlg = CreateFileDialogFragment.newInstance(new CreateFileDialogFragment.CreateFileDlgArgs(
                    presenter.getCurPath().getAbsolutePath(), presenter.getFileType()
            ));
            dlg.setListener(this);
            dlg.show(getSupportFragmentManager(), INPUT_FLNAME_DLG_TAG);
        } else if (v == binding.tvCurPath) {
            presenter.toUp();
        }
    }

    @Override
    public void onItemSelected(FileItem fileItem) {
        if (!fileItem.isFile) {
            presenter.openDir(fileItem.name);
            return;
        }
        if (presenter.getMode() == FileProviderPresenter.WorkMode.OPEN_FILE) {
            Intent resultIntent = new Intent();
            File f = new File(presenter.getCurPath().getAbsolutePath(), fileItem.name);
            resultIntent.putExtra("path", f.getAbsolutePath());
            resultIntent.setData(Uri.fromFile(f));
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void onCreateFileDone(CreateFileDialogFragment dlg, File f) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("path", f.getAbsolutePath());
        resultIntent.setData(Uri.fromFile(f));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void refreshUI() {
        boolean isCreateMode = presenter.getMode() == FileProviderPresenter.WorkMode.CREATE_FILE;
        setTitle(isCreateMode ? R.string.storage_create : R.string.select_storage);
        binding.fdCreateStorage.setVisibility(isCreateMode ? View.VISIBLE : View.GONE);
        ViewUtils.changeTextIfNeed(binding.tvCurPath, UserShortPaths.shortPathName(presenter.getCurPath().getAbsolutePath()));
        presenter.popFlatListChanges().applyTo(adapter);
    }


}
