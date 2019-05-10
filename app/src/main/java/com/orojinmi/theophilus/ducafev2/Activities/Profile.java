package com.orojinmi.theophilus.ducafev2.Activities;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.BitmapUtil;

public class Profile extends AppCompatActivity {

    ImageView qrCodeImageView, barcodeImageView;
    TextView textView;
    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar         = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        init();
    }

    private void init(){
        qrCodeImageView = findViewById(R.id.qrCodeImageProfile);
        barcodeImageView    = findViewById(R.id.barcodeImageProfile);
        TextView scanMeNow  = findViewById(R.id.scanPayMerchant);
        textView            = findViewById(R.id.titleQr);
        mTypefaceBlack      = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        mTypefaceBold       = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");

        textView.setTypeface(mTypefaceBold);
        scanMeNow.setTypeface(mTypefaceBold);
        setQrImageToImageView();
    }

    private void setQrImageToImageView(){
        String text     = "60554903909220";
        Bitmap bitmap   = BitmapUtil.generateBitmap(text, 9, 900,900);
        Bitmap bitmap2  = BitmapUtil.generateBitmap(text, 8, 900,200);
        if(bitmap != null && bitmap2 != null){
            qrCodeImageView.setImageDrawable(new BitmapDrawable(bitmap));
            barcodeImageView.setImageDrawable(new BitmapDrawable(bitmap2));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
