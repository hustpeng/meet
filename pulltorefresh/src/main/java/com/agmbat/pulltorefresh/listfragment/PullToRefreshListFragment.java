package com.agmbat.pulltorefresh.listfragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;

import com.agmbat.pulltorefresh.view.PullToRefreshListView;


/**
 * A sample implementation of how to use {@link PullToRefreshListView} with
 * {@link ListFragment}. This implementation simply replaces the ListView that
 * {@code ListFragment} creates with a new PullToRefreshListView. This means
 * that ListFragment still works 100% (e.g. <code>setListShown(...)</code> ).
 * <p/>
 * The new PullToRefreshListView is created in the method
 * {@link #onCreatePullToRefreshListView(LayoutInflater, Bundle)}. If you wish
 * to customise the {@code PullToRefreshListView} then override this method and
 * return your customised instance.
 */
public class PullToRefreshListFragment extends PullToRefreshBaseListFragment<PullToRefreshListView> {

    protected PullToRefreshListView onCreatePullToRefreshListView(LayoutInflater inflater, Bundle savedInstanceState) {
        return new PullToRefreshListView(getActivity());
    }

}