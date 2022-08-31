package com.kee0kai.thekey.utils.collections;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WeakListCollection<T> {


    private final LinkedList<WeakReference<T>> items = new LinkedList<>();

    public List<T> toList() {
        clearNulls(null);
        LinkedList<T> list = new LinkedList<>();
        for (WeakReference<T> ref : items)
            list.add(ref.get());
        return list;
    }

    public void add(T o) {
        items.add(new WeakReference<>(o));
        clearNulls(null);
    }

    public void add(int pos, T o) {
        items.add(pos, new WeakReference<>(o));
        clearNulls(null);
    }

    public void remove(T item) {
        clearNulls(item);
    }

    public void clearNulls(T item) {
        Iterator<WeakReference<T>> it = items.iterator();
        while (it.hasNext()) {
            WeakReference<T> ref = it.next();
            if (ref == null || ref.get() == null || ref.get() == item)
                it.remove();
        }
    }

}
