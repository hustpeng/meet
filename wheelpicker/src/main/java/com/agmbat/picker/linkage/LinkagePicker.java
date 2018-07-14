package com.agmbat.picker.linkage;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agmbat.picker.WheelPicker;
import com.agmbat.picker.wheelview.WheelView;

import java.util.List;

/**
 * 两级、三级联动选择器。默认只初始化第一级数据，第二三级数据由联动获得。
 * <p/>
 *
 * @see Provider
 * @see DataProvider
 */
public class LinkagePicker<Fst extends LinkageFirst<Snd>, Snd extends LinkageSecond<Trd>, Trd> extends WheelPicker {

    protected Fst selectedFirstItem;
    protected Snd selectedSecondItem;
    protected Trd selectedThirdItem;

    protected String firstLabel = "", secondLabel = "", thirdLabel = "";
    protected int selectedFirstIndex = 0, selectedSecondIndex = 0, selectedThirdIndex = 0;
    protected Provider provider;
    protected float firstColumnWeight = 1.0f;//第一级显示的宽度比重
    protected float secondColumnWeight = 1.0f;//第二级显示的宽度比重
    protected float thirdColumnWeight = 1.0f;//第三级显示的宽度比重

    private OnPickListener onPickListener;
    private OnWheelLinkageListener onWheelLinkageListener;

    public LinkagePicker(Context context) {
        super(context);
    }

    public LinkagePicker(Context context, DataProvider provider) {
        super(context);
        this.provider = provider;
    }

    public LinkagePicker(Context context, Provider<Fst, Snd, Trd> provider) {
        super(context);
        this.provider = provider;
    }

    protected void setProvider(DataProvider provider) {
        this.provider = provider;
    }

    protected void setProvider(Provider<Fst, Snd, Trd> provider) {
        this.provider = provider;
    }

    public void setSelectedIndex(int firstIndex, int secondIndex) {
        setSelectedIndex(firstIndex, secondIndex, 0);
    }

    public void setSelectedIndex(int firstIndex, int secondIndex, int thirdIndex) {
        selectedFirstIndex = firstIndex;
        selectedSecondIndex = secondIndex;
        selectedThirdIndex = thirdIndex;
    }

    public void setSelectedItem(Fst fst, Snd snd) {
        setSelectedItem(fst, snd, null);
    }

