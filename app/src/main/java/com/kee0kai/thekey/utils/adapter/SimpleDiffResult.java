package com.kee0kai.thekey.utils.adapter;


import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.hannesdorfmann.adapterdelegates3.AbsDelegationAdapter;

import java.util.ArrayList;
import java.util.List;

public class SimpleDiffResult<T> {

    private DiffUtil.DiffResult diffResult;
    private List<T> oldList, newList;


    public SimpleDiffResult(DiffUtil.DiffResult diffResult, List<T> oldList, List<T> newList) {
        this.diffResult = diffResult;
        this.oldList = oldList;
        this.newList = newList;
    }

    public void dispatchUpdatesTo(final RecyclerView.Adapter adapter) {
        if (diffResult != null)
            diffResult.dispatchUpdatesTo(adapter);
        else adapter.notifyDataSetChanged();
    }


    public DiffUtil.DiffResult getDiffResult() {
        return diffResult;
    }

    public void applyTo(AbsDelegationAdapter<List<Object>> adapter) {
        int oldListLen = oldList != null ? oldList.size() : 0;
        int oldAdapterCount = adapter.getItemCount();

        if (newList != null) adapter.setItems(new ArrayList<>(newList));
        else adapter.setItems(null);
        if (oldList == null || oldListLen != oldAdapterCount || diffResult == null) {
            adapter.notifyDataSetChanged();
            return;
        }

        diffResult.dispatchUpdatesTo(adapter);
    }

}
