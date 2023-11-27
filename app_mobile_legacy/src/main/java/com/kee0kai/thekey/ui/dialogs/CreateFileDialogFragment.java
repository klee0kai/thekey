package com.kee0kai.thekey.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.DlgCreateStorageBinding;
import com.kee0kai.thekey.utils.android.UserShortPaths;

import java.io.File;

public class CreateFileDialogFragment extends DialogFragment {

    public interface ICreateFileListener {
        void onCreateFileDone(CreateFileDialogFragment dlg, File f);
    }

    private static final String ARGS_EXTRA = "ar";

    private ICreateFileListener listener;

    public static CreateFileDialogFragment newInstance(CreateFileDlgArgs args) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_EXTRA, args);
        CreateFileDialogFragment fr = new CreateFileDialogFragment();
        fr.setArguments(bundle);
        return fr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        CreateFileDlgArgs args = null;
        if (getArguments() != null)
            args = (CreateFileDlgArgs) getArguments().getParcelable(ARGS_EXTRA);
        if (args == null) args = new CreateFileDlgArgs();
        CreateFileDlgArgs finalArgs = args;
        DlgCreateStorageBinding binding = DlgCreateStorageBinding.inflate(getLayoutInflater());
        return new AlertDialog.Builder(requireContext())
                .setTitle(UserShortPaths.shortPathName(args.folder + "/"))
                .setView(binding.getRoot())
                .setPositiveButton(R.string.create, (dialog, which) -> {
                    String storageName = binding.edStorageName.getText().toString();
                    if (storageName.isEmpty()) {
                        Toast.makeText(requireContext(), R.string.err_input_path, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    File stFile = new File(finalArgs.folder, storageName + finalArgs.fileType);
                    if (listener != null)
                        listener.onCreateFileDone(this, stFile);
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }


    public void setListener(ICreateFileListener listener) {
        this.listener = listener;
    }

    public ICreateFileListener getListener() {
        return listener;
    }

    public static class CreateFileDlgArgs implements Parcelable {
        public String folder;
        public String fileType;

        public CreateFileDlgArgs() {
        }

        public CreateFileDlgArgs(String folder, String fileType) {
            this.folder = folder;
            this.fileType = fileType;
        }

        protected CreateFileDlgArgs(Parcel in) {
            folder = in.readString();
            fileType = in.readString();
        }

        public static final Creator<CreateFileDlgArgs> CREATOR = new Creator<CreateFileDlgArgs>() {
            @Override
            public CreateFileDlgArgs createFromParcel(Parcel in) {
                return new CreateFileDlgArgs(in);
            }

            @Override
            public CreateFileDlgArgs[] newArray(int size) {
                return new CreateFileDlgArgs[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(folder);
            dest.writeString(fileType);
        }
    }


}
