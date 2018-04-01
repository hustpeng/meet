/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smackx.message;

import org.jivesoftware.smack.packet.Message.SubType;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageObject{
    public enum Msg_Status {
        READ,
        UNREAD,
        SENDING,
        SEND,
        FAILED,
        LOCATING,
        UPLOADING,
        MSG_HIDDEN;

        public static Msg_Status fromString(String name) {
            try {
                return Msg_Status.valueOf(name);
            }
            catch (Exception e) {
                return MSG_HIDDEN;
            }
        }
    }

    private String senderJid;
    private String senderNickName;
    private String receiverJid;
    private String body;
    private boolean outgoing;
    private String msg_id;
    private SubType msg_type;
    private Msg_Status msg_status;
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

    public SubType getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(SubType msg_type) {
        this.msg_type = msg_type;
    }

    public Msg_Status getMsg_status() {
        return msg_status;
    }

    public void setMsg_status(Msg_Status msg_status) {
        this.msg_status = msg_status;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public static String getXmlNode(MessageObject object)
    {
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
    public String getImageThumbUrl()
    {
        if (imageThumbUrl != null) {
            return imageThumbUrl;
        }

        if (!TextUtils.isEmpty(html)) {
            Matcher matcher = IMAGE_THUMB_PATTERN.matcher(html);
            if(matcher.find()){
                imageThumbUrl = matcher.group(1);
                return imageThumbUrl;
            }
        }

        return null;
    }

    private static final Pattern IMAGE_SRC_PATTERN = Pattern.compile("src=\"(.*?)\"");
    public String getImageSrcUrl()
    {
        if (imageSrcUrl != null) {
            return imageSrcUrl;
        }

        if (!TextUtils.isEmpty(html)) {
            Matcher matcher = IMAGE_SRC_PATTERN.matcher(html);
            if(matcher.find()){
                imageSrcUrl = matcher.group(1);
                return imageSrcUrl;
            }
        }

        return null;
    }
}