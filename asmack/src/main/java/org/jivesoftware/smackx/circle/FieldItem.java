package org.jivesoftware.smackx.circle;

public class FieldItem {

    private static final String TYPE_TEXT = "text-single";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_LIST_SINGLE = "list-single";

    public String type;
    public String var;
    public String label;
    public String value;

    public String toXML() {
        return getField(type, var, label, value);
    }

    private String getField(String type, String var, String label, String value) {
        StringBuilder builder = new StringBuilder();
        builder.append("<field type=\"");
        builder.append(type);
        builder.append("\" var=\"");
        builder.append(var);
        builder.append("\" label=\"");
        builder.append(label);
        builder.append("><value>");
        builder.append(value);
        builder.append("</value>");
        builder.append("</field>");
        return builder.toString();
    }

}
