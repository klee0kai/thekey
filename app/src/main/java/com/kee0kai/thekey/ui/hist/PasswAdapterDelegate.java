package com.kee0kai.thekey.ui.hist;

import androidx.annotation.NonNull;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ItemPasswBinding;
import com.kee0kai.thekey.engine.model.DecryptedPassw;
import com.kee0kai.thekey.utils.TimeFormats;
import com.kee0kai.thekey.utils.adapter.ClassAdapterDelegate;
import com.kee0kai.thekey.utils.adapter.SimpleViewHolder;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PasswAdapterDelegate extends ClassAdapterDelegate<DecryptedPassw> {

    private final DateFormat dateFormat = TimeFormats.simpleDateFormat();

    public PasswAdapterDelegate(int layoutRes) {
        super(DecryptedPassw.class, layoutRes);
    }

    @Override
    protected void onBindViewHolder(DecryptedPassw it, int pos, @NonNull SimpleViewHolder vh, @NonNull List<Object> allObjects, @NonNull List<Object> allOldlist) {
        ItemPasswBinding binding = ItemPasswBinding.bind(vh.itemView);
        binding.tvPassw.setText(it.passw);
        binding.tvChangeDate.setText(vh.itemView.getResources().getString(R.string.gen_date,
                dateFormat.format(new Date(TimeUnit.SECONDS.toMillis(it.chTime)))));
    }
}
