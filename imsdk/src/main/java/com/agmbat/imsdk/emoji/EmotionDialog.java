package com.agmbat.imsdk.emoji;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.agmbat.android.AppResources;
import com.agmbat.imsdk.R;

import java.util.List;

public class EmotionDialog extends Dialog {

    private static final int EMOTION_WIDTH = (int) AppResources.dipToPixel(50);
    private static final int EMOTION_HEIGHT = (int) AppResources.dipToPixel(45);

    private OnSelectedEmojiListener mOnSelectedEmojiListener;
    private TableLayout mEmotionTable;

    public EmotionDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(R.drawable.im_bg_emotion_dialog_bottom);
        // 确保对话框可以撑满屏幕宽度
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.im_emotion_dialog);
        mEmotionTable = (TableLayout) findViewById(R.id.emotion_table);
        initEmotionTable();
    }

    public void setOnSelectedEmojiListener(OnSelectedEmojiListener l) {
        mOnSelectedEmojiListener = l;
    }

    private void initEmotionTable() {
        List<EmojiObject> emojiList = Emotion.getEmojiList();

        TableRow row = null;
        int columns = 6;
        int rows = 0;

        int length = emojiList.size();

        if (length % columns != 0) {
            rows = length / columns + 1;
        } else {
            rows = length / columns;
        }
        for (int i = 0; i < length; i++) {
            int columnIndex = i % columns;
            int rowIndex = i / columns;
            if (columnIndex == 0) {
                row = new TableRow(getContext());
                TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                row.setLayoutParams(params);
            }
            row.addView(getEmotionImageView(emojiList.get(i)));

            if (columnIndex != columns - 1) {
                row.addView(getVetrialDivider());
            } else {
                mEmotionTable.addView(row);

                if (rowIndex != rows - 1) {
                    mEmotionTable.addView(getHorizonalDivider());
                }
            }
        }

    }

    private ImageView getVetrialDivider() {
        ImageView dividerView = new ImageView(getContext());
        TableRow.LayoutParams dividerParams = new TableRow.LayoutParams();
        dividerParams.width = (int) AppResources.dipToPixel(1);
        dividerParams.height = TableRow.LayoutParams.MATCH_PARENT;
        dividerView.setLayoutParams(dividerParams);
        dividerView.setBackgroundResource(R.drawable.emotion_divider);
        return dividerView;
    }

    private ImageView getHorizonalDivider() {
        ImageView dividerView = new ImageView(getContext());
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams();
        layoutParams.height = (int) AppResources.dipToPixel(1);
        dividerView.setLayoutParams(layoutParams);
        dividerView.setBackgroundResource(R.drawable.emotion_divider);
        return dividerView;
    }

    private ImageButton getEmotionImageView(EmojiObject object) {
        ImageButton emotionBtn = new ImageButton(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.width = EMOTION_WIDTH;
        layoutParams.height = EMOTION_HEIGHT;
        layoutParams.weight = 1.0F;
        emotionBtn.setLayoutParams(layoutParams);
        emotionBtn.setId(object.mResId);
        emotionBtn.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        emotionBtn.setImageResource(object.mResId);
        emotionBtn.setTag(object.mCodeName);
        emotionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnSelectedEmojiListener != null) {
                    String emoji = (String) v.getTag();
                    mOnSelectedEmojiListener.onSelectedEmoji(emoji);
                }
            }
        });
        return emotionBtn;
    }

    public interface OnSelectedEmojiListener {

        public void onSelectedEmoji(String emoji);
    }

}
