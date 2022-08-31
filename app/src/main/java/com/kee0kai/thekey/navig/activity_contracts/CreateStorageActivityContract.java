package com.kee0kai.thekey.navig.activity_contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kee0kai.thekey.ui.createstorage.CreateStorageActivity;

public class CreateStorageActivityContract extends ActivityResultContract<Object, Uri> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Object o) {
        return new Intent(context, CreateStorageActivity.class);
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && intent != null && intent.getData() != null)
            return intent.getData();
        return null;
    }
}
