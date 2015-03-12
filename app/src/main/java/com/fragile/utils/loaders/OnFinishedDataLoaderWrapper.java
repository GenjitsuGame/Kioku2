package com.fragile.utils.loaders;

import android.content.Context;
import android.os.Bundle;

public abstract class OnFinishedDataLoaderWrapper<D> extends AsyncLoader<D> {

    private final Bundle onFinishedData;

    public OnFinishedDataLoaderWrapper(Context context, Bundle onFinishedData) {
        super(context);
        this.onFinishedData = onFinishedData;
    }

    public Bundle getOnFinishedData() {
        return this.onFinishedData;
    }
}
