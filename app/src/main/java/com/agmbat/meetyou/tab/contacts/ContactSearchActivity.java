package com.agmbat.meetyou.tab.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.asmack.roster.ContactDBCache;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.widget.BaseRecyclerAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactSearchActivity extends Activity {

    private static final String EXTRA_KEYWORD = "keyword";

    @BindView(R.id.contact_list)
    RecyclerView mResultListView;
    @BindView(R.id.result)
    TextView mResultView;

    private ContactsAdapter mContactAdapter;
    private String mKeyword;

    public static void launch(Context context, String keyword) {
        Intent intent = new Intent(context, ContactSearchActivity.class);
        intent.putExtra(EXTRA_KEYWORD, keyword);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKeyword = getIntent().getStringExtra(EXTRA_KEYWORD);
        setContentView(R.layout.activity_contact_search);
        ButterKnife.bind(this);
        initContentView();
        searchContacts(mKeyword);
    }


    private void initContentView() {
        mResultListView.setLayoutManager(new GridLayoutManager(getBaseContext(), 4));
        mContactAdapter = new ContactsAdapter();
        mResultListView.setAdapter(mContactAdapter);
    }

    private ISLoadingDialog mLoadingDialog;
    private SearchTask mSearchTask;

    private void searchContacts(String keyword) {
        if (null == mSearchTask) {
            mSearchTask = new SearchTask(keyword);
            mSearchTask.execute();
        }
    }

    private void showSearchingDialog() {
        if (null == mLoadingDialog || !mLoadingDialog.isShowing()) {
            mLoadingDialog = new ISLoadingDialog(this);
            mLoadingDialog.setMessage("搜索中");
            mLoadingDialog.show();
        }
    }

    private void dismissSearchingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }


    private class SearchTask extends AsyncTask<Void, Void, List<ContactInfo>> {

        private String mKeyword;

        public SearchTask(String keyword) {
            mKeyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            showSearchingDialog();
        }

        @Override
        protected List<ContactInfo> doInBackground(Void... voids) {
            return ContactDBCache.searchContacts(mKeyword);
        }


        @Override
        protected void onPostExecute(List<ContactInfo> contactInfos) {
            mSearchTask = null;
            dismissSearchingDialog();
            mContactAdapter.setAll(contactInfos);
        }
    }


    private class ContactsAdapter extends BaseRecyclerAdapter<ContactInfo, ContactsAdapter.ContactViewHolder> {


        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_group_member_item, null, false);
            return new ContactViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            holder.bindData(getItem(position));
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {

            private ImageView mAvatarView;
            private TextView mNameView;

            public ContactViewHolder(View itemView) {
                super(itemView);
                mAvatarView = (ImageView) itemView.findViewById(R.id.avatar);
                mNameView = (TextView) itemView.findViewById(R.id.name);
            }

            public void bindData(ContactInfo contactInfo) {
                ImageManager.displayImage(contactInfo.getAvatar(), mAvatarView, AvatarHelper.getGroupOptions());
                mNameView.setText(contactInfo.getNickName());
            }
        }
    }


}
