package com.kee0kai.thekey.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kee0kai.thekey.R;

public class AcceptDialogFragment extends DialogFragment {

    private static final String ARGS_EXTRA = "ar";

    public interface IAcceptListener {
        void onAcceptDlgDone(AcceptDialogFragment dlg, boolean accept);
    }

    private IAcceptListener listener;

    public static AcceptDialogFragment newInstance(AcceptDialogArgs args) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_EXTRA, args);
        AcceptDialogFragment fr = new AcceptDialogFragment();
        fr.setArguments(bundle);
        return fr;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AcceptDialogArgs args = null;
        if (getArguments() != null)
            args = (AcceptDialogArgs) getArguments().getParcelable(ARGS_EXTRA);
        if (args == null) args = new AcceptDialogArgs();
        return new AlertDialog.Builder(requireContext())
                .setTitle(args.title)
                .setMessage(args.message)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (listener != null)
                        listener.onAcceptDlgDone(this, true);
                })
                .setNegativeButton(getString(R.string.no), ((dialog, which) -> {
                    if (listener != null)
                        listener.onAcceptDlgDone(this, false);
                }))
                .create();
    }

    public IAcceptListener getListener() {
        return listener;
    }

    public void setListener(IAcceptListener listener) {
        this.listener = listener;
    }

    public static class AcceptDialogArgs implements Parcelable {

        public String title;
        public String message;

        public AcceptDialogArgs() {
        }

        public AcceptDialogArgs(String title, String message) {
            this.title = title;
            this.message = message;
        }

        protected AcceptDialogArgs(Parcel in) {
            title = in.readString();
            message = in.readString();
        }

        public static final Creator<AcceptDialogArgs> CREATOR = new Creator<AcceptDialogArgs>() {
            @Override
            public AcceptDialogArgs createFromParcel(Parcel in) {
                return new AcceptDialogArgs(in);
            }

            @Override
            public AcceptDialogArgs[] newArray(int size) {
                return new AcceptDialogArgs[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(message);
        }
    }


}
