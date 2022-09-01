package com.kee0kai.thekey.utils.adapter;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import com.hannesdorfmann.adapterdelegates3.AbsDelegationAdapter;
import com.kee0kai.thekey.utils.Logs;

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

    public void logChanges() {
        if (diffResult != null)
            diffResult.dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onInserted(int position, int count) {
                    Logs.log("SimpleDiffResult onInserted " + position + " - " + count);

                }

                @Override
                public void onRemoved(int position, int count) {
                    Logs.log("SimpleDiffResult onRemoved " + position + " - " + count);

                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {
                    Logs.log("SimpleDiffResult onMoved " + fromPosition + " - " + toPosition);

                }

                @Override
                public void onChanged(int position, int count, @Nullable Object payload) {
                    Logs.log("SimpleDiffResult onChanged " + position + " - " + count);
                }
            });
        else {
            Logs.log("SimpleDiffResult change all " + newList.size());
        }
    }
}
