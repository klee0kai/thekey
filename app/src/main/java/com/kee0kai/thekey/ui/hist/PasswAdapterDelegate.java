package com.kee0kai.thekey.ui.hist;

import androidx.annotation.NonNull;

import com.github.klee0kai.hummus.adapterdelegates.delegate.simple.ClassAdapterDelegate;
import com.github.klee0kai.hummus.adapterdelegates.delegate.simple.SimpleViewHolder;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ItemPasswBinding;
import com.kee0kai.thekey.engine.model.DecryptedPassw;
import com.kee0kai.thekey.utils.TimeFormats;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PasswAdapterDelegate extends ClassAdapterDelegate<DecryptedPassw> {

    private final DateFormat dateFormat = TimeFormats.simpleDateFormat();

    public PasswAdapterDelegate(int layoutRes) {
        super(DecryptedPassw.class, layoutRes);
    }

    @Override
    protected void onBindViewHolder(DecryptedPassw it, int i, @NonNull SimpleViewHolder vh) {
        ItemPasswBinding binding = ItemPasswBinding.bind(vh.itemView);
        binding.tvPassw.setText(it.passw);
        binding.tvChangeDate.setText(vh.itemView.getResources().getString(R.string.gen_date,
                dateFormat.format(new Date(TimeUnit.SECONDS.toMillis(it.chTime)))));
    }
}
