package com.kee0kai.thekey.navig.activity_contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kee0kai.thekey.ui.fileprovider.FileProviderActivity;
import com.kee0kai.thekey.ui.fileprovider.FileProviderPresenter;
import com.kee0kai.thekey.ui.storage.StoragesActivity;

public class CreateFileActivityContract extends ActivityResultContract<String, Uri> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String type) {
        Intent intent = new Intent(context, FileProviderActivity.class);
        intent.putExtra(FileProviderActivity.WORK_MODE_EXTRA, FileProviderPresenter.WorkMode.CREATE_STORAGE.ordinal());
        intent.setType(type);
        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && intent != null && intent.getData() != null)
            return intent.getData();
        return null;
    }
}
