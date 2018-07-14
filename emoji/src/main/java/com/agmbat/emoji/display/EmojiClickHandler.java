package com.agmbat.emoji.display;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.agmbat.android.utils.KeyboardUtils;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.panel.EmoticonClickListener;

public class EmojiClickHandler implements EmoticonClickListener {

    private EditText mEditText;

    public EmojiClickHandler(EditText editText) {
        mEditText = editText;
    }

    @Override
    public void onEmoticonClick(EmojiBean o, boolean isDelBtn) {
        if (isDelBtn) {
            KeyboardUtils.delClick(mEditText);
        } else {
            if (o == null) {
                return;
            }
            int actionType = o.getActionType();
            if (actionType == EmojiBean.ACTION_TYPE_BIG_IMAGE) {
//                OnSendImage(((EmojiBean) o).getIconUri());
            } else {
                String content = o.getContent();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                int index = mEditText.getSelectionStart();
                Editable editable = mEditText.getText();
                editable.insert(index, content);
            }
        }
    }


//    public static EmoticonClickListener getCommonEmoticonClickListener(final EditText editText) {
//        return new EmoticonClickListener() {
//            @Override
//            public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {
//                if (isDelBtn) {
//                    SimpleCommonUtils.delClick(editText);
//                } else {
//                    if (o == null) {
//                        return;
//                    }
//                    if (actionType == Constants.EMOTICON_CLICK_TEXT) {
//                        String content = null;
//                        if (o instanceof EmojiBean) {
//                            content = ((EmojiBean) o).emoji;
//                        } else if (o instanceof EmojiBean) {
//                            content = ((EmojiBean) o).getContent();
//                        }
//
//                        if (TextUtils.isEmpty(content)) {
//                            return;
//                        }
//                        int index = editText.getSelectionStart();
//                        Editable editable = editText.getText();
//                        editable.insert(index, content);
//                    }
//                }
//            }
//        };
//    }

}
