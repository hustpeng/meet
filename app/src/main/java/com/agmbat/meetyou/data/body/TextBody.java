package com.agmbat.meetyou.data.body;

public class TextBody extends Body {

    private final String mContent;

    public TextBody(String content) {
        mContent = content;
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
        builder.append("</wrap>");
        return builder.toString();
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.TEXT;
    }

}
