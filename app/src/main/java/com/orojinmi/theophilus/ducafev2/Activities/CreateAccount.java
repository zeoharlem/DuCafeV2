package com.orojinmi.theophilus.ducafev2.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.ScRow;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private EditText firstname, lastname, email, phone, location;
    private String merchant_id;

    private static final String CREATE_ACCOUNT_TAG = "createAccountTag";
    private static final int CREATE_ACCOUNT_KEY_CODE = 56;

    private RelativeLayout createAccountLayout;
    private Button createBtnAcc, assignBtnAcc;
    private ArrayList<String> editTextArrayList;
    private String getDataResult;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;

    private SharedPreferences sharedPreferences;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Toolbar toolbar         = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView merchantText   = findViewById(R.id.merchantTxt);
        mTypefaceBold           = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");
        mTypefaceBlack          = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        merchantText.setTypeface(mTypefaceBold);

        sharedPreferences   = getSharedPreferences("MyDataSdch", MODE_PRIVATE);

        //Set Edit Text ArrayList<EditText>
        setEditTextRow();
        //Activate the button for OnclickListener
        setButtonTypeRow();

        createAccountLayout = findViewById(R.id.createAccountRelativeLayout);
        createAccountLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard(v);
                //v.performClick();
                return false;
            }
        });
    }

    private void setEditTextRow(){
        merchant_id = getSharedPreferences("MyDataSdch", MODE_PRIVATE).getString("admin_id","");
        firstname   = findViewById(R.id.firstname);
        lastname    = findViewById(R.id.lastname);
        email       = findViewById(R.id.userEmailId);
        phone       = findViewById(R.id.mobileNumber);
        location    = findViewById(R.id.location);

        email.setTypeface(mTypefaceBold);
        phone.setTypeface(mTypefaceBold);
        lastname.setTypeface(mTypefaceBold);
        firstname.setTypeface(mTypefaceBold);
        location.setTypeface(mTypefaceBold);
    }

    /**
     * If false the submission action should run
     * @return boolean
     */
    private boolean checkEditTextViewNotEmpty(){
        boolean editTextStateAction = false;
        if(email.getText().toString().isEmpty() || firstname.getText().toString().isEmpty() || lastname.getText().toString().isEmpty()){
            editTextStateAction = true;
            L.l(getApplicationContext(), "Check Email Address or Firstname or Lastname Field");
        }
        else if(phone.getText().toString().isEmpty() || location.getText().toString().isEmpty()){
            editTextStateAction = true;
            L.l(getApplicationContext(), "Check Phone Number or Hospital Number or Location Field");
        }
        return editTextStateAction;
    }

    private void setButtonTypeRow(){
        createBtnAcc    = findViewById(R.id.createBtnAcc);
        assignBtnAcc    = findViewById(R.id.assignBtn);

        createBtnAcc.setTypeface(mTypefaceBlack);
        assignBtnAcc.setTypeface(mTypefaceBlack);

        createBtnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkEditTextViewNotEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), ScRow.class);
                    startActivityForResult(intent, CREATE_ACCOUNT_KEY_CODE);
                }
            }
        });

        assignBtnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDataResult != null) {
                    //L.l(getApplicationContext(), getDataResult);
                    myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();
                    myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "MyLoadingAlertBox");
                    sendAccountJsonRequest(new VolleyCallBackTask() {
                        @Override
                        public void onSuccess(String msg) {
                            myLoadingAlertDialogFrag.dismiss();
                            assignBtnAcc.setVisibility(View.GONE);
                            createBtnAcc.setVisibility(View.VISIBLE);
                            L.l(getApplicationContext(), msg);
                            restartActivity(CreateAccount.this);
                        }

                        @Override
                        public void onFailure(String error) {
                            myLoadingAlertDialogFrag.dismiss();
                            assignBtnAcc.setVisibility(View.GONE);
                            createBtnAcc.setVisibility(View.VISIBLE);
                            L.l(getApplicationContext(), error);
                        }
                    });
                }
                else{
                    L.l(getApplicationContext(), "getDataResult is NULL");
                }
            }
        });
    }

    private void restartActivity(Activity activity){
        if(Build.VERSION.SDK_INT >= 11){
            activity.recreate();
        }
        else{
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        getDataResult    = data.getStringExtra("keyQrCode");
        if(requestCode == CREATE_ACCOUNT_KEY_CODE && resultCode == RESULT_OK){
            if(createBtnAcc.getVisibility() == View.VISIBLE){
                createBtnAcc.setVisibility(View.GONE);
                assignBtnAcc.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendAccountJsonRequest(final VolleyCallBackTask volleyCallBackTask){
        String urlStringParam       = Helpers.URL_STRING + "/users/create";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        String errMessage   = jsonObject.getString("message");
                        volleyCallBackTask.onFailure(errMessage);
                        throw new Exception(errMessage);
                    }
                    volleyCallBackTask.onSuccess(jsonObject.getString("message"));
                }
                catch (JSONException e) {
                    volleyCallBackTask.onFailure("JSON: "+e.getMessage());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    volleyCallBackTask.onFailure("Error:"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallBackTask.onFailure("Vo: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> queryParam  = new HashMap<>();
                queryParam.put("apiId",Helpers.API_ID);
                queryParam.put("apiKey",Helpers.API_KEY);
                queryParam.put("phone", phone.getText().toString().trim());
                queryParam.put("email", email.getText().toString().trim());
                queryParam.put("firstname", firstname.getText().toString().trim());
                queryParam.put("lastname", lastname.getText().toString().trim());
                queryParam.put("address", location.getText().toString().trim());
                queryParam.put("merchant_id", merchant_id);
                queryParam.put("walletcode", getDataResult);
                return queryParam;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, CREATE_ACCOUNT_TAG);
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

    private void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager   = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private interface VolleyCallBackTask{
        void onSuccess(String message);
        void onFailure(String message);
    }
}
