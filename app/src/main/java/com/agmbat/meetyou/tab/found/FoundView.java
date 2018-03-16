package com.agmbat.meetyou.tab.found;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.agmbat.meetyou.R;

public class FoundView extends FrameLayout {

    public FoundView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.found_item, this);
    }
}
