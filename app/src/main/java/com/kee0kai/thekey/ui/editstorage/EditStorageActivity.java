package com.kee0kai.thekey.ui.editstorage;

import static com.kee0kai.thekey.App.DI;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityStorageCreateBinding;
import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.navig.activity_contracts.EditStorageActivityContract;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.utils.android.UserShortPaths;
import com.kee0kai.thekey.utils.arch.IRefreshView;
import com.kee0kai.thekey.utils.views.EmptyTextWatcher;
import com.kee0kai.thekey.utils.views.ViewUtils;

public class EditStorageActivity extends BaseActivity implements IRefreshView, View.OnFocusChangeListener, View.OnClickListener {

    private final EditStoragePresenter presenter = DI.presenter().editStoragePresenter();

    private ActivityStorageCreateBinding binding;

    private final EmptyTextWatcher tvWatcherUpdate = new EmptyTextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            if (!ignoreChanges) cacheInput();
        }
    };

    @Override
    public SecureType getSecType() {
        return SecureType.PUBLIC;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStorageCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.edStoragePath.setAdapter(new PathAutoCompleteAdapter());
        binding.edStoragePath.addTextChangedListener(new UserShortPaths.ColoringUserPath());
        binding.edStoragePath.setOnFocusChangeListener(this);
        binding.edStoragePath.addTextChangedListener(tvWatcherUpdate);
        binding.edStorageDescription.addTextChangedListener(tvWatcherUpdate);
        binding.edStorageName.addTextChangedListener(tvWatcherUpdate);
        binding.edStoragePassw.addTextChangedListener(tvWatcherUpdate);

        presenter.subscribe(this);

        EditStorageActivityContract.CreateStorageTask createStorageTask = getIntent().getParcelableExtra(EditStorageActivityContract.CHANGE_TASK_EXTRA);
        if (createStorageTask != null)
            presenter.init(createStorageTask.storagePath, createStorageTask.mode);
        else presenter.init(null, null);

        binding.btSave.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void onClick(View view) {
        String passw = binding.edStoragePassw.getText() != null ? binding.edStoragePassw.getText().toString() : "";
        presenter.save(passw);
    }

    @Override
    public void refreshUI() {

        int successRes = R.string.success_create_storage;
        int errRes = R.string.err_create_storage;
        switch (presenter.getMode()) {
            case CREATE:
                binding.toolbar.setTitle(R.string.storage_create);
                binding.btSave.setText(R.string.bt_save);
                binding.tlStoragePassw.setVisibility(View.GONE);
                binding.vPasswDivider.setVisibility(View.GONE);
                successRes = R.string.success_create_storage;
                errRes = R.string.err_create_storage;
                break;
            case DETAILS:
                binding.toolbar.setTitle(R.string.storage_details);
                binding.edStoragePath.setFocusable(false);
                binding.edStorageName.setFocusable(false);
                binding.edStorageDescription.setFocusable(false);
                binding.tlStoragePassw.setVisibility(View.GONE);
                binding.vPasswDivider.setVisibility(View.GONE);
            case CHANGE:
                binding.edStoragePath.setFocusable(false);
                binding.toolbar.setTitle(R.string.popup_change);
                binding.btSave.setText(R.string.popup_change);
                binding.tlStoragePassw.setVisibility(View.GONE);
                binding.vPasswDivider.setVisibility(View.GONE);
                successRes = R.string.success_change_storage;
                errRes = R.string.err_change_storage;
                break;
            case COPY:
                binding.toolbar.setTitle(R.string.storage_copy);
                binding.btSave.setText(R.string.popup_copy);
                binding.tlStoragePassw.setVisibility(View.GONE);
                binding.vPasswDivider.setVisibility(View.GONE);
                successRes = R.string.success_copy_storage;
                errRes = R.string.err_copy_storage;
                break;
            case CHANGE_PASSW:
                binding.toolbar.setTitle(R.string.storage_change_move);
                binding.btSave.setText(R.string.bt_save);
                binding.tlStoragePassw.setVisibility(View.VISIBLE);
                binding.vPasswDivider.setVisibility(View.VISIBLE);
                successRes = R.string.success_change_storage;
                errRes = R.string.err_change_storage;
                break;
        }
        Storage storage = presenter.getStorage();
        if (storage != null) {
            tvWatcherUpdate.ignoreChanges = true;
            String path = storage.path;
            path = path.substring(0, path.lastIndexOf("."));
            ViewUtils.changeTextIfNeed(binding.edStoragePath, path);
            ViewUtils.changeTextIfNeed(binding.edStorageName, storage.name);
            ViewUtils.changeTextIfNeed(binding.edStorageDescription, storage.description);
            tvWatcherUpdate.ignoreChanges = false;
        }

        binding.prSaveProcessing.setVisibility(presenter.saveStorageFuture.isInProcess() ? View.VISIBLE : View.GONE);

        boolean isSaveAvailable = presenter.getMode() != EditStoragePresenter.ChangeStorageMode.DETAILS;
        boolean hasChanges = presenter.hasChanges();
        boolean isPathEmpty = binding.edStoragePath.getText().toString().isEmpty();
        binding.btSave.setVisibility(isSaveAvailable && hasChanges && !isPathEmpty ? View.VISIBLE : View.GONE);

        EditStoragePresenter.SaveStorageResult result = presenter.saveStorageFuture.popResult();
        if (result != null) switch (result) {
            case SUCCESS:
                Toast.makeText(this, successRes, Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                break;
            case EMPTY_STORAGE_PATH_ERROR:
                Toast.makeText(this, R.string.err_input_path, Toast.LENGTH_LONG).show();
                break;
            case FILE_EXIST_ERROR:
                Toast.makeText(this, R.string.err_exist_file, Toast.LENGTH_LONG).show();
                break;
            case ERROR:
                Toast.makeText(this, errRes, Toast.LENGTH_LONG).show();
                break;
        }

    }

    private void cacheInput() {
        presenter.setStorage(new Storage(
                UserShortPaths.absolutePath(binding.edStoragePath.getText().toString() + getString(R.string.tkey_format)),
                binding.edStorageName.getText().toString(),
                binding.edStorageDescription.getText().toString()
        ));
    }

}
