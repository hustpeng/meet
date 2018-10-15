package com.agmbat.android.utils;

import android.os.Bundle;
import android.os.Parcel;

import com.agmbat.io.IoUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by chenming03 on 16/10/7.
 */
public class ParcelableUtils {

    public static void writeBundleToStream(Bundle data, OutputStream stream) throws IOException {
        final Parcel parcel = Parcel.obtain();
        data.writeToParcel(parcel, 0);
        stream.write(parcel.marshall());
        parcel.recycle();
    }

    public static Bundle readBundleFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IoUtils.copyStream(stream, out);
        Parcel parcel = Parcel.obtain();
        byte[] data = out.toByteArray();
        parcel.unmarshall(data, 0, data.length);
        parcel.setDataPosition(0);
        Bundle bundle = new Bundle();
        bundle.readFromParcel(parcel);
        parcel.recycle();
        return bundle;
    }
}
