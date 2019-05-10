package com.orojinmi.theophilus.ducafev2.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orojinmi.theophilus.ducafev2.BaseApp;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyFragDialogBox;
import com.orojinmi.theophilus.ducafev2.Model.WalletActivity;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.AidlUtil;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;
import com.orojinmi.theophilus.ducafev2.bean.TableItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import woyou.aidlservice.jiuiv5.ICallback;

public class PaymentDetail extends AppCompatActivity implements View.OnClickListener, MyFragDialogBox.Communicator {

    private static final String PAYMENT_ROW_DETAIL              = "PayRowDetail";
    private static final String INTENT_EXTRA_AMOUNT_PAYABLE     = "AmountPayable";
    private static final String INTENT_EXTRA_WALLET_KEY_CODE    = "WalletKeyQrCode";
    private static final String DEFAULT_VALUE = "N/A";

    private Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private EditText paymentPurpose, amountPayable;
    private Button decline, submit, sendMsg, callNow;
    private RelativeLayout parentActivityPayDetail;
    private TextView headerTextView;
    private LinearLayout cardViewPayment;
    BaseApp baseApp;
    boolean mark;

    LinkedList<TableItem> datalist;
    private TextView email, payTxt, phone;

    private MyFragDialogBox myFragDialogBox;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        baseApp = (BaseApp) getApplication();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Payment Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTypeface           = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Thin.ttf");
        mTypefaceRegular    = Typeface.createFromAsset(getAssets(),"fonts/DaxlinePro-Regular.ttf");
        mTypefaceBold       = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");
        mTypefaceBlack      = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");

        cardViewPayment         = findViewById(R.id.paymentTwo);
        parentActivityPayDetail = findViewById(R.id.parentActivityPaymentDetail);

