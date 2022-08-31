package com.kee0kai.thekey.utils.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;

public class SimpleViewHolder extends RecyclerView.ViewHolder {

    protected final HashMap<String, Object> metaData = new HashMap<>();


    public SimpleViewHolder(View itemView) {
        super(itemView);
    }


    public Object getMeta(String stateId) {
        return metaData.get(stateId);
    }

    public void setMeta(String stateId, Object state) {
        metaData.put(stateId, state);
    }


}
