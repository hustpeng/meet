package com.agmbat.text;

public class Html {

    public static final String HTML = "<html>\n<body>\n\n%s\n\n</body>\n</html>";

    public static final String HTML_FILE = "<p><a href=\"%s\">%s</a></p>\n";

    private static final String HTML_TEXT_FORMAT =
            "<tr><td width=50%% align=center>%s</td><td width=50%% align=center>%s</td></tr>";

    public static String formatTable(String left, String right) {
        return String.format(HTML_TEXT_FORMAT, left, right);
    }

    public static final String a(String url) {
        return a(url, url);
    }

    public static final String a(String displayName, String url) {
        if (displayName == null) {
            displayName = url;
        }
        String format = "<a href=\"%s\">%s</a>";
        return String.format(format, url, displayName);
    }

    public static final String br() {
        return "<br />\n";
    }

    public static final String p(String content) {
        String format = "<p>%s</p>\n";
        return String.format(format, content);
    }

    public static final String table(String content) {
        String format = "<table border=\"1\" cellspacing=\"0\">%s</table>";
        return String.format(format, content);
    }

    public static final String td(String content) {
        String format = "<td align=\"center\">%s</td>\n";
        return String.format(format, content);
    }

    public static final String td(String content, String width) {
        String format = "<td width=\"%s\" align=\"center\">%s</td>\n";
        return String.format(format, width, content);
    }

    public static final String th(String content) {
        String format = "<th>%s</th>\n";
        return String.format(format, content);
    }

    public static final String th(String content, String colspan) {
        String format = "<th colspan=\"%s\">%s</th>\n";
        return String.format(format, colspan, content);
    }

    public static final String tr(String content) {
        String format = "<tr>\n%s</tr>\n";
        return String.format(format, content);
    }
}
