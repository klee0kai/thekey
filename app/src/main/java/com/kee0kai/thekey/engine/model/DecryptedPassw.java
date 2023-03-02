package com.kee0kai.thekey.engine.model;


import com.github.klee0kai.hummus.model.ICloneable;
import com.github.klee0kai.hummus.model.ISameModel;

import java.util.Objects;

public class DecryptedPassw implements ICloneable, ISameModel {

    public String passw;
    public long chTime;

    public DecryptedPassw(String passw, long chTime) {
        this.passw = passw;
        this.chTime = chTime;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean isSame(Object ob) {
        return ob instanceof DecryptedPassw && Objects.equals(passw, ((DecryptedPassw) ob).passw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecryptedPassw that = (DecryptedPassw) o;
        return chTime == that.chTime && Objects.equals(passw, that.passw);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passw, chTime);
    }
}
