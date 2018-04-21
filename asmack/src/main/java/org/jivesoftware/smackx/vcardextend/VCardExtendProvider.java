package org.jivesoftware.smackx.vcardextend;

import com.agmbat.text.StringParser;

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
                parseField(item, parserName, parser);
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("vCard".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        packet.setObject(item);
        return packet;
    }

    /**
     * 解析字段
     *
     * @param item
     * @param parserName
     * @param parser
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static void parseField(VCardExtendObject item, String parserName, XmlPullParser parser)
            throws IOException, XmlPullParserException {
        if (VCardExtendObject.KEY_HEIGHT.equals(parserName)) {
            item.setHeight(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_EDUCATION.equals(parserName)) {
            item.setEducation(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_WAGE.equals(parserName)) {
            item.setWage(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_WORKAREA.equals(parserName)) {
            item.setWorkarea(parser.nextText());
        } else if (VCardExtendObject.KEY_MARRIAGE.equals(parserName)) {
            item.setMarriage(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_WEIGHT.equals(parserName)) {
            item.setWeight(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_BIRTHPLACE.equals(parserName)) {
            item.setBirthplace(parser.nextText());
        } else if (VCardExtendObject.KEY_RESIDENCE.equals(parserName)) {
            item.setResidence(parser.nextText());
        } else if (VCardExtendObject.KEY_INDUSTRY.equals(parserName)) {
            item.setIndustry(parser.nextText());
        } else if (VCardExtendObject.KEY_CAREER.equals(parserName)) {
            item.setCareer(parser.nextText());
        } else if (VCardExtendObject.KEY_HOUSE.equals(parserName)) {
            item.setHouse(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_CAR.equals(parserName)) {
            item.setCar(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_HOBBY.equals(parserName)) {
            item.setHobby(parser.nextText());
        } else if (VCardExtendObject.KEY_INTRODUCE.equals(parserName)) {
            item.setIntroduce(parser.nextText());
        } else if (VCardExtendObject.KEY_DEMAND.equals(parserName)) {
            item.setDemand(parser.nextText());
        } else if (VCardExtendObject.KEY_STATUS.equals(parserName)) {
            item.setStatus(parser.nextText());
        }
    }

    private static ArrayList<String> getItemsOfParser(XmlPullParser parser, String nodeName) {
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
