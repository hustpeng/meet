package com.agmbat.baidumap.position;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.baidu.mapapi.search.sug.SuggestionResult;

import java.util.List;

/**
 * poi地点列表适配器(搜索页面)
 */
public class SearchPositionAdapter extends ArrayAdapter<SuggestionResult.SuggestionInfo> {

    /**
     * 选中的item下标
     */
    private int mSelectItemIndex;

    public SearchPositionAdapter(Context context, List<SuggestionResult.SuggestionInfo> list) {
        super(context, 0, list);
        // 默认第一个为选中项
        mSelectItemIndex = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new PositionView(getContext());
        }
        SuggestionResult.SuggestionInfo suggestionInfo = getItem(position);
        PositionView view = (PositionView) convertView;
        view.update(suggestionInfo, mSelectItemIndex == position);
        return convertView;
    }

    public void setSelectSearchItemIndex(int selectItemIndex) {
        mSelectItemIndex = selectItemIndex;
    }

}
