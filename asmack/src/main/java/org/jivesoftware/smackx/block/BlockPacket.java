package org.jivesoftware.smackx.block;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.IQ;

import java.util.ArrayList;

public class BlockPacket extends IQ {
    private final ArrayList<BlockObject> BlockItems = new ArrayList<BlockObject>();

    private String mDefaultName;
    private String mListName;
    private String mActiveName;

    public void AddBlockItem(BlockObject item) {
        synchronized (BlockItems) {
            BlockItems.add(item);
        }
    }

    public ArrayList<BlockObject> getBlockItems() {
        synchronized (BlockItems) {
            return BlockItems;
        }
    }

    public String getListName() {
        if (null == mListName) {
            return "";
        }
        return mListName;
    }

    public void setListName(String name) {
        mListName = name;
    }

    public String getDefaultName() {
        if (null == mDefaultName) {
            return "";
        }
        return mDefaultName;
    }

    public void setDefaultName(String name) {
        mDefaultName = name;
    }

    public String getActiveName() {
        if (null == mActiveName) {
            return "";
        }
        return mActiveName;
    }

    public void setActiveName(String name) {
        mActiveName = name;
    }

    @Override
    public String getChildElementXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<");
        buffer.append(BlockProvider.elementName());
        buffer.append(" xmlns=\"");
        buffer.append(BlockProvider.namespace());
        if (TextUtils.isEmpty(mListName)) {
            buffer.append("\"/>");
        } else {
            buffer.append("\">");
            buffer.append("<list name=\"");
            buffer.append(mListName);
            buffer.append("\"/></");
            buffer.append(BlockProvider.elementName());
            buffer.append(">");

        }
        return buffer.toString();
    }

}
