package com.yashoid.talktome.view.viewbunch;

import android.database.DataSetObserver;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseViewBunchAdapter implements ViewBunch.ViewBunchAdapter {

    private List<DataSetObserver> mObservers = new ArrayList<>();

    public BaseViewBunchAdapter() {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mObservers.remove(observer);
        mObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservers.remove(observer);
    }

    public void notifyDataSetChanged() {
        List<DataSetObserver> observers = new ArrayList<>(mObservers);

        for (DataSetObserver observer: observers) {
            observer.onChanged();
        }
    }

}
