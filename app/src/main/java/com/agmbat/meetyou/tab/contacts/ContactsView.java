package com.agmbat.meetyou.tab.contacts;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agmbat.android.image.ImageManager;
import com.agmbat.imsdk.data.ContactInfo;
import com.agmbat.meetyou.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

public class ContactsView extends RelativeLayout {

    public ImageView mAvatarView;
    public TextView mNameView;

    public ContactsView(Context context) {
        super(context);
        View.inflate(context, R.layout.contacts_item, this);
        mAvatarView = (ImageView) findViewById(R.id.avatar);
        mNameView = (TextView) findViewById(R.id.name);
    }

    public void update(ContactInfo contactInfo) {
        mNameView.setText(contactInfo.getNickName());
        String uri = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3109469113,4179252995&fm=27&gp=0.jpg";
        ImageManager.displayImage(uri, mAvatarView, getOptions());
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
