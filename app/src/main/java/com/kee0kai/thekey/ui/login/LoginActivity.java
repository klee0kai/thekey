package com.kee0kai.thekey.ui.login;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.kee0kai.thekey.databinding.ActivityLoginBinding;

public class LoginActivity extends Activity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}
