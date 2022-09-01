package com.kee0kai.thekey.ui.note;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityAccountNoteBinding;
import com.kee0kai.thekey.engine.model.DecryptedNote;
import com.kee0kai.thekey.navig.InnerNavigator;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.utils.TimeFormats;
import com.kee0kai.thekey.utils.arch.IRefreshView;
import com.kee0kai.thekey.utils.views.EmptyTextWatcher;
import com.kee0kai.thekey.utils.views.ViewUtils;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class NoteActivity extends BaseActivity implements IRefreshView, View.OnLayoutChangeListener, View.OnClickListener {

    public static final String NOTE_PTR_EXTRA = "n_ptr";

    private final DateFormat dateFormat = TimeFormats.simpleDateFormat();
    private final NotePresenter presenter = DI.presenter().notePresenter();
    private final InnerNavigator navigator = DI.control().innerNavigator();

    private final EmptyTextWatcher tvWatcherUpdate = new EmptyTextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            super.afterTextChanged(editable);
            if (!ignoreChanges) cacheInput();
        }
    };

    private ActivityAccountNoteBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.svScreenScrollview.addOnLayoutChangeListener(this);
        binding.edSite.addTextChangedListener(tvWatcherUpdate);
        binding.edPassw.addTextChangedListener(tvWatcherUpdate);
        binding.edLoginName.addTextChangedListener(tvWatcherUpdate);
        binding.edSiteDescription.addTextChangedListener(tvWatcherUpdate);
        binding.btGenerate.setOnClickListener(this);
        binding.btHistory.setOnClickListener(this);
        binding.btSave.setOnClickListener(this);
        binding.clContainer.setOnClickListener(this);

        presenter.subscribe(this);
        presenter.init(getIntent().getLongExtra(NOTE_PTR_EXTRA, 0),
                savedInstanceState == null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (binding.clContainer.getMinHeight() != binding.svScreenScrollview.getHeight())
            binding.clContainer.setMinHeight(binding.svScreenScrollview.getHeight());
    }


    @Override
    public void onClick(View v) {
        if (v == binding.btSave) {
            cacheInput();
            presenter.save();
        } else if (v == binding.btGenerate) {
            presenter.genPassw();
        } else if (v == binding.btHistory) {
            if (presenter.getPtNote() != 0)
                startActivity(navigator.noteHist(presenter.getPtNote()));
        } else if (v == binding.clContainer) {
            binding.clContainer.clearFocus();
        }
    }

    @Override
    public void refreshUI() {
        tvWatcherUpdate.ignoreChanges = true;
        DecryptedNote decryptedNote = presenter.getNote();
        binding.btHistory.setVisibility(presenter.getNote().hist != null && presenter.getNote().hist.length > 0 ? View.VISIBLE : View.GONE);
        binding.btSave.setVisibility(presenter.isSaveAvailable() ? View.VISIBLE : View.GONE);
        ViewUtils.changeTextIfNeed(binding.edSite, decryptedNote.site);
        ViewUtils.changeTextIfNeed(binding.edSiteDescription, decryptedNote.desc);
        ViewUtils.changeTextIfNeed(binding.edLoginName, decryptedNote.login);
        ViewUtils.changeTextIfNeed(binding.edPassw, decryptedNote.passw);
        ViewUtils.changeTextIfNeed(binding.tvLastUpdateMessage,
                getString(R.string.last_update_param, dateFormat.format(
                        new Date(TimeUnit.SECONDS.toMillis(decryptedNote.chTime)))));
        Boolean saveResult = presenter.saveFuture.popResult();
        if (saveResult != null && saveResult) {
            finish();
        }
        tvWatcherUpdate.ignoreChanges = false;
    }

    private void cacheInput() {
        DecryptedNote note = presenter.getNote();
        note.site = binding.edSite.getText().toString();
        note.desc = binding.edSiteDescription.getText().toString();
        note.login = binding.edLoginName.getText().toString();
        note.passw = binding.edPassw.getText().toString();
        presenter.setNote(note);
    }

}
