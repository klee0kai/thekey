package com.kee0kai.thekey.ui.notes.gen;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.FragmentGenerationBinding;
import com.kee0kai.thekey.navig.InnerNavigator;
import com.kee0kai.thekey.utils.arch.IRefreshView;
import com.kee0kai.thekey.utils.views.ViewUtils;

public class GenPasswFragment extends Fragment implements IRefreshView, View.OnClickListener, CompoundButton.OnCheckedChangeListener, Slider.OnChangeListener {

    private final GenPasswPresenter presenter = DI.presenter().genPasswPresenter();
    private final InnerNavigator navigator = DI.control().innerNavigator();

    private FragmentGenerationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGenerationBinding.inflate(inflater, container, false);
        presenter.subscribe(this);
        presenter.init();

        binding.swIncludeSpecSymbols.setOnCheckedChangeListener(this);
        binding.swIncludeEn.setOnCheckedChangeListener(this);
        binding.tvGeneratedPassw.setOnClickListener(this);
        binding.btGenerate.setOnClickListener(this);
        binding.btHistory.setOnClickListener(this);
        binding.btSave.setOnClickListener(this);
        binding.slPasswLen.addOnChangeListener(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unsubscribe(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btGenerate) {
            presenter.genPassw(false);
        } else if (v == binding.btSave) {

        } else if (v == binding.btHistory) {
            startActivity(navigator.getGenHist());
        } else if (v == binding.tvGeneratedPassw) {
            presenter.copyPassw();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean b) {
        if (v == binding.swIncludeEn) {
            presenter.setIncludeEn(b);
        } else if (v == binding.swIncludeSpecSymbols) {
            presenter.setIncludeSpecSymbols(b);
        }
    }

    @Override
    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        presenter.setPasswLen((int) value);
    }

    @Override
    public void refreshUI() {
        ViewUtils.changeTextIfNeed(binding.tvGeneratedPassw, presenter.getPassw());
        ViewUtils.changeCheckedIfNeed(binding.swIncludeEn, presenter.isIncludeEn());
        ViewUtils.changeCheckedIfNeed(binding.swIncludeSpecSymbols, presenter.isIncludeSpecSymbols());
        ViewUtils.changeSliderValueIfNeed(binding.slPasswLen, presenter.getPasswLen());
        ViewUtils.changeTextIfNeed(binding.tvPasswLen, getString(R.string.passw_len_parram, presenter.getPasswLen()));
        ViewUtils.changeTextIfNeed(binding.tvGeneratedPassw, presenter.getPassw());

    }


}
