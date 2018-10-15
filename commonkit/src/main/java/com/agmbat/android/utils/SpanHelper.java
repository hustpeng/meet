package com.agmbat.android.utils;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanHelper {

    public static final Pattern URL_PATTERN = Pattern
            .compile("(http://[a-zA-Z0-9+&@#/%?=~_\\-|!:\\.]*?)([^a-zA-Z0-9+&@#/%?=~_\\-|!:\\.]|$)");
    private static final boolean ENABLE_HTML_FORMAT = false;

    public static CharSequence getUrlText(CharSequence text, LinkCallback callback) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        URLSpan[] urls = builder.getSpans(0, builder.length(), URLSpan.class);
        for (int i = 0; i < urls.length; i++) {
            URLSpan span = urls[i];
            int start = builder.getSpanStart(span);
            int end = builder.getSpanEnd(span);
            int flag = builder.getSpanFlags(span);
            builder.removeSpan(span);
            builder.setSpan(new UrlClickableSpan(span.getURL(), callback), start, end, flag);
        }
        return builder;
    }

    public static CharSequence formatCharSequence(CharSequence charSequence) {
        return formatCharSequence(charSequence, false);
    }

    public static CharSequence formatCharSequence(CharSequence charSequence, boolean autoLink) {
        CharSequence result = charSequence;
        if (ENABLE_HTML_FORMAT) {
            if (charSequence instanceof String) {
                if (!TextUtils.isEmpty(charSequence)) {
                    Spanned html = Html.fromHtml((String) charSequence);
                    SpannableStringBuilder builder = new SpannableStringBuilder(html);
                    removeSpan(builder, ImageSpan.class);
                    removeSpan(builder, URLSpan.class);
                    fixNewLines(builder);
                    if (autoLink) {
                        addLinks(builder);
                    }
                    result = new SpanHelper.HtmlCharSequence((String) charSequence, builder);
                } else {
                    result = new SpannedString("");
                }
            } else if (charSequence instanceof SpannableStringBuilder) {
                if (!TextUtils.isEmpty(charSequence) && autoLink) {
                    addLinks((SpannableStringBuilder) charSequence);
                }
            }
        } else {
            if (charSequence instanceof String) {
                if (!TextUtils.isEmpty(charSequence)) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(charSequence);
                    if (autoLink) {
                        addLinks(builder);
                    }
                    result = builder;
                } else {
                    result = new SpannedString("");
                }
            } else if (charSequence instanceof SpannableStringBuilder) {
                if (!TextUtils.isEmpty(charSequence) && autoLink) {
                    addLinks((SpannableStringBuilder) charSequence);
                }
            }
        }
        return result;
    }

    /**
     * Applies a regex to a Spannable turning the matches into links.
     *
     * @param s           Spannable whose text is to be marked-up with links
     * @param p           Regex pattern to be used for finding links
     * @param scheme      Url scheme string (eg <code>http://</code> to be prepended to the url of links that do not
     *                    have a
     *                    scheme specified in the link text
     * @param matchFilter The filter that is used to allow the client code additional control over which pattern
     *                    matches are to
     *                    be converted into links.
     */
    private static final boolean addLinks(Spannable s) {
        boolean hasMatches = false;
        Matcher m = URL_PATTERN.matcher(s);
        while (m.find()) {
            int start = m.start(1);
            int end = m.end(1);
            String url = m.group(1);
            applyLink(url, start, end, s);
            hasMatches = true;
        }
        return hasMatches;
    }

    private static final void applyLink(String url, int start, int end, Spannable text) {
        URLSpan span = new URLSpan(url);
        text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void fixNewLines(SpannableStringBuilder builder) {
        int start = 0;
        int end = builder.length() - 1;
        while ((start <= end) && (builder.charAt(start) <= ' ')) {
            start++;
        }
        if (start > 0) {
            builder.delete(0, start);
        }
        start = 0;
        end = builder.length() - 1;
        while ((end >= start) && (builder.charAt(end) <= ' ')) {
            end--;
        }
        if (end < builder.length() - 1) {
            builder.delete(end, builder.length());
        }
        for (int i = 0; i < builder.length(); i++) {
            if (i + 2 < builder.length() && builder.charAt(i) == '\n' && builder.charAt(i + 1) == '\n'
                    && builder.charAt(i + 2) == '\n') {
                builder.delete(i, i + 1);
                i--;
            }
        }
    }

    private static void removeSpan(SpannableStringBuilder builder, Class type) {
        Object[] spans = builder.getSpans(0, builder.length(), type);
        if (spans != null) {
            for (int i = 0; i < spans.length; i++) {
                Object span = spans[i];
                int start = builder.getSpanStart(span);
                int end = builder.getSpanEnd(span);
                builder.delete(start, end);
            }
        }
    }

    public static String extractLink(CharSequence message) {
        String result = null;
        if (!TextUtils.isEmpty(message)) {
            Matcher matcher = URL_PATTERN.matcher(message);
            if (matcher.find()) {
                result = matcher.group(1);
            }
        }
        // This handle some special case for twitter,
        // that the url get truncated at the end.
        // In this case we are not able to get the correct url.
        if (!TextUtils.isEmpty(result)) {
            if (message.toString().endsWith(result + " ...")) {
                result = null;
            }
        }
        try {
            if (!TextUtils.isEmpty(result)) {
                new URL(result);
                // Log.i("extractLink", result);
            }
        } catch (MalformedURLException e) {
            result = null;
        }
        return result;
    }

    public static String toString(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return "";
        }
        if (charSequence instanceof HtmlCharSequence) {
            return ((HtmlCharSequence) charSequence).getHtml();
        }
        return charSequence.toString();
    }

    public interface LinkCallback {
        void onClick(String url);
    }

    private static class UrlClickableSpan extends ClickableSpan {
        private String mUrl;
        private LinkCallback mCallback;

        UrlClickableSpan(String url, LinkCallback callback) {
            mUrl = url;
            mCallback = callback;
        }

        @Override
        public void onClick(View widget) {
            mCallback.onClick(mUrl);
        }
    }

    public static class HtmlCharSequence implements Spanned {
        private String mHtml;
        private Spanned mCharSequence;

        /**
         * Initiate a new instance of {@link HtmlCharSequence}.
         *
         * @param html
         * @param charSequence
         */
        public HtmlCharSequence(String html, Spanned charSequence) {
            super();
            mHtml = html;
            mCharSequence = charSequence;
        }

        @Override
        public int length() {
            return mCharSequence.length();
        }

        @Override
        public char charAt(int index) {
            return mCharSequence.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mCharSequence.subSequence(start, end);
        }

        @Override
        public <T> T[] getSpans(int start, int end, Class<T> type) {
            return mCharSequence.getSpans(start, end, type);
        }

        @Override
        public int getSpanStart(Object tag) {
            return mCharSequence.getSpanStart(tag);
        }

        @Override
        public int getSpanEnd(Object tag) {
            return mCharSequence.getSpanEnd(tag);
        }

        @Override
        public int getSpanFlags(Object tag) {
            return mCharSequence.getSpanFlags(tag);
        }

        @Override
        public int nextSpanTransition(int start, int limit, Class type) {
            return mCharSequence.nextSpanTransition(start, limit, type);
        }

        @Override
        public String toString() {
            return mCharSequence.toString();
        }

        /**
         * Retrieve the html.
         *
         * @return the html
         */
        public String getHtml() {
            return mHtml;
        }

    }
}