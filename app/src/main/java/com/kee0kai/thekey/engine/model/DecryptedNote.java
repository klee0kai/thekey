package com.kee0kai.thekey.engine.model;

import com.kee0kai.thekey.utils.adapter.ICloneable;
import com.kee0kai.thekey.utils.adapter.ISameModel;

import java.util.Arrays;
import java.util.Objects;

public class DecryptedNote implements ICloneable, ISameModel {

    public String site = null;
    public String login = null;
    public String passw = null;
    public String desc = null;
    public long chTime;
    public DecryptedPassw[] hist;

    public DecryptedNote() {
        this.chTime = 0;
        this.hist = null;
    }

    public DecryptedNote(String site, String login, String passw, String desc) {
        this.site = site;
        this.login = login;
        this.passw = passw;
        this.desc = desc;
        this.chTime = 0;
        this.hist = null;
    }

    public DecryptedNote(String site, String login, String passw, String desc, long chTime, DecryptedPassw[] hist) {
        this.site = site;
        this.login = login;
        this.passw = passw;
        this.desc = desc;
        this.chTime = chTime;
        this.hist = hist;
    }

    public DecryptedNote(DecryptedNote origin) {
        this.site = origin.site;
        this.login = origin.login;
        this.passw = origin.passw;
        this.desc = origin.desc;
        this.chTime = origin.chTime;
        this.hist = origin.hist;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptedNote that = (DecryptedNote) o;
        return chTime == that.chTime &&
                Objects.equals(site, that.site) &&
                Objects.equals(login, that.login) &&
                Objects.equals(passw, that.passw) &&
                Objects.equals(desc, that.desc) &&
                Arrays.equals(hist, that.hist);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(site, login, passw, desc, chTime);
        result = 31 * result + Arrays.hashCode(hist);
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean isSame(Object ob) {
        return equals(ob);
    }
}
