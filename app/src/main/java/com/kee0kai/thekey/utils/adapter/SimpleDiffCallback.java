package com.kee0kai.thekey.utils.adapter;


import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

public class SimpleDiffCallback extends DiffUtil.Callback {

    private final List<Object> oldList;
    private final List<Object> newList;
    private final boolean changeAll;

    public SimpleDiffCallback(List<Object> oldList, List<Object> newList, boolean changeAll) {
        this.oldList = oldList;
        this.newList = newList;
        this.changeAll = changeAll;
    }


    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldObject = oldList.get(oldItemPosition);
        Object newObject = newList.get(newItemPosition);
        return oldObject instanceof ISameModel && ((ISameModel) oldObject).isSame(newObject) || Objects.equals(oldObject, newObject);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldObject = oldList.get(oldItemPosition);
        Object newObject = newList.get(newItemPosition);
        return !changeAll && Objects.equals(oldObject, newObject);
    }
}
