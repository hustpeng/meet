package com.agmbat.meetyou.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.agmbat.android.utils.KeyboardUtils;
import com.agmbat.android.utils.ToastUtil;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.imsdk.search.SearchManager;
import com.agmbat.imsdk.search.group.GroupCategory;
import com.agmbat.imsdk.search.group.GroupCategoryResult;
import com.agmbat.imsdk.search.group.GroupInfo;
import com.agmbat.imsdk.search.group.OnGetGroupCategoryListener;
import com.agmbat.imsdk.search.group.SearchGroupResult;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.discovery.search.TagSelectedView;
import com.agmbat.pagedataloader.PageData;
import com.agmbat.pagedataloader.PageDataLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 找群的Activity界面
 */
public class GroupSearchActivity extends Activity {

    @BindView(R.id.tag_selected_view)
    TagSelectedView mTagSelectedView;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.input_text)
    EditText mEditText;

    @BindView(R.id.btn_search_group)
    ImageView mSearchButton;

    private GroupCategory mGroupCategory;
    private List<GroupCategory> mGroupCategoryList;

    private String mKeyword;

    /**
     * 分页数据加载器
     */
    private PageDataLoader mPageDataLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, getResources().getColor(R.color.bg_status_bar));
        setContentView(R.layout.activity_group_search);
        ButterKnife.bind(this);
        loadGroupCategories();
        mPageDataLoader = new DiscoveryPageLoader(this);
        mPageDataLoader.setupViews(findViewById(android.R.id.content));

//        mPageDataLoader.loadData();
    }

    private void loadGroupCategories() {
        List<GroupCategory> cachedGroupCategories = GroupDBCache.getGroupCategories();
        if ((null == cachedGroupCategories || cachedGroupCategories.size() == 0)) {
            mProgressBar.setVisibility(View.VISIBLE);
            mTagSelectedView.setVisibility(View.GONE);
            SearchManager.getGroupCategory(new OnGetGroupCategoryListener() {
                @Override
                public void onGetGroupCategory(GroupCategoryResult result) {
                    if (result.mResult && null != result.mData) {
                        GroupDBCache.saveGroupCategories(result.mData);
                        updateCategory(result.mData);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mTagSelectedView.setVisibility(View.VISIBLE);
                }

            });
        } else {
            updateCategory(cachedGroupCategories);
            mProgressBar.setVisibility(View.GONE);
            mTagSelectedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 点击返回键
     */
    @OnClick(R.id.title_btn_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.btn_search_group)
    void onClickSearch() {
        if (mGroupCategory == null) {
            ToastUtil.showToast("请选择群分类");
            return;
        }
        String keyword = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            ToastUtil.showToast("请输入关键字");
            return;
        }
        KeyboardUtils.hideInputMethod(mEditText);
        mKeyword = keyword;
        mPageDataLoader.loadData();
    }

    /**
     * 更新分类列表
     *
     * @param list
     */
    private void updateCategory(List<GroupCategory> list) {
        GroupCategory all = new GroupCategory();
        all.setId(0);
        all.setName("所有");
        list.add(0, all);
        mGroupCategoryList = list;
        mGroupCategory = all;
        List<String> textList = new ArrayList<>();
        for (GroupCategory groupCategory : list) {
            textList.add(groupCategory.getName());
        }
        mTagSelectedView.setVisibility(View.VISIBLE);
        mTagSelectedView.setTagList(textList);
        mTagSelectedView.setSelectedTag(mGroupCategory.getName());
        mTagSelectedView.setOnSelectedListener(new TagSelectedView.OnSelectedListener() {
            @Override
            public void onSelected(int index, String tag) {
                mGroupCategory = findGroupCategory(tag);
                if(!TextUtils.isEmpty(mKeyword)){
                    mPageDataLoader.loadData();
                }
            }
        });
    }


    private GroupCategory findGroupCategory(String name) {
        if (mGroupCategoryList != null) {
            for (GroupCategory groupCategory : mGroupCategoryList) {
                if (groupCategory.getName().equals(name)) {
                    return groupCategory;
                }
            }
        }
        return null;
    }

    public class DiscoveryPageLoader extends PageDataLoader<GroupInfo> {

        public DiscoveryPageLoader(Context context) {
            super(context);
        }

        @Override
        public void setupViews(View view) {
            super.setupViews(view);
        }

        @Override
        public SearchGroupResult onLoadData(int page) {
            if (TextUtils.isEmpty(mKeyword) || mGroupCategory == null) {
                return null;
            }
            return SearchManager.searchGroupSync(mKeyword, mGroupCategory.getId(), page);
        }

        @Override
        protected ArrayAdapter<GroupInfo> createListAdapter(Context context, PageData<GroupInfo> data) {
            return new GroupInfoAdapter(context, data.getDataList());
        }

        @Override
        protected void onLoadingError(PageData data) {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            super.onItemClick(parent, view, position, id);
            GroupInfo contactInfo = (GroupInfo) parent.getItemAtPosition(position);
            GroupInfoActivity.launch(GroupSearchActivity.this, contactInfo);
        }
    }
}