        cardViewPayment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard(v);
                return false;
            }
        });

        parentActivityPayDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard(v);
                return false;
            }
        });
        initRow();

        //Set up the myFragDialogBox Object
        myFragDialogBox = new MyFragDialogBox();

        AidlUtil.getInstance().initPrinter();

    }

    @SuppressLint("SetTextI18n")
    private void initRow(){
        datalist        = new LinkedList<>();
        addOneData(datalist);

        paymentPurpose  = findViewById(R.id.payment_purpose);
        amountPayable   = findViewById(R.id.amount_to_pay);
        headerTextView  = findViewById(R.id.headerTitle);
        payTxt = findViewById(R.id.payReceiverTxt);
        email  = findViewById(R.id.emailHeader);
        phone  = findViewById(R.id.phoneHeader);

        //Set the content from getIntent()
        phone.setText(getIntent().getStringExtra("phone"));
        email.setText(getIntent().getStringExtra("email"));
        headerTextView.setText(getIntent().getStringExtra("firstname")
                +" "+getIntent().getStringExtra("lastname"));

        amountPayable.setTypeface(mTypefaceBlack);
        headerTextView.setTypeface(mTypefaceBold);
        paymentPurpose.setTypeface(mTypefaceBold);
        payTxt.setTypeface(mTypefaceBold);

        amountPayable.setText(getIntent().getStringExtra("AmountPayable"));

        decline         = findViewById(R.id.declineTransaction);
        submit          = findViewById(R.id.submitTransaction);
        sendMsg         = findViewById(R.id.sendMsg);
        callNow         = findViewById(R.id.callUser);

        //Hide Soft KeyBoard on clicking Layout
        editTextHideSoftKeyClick();

        //Set and Attach the OnclickListener to the butttons
        submit.setTypeface(mTypefaceBlack);
        decline.setTypeface(mTypefaceBold);
        sendMsg.setTypeface(mTypefaceBold);
        callNow.setTypeface(mTypefaceBold);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.l(getApplicationContext(), "Send Message Not Available");
            }
        });

        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.l(getApplicationContext(), "Call User Not Available");
            }
        });

        submit.setOnClickListener(this);
        decline.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitTransaction:
                showDialogMyFragDialogBox(v);
                break;
            case R.id.declineTransaction:
                Intent payNow   = new Intent(this, PayNow.class);
                payNow.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(payNow);
                break;
        }
    }

    public void showDialogMyFragDialogBox(View view){
        FragmentManager manager = getSupportFragmentManager();
        //MyFragDialogBox myFragDialogBox = new MyFragDialogBox();
        myFragDialogBox.show(manager, "MyFragDialogBox");
    }

    public void addOneData(LinkedList<TableItem> data) {
        TableItem tableItemHead = new TableItem();
        data.add(tableItemHead);
    }

    private void editTextHideSoftKeyClick(){
        paymentPurpose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });

        amountPayable.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });
    }

    private void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager   = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onDialogMsgTask(String textString) {
        //Perform the Print Action Task Here
        if(!textString.trim().equals("") || !textString.trim().isEmpty()){
            //byte[] rv       = BytesUtil.getBaiduTestBytes();
            final Bitmap bitmap   = BitmapFactory.decodeResource(getResources(), R.mipmap.duilong);
            //rv              = BytesUtil.byteMerger(ESCUtil.printBitmap(bitmap, 0), rv);
            //Perform VolleyActionCallBack Here
            sendJsonRowAction(textString, amountPayable.getText().toString().trim(), paymentPurpose.getText().toString().trim(), new VolleyCallBackAction() {

                @Override
                public void onSuccessListener(WalletActivity walletActivity) {
                    @SuppressLint("DefaultLocale")
                    double formatAmt    = Double.parseDouble(walletActivity.getBalance());
                    String[] purpose    = new String[]{paymentPurpose.getText().toString(), getIntent().getStringExtra(INTENT_EXTRA_AMOUNT_PAYABLE)};

                    @SuppressLint("DefaultLocale")
                    String[] balance    = new String[]{"Total Balance", String.format("%,.2f", formatAmt)};

                    setTableItemLink(bitmap, purpose, balance, walletActivity.getDateAdded(), walletActivity.getCodename());
                    tableIntentAction(walletActivity);
                    myFragDialogBox.dismiss();
                }

                @Override
                public void onFailureListener(String message) {
                    myFragDialogBox.dismiss();
                    L.l(getApplicationContext(), message);
                }
            });

        }
    }

    private void sendJsonRowAction(final String password, final String amount, final String purpose, final VolleyCallBackAction volleyCallBackAction){
        String urlStringParam       = Helpers.URL_STRING + "/wallet/set";
        //String urlStringParam       = Helpers.URL_STRING + "/wallet/set?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException("Error:"+jsonObject.getString("message"));
                    }
                    volleyCallBackAction.onSuccessListener(activityParseJson(jsonObject));
                }
                catch (JSONException e) {
                    volleyCallBackAction.onFailureListener(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallBackAction.onFailureListener("V Error:"+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param   = new HashMap<>();
                param.put("amount", amount);
                param.put("password", password);
                param.put("username", email.getText().toString().trim());
                param.put("createdBy", Objects.requireNonNull(getSharedPreferences("MyDataSdch", MODE_PRIVATE).getString("admin_id", "")));
                param.put("type", "payment");
                param.put("purpose", purpose);
                param.put("apiKey", Helpers.API_KEY);
                param.put("apiId", Helpers.API_ID);
                return param;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, "PayDetail");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private WalletActivity activityParseJson(JSONObject jsonObject) throws JSONException {
        WalletActivity walletActivity   = new WalletActivity();
        JSONObject getData  = jsonObject.getJSONObject("data");
        walletActivity.setDateAdded(getData.getString("transaction_date"));
        walletActivity.setAmount(getData.getString("amount"));
        walletActivity.setBalance(getData.getString("balance"));
        walletActivity.setCodename(getData.getString("codename"));
        return walletActivity;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * @param bitmap @imageLogo to be printed
     * @param p @single Array Item or Purpose for Payment
     * @param b @balance of the transaction
     */
    private void setTableItemLink(Bitmap bitmap, String[] p, String[] b, String date, String code){
        //Printing Task to show the Item Paid for and balance
        datalist.add(new TableItem(new String[]{p[0], p[1]}));
        datalist.add(new TableItem(new String[]{b[0], b[1]}));
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataSdch", MODE_PRIVATE);
        String merchantAgent                = sharedPreferences.getString("fullname", DEFAULT_VALUE);
        AidlUtil.getInstance().printTextBitmap(bitmap, 0, datalist, headerTextView.getText().toString(), merchantAgent, date, code);
    }

    private void tableIntentAction(WalletActivity walletActivity){
        Intent intent   = new Intent(getApplicationContext(), Success.class);
        intent.putExtra(PAYMENT_ROW_DETAIL, new Gson().toJson(datalist));
        intent.putExtra("datePay", walletActivity.getDateAdded());
        intent.putExtra("TransactCode", walletActivity.getCodename());
        intent.putExtra("PayerName", headerTextView.getText().toString().trim());
        intent.putExtra(INTENT_EXTRA_AMOUNT_PAYABLE, getIntent().getStringExtra(INTENT_EXTRA_AMOUNT_PAYABLE));
        intent.putExtra(INTENT_EXTRA_WALLET_KEY_CODE, getIntent().getStringExtra(INTENT_EXTRA_WALLET_KEY_CODE));
        startActivity(intent);
        finish();
    }

    ICallback mICallback    = new ICallback.Stub() {
        @Override
        public void onRunResult(boolean isSuccess) throws RemoteException {
            //L.l(getApplicationContext(), "onRunResult");
        }

        @Override
        public void onReturnString(String result) throws RemoteException {
            //L.l(getApplicationContext(), "onReturnString"+result);
        }

        @Override
        public void onRaiseException(int code, String msg) throws RemoteException {
            //L.l(getApplicationContext(), "onRaiseException"+code + msg);
        }

        @Override
        public void onPrintResult(final int code, String msg) throws RemoteException {
            final int res = code;
            L.l(getApplicationContext(), "onPrintResult"+code + msg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Perform After Task Here
                    if(code == 0){

                    }
                    else{
                        L.l(getApplicationContext(), "onPrintResult");
                    }
                }
            });
        }
    };

    interface VolleyCallBackAction{
        void onSuccessListener(WalletActivity walletActivity);
        void onFailureListener(String message);
    }
}
