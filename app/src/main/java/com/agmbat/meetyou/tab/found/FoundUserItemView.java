package com.agmbat.meetyou.tab.found;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;


public class FoundUserItemView extends FrameLayout {

    private ImageView mImageView;
    private TextView mNameView;
    private TextView mLocationView;

    public FoundUserItemView(@NonNull Context context) {
        super(context);
        init();
    }

    public FoundUserItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.found_item_user, this);
        mImageView = (ImageView) findViewById(R.id.icon);
        mNameView = (TextView) findViewById(R.id.name);
        mLocationView = (TextView) findViewById(R.id.location);
    }

    /**
     * 设置数据信息
     *
     * @param contactInfo
     */
    public void bindUser(ContactInfo contactInfo) {
        String uri = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3109469113,4179252995&fm=27&gp=0.jpg";
        ImageManager.displayImage(uri, mImageView, getOptions());
        mNameView.setText(contactInfo.getNickName());
    }

    /**
     * 图片显示参数
     */
    private static DisplayImageOptions sOptions = null;

    private static DisplayImageOptions getOptions() {
        if (sOptions == null) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            // builder.showImageOnLoading(R.drawable.ic_stub);
            int drawableId = R.drawable.ic_default_avatar;
            builder.showImageForEmptyUri(drawableId);
            builder.showImageOnFail(drawableId);
            builder.cacheInMemory(true);
            builder.cacheOnDisk(true);
            builder.considerExifParams(false);
//            builder.displayer(new SimpleBitmapDisplayer()); // new RoundedBitmapDisplayer(20)
            builder.displayer(new CircleBitmapDisplayer()); // new RoundedBitmapDisplayer(20)
            sOptions = builder.build();
        }
        return sOptions;
    }
}
