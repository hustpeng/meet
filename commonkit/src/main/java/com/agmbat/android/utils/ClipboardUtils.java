package com.agmbat.android.utils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

/**
 * Created by chenming03 on 16/3/15.
 */
public class ClipboardUtils {

    public static final CharSequence getClipboardText(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        return ClipboardManagerProxy.getInstance(context).getText();

    }

    public static final Uri getClipboardUri(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        return ClipboardManagerProxy.getInstance(context).getUri();

    }

    public static final void setClipboardText(Context context, CharSequence text) {

        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }

        ClipboardManagerProxy.getInstance(context).setText(text);
    }

    public static final void setClipboardUri(Context context, Uri uri) {

        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }

        ClipboardManagerProxy.getInstance(context).setUri(uri);
    }

    public static boolean hasUri(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        return ClipboardManagerProxy.getInstance(context).hasUri();
    }

    public static boolean hasText(Context context) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        return ClipboardManagerProxy.getInstance(context).hasText();
    }

    /**
     * A proxy class to access clipboard.
     */
    public static abstract class ClipboardManagerProxy {

        /**
         * Retrieve a suitable {@linkplain ClipboardManagerProxy} to access clipboard.
         *
         * @param context the context of the application.
         * @return the suitable instance of {@linkplain ClipboardManagerProxy} to acces the clipboard.
         */
        public static final ClipboardManagerProxy getInstance(Context context) {
            if (null == context) {
                throw new IllegalArgumentException("context may not be not null.");
            }
            return getInstanceUnchecked(context);
        }

        static final ClipboardManagerProxy getInstanceUnchecked(Context context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                return new ClipboardManagerV1(context);
            } else {
                return new ClipboardManagerV11(context);
            }
        }

        /**
         * Retrieve the raw URI contained in this Item.
         */
        public abstract Uri getUri();

        /**
         * Sets the contents of the clipboard to the specified URI (or its string representation).
         */
        public abstract void setUri(Uri uri);

        /**
         * Returns the text on the clipboard.
         */
        public abstract CharSequence getText();

        /**
         * Sets the contents of the clipboard to the specified text.
         */
        public abstract void setText(CharSequence text);

        /**
         * Returns true if the clipboard contains text or a text that can be convert into a URI; false otherwise.
         */
        public abstract boolean hasUri();

        /**
         * Returns true if the clipboard contains text; false otherwise.
         */
        public abstract boolean hasText();

    }

    @SuppressWarnings("deprecation")
    static final class ClipboardManagerV1 extends ClipboardManagerProxy {

        private android.text.ClipboardManager mInstance;

        ClipboardManagerV1(Context context) {
            mInstance = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @Override
        public Uri getUri() {
            Uri result = null;
            try {
                CharSequence text = mInstance.getText();
                if (!TextUtils.isEmpty(text)) {
                    result = Uri.parse(text.toString());
                }
            } catch (Exception e) {
                // Not a URI.
            }
            return result;
        }

        @Override
        public void setUri(Uri uri) {
            if (uri != null) {
                mInstance.setText(uri.toString());
            } else {
                mInstance.setText(null);
            }
        }

        @Override
        public CharSequence getText() {
            return mInstance.getText();
        }

        @Override
        public void setText(CharSequence text) {
            mInstance.setText(text);
        }

        @Override
        public boolean hasUri() {
            Uri uri = getUri();
            return uri != null;
        }

        @Override
        public boolean hasText() {
            return mInstance.hasText();
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static final class ClipboardManagerV11 extends ClipboardManagerProxy {

        private ClipboardManager mInstance;

        ClipboardManagerV11(Context context) {
            mInstance = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @Override
        public Uri getUri() {
            ClipData clip = mInstance.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                return clip.getItemAt(0).getUri();
            }
            return null;
        }

        @Override
        public void setUri(Uri uri) {
            if (uri != null) {
                ClipData clip = ClipData.newRawUri(uri.toString(), uri);
                mInstance.setPrimaryClip(clip);
            } else {
                setText(null);
            }
        }

        @Override
        public CharSequence getText() {
            ClipData clip = mInstance.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                return clip.getItemAt(0).getText();
            }
            return null;
        }

        @Override
        public void setText(CharSequence text) {
            if (TextUtils.isEmpty(text)) {
                text = "";
            }
            ClipData clip = ClipData.newPlainText(text, text);
            mInstance.setPrimaryClip(clip);
        }

        @Override
        public boolean hasUri() {
            return getUri() != null;
        }

        @Override
        public boolean hasText() {
            return getText() != null;
        }

    }
}
