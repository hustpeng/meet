
package org.jivesoftware.smackx.paid;

import java.util.Date;

public class PaidAccountObject {

    private int coins;
    private boolean isSubscriptionVaild;
    private Date subscriptionTime;

    public static String getXmlNode(PaidAccountObject object)
    {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("<");
        buf.append(PaidAccountProvider.elementName());
        buf.append(" xmlns=\"");
        buf.append(PaidAccountProvider.namespace());
        buf.append("\">");

        buf.append("<profile><coins balance=\"");
        buf.append(object.getCoins());
        buf.append("\"/><subcription expired=\"");
        buf.append(object.isSubscriptionVaild());
        buf.append("\" expireddate=\"");
        buf.append(object.getSubscriptionTime().toString());
        buf.append("\"/></profile>");

        buf.append("</");
        buf.append(PaidAccountProvider.elementName());
        buf.append(">");
        return buf.toString();
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean isSubscriptionVaild() {
        return isSubscriptionVaild;
    }

    public void setSubscriptionVaild(boolean isSubscriptionVaild) {
        this.isSubscriptionVaild = isSubscriptionVaild;
    }

    public Date getSubscriptionTime() {
        return subscriptionTime;
    }

    public void setSubscriptionTime(Date subscriptionTime) {
        this.subscriptionTime = subscriptionTime;
    }
}