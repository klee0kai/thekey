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

import com.kee0kai.thekey.ui.editstorage.EditStoragePresenter;
import com.kee0kai.thekey.ui.editstorage.EditStorageActivity;
import com.kee0kai.thekey.utils.adapter.ICloneable;

public class EditStorageActivityContract extends ActivityResultContract<EditStorageActivityContract.EditStorageTask, Uri> {

    public static final String CHANGE_TASK_EXTRA = "ch";

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, EditStorageTask task) {
        Intent intent = new Intent(context, EditStorageActivity.class);
        intent.putExtra(CHANGE_TASK_EXTRA, task);
        return intent;
    }

    @Override
    public Uri parseResult(int resultCode, @Nullable Intent intent) {
        if (resultCode == Activity.RESULT_OK && intent != null && intent.getData() != null)
            return intent.getData();
        return null;
    }

    public static class EditStorageTask implements ICloneable, Parcelable {
        public String storagePath = null;
        public EditStoragePresenter.ChangeStorageMode mode = EditStoragePresenter.ChangeStorageMode.CREATE;

        public EditStorageTask() {

        }

        public EditStorageTask(String storagePath, EditStoragePresenter.ChangeStorageMode mode) {
            this.storagePath = storagePath;
            this.mode = mode;
        }

        protected EditStorageTask(Parcel in) {
            storagePath = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(storagePath);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<EditStorageTask> CREATOR = new Creator<EditStorageTask>() {
            @Override
            public EditStorageTask createFromParcel(Parcel in) {
                return new EditStorageTask(in);
            }

            @Override
            public EditStorageTask[] newArray(int size) {
                return new EditStorageTask[size];
            }
        };

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
