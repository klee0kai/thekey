package com.kee0kai.thekey.utils.collections;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class ExtSparseArray<T> extends SparseArray<T> {

    public List<T> toList() {
        List<T> list = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            list.add(valueAt(i));
        }
        return list;
    }

    public boolean containsItem(T it) {
        for (int i = 0; i < size(); i++)
            if (valueAt(i).equals(it))
                return true;
        return false;
    }

    public void removeItem(T it) {
        for (int i = 0; i < size(); i++)
            if (valueAt(i).equals(it))
                removeAt(i);
    }
}
