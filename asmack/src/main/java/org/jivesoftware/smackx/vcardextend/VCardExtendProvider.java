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
            item.setEducation(parser.nextText());
        } else if (VCardExtendObject.KEY_WAGE.equals(parserName)) {
            item.setWage(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_WORKAREA.equals(parserName)) {
            item.setWorkarea(parser.nextText());
        } else if (VCardExtendObject.KEY_MARRIAGE.equals(parserName)) {
            item.setMarriage(parser.nextText());
        } else if (VCardExtendObject.KEY_WEIGHT.equals(parserName)) {
            item.setWeight(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_BIRTHPLACE.equals(parserName)) {
            item.setBirthplace(parser.nextText());
        } else if (VCardExtendObject.KEY_RESIDENCE.equals(parserName)) {
            item.setResidence(parser.nextText());
        } else if (VCardExtendObject.KEY_INDUSTRY.equals(parserName)) {
            item.setIndustry(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_CAREER.equals(parserName)) {
            item.setCareer(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_HOUSE.equals(parserName)) {
            item.setWage(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_CAR.equals(parserName)) {
            item.setCar(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_HOBBY.equals(parserName)) {
            item.setHobby(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_INTRODUCE.equals(parserName)) {
            item.setIntroduce(parser.nextText());
        } else if (VCardExtendObject.KEY_DEMAND.equals(parserName)) {
            item.setDemand(StringParser.parseInt(parser.nextText()));
        } else if (VCardExtendObject.KEY_STATUS.equals(parserName)) {
            item.setStatus(parser.nextText());
        }

//        if ("BIRTHDAY".equals(parserName)) {
//            item.setBirthday(parser.nextText());
//        } else if ("ASTROLOGICAL".equals(parserName)) {
//            item.setAstrological(parser.nextText());
//        } else if ("ETHNICITY".equals(parserName)) {
//            item.setEthnicity(parser.nextText());
//        } else if ("HEIGHT".equals(parserName)) {
//            item.setHeight(StringParser.parseInt(parser.nextText()));
//        } else if ("WEIGHT".equals(parserName)) {
//            item.setWeight(parser.nextText());
//        } else if ("BODYTYPE".equals(parserName)) {
//            item.setBodyType(parser.nextText());
//        } else if ("RELATIONSHIP".equals(parserName)) {
//            item.setRelationship(parser.nextText());
//        } else if ("PERSONALITY".equals(parserName)) {
//            item.setPersonality(getItemsOfParser(parser, parserName));
//        } else if ("LOOKINGFOR".equals(parserName)) {
//            item.setLookingFor(getItemsOfParser(parser, parserName));
//        } else if ("PERSONALITY".equals(parserName)) {
//            item.setPersonality(getItemsOfParser(parser, parserName));
//        } else if ("LANGUAGE".equals(parserName)) {
//            item.setLanguage(getItemsOfParser(parser, parserName));
//        } else if ("PUBLICPHOTOS".equals(parserName)) {
//            item.setPublicPhotos(getItemsOfParser(parser, parserName));
//        } else if ("PRIVATE".equals(parserName)) {
//            item.setHasPrivateInfo(true);
//        } else if ("SEXUALPOSITION".equals(parserName)) {
//            item.setSexualPosition(parser.nextText());
//        } else if ("ORIENTATION".equals(parserName)) {
//            item.setOrientation(parser.nextText());
//        } else if ("HIV".equals(parserName)) {
//            item.setHIVStatus(parser.nextText());
//        } else if ("PRIVATEPHOTOS".equals(parserName)) {
//            item.setPrivatePhotos(getItemsOfParser(parser, parserName));
//        }
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
