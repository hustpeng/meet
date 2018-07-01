package org.jivesoftware.smackx.ping;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class PingProvider implements IQProvider {

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        PingExtension pingExtension = new PingExtension();
        return pingExtension;
    }

}
