package com.agmbat.pulltorefresh.listfragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;

import com.agmbat.pulltorefresh.view.PullToRefreshExpandableListView;


/**
 * A sample implementation of how to use {@link PullToRefreshExpandableListView}
 * with {@link ListFragment}. This implementation simply replaces the ListView
 * that {@code ListFragment} creates with a new
 * {@code PullToRefreshExpandableListView}. This means that ListFragment still
 * works 100% (e.g. <code>setListShown(...)</code> ).
 * <p/>
 * The new PullToRefreshListView is created in the method
 * {@link #onCreatePullToRefreshListView(LayoutInflater, Bundle)}. If you wish
 * to customise the {@code PullToRefreshExpandableListView} then override this
 * method and return your customised instance.
 */
public class PullToRefreshExpandableListFragment extends PullToRefreshBaseListFragment<PullToRefreshExpandableListView> {

    protected PullToRefreshExpandableListView onCreatePullToRefreshListView(LayoutInflater inflater,
                                                                            Bundle savedInstanceState) {
        return new PullToRefreshExpandableListView(getActivity());
    }

}