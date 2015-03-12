package com.fragile.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import com.fragile.kioku2.R;

import java.util.ArrayList;
import java.util.List;

import fm.last.api.Event;


public abstract class CustomArrayAdapter<T> extends ArrayAdapter<T> {

    protected final LayoutInflater mInflater;

    protected CustomArrayAdapter(Context context, int resource) {
        super(context, resource);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<T> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }
}
