
package org.jivesoftware.smackx.visitor;

public interface VisitorMeListener {
    public void notifyVisitorMeChanged();

    public void notifyFetchVisitorMeResult(boolean success, String error);
    /**
     * 返回新Visitor的总数目
     */
    public void notifyNewVisitor(int count);
}