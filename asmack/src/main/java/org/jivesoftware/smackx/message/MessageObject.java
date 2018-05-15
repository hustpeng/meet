/**
 * $RCSfile$
 * $Revision$
 * $Date$
 * <p>
 * Copyright 2003-2007 Jive Software.
 * <p>
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.message;

import android.text.TextUtils;

import org.jivesoftware.smack.packet.MessageSubType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示一条消息, ui显示与数据库存储
 */
public class MessageObject {

    private String senderJid;
    private String senderNickName;
    private String receiverJid;
    private String body;
    private boolean outgoing;
    private String msg_id;
    private MessageSubType msg_type;
    private MessageObjectStatus msg_status;
    private Long date;
    private String html;

    private String imageThumbUrl;
    private String imageSrcUrl;

    public String getSenderJid() {
        return senderJid;
    }

    public void setSenderJid(String senderJid) {
        this.senderJid = senderJid;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public MessageSubType getMsgType() {
        return msg_type;
    }

    public void setMsgType(MessageSubType msg_type) {
        this.msg_type = msg_type;
    }

    public MessageObjectStatus getMsg_status() {
        return msg_status;
    }

    public void setMsg_status(MessageObjectStatus msg_status) {
        this.msg_status = msg_status;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public static String getXmlNode(MessageObject object) {
        if (object == null) {
            return null;
        }

        StringBuilder buf = new StringBuilder();
        buf.append("MessageObject: ");

        return buf.toString();
    }

    @Override
    public String toString() {
        return getXmlNode(this);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReceiverJid() {
        return receiverJid;
    }

    public void setReceiverJid(String receiverJid) {
        this.receiverJid = receiverJid;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    private static final Pattern IMAGE_THUMB_PATTERN = Pattern.compile("thumb=\"(.*?)\"");

    public String getImageThumbUrl() {
        if (imageThumbUrl != null) {
            return imageThumbUrl;
        }
        if (!TextUtils.isEmpty(html)) {
            Matcher matcher = IMAGE_THUMB_PATTERN.matcher(html);
            if (matcher.find()) {
                imageThumbUrl = matcher.group(1);
                return imageThumbUrl;
            }
        }
        return null;
    }

    private static final Pattern IMAGE_SRC_PATTERN = Pattern.compile("src=\"(.*?)\"");

    public String getImageSrcUrl() {
        if (imageSrcUrl != null) {
            return imageSrcUrl;
        }
        if (!TextUtils.isEmpty(html)) {
            Matcher matcher = IMAGE_SRC_PATTERN.matcher(html);
            if (matcher.find()) {
                imageSrcUrl = matcher.group(1);
                return imageSrcUrl;
            }
        }
        return null;
    }
}