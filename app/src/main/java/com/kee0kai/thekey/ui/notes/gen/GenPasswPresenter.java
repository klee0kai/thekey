package com.kee0kai.thekey.ui.notes.gen;

import static com.kee0kai.thekey.App.DI;
import static com.kee0kai.thekey.domain.AppSettingsRepository.*;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.kee0kai.thekey.domain.AppSettingsRepository;
import com.kee0kai.thekey.engine.CryptStorageEngine;
import com.kee0kai.thekey.utils.arch.SimplePresenter;
import com.kee0kai.thekey.utils.arch.Threads;

import java.util.concurrent.ThreadPoolExecutor;

public class GenPasswPresenter extends SimplePresenter {

    private final ThreadPoolExecutor secThread = Threads.newSingleThreadExecutor("egn_passw");
    private final CryptStorageEngine engine = DI.engine().cryptEngine();
    private final AppSettingsRepository setRep = DI.domain().appSettingsRepository();


    private String passw = null;
    private boolean includeEn = false;
    private boolean includeSpecSymbols = false;
    private int passwLen = 4;// values [ 4..16 ]

    public void init() {
        passwLen = Integer.parseInt(setRep.get(SETTING_GEN_PASS_LEN, Integer.toString(8)));
        includeEn = Boolean.parseBoolean(setRep.get(SETTING_GEN_PASS_INCLUDE_EN, Boolean.toString(true)));
        includeSpecSymbols = Boolean.parseBoolean(setRep.get(SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS, Boolean.toString(false)));
        genPassw(true);
    }

    public void genPassw(boolean fromHistIfAvailable) {
        secThread.submit(() -> {
            try {
                if (fromHistIfAvailable) {
                    long[] passwHist = engine.getGenPasswds();
                    if (passwHist != null && passwHist.length > 0) {
                        this.passw = engine.getGenPassw(passwHist[passwHist.length - 1]).passw;
                        return;
                    }
                }

                int encoding = CryptStorageEngine.GenPasswEncoding.ENC_PASSW_NUM_ONLY;
                if (includeEn) encoding = CryptStorageEngine.GenPasswEncoding.ENC_PASSW_EN_NUM;
                if (includeSpecSymbols)
                    encoding = CryptStorageEngine.GenPasswEncoding.ENC_PASSW_EN_NUM_SPEC_SYMBOLS;
                passw = engine.generateNewPassw(passwLen, encoding);
            } finally {
                views.refreshAllViews(10);
            }
        });
    }

    public void copyPassw() {
        if (DI.app().application() == null || passw == null) return;
        ClipboardManager clipboard = (ClipboardManager) DI.app().application().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return;
        ClipData clip = ClipData.newPlainText("ps", passw);
        clipboard.setPrimaryClip(clip);
    }


    //getters and setters
    public void setIncludeEn(boolean includeEn) {
        if (this.includeEn == includeEn) return;
        this.includeEn = includeEn;
        secThread.submit(() -> {
            setRep.set(SETTING_GEN_PASS_INCLUDE_EN, Boolean.toString(includeEn));
            views.refreshAllViews();
        });

    }

    public void setIncludeSpecSymbols(boolean includeSpecSymbols) {
        if (this.includeSpecSymbols == includeSpecSymbols) return;
        this.includeSpecSymbols = includeSpecSymbols;
        secThread.submit(() -> {
            setRep.set(SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS, Boolean.toString(includeSpecSymbols));
            views.refreshAllViews();
        });
    }

    public void setPasswLen(int passwLen) {
        if (this.passwLen == passwLen) return;
        this.passwLen = passwLen;
        secThread.submit(() -> {
            setRep.set(SETTING_GEN_PASS_LEN, Integer.toString(passwLen));
            views.refreshAllViews();
        });
    }

    public String getPassw() {
        return passw;
    }

    public boolean isIncludeEn() {
        return includeEn;
    }

    public boolean isIncludeSpecSymbols() {
        return includeSpecSymbols;
    }

    public int getPasswLen() {
        return passwLen;
    }
}
