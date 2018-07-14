package com.agmbat.picker.linkage;

import java.util.List;

/**
 * 用于联动选择器展示的第一级条目
 *
 * @see LinkagePicker
 */
public interface LinkageFirst<Snd> extends LinkageItem {

    List<Snd> getSeconds();

}