/*
 * Copyright (C) 2015 mayimchen <mayimchen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agmbat.pagedataloader;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.agmbat.android.task.AsyncTask;
import com.agmbat.android.task.AsyncTaskUtils;
import com.agmbat.android.utils.NetworkUtil;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.UiUtils;
import com.agmbat.android.utils.ViewUtils;
import com.agmbat.pulltorefresh.PullToRefreshBase;
import com.agmbat.pulltorefresh.view.PullToRefreshListView;
import com.agmbat.time.TimeUtils;

import java.util.List;

/**
 * Page data 加载类
 */
public abstract class PageDataLoader<T> implements OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView> {

    private final Context mContext;

    /**
     * 加载中的控件
     */
    private View mLoadingView;

    /**
     * 当list为空时显示的控件
     */
    private View mEmptyView;

    /**
     * 当list加载失败时显示的控件
     */
    private View mErrorView;

    /**
     * 下拉控件
     */
    private PullToRefreshListView mPtrListView;

    /**
     * 订单列表
     */
    private ListView mListView;

    /**
     * 当前的结果
     */
    private PageData mLastPageData;

    /**
     * 加载task
     */
    private LoadTask mLoadTask;

    public PageDataLoader(Context context) {
        mContext = context;
    }

    /**
     * 判断是否还有下一页数据
     *
     * @param sum      总共数据条数
     * @param pageSize 第页数据条数
     * @param pageNum  当前页数
     * @return true 存在下一页
     */
    public static boolean hasNextPage(int sum, int pageSize, int pageNum) {
        // 还有一种计算方法较为高效  (sum + (pageSize -1 )) / pageNum
        int pageCount = sum / pageSize + (sum % pageSize == 0 ? 0 : 1);
        return (pageNum + 1 < pageCount);
    }

    /**
     * Setup views
     *
     * @param view 根视图
     */
    public void setupViews(View view) {
        mPtrListView = (PullToRefreshListView) view.findViewById(R.id.ptr_list);
        mPtrListView.setOnRefreshListener(this);
        mPtrListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView = mPtrListView.getRefreshableView();
        mListView.setOnItemClickListener(this);
        mEmptyView = view.findViewById(R.id.empty);
        mErrorView = view.findViewById(R.id.load_data_error_layout);
        mLoadingView = view.findViewById(R.id.loading_view);
        view.findViewById(R.id.load_data_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    /**
     * 加载数据，并显示对应状态视图
     */
    public void loadData() {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtil.showToastShort(R.string.page_data_loader_no_network_msg);
            return;
        }
        if (mLoadTask == null) {
            mLoadTask = new LoadTask();
            AsyncTaskUtils.executeAsyncTask(mLoadTask, AsyncTaskUtils.Priority.HIGH);
        }
    }

    @Override
    public void onPullStartToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtil.showToastShort(R.string.page_data_loader_no_network_msg);
            UiUtils.post(new Runnable() {
                @Override
                public void run() {
                    mPtrListView.onRefreshComplete();
                }
            });
            return;
        }
        AsyncTaskUtils.executeAsyncTask(new PullStartTask(), AsyncTaskUtils.Priority.HIGH);
    }

    @Override
    public void onPullEndToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!NetworkUtil.isNetworkAvailable()) {
            ToastUtil.showToastShort(R.string.page_data_loader_no_network_msg);
            UiUtils.post(new Runnable() {
                @Override
                public void run() {
                    mPtrListView.onRefreshComplete();
                }
            });
            return;
        }
        AsyncTaskUtils.executeAsyncTask(new PullEndTask(), AsyncTaskUtils.Priority.HIGH);
    }

    /**
     * 后台加载数据
     *
     * @param page 需要加载的页码
     * @return
     */
    public abstract PageData<T> onLoadData(int page);

    /**
     * 显示数据为空的视图
     */
    protected void showEmptyView() {
        mPtrListView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 显示加载失败的视图
     */
    protected void showErrorView() {
        mPtrListView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 显示Loading控件
     */
    protected void showLoadingView() {
        mPtrListView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示Loading控件
     */
    protected void hideLoadingView() {
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 获取第一页的页码
     *
     * @return
     */
    protected int getFirstPageNum() {
        return 0;
    }

    /**
     * 能否请求下一页数据
     *
     * @param data
     * @return
     */
    protected boolean canPullEnd(PageData<T> data) {
        return data.hasNextPageData();
    }

    /**
     * 创建ListAdapter
     *
     * @param context
     * @param data
     * @return
     */
    protected abstract ArrayAdapter<T> createListAdapter(Context context, PageData<T> data);

    /**
     * 首次加载完成
     *
     * @param data
     */
    protected void onLoadFinish(PageData<T> data) {
        if (data != null) {
            if (data.isSuccess()) {
                List<T> dataList = data.getDataList();
                if (dataList != null && dataList.size() > 0) {
                    mLastPageData = data;
                    mPtrListView.setVisibility(View.VISIBLE);
                    mListView.setAdapter(createListAdapter(mContext, data));
                    if (!canPullEnd(data)) {
                        mPtrListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        mPtrListView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                } else {
                    showEmptyView();
                }
            } else {
                showErrorView();
                onLoadFinishError(data);
            }
        } else {
            showErrorView();
            ToastUtil.showToastShort(R.string.page_data_loader_network_time_out_msg);
            onLoadFinishError(data);
        }
    }

    /**
     * 下拉加载完成
     *
     * @param data
     */
    protected void onPullStartFinish(PageData<T> data) {
        if (data != null) {
            if (data.isSuccess()) {
                List<T> dataList = data.getDataList();
                if (dataList != null && dataList.size() > 0) {
                    mLastPageData = data;
                    ListAdapter adapter = createListAdapter(mContext, data);
                    mListView.setAdapter(adapter);
                    if (!canPullEnd(data)) {
                        mPtrListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        mPtrListView.setMode(PullToRefreshBase.Mode.BOTH);
                    }
                } else {
                    mPtrListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            } else {
                onPullStartFinishError(data);
            }
        } else {
            ToastUtil.showToastShort(R.string.page_data_loader_network_time_out_msg);
            onPullStartFinishError(data);
        }
    }

    /**
     * 上拉加载完成
     *
     * @param data
     */
    protected void onPullEndFinish(PageData<T> data) {
        if (data != null) {
            if (data.isSuccess()) {
                List<T> dataList = data.getDataList();
                if (dataList != null && dataList.size() > 0) {
                    mLastPageData = data;
                    ArrayAdapter adapter = (ArrayAdapter) ViewUtils.getActualAdapter(mListView);
                    adapter.addAll(dataList);
                    adapter.notifyDataSetChanged();
                    if (!canPullEnd(data)) {
                        mPtrListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                } else {
                    mPtrListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            } else {
                onPullEndFinishError(data);
            }
        } else {
            ToastUtil.showToastShort(R.string.page_data_loader_network_time_out_msg);
            onPullEndFinishError(data);
        }
    }

    /**
     * 首次加载数据错误
     *
     * @param data
     */
    protected void onLoadFinishError(PageData data) {
        onLoadingError(data);
    }

    /**
     * 下拉加载数据错误
     *
     * @param data
     */
    protected void onPullStartFinishError(PageData data) {
        onLoadingError(data);
    }

    /**
     * 上拉加载数据错误
     *
     * @param data
     */
    protected void onPullEndFinishError(PageData data) {
        onLoadingError(data);
    }

    /**
     * 加载数据错误
     *
     * @param data
     */
    protected void onLoadingError(PageData<T> data) {
    }

    /**
     * 首次加载task
     */
    private class LoadTask extends AsyncTask<Void, Void, PageData<T>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingView();
        }

        @Override
        protected PageData<T> doInBackground(Void... arg0) {
            return onLoadData(getFirstPageNum());
        }

        @Override
        protected void onPostExecute(PageData<T> data) {
            super.onPostExecute(data);
            mLoadTask = null;
            hideLoadingView();
            onLoadFinish(data);
        }
    }

    /**
     * 下拉刷新处理
     */
    private class PullStartTask extends AsyncTask<Void, Void, PageData<T>> {

        @Override
        protected PageData<T> doInBackground(Void... arg0) {
            return onLoadData(getFirstPageNum());
        }

        @Override
        protected void onPostExecute(PageData<T> data) {
            super.onPostExecute(data);
            String label = "更新于" + TimeUtils.formatTime(System.currentTimeMillis());
            // Update the LastUpdatedLabel
            mPtrListView.getHeaderLayout().setLastUpdatedLabel(label);
            mPtrListView.onRefreshComplete();
            onPullStartFinish(data);
        }
    }

    /**
     * 上拉加载下一页面数据
     */
    private class PullEndTask extends AsyncTask<Void, Void, PageData<T>> {

        @Override
        protected PageData<T> doInBackground(Void... arg0) {
            if (mLastPageData != null) {
                int page = mLastPageData.getPageNum() + 1;
                return onLoadData(page);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PageData<T> data) {
            super.onPostExecute(data);
            mPtrListView.onRefreshComplete();
            onPullEndFinish(data);
        }
    }
}