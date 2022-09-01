package com.kee0kai.thekey.ui.login;

import static com.kee0kai.thekey.App.DI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.kee0kai.thekey.App;
import com.kee0kai.thekey.R;
import com.kee0kai.thekey.databinding.ActivityLoginBinding;
import com.kee0kai.thekey.navig.InnerNavigator;
import com.kee0kai.thekey.navig.activity_contracts.FindStorageActivityContract;
import com.kee0kai.thekey.ui.common.BaseActivity;
import com.kee0kai.thekey.utils.arch.IRefreshView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity implements IRefreshView, View.OnClickListener {

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final LoginPresenter presenter = DI.presenter().loginPresenter();

    private ActivityLoginBinding binding;
    private ActivityResultLauncher<String> findStorageLauncher;
    private ActivityResultLauncher<String[]> reqPermLauncher;

    @Override
    public SecureType getSecType() {
        return SecureType.PUBLIC;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        presenter.subscribe(this);
        presenter.init();

        findStorageLauncher = registerForActivityResult(new FindStorageActivityContract(), presenter::setStorage);
        reqPermLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), ignore -> {
        });
        binding.edPassw.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
        binding.btLogin.setOnClickListener(this);
        binding.btChangeStorage.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe(this);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.btLogin) {
            login();
        } else if (view == binding.btChangeStorage) {
            findStorageLauncher.launch(App.STORAGE_EXT);
        }
    }

    @Override
    public void refreshUI() {

    }

    private void login() {
        binding.edPassw.clearFocus();
        String passw = binding.edPassw.getText() != null ? binding.edPassw.getText().toString() : "";
        if (passw.isEmpty()) {
            Toast.makeText(this, R.string.input_passw, Toast.LENGTH_SHORT).show();
            return;
        }
        presenter.login(passw);
    }

    private void checkPermissions() {
        for (String perm : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                boolean showRationale = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(perm);
                if (!showRationale) {
                    // user also CHECKED "never ask again"
                    Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_LONG).show();
                } else reqPermLauncher.launch(PERMISSIONS);
                return;
            }
        }
    }


}
