package com.kee0kai.thekey.utils.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.kee0kai.thekey.utils.Logs;

import java.util.ArrayList;
import java.util.List;

public class SimpleDiffUtilHelper<T extends ICloneable> {
    private List<T> oldList = null;

    private SimpleDiffResult<T> diffResult = null;

    public SimpleDiffUtilHelper() {
    }

    public void saveOld(List<T> old) {
        if (old == null) {
            this.oldList = null;
            return;
        }

        this.oldList = new ArrayList<>(old.size());
        for (ICloneable item : old) {
            try {
                oldList.add((T) item.clone());
            } catch (CloneNotSupportedException e) {
                Logs.e(e);
            }
        }
    }


    public SimpleDiffResult<T> calculateWith(List<T> newList) {
        if (oldList == null)
            //если нет старого списка проталкиваем обновления на весь список
            return diffResult = new SimpleDiffResult<T>(null, oldList, newList);

        diffResult = new SimpleDiffResult<T>(DiffUtil.calculateDiff(new SimpleDiffCallback(
                oldList != null ? new ArrayList<>(oldList) : null,
                newList != null ? new ArrayList<>(newList) : null, false), true), oldList, newList);
        oldList = null;
        return diffResult;
    }

    public SimpleDiffResult<T> calculateWith(List<T> newList, boolean detectMoves) {
        diffResult = new SimpleDiffResult<T>(DiffUtil.calculateDiff(new SimpleDiffCallback(oldList != null ? new ArrayList<>(oldList) : null,
                newList != null ? new ArrayList<>(newList) : null, false), detectMoves), oldList, newList);
        oldList = null;
        return diffResult;
    }

    public SimpleDiffResult<T> popDiffResult(List<T> list) {
        SimpleDiffResult<T> changes = diffResult;
        diffResult = null;
        return changes != null ? changes : new SimpleDiffResult<T>(null, null, list);
    }


}
