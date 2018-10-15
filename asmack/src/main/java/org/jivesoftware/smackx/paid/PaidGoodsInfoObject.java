package org.jivesoftware.smackx.paid;

import java.util.ArrayList;

public class PaidGoodsInfoObject {
    private ArrayList<PaidGoodItem> goodsInfo;

    public PaidGoodsInfoObject() {
        setGoodsInfo(new ArrayList<PaidGoodItem>());
    }

    public ArrayList<PaidGoodItem> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(ArrayList<PaidGoodItem> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public static class PaidGoodItem {
        public String title;
        public String description;
        public double price;
    }
}