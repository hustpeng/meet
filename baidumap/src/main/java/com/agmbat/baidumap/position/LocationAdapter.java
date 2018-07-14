package com.agmbat.baidumap.position;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

/**
 * 地点列表适配器
 */
public class LocationAdapter extends ArrayAdapter<PoiInfo> {

    /**
     * 选中的item下标
     */
    private int mSelectItemIndex;

    public LocationAdapter(Context context, List<PoiInfo> objects) {
        super(context, 0, objects);
        // 默认第一个为选中项
        mSelectItemIndex = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new PositionView(getContext());
        }
        PoiInfo poiInfo = getItem(position);
        PositionView view = (PositionView) convertView;
        view.update(poiInfo, mSelectItemIndex == position);
        return convertView;
    }

    /**
     * 设置选中项
     *
     * @param selectItemIndex
     */
    public void setSelectItemIndex(int selectItemIndex) {
        mSelectItemIndex = selectItemIndex;
    }


    private static class LocatorViewHolder {


    }
}
