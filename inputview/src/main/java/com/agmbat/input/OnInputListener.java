package com.agmbat.input;

/**
 * 输入监听
 */
public interface OnInputListener {

    /**
     * 输入内容文本类型
     */
    public static final int TYPE_TEXT = 1;

    /**
     * 语音类型
     */
    public static final int TYPE_VOICE = 2;

    /**
     * 表情图片
     */
    public static final int TYPE_EMOJI_IMAGE = 3;

    /**
     * 图片
     */
    public static final int TYPE_IMAGE = 4;

    /**
     * 视频
     */
    public static final int TYPE_VIDEO = 5;

    /**
     * 回调输入的内容
     *
     * @param type
     * @param content
     */
    public void onInput(int type, String content);
}
