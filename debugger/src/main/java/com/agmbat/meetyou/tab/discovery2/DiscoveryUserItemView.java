package com.agmbat.meetyou.tab.discovery2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.debugger.R;
import com.agmbat.imsdk.asmack.roster.ContactInfo;


public class DiscoveryUserItemView extends FrameLayout {

    private ImageView mImageView;

    private TextView mNameView;

    private TextView mLocationView;

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
        mImageView = findViewById(R.id.icon);
        mNameView = findViewById(R.id.name);
        mLocationView = findViewById(R.id.location);
    }

    /**
     * 设置数据信息
     *
     * @param contactInfo
     */
    public void bindUser(ContactInfo contactInfo) {
        String uri = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3109469113,4179252995&fm=27&gp=0.jpg";
        // , AvatarHelper.getOptions()
        ImageManager.displayImage(uri, mImageView);
        mNameView.setText(contactInfo.getNickName());
    }


}