    public void setSelectedItem(Fst fst, Snd snd, Trd trd) {
        if (null == provider) {
            throw new IllegalArgumentException("please set data provider at first");
        }
        //noinspection unchecked
        List<Fst> fsts = provider.initFirstData();
        int i = 0;
        for (Fst f : fsts) {
            if (f.equals(fst)) {
                selectedFirstIndex = i;
                break;
            } else if (f.getId().equals(fst.getId()) || f.getName().contains(fst.getName())) {
                selectedFirstIndex = i;
                break;
            }
            i++;
        }
        //noinspection unchecked
        List<Snd> snds = provider.linkageSecondData(selectedFirstIndex);
        int j = 0;
        for (Snd s : snds) {
            if (s.equals(snd)) {
                selectedFirstIndex = i;
                break;
            } else if (s.getId().equals(snd.getId()) || s.getName().contains(snd.getName())) {
                selectedSecondIndex = j;
                break;
            }
            j++;
        }
        if (provider.isOnlyTwo()) {
            return;//仅仅二级联动
        }
        //noinspection unchecked
        List<Trd> trds = provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex);
        int k = 0;
        for (Trd t : trds) {
            if (t.equals(trd)) {
                selectedThirdIndex = k;
                break;
            } else if (t instanceof LinkageThird) {
                LinkageThird ltrd = (LinkageThird) trd;
                LinkageThird lt = (LinkageThird) t;
                if (lt.getId().equals(ltrd.getId()) || lt.getName().contains(ltrd.getName())) {
                    selectedThirdIndex = k;
                    break;
                }
            }
            k++;
        }
    }

    public void setLabel(String firstLabel, String secondLabel) {
        setLabel(firstLabel, secondLabel, "");
    }

    public void setLabel(String firstLabel, String secondLabel, String thirdLabel) {
        this.firstLabel = firstLabel;
        this.secondLabel = secondLabel;
        this.thirdLabel = thirdLabel;
    }

    public Fst getSelectedFirstItem() {
        if (selectedFirstItem == null) {
            //noinspection unchecked
            selectedFirstItem = (Fst) provider.initFirstData().get(selectedFirstIndex);
        }
        return selectedFirstItem;
    }

    public Snd getSelectedSecondItem() {
        if (selectedSecondItem == null) {
            //noinspection unchecked
            selectedSecondItem = (Snd) provider.linkageSecondData(selectedFirstIndex).get(selectedSecondIndex);
        }
        return selectedSecondItem;
    }

    public Trd getSelectedThirdItem() {
        if (selectedThirdItem == null) {
            List<Trd> thirdData = provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex);
            if (thirdData.size() > 0) {
                selectedThirdItem = thirdData.get(selectedThirdIndex);
            }
        }
        return selectedThirdItem;
    }

    public int getSelectedFirstIndex() {
        return selectedFirstIndex;
    }

    public int getSelectedSecondIndex() {
        return selectedSecondIndex;
    }

    public int getSelectedThirdIndex() {
        return selectedThirdIndex;
    }

    /**
     * 设置每列的宽度比例，将屏幕分为三列，每列范围为0.0～1.0，如0.3333表示约占宽度的三分之一。
     */
    public void setColumnWeight(@FloatRange(from = 0, to = 1) float firstColumnWeight,
                                @FloatRange(from = 0, to = 1) float secondColumnWeight,
                                @FloatRange(from = 0, to = 1) float thirdColumnWeight) {
        this.firstColumnWeight = firstColumnWeight;
        this.secondColumnWeight = secondColumnWeight;
        this.thirdColumnWeight = thirdColumnWeight;
    }

    /**
     * 设置每列的宽度比例，将屏幕分为两列，每列范围为0.0～1.0，如0.5表示占宽度的一半。
     */
    public void setColumnWeight(@FloatRange(from = 0, to = 1) float firstColumnWeight,
                                @FloatRange(from = 0, to = 1) float secondColumnWeight) {
        this.firstColumnWeight = firstColumnWeight;
        this.secondColumnWeight = secondColumnWeight;
        this.thirdColumnWeight = 0;
    }

    /**
     * 设置滑动过程数据联动监听器
     */
    public void setOnWheelLinkageListener(OnWheelLinkageListener onWheelLinkageListener) {
        this.onWheelLinkageListener = onWheelLinkageListener;
    }

    /**
     * 设置完成选泽监听器
     */
    public void setOnPickListener(OnPickListener<Fst, Snd, Trd> onPickListener) {
        this.onPickListener = onPickListener;
    }

    /**
     * 设置完成选泽监听器
     */
    public void setOnStringPickListener(OnStringPickListener onStringPickListener) {
        this.onPickListener = onStringPickListener;
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        if (null == provider) {
            throw new IllegalArgumentException("please set data provider before make view");
        }
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        final WheelView firstView = createWheelView();
        firstView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, firstColumnWeight));
        layout.addView(firstView);
        if (!TextUtils.isEmpty(firstLabel)) {
            TextView labelView = createLabelView();
            labelView.setText(firstLabel);
            layout.addView(labelView);
        }

        final WheelView secondView = createWheelView();
        secondView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, secondColumnWeight));
        layout.addView(secondView);
        if (!TextUtils.isEmpty(secondLabel)) {
            TextView labelView = createLabelView();
            labelView.setText(secondLabel);
            layout.addView(labelView);
        }

        final WheelView thirdView = createWheelView();
        if (!provider.isOnlyTwo()) {
            thirdView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, thirdColumnWeight));
            layout.addView(thirdView);
            if (!TextUtils.isEmpty(thirdLabel)) {
                TextView labelView = createLabelView();
                labelView.setText(thirdLabel);
                layout.addView(labelView);
            }
        }

        firstView.setItems(provider.initFirstData(), selectedFirstIndex);
        firstView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                //noinspection unchecked
                selectedFirstItem = (Fst) provider.initFirstData().get(index);
                selectedFirstIndex = index;
                selectedSecondIndex = 0;//重置第二级索引
                selectedThirdIndex = 0;//重置第三级索引
                //根据第一级数据获取第二级数据
                //noinspection unchecked
                List<Snd> snds = provider.linkageSecondData(selectedFirstIndex);
                selectedSecondItem = snds.get(selectedSecondIndex);
                secondView.setItems(snds, selectedSecondIndex);
                if (!provider.isOnlyTwo()) {
                    //根据第二级数据获取第三级数据
                    //noinspection unchecked
                    List<Trd> trds = provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex);
                    selectedThirdItem = trds.get(selectedThirdIndex);
                    thirdView.setItems(trds, selectedThirdIndex);
                }
                if (onWheelLinkageListener != null) {
                    onWheelLinkageListener.onLinkage(selectedFirstIndex, 0, 0);
                }
            }
        });

        secondView.setItems(provider.linkageSecondData(selectedFirstIndex), selectedSecondIndex);
        secondView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                //noinspection unchecked
                selectedSecondItem = (Snd) provider.linkageSecondData(selectedFirstIndex).get(index);
                selectedSecondIndex = index;
                if (!provider.isOnlyTwo()) {
                    selectedThirdIndex = 0;//重置第三级索引
                    //noinspection unchecked
                    List<Trd> trds = provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex);
                    selectedThirdItem = trds.get(selectedThirdIndex);
                    //根据第二级数据获取第三级数据
                    thirdView.setItems(trds, selectedThirdIndex);
                }
                if (onWheelLinkageListener != null) {
                    onWheelLinkageListener.onLinkage(selectedFirstIndex, selectedSecondIndex, 0);
                }
            }
        });
        if (provider.isOnlyTwo()) {
            return layout;//仅仅二级联动
        }

        thirdView.setItems(provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex), selectedThirdIndex);
        thirdView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                //noinspection unchecked
                selectedThirdItem = (Trd) provider.linkageThirdData(selectedFirstIndex, selectedSecondIndex).get(index);
                selectedThirdIndex = index;
                if (onWheelLinkageListener != null) {
                    onWheelLinkageListener.onLinkage(selectedFirstIndex, selectedSecondIndex, selectedThirdIndex);
                }
            }
        });
        return layout;
    }

    @Override
    public void onSubmit() {
        Fst fst = getSelectedFirstItem();
        Snd snd = getSelectedSecondItem();
        Trd trd = getSelectedThirdItem();
        if (provider.isOnlyTwo()) {
            if (onPickListener != null) {
                //noinspection unchecked
                onPickListener.onPicked(fst, snd, null);
            }
        } else {
            if (onPickListener != null) {
                //noinspection unchecked
                onPickListener.onPicked(fst, snd, trd);
            }
        }
    }

    /**
     * 数据选择完成监听器
     */
    public interface OnPickListener<Fst, Snd, Trd> {

        void onPicked(Fst first, Snd second, Trd third);

    }

    /**
     * 滑动过程数据联动监听器
     */
    public interface OnWheelLinkageListener {

        void onLinkage(int firstIndex, int secondIndex, int thirdIndex);

    }


}
