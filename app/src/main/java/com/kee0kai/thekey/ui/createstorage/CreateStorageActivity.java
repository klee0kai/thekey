package com.kee0kai.thekey.ui.createstorage;

import static com.kee0kai.thekey.App.DI;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kee0kai.thekey.databinding.ActivityStorageCreateBinding;
import com.kee0kai.thekey.utils.arch.IRefreshView;

public class CreateStorageActivity extends AppCompatActivity implements IRefreshView {

    private final CreateStoragePresenter presenter = DI.presenter().createStoragePresenter();

    private ActivityStorageCreateBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStorageCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        presenter.subscribe(this);
        presenter.init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }


    @Override
    public void refreshUI() {

    }
}
