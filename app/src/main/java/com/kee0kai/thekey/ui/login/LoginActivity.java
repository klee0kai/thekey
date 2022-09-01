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
    private final InnerNavigator navigator = DI.control().innerNavigator();

    private boolean permRegDone = false;
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
            permRegDone = true;
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
        presenter.unlogin();
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
        binding.tvStorage.setText(presenter.getStoragePath());
        binding.tvStorageName.setText(presenter.getStorageInfo() != null ? presenter.getStorageInfo().name : "");
        binding.btLogin.setVisibility(presenter.getStorageInfo() != null ? View.VISIBLE : View.GONE);
        binding.prLogingProcessing.setVisibility(presenter.loginFuture.isInProcess() ? View.VISIBLE : View.GONE);

        Boolean loginResult = presenter.loginFuture.popResult();
        if (loginResult != null)
            if (loginResult) {
                startActivity(navigator.notes());
            } else {
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
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
                if (permRegDone && !showRationale) {
                    // user also CHECKED "never ask again"
                    Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_SHORT).show();
                } else reqPermLauncher.launch(PERMISSIONS);
                return;
            }
        }
    }


}
