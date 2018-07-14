package com.agmbat.baidumap.position;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.baidumap.R;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.sug.SuggestionResult;

public class PositionView extends LinearLayout {

    /**
     * 名称
     */
    private TextView mPoiNameView;

    /**
     * 地址
     */
    private TextView mPoiAddressView;

    /**
     * 选中的按钮
     */
    private ImageView mCurrentPointView;


    public PositionView(Context context) {
        super(context);
        View.inflate(getContext(), R.layout.map_location_item_poi, this);
        mPoiNameView = (TextView) findViewById(R.id.tv_poi_name);
        mPoiAddressView = (TextView) findViewById(R.id.tv_poi_address);
        mCurrentPointView = (ImageView) findViewById(R.id.img_cur_point);
    }

    public void update(PoiInfo poiInfo, boolean selected) {
        mPoiAddressView.setText(poiInfo.address);
        mPoiNameView.setText(poiInfo.name);
        if (selected) {
            mCurrentPointView.setImageResource(R.drawable.position_is_select);
        } else {
            mCurrentPointView.setImageDrawable(null);
        }
    }

    public void update(SuggestionResult.SuggestionInfo suggestionInfo, boolean selected) {
        mPoiAddressView.setText(suggestionInfo.city + suggestionInfo.district);
        mPoiNameView.setText(suggestionInfo.key);
        if (selected) {
            mCurrentPointView.setImageResource(R.drawable.position_is_select);
        } else {
            mCurrentPointView.setImageDrawable(null);
        }
    }
}
