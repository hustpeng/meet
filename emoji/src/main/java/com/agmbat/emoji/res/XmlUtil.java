package com.agmbat.emoji.res;

import android.util.Xml;

import com.agmbat.emoji.DelBtnStatus;
import com.agmbat.emoji.EmojiBean;
import com.agmbat.emoji.panel.p2.EmoticonPageSetEntity;
import com.agmbat.io.IoUtils;
import com.nostra13.universalimageloader.core.download.Scheme;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XmlUtil {

    public EmoticonPageSetEntity ParserXml(String filePath, InputStream inStream) {

        String arrayParentKey = "EmoticonBean";
        boolean isChildCheck = false;

        EmoticonPageSetEntity emoticonPageSetEntity = new EmoticonPageSetEntity();
        ArrayList<EmojiBean> emoticonList = new ArrayList<>();
        emoticonPageSetEntity.setEmoticonList(emoticonList);
        EmojiBean emoticonBeanTemp = null;

        if (null != inStream) {
            XmlPullParser pullParser = Xml.newPullParser();
            try {
                pullParser.setInput(inStream, "UTF-8");
                int event = pullParser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            String skeyName = pullParser.getName();
                            /**
                             * EmoticonBeans data
                             */
                            if (isChildCheck && emoticonBeanTemp != null) {
                                if (skeyName.equals("eventType")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setEventType(Integer.parseInt(value));
                                    } catch (NumberFormatException e) {
                                    }
                                } else if (skeyName.equals("iconUri")) {
                                    String value = pullParser.nextText();
                                    String uri = Scheme.wrapUri("assets", filePath + "/" + value);
                                    emoticonBeanTemp.setIconUri(uri);
                                } else if (skeyName.equals("content")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setContent(value);
                                }
                            }
                            /**
                             * EmoticonSet data
                             */
                            else {
                                try {
                                    if (skeyName.equals("name")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setSetName(value);
                                    } else if (skeyName.equals("line")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setLine(Integer.parseInt(value));
                                    } else if (skeyName.equals("row")) {
                                        String value = pullParser.nextText();
                                        emoticonPageSetEntity.setRow(Integer.parseInt(value));
                                    } else if (skeyName.equals("iconUri")) {
                                        String value = pullParser.nextText();
                                        String uri = Scheme.wrapUri("assets", filePath + "/" + value);
                                        emoticonPageSetEntity.setIconUri(uri);
                                    } else if (skeyName.equals("isShowDelBtn")) {
                                        String value = pullParser.nextText();
                                        DelBtnStatus delBtnStatus;
                                        if (Integer.parseInt(value) == 1) {
                                            delBtnStatus = DelBtnStatus.FOLLOW;
                                        } else if (Integer.parseInt(value) == 2) {
                                            delBtnStatus = DelBtnStatus.LAST;
                                        } else {
                                            delBtnStatus = DelBtnStatus.GONE;
                                        }
                                        emoticonPageSetEntity.setShowDelBtn(delBtnStatus);
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (skeyName.equals(arrayParentKey)) {
                                isChildCheck = true;
                                emoticonBeanTemp = new EmojiBean();
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            String ekeyName = pullParser.getName();
                            if (isChildCheck && ekeyName.equals(arrayParentKey)) {
                                isChildCheck = false;
                                emoticonBeanTemp.setActionType(EmojiBean.ACTION_TYPE_BIG_IMAGE);
                                emoticonList.add(emoticonBeanTemp);
                            }
                            break;
                        default:
                            break;
                    }
                    event = pullParser.next();
                }
                return emoticonPageSetEntity;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IoUtils.closeQuietly(inStream);
            }
        }
        return emoticonPageSetEntity;
    }
}
