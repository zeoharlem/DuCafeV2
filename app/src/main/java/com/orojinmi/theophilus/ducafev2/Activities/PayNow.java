package com.orojinmi.theophilus.ducafev2.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyDialogBox;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.Model.Users;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.ScRow;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONException;
import org.json.JSONObject;


public class PayNow extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private EditText editTextAmount;
    private static final int REQUEST_CODE   = 1;
    private static final String PAY_N       = "payNow";
    private Button num1, num2, num3, num4, num5, num6, num7, num8, num9, num0, num00, dotted, enterBtn, clearBtn;

    private static final String INTENT_EXTRA_AMOUNT_PAYABLE     = "AmountPayable";
    private static final String INTENT_EXTRA_WALLET_KEY_CODE    = "WalletKeyQrCode";

    private String getEditTextVal   = "";
    private TextView textViewBal;

    private MyVolleySingleton myVolleySingleton;

    private Button qrCodeView;
    private MyLoadingAlertDialogFrag myAlertLoadingBox;

    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    MyDialogBox myDialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_now);
        Toolbar toolbar         = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView merchantText   = findViewById(R.id.merchantTxt);
        mTypeface               = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Thin.ttf");
        mTypefaceRegular        = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Reg.ttf");
        mTypefaceBold           = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");
        mTypefaceBlack          = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        merchantText.setTypeface(mTypefaceBold);

        myVolleySingleton       = MyVolleySingleton.getInstance(this);
        myAlertLoadingBox       = new MyLoadingAlertDialogFrag();

        //Activate the buttons of the calculator
        initTask();

        //EditText to HideSoftKeyBoard
        editTextHideSoftKeyClick();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        //Trigger the EnterBtn to Initialize the Sunmi Scanner Package
        setEnterBtnAction();
    }

    private void editTextHideSoftKeyClick(){
        editTextAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });
    }

    private void initTask() {
        editTextAmount = findViewById(R.id.inputTextAmount);
        num0 = findViewById(R.id.num0);
        num00 = findViewById(R.id.num00);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);
        num7 = findViewById(R.id.num7);
        num8 = findViewById(R.id.num8);
        num9 = findViewById(R.id.num9);
        dotted = findViewById(R.id.dotted);

        clearBtn    = findViewById(R.id.clearFigureBtn);
        enterBtn    = findViewById(R.id.enterFigureBtn);
        qrCodeView  = findViewById(R.id.qrCodeView);

        //SetOnClickListener for the Number Buttons
        num0.setOnClickListener(this);
        num00.setOnClickListener(this);
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
        dotted.setOnClickListener(this);

        //Perform the Long and Short Click Events
        clearBtn.setOnClickListener(this);
        clearBtn.setOnLongClickListener(this);

        num0.setTypeface(mTypefaceBlack);
        num00.setTypeface(mTypefaceBlack);
        num1.setTypeface(mTypefaceBlack);
        num2.setTypeface(mTypefaceBlack);
        num3.setTypeface(mTypefaceBlack);
        num4.setTypeface(mTypefaceBlack);
        num5.setTypeface(mTypefaceBlack);
        num6.setTypeface(mTypefaceBlack);
        num7.setTypeface(mTypefaceBlack);
        num8.setTypeface(mTypefaceBlack);
        num9.setTypeface(mTypefaceBlack);
        dotted.setTypeface(mTypefaceBlack);

        enterBtn.setTypeface(mTypefaceBlack);
        qrCodeView.setTypeface(mTypefaceBlack);
        clearBtn.setTypeface(mTypefaceBlack);
    }

    //Set the EnterBtnAction to Initialize the Scanner
    private void setEnterBtnAction(){
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTextField()) {
                    //openZxingScanner(v);
                    Intent intent   = new Intent(getApplicationContext(), ScRow.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            assert data != null;
            final String getKeyResult = data.getStringExtra("keyQrCode");
            //L.l(getApplicationContext(), "Please Perform Volley Request | "+getKeyResult);
            myAlertLoadingBox.show(getSupportFragmentManager(),"myAlertFrag");

            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendJsonPackRow(getKeyResult, new VolleyCallbackAction() {
                        @Override
                        public void onSuccess(Users users) {
                            Intent intent   = new Intent(getApplicationContext(), PaymentDetail.class);
                            intent.putExtra("email", users.getEmail());
                            intent.putExtra("phone", users.getPhone());
                            intent.putExtra("lastname", users.getLastname());
                            intent.putExtra("firstname", users.getFirstname());
                            intent.putExtra(INTENT_EXTRA_AMOUNT_PAYABLE, editTextAmount.getText().toString().trim());
                            intent.putExtra(INTENT_EXTRA_WALLET_KEY_CODE, getKeyResult);

                            myAlertLoadingBox.dismiss();

                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String message) {
                            myAlertLoadingBox.dismiss();
                            L.l(getApplicationContext(), message);
                        }
                    });
                }
            }, 3000);
        }
    }

    private void sendJsonPackRow(final String walletCode, final VolleyCallbackAction volleyCallbackAction){
        String urlStringParam       = "/wallet/find/"+walletCode+"?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Helpers.URL_STRING+urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException("Status Message:"+jsonObject.getString("message"));
                    }
                    volleyCallbackAction.onSuccess(parseJson(jsonObject));
                }
                catch (JSONException e) {
                    volleyCallbackAction.onFailure("Json Error: "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallbackAction.onFailure("Server Error: "+error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        myVolleySingleton.addToRequestQueue(stringRequest, PAY_N);
    }

    private Users parseJson(JSONObject jsonObject) throws JSONException {
        Users users         = new Users();
        JSONObject getData  = jsonObject.getJSONObject("data");
        if(!getData.getString("register_id").isEmpty()) {
            users.setLastname(getData.getString("lastname"));
            users.setWalletCode(getData.getString("walletcode"));
            users.setFirstname(getData.getString("firstname"));
            users.setEmail(getData.getString("email"));
            users.setPhone(getData.getString("phone"));
        }
        return users;
    }

    @SuppressLint("SetTextI18n")
    private void setGetEditTextVal(View v){
        Button button   = (Button)v;
        getEditTextVal  = editTextAmount.getText().toString().trim();
        if(getEditTextVal.length() < 10) {
            String btnText = button.getText().toString().trim();
            editTextAmount.setText(getEditTextVal + "" + btnText);
        }
    }

    private void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager   = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private boolean checkTextField(){
        if(editTextAmount.getText().toString().isEmpty() || editTextAmount.getText().length() < 1){
            L.l(getApplicationContext(), "Empty Amount Selected");
            return false;
        }
        else if(editTextAmount.getText().toString().trim().equals(0)){
            L.l(getApplicationContext(), "You cannot receive zero amount");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clearFigureBtn:
                //Clear EditTextValue on a Single Tap
                if(editTextAmount.getText().toString().trim().length() > 0){
                    CharSequence currentText    = editTextAmount.getText().toString().trim();
                    editTextAmount.setText(currentText.subSequence(0, currentText.length()-1));
                }
                break;
            default:
                setGetEditTextVal(v);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        editTextAmount.setText("");
        return true;
    }

    public void openZxingScanner(View view){
        Intent intent   = new Intent(this, ScannerActivity.class);
        intent.putExtra("amttopay", editTextAmount.getText().toString().trim());
        startActivityForResult(intent, 1);
    }

    private interface VolleyCallbackAction{
        void onSuccess(Users users);
        void onFailure(String message);
    }
}
