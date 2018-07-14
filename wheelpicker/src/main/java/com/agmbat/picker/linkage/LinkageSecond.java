package com.agmbat.picker.linkage;

import java.util.List;

/**
 * 用于联动选择器展示的第二级条目
 *
 * @see LinkagePicker
 */
public interface LinkageSecond<Trd> extends LinkageItem {

    List<Trd> getThirds();

}