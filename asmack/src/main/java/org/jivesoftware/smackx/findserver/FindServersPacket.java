package org.jivesoftware.smackx.findserver;

import org.jivesoftware.smack.packet.IQ;

/**
 * 发送查询请求的包
 */
public class FindServersPacket extends IQ {

    public FindServersPacket(String serviceName) {
        setTo(serviceName);
    }

    public String getChildElementXML() {
        return new StringBuffer()
                .append("<")
                .append(FindServerProvider.elementName())
                .append(" xmlns=\"")
                .append(FindServerProvider.namespace())
                .append("\"/>")
                .toString();
    }
}