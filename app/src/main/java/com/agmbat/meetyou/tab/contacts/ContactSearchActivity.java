package com.agmbat.meetyou.tab.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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

import com.agmbat.android.SysResources;
import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.asmack.roster.ContactDBCache;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.isdialog.ISLoadingDialog;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.chat.ChatActivity;
import com.agmbat.meetyou.helper.AvatarHelper;
import com.agmbat.meetyou.widget.BaseRecyclerAdapter;
import com.agmbat.meetyou.widget.OnRecyclerViewItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactSearchActivity extends Activity {

    private static final String EXTRA_KEYWORD = "keyword";

    @BindView(R.id.contact_list)
    RecyclerView mResultListView;
    @BindView(R.id.result)
    TextView mResultView;

    private ContactsAdapter mContactAdapter;
    private String mKeyword;
    private ISLoadingDialog mLoadingDialog;
    private SearchTask mSearchTask;

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
        mContactAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ContactsAdapter.ContactViewHolder>() {
            @Override
            public void onItemClick(View view, int position, ContactsAdapter.ContactViewHolder viewHolder) {
                ContactInfo contactInfo = mContactAdapter.getItem(position);
                ChatActivity.openChat(getBaseContext(), contactInfo);
            }

            @Override
            public void onLongClick(View view, int position, ContactsAdapter.ContactViewHolder viewHolder) {

            }
        });
        mResultListView.setAdapter(mContactAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                outRect.top = (int) SysResources.dipToPixel(15);
            }
        });
    }

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

    @OnClick(R.id.title_btn_back)
    public void onClickBack() {
        finish();
    }

    private class SearchTask extends AsyncTask<Void, Void, List<ContactInfo>> {

        private String mKeyword;

        public SearchTask(String keyword) {
            mKeyword = keyword;
        }

        @Override
        protected void onPreExecute() {
            showSearchingDialog();
            mResultView.setVisibility(View.GONE);
        }

        @Override
        protected List<ContactInfo> doInBackground(Void... voids) {
            return ContactDBCache.searchContacts(mKeyword);
        }


        @Override
        protected void onPostExecute(List<ContactInfo> contactInfos) {
            mSearchTask = null;
            dismissSearchingDialog();
            if (null == contactInfos || contactInfos.size() == 0) {
                mResultView.setVisibility(View.VISIBLE);
                mResultListView.setVisibility(View.GONE);
            } else {
                mContactAdapter.setAll(contactInfos);
                mResultView.setVisibility(View.GONE);
                mResultListView.setVisibility(View.VISIBLE);
            }
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
