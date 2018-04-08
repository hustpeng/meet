package com.agmbat.meetyou.settings;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.agmbat.android.AppResources;
import com.agmbat.android.utils.WindowUtils;
import com.agmbat.meetyou.R;
import com.google.zxing.client.android.encode.QRCodeEncoder;

public class QRCodeCardActivity extends Activity {

    private ImageView mQrCodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtils.setStatusBarColor(this, 0xff232325);
        setContentView(R.layout.activity_qr_code_card);
        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mQrCodeView = (ImageView) findViewById(R.id.qr_code);
        String text = "1343712259";
        int dimension = (int) AppResources.dipToPixel(250);
        Bitmap bitmap = QRCodeEncoder.encode(text, dimension);
        mQrCodeView.setImageBitmap(bitmap);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


}
