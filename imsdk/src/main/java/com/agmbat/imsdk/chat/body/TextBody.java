package com.agmbat.imsdk.chat.body;

import java.util.List;

public class TextBody extends Body {

    private String mContent = "";
    private List<AtUser> mAtUsers = null;

    public TextBody(String content) {
        mContent = content;
    }

    public TextBody(String content, List<AtUser> atUsers) {
        mContent = content;
        mAtUsers = atUsers;
    }

    public List<AtUser> getAtUsers() {
        return mAtUsers;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public String toXml() {
        StringBuilder builder = new StringBuilder();
        builder.append("<wrap>");
        builder.append("<type>").append(getBodyType()).append("</type>");
        builder.append("<content>").append(mContent).append("</content>");
        if (null != mAtUsers && mAtUsers.size() > 0) {
            builder.append("<at>");
            for (int i = 0; i < mAtUsers.size(); i++) {
                AtUser atUser = mAtUsers.get(i);
                builder.append("<user>");
                builder.append("<jid>").append(atUser.getJid()).append("</jid>");
                builder.append("<nickname>").append(atUser.getNickName()).append("</nickname>");
                builder.append("</user>");
            }
            builder.append("</at>");
        }
        builder.append("</wrap>");
        return builder.toString();
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.TEXT;
    }


    public static class AtUser {

        private String jid;

        private String nickName;

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }

}
