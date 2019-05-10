package com.orojinmi.theophilus.ducafev2.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.orojinmi.theophilus.ducafev2.R;

public class ScannerActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
    }
}

//public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
//
//    private static final int REQUEST_CAMERA = 1;
//    private static final String URLSTRING  = "http://diportal.net";
//    private ZXingScannerView zXingScannerView;
//    private String amountToPay;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_scanner);
//        amountToPay = getIntent().getStringExtra("amttopay");
//        openZxing(new View(this));
//    }
//
//    private void openZxing(View view) {
//        zXingScannerView    = new ZXingScannerView(getApplicationContext());
//        setContentView(zXingScannerView);
//        zXingScannerView.setResultHandler(this);
//        zXingScannerView.startCamera();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        zXingScannerView.stopCamera();
//    }
//
//    @Override
//    public void handleResult(Result result) {
//        Intent i    = new Intent();
//        setResult(RESULT_OK, i.putExtra("qrCodeReturn", String.valueOf(result)));
//        zXingScannerView.resumeCameraPreview(this);
//        finish();
//    }
//}
