package org.jivesoftware.smackx.vcardextend;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class VCardExtendProvider implements IQProvider {

    public static String elementName() {
        return "vCard";
    }

    public static String namespace() {
        return "vcard-extended";
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {
        VCardExtendPacket packet = new VCardExtendPacket();
        VCardExtendObject item = new VCardExtendObject();

        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG) {
                String parserName = parser.getName();

                if ("BIRTHDAY".equals(parserName)) {
                    item.setBirthday(parser.nextText());
                } else if ("ASTROLOGICAL".equals(parserName)) {
                    item.setAstrological(parser.nextText());
                } else if ("ETHNICITY".equals(parserName)) {
                    item.setEthnicity(parser.nextText());
                } else if ("HEIGHT".equals(parserName)) {
                    item.setHeight(parser.nextText());
                } else if ("WEIGHT".equals(parserName)) {
                    item.setWeight(parser.nextText());
                } else if ("BODYTYPE".equals(parserName)) {
                    item.setBodyType(parser.nextText());
                } else if ("RELATIONSHIP".equals(parserName)) {
                    item.setRelationship(parser.nextText());
                } else if ("PERSONALITY".equals(parserName)) {
                    item.setPersonality(getItemsOfParser(parser, parserName));
                } else if ("LOOKINGFOR".equals(parserName)) {
                    item.setLookingFor(getItemsOfParser(parser, parserName));
                } else if ("PERSONALITY".equals(parserName)) {
                    item.setPersonality(getItemsOfParser(parser, parserName));
                } else if ("LANGUAGE".equals(parserName)) {
                    item.setLanguage(getItemsOfParser(parser, parserName));
                } else if ("PUBLICPHOTOS".equals(parserName)) {
                    item.setPublicPhotos(getItemsOfParser(parser, parserName));
                } else if ("PRIVATE".equals(parserName)) {
                    item.setHasPrivateInfo(true);
                } else if ("SEXUALPOSITION".equals(parserName)) {
                    item.setSexualPosition(parser.nextText());
                } else if ("ORIENTATION".equals(parserName)) {
                    item.setOrientation(parser.nextText());
                } else if ("HIV".equals(parserName)) {
                    item.setHIVStatus(parser.nextText());
                } else if ("PRIVATEPHOTOS".equals(parserName)) {
                    item.setPrivatePhotos(getItemsOfParser(parser, parserName));
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("vCard".equals(parser.getName())) {
                    done = true;
                }
            }
        }

        packet.setObject(item);
        return packet;
    }

    private ArrayList<String> getItemsOfParser(XmlPullParser parser, String nodeName) {
        if (nodeName == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();
        boolean done = false;
        try {
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG) {
                    if ("V".equals(parser.getName())) {
                        list.add(parser.nextText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (nodeName.equals(parser.getName())) {
                        done = true;
                    }
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
