package org.jivesoftware.smackx.paid;

import java.util.ArrayList;

public class PaidPageInfoObject {
    private ArrayList<PageItem> coinsPageInfo;
    private ArrayList<PageItem> subscriptionPageInfo;
    public PaidPageInfoObject() {
        coinsPageInfo = new ArrayList<PageItem>();
        subscriptionPageInfo = new ArrayList<PageItem>();
    }

    public ArrayList<PageItem> getCoinsPageInfo() {
        return coinsPageInfo;
    }

    public void setCoinsPageInfo(ArrayList<PageItem> coinsPageInfo) {
        this.coinsPageInfo = coinsPageInfo;
    }

    public ArrayList<PageItem> getSubscriptionPageInfo() {
        return subscriptionPageInfo;
    }

    public void setSubscriptionPageInfo(ArrayList<PageItem> subscriptionPageInfo) {
        this.subscriptionPageInfo = subscriptionPageInfo;
    }

    public static class PageItem {
        public String title;
        public String description;
    }

}