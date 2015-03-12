package com.fragile.utils.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.fragile.utils.views.ClearOnCollapseSearchView;


public abstract class QuerySubmitListFragment extends ListFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks{

    public interface OnNoResultFoundListener {
        void onNoResultFound(String query);
    }


    public interface OnQuerySubmitListener {
        void onQuerySubmitted(String query);
    }

    protected String query;

    protected SearchView mSearchView;

    protected OnNoResultFoundListener onNoResultFoundListener;
    protected OnQuerySubmitListener onQuerySubmitListener;

    @Override
    public boolean onClose() {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        setListShown(false);
        query = s;
        onQueryTextSubmitting();
        onQuerySubmitListener.onQuerySubmitted(s);
        return true;
    }

    public abstract void onQueryTextSubmitting();

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Place an action bar item for searching.
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new ClearOnCollapseSearchView(getActivity());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        item.setActionView(mSearchView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onNoResultFoundListener = (OnNoResultFoundListener) activity;
        onQuerySubmitListener = (OnQuerySubmitListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNoResultFoundListener = null;
        onQuerySubmitListener = null;
    }
}
