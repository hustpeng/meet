
package org.jivesoftware.smackx.reconnection;


public interface ReconnectionListener {
    public void notifyReconnectionStart();

    public void notifyReconnectionFailed();

    public void notifyReconnectionSuccess();

    public void notifyAutoLoginStart();

    public void notifyAutoLoginFailed();

    public void notifyAutoLoginSuccess();
}