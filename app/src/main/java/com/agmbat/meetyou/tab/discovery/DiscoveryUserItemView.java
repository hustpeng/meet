package com.agmbat.meetyou.tab.discovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.asmack.roster.ContactInfo;
import com.agmbat.meetyou.R;
import com.agmbat.meetyou.helper.AvatarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DiscoveryUserItemView extends FrameLayout {

    @BindView(R.id.icon)
    ImageView mImageView;

    @BindView(R.id.name)
    TextView mNameView;

    @BindView(R.id.location)
    TextView mLocationView;

    public DiscoveryUserItemView(@NonNull Context context) {
        super(context);
        init();
    }

    public DiscoveryUserItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.found_item_user, this);
        ButterKnife.bind(this, this);
    }

    /**
     * 设置数据信息
     *
     * @param contactInfo
     */
    public void bindUser(ContactInfo contactInfo) {
        String uri = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3109469113,4179252995&fm=27&gp=0.jpg";
        ImageManager.displayImage(uri, mImageView, AvatarHelper.getOptions());
        mNameView.setText(contactInfo.getNickName());
    }


}
