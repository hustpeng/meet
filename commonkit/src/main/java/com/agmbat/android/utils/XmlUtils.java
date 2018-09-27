/**
 * Copyright (C) 2016 mayimchen <mayimchen@gmail.com> All Rights Reserved.
 *
 * jutils
 *
 * @author mayimchen
 * @since 2016-10-07
 */
package com.agmbat.android.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class XmlUtils {

    public static String getNodeValue(String xmlString, String nodeName) {
        String result = null;
        try {
            Document document = DocumentHelper.parseText(xmlString);
            Element ele = document.getRootElement();
            for (Iterator<?> i = ele.elementIterator(); i.hasNext();) {
                Element node = (Element) i.next();
                if (nodeName.equals(node.getName())) {
                    result = node.getText();
                }
            }
        } catch (DocumentException e) {
        }
        return result;
    }


    static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException,
            IOException {
        int type;
        while ((type = parser.next()) != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
            ;
        }
        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }
        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() + ", expected "
                    + firstElementName);
        }
    }
}
