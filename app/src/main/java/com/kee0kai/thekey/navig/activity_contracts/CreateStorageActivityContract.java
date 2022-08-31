package com.kee0kai.thekey.navig.activity_contracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kee0kai.thekey.model.Storage;
import com.kee0kai.thekey.ui.createstorage.CreateStorageActivity;
import com.kee0kai.thekey.ui.createstorage.CreateStoragePresenter;
import com.kee0kai.thekey.utils.adapter.ICloneable;

public class CreateStorageActivityContract extends ActivityResultContract<CreateStorageActivityContract.CreateStorageTask, Uri> {

    public static final String CHANGE_TASK_EXTRA = "ch";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, CreateStorageTask task) {
        Intent intent = new Intent(context, CreateStorageActivity.class);
        intent.putExtra(CHANGE_TASK_EXTRA, 0);
        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && intent != null && intent.getData() != null)
            return intent.getData();
        return null;
    }

    public static class CreateStorageTask implements ICloneable, Parcelable {
        public Storage storage = null;
        public CreateStoragePresenter.ChangeStorageMode mode = CreateStoragePresenter.ChangeStorageMode.CREATE;

        public CreateStorageTask() {

        }

        public CreateStorageTask(Storage storage, CreateStoragePresenter.ChangeStorageMode mode) {
            this.storage = storage;
            this.mode = mode;
        }

        protected CreateStorageTask(Parcel in) {
            storage = in.readParcelable(Storage.class.getClassLoader());
        }

        public static final Creator<CreateStorageTask> CREATOR = new Creator<CreateStorageTask>() {
            @Override
            public CreateStorageTask createFromParcel(Parcel in) {
                return new CreateStorageTask(in);
            }

            @Override
            public CreateStorageTask[] newArray(int size) {
                return new CreateStorageTask[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(storage, i);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
