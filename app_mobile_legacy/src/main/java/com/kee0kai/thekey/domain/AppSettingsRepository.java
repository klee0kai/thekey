package com.kee0kai.thekey.domain;

import static com.kee0kai.thekey.App.DI;

import com.kee0kai.thekey.domain.room.dao.SettingDao;
import com.kee0kai.thekey.domain.room.model.SettingPairEntry;

public class AppSettingsRepository {


    public static final long SETTING_DEFAULT_STORAGE_PATH = 944;
    public static final int SETTING_GEN_PASS_LEN = 247;
    public static final int SETTING_GEN_PASS_INCLUDE_EN = 43;
    public static final int SETTING_GEN_PASS_INCLUDE_SPEC_SYMBOLS = 44;


    private final SettingDao settingDao = DI.provider().keyDatabase().settingsDao();

    public String get(long setID) {
        SettingPairEntry entry = settingDao.get(setID);
        return entry != null ? entry.value : null;
    }

    public String get(long setID, String def) {
        String v = get(setID);
        return v != null ? v : def;
    }

    public void set(long setID, String value) {
        settingDao.update(new SettingPairEntry(setID, value));
    }


}
