package com.kee0kai.thekey.navig.activity_contracts;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimplerActivityContract extends ActivityResultContract<Intent, Intent> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Intent intent) {
        return intent;
    }

    @Override
    public Intent parseResult(int i, @Nullable Intent intent) {
        return intent;
    }
}
