package com.orojinmi.theophilus.ducafev2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.MainMenuBoard;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Settings extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button updatePswd;
    private TextView changePswdText;
    private Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;
    private EditText oldPswd, newPswd, newPswdConf;
    private AppCompatCheckBox compatCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar             = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Change Password");


        myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();

        compatCheckBox  = findViewById(R.id.checkBoxShowPswd);
        compatCheckBox.setOnCheckedChangeListener(this);

        init();

        oldPswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });

        newPswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });
    }

    private void init(){
        mTypeface       = Typeface.createFromAsset(getAssets(),"fonts/DaxlinePro-Bold.ttf");
        mTypefaceRegular= Typeface.createFromAsset(getAssets(),"fonts/DaxlinePro-Regular.ttf");
        mTypefaceBold   = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");
        mTypefaceBlack  = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        newPswd         = findViewById(R.id.newPswd);
        updatePswd      = findViewById(R.id.updatePswd);
        changePswdText  = findViewById(R.id.merchantTxt);
        oldPswd         = findViewById(R.id.oldPswd);

        oldPswd.setTypeface(mTypefaceBold);
        newPswd.setTypeface(mTypefaceBold);

        changePswdText.setTypeface(mTypefaceBold);

        updatePswd.setTypeface(mTypefaceBlack);
        updatePswd.setOnClickListener(this);
    }

    private boolean validateRow(){
        if(oldPswd.getText().toString().trim().equals("") || oldPswd.getText().toString().trim().isEmpty()){
            L.l(getApplicationContext(), "Empty Old Password");
            return false;
        }
        else if(newPswd.getText().toString().trim().equals("") || newPswd.getText().toString().trim().isEmpty()){
            L.l(getApplicationContext(), "Empty New Password");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(validateRow()) {
            myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "SettingsDialogBox");
            if (v.getId() == R.id.updatePswd) {
                sendPswdResetAction(new VolleyCallbackListener() {
                    @Override
                    public void onSuccess(String message) {
                        myLoadingAlertDialogFrag.dismiss();
                        L.l(getApplicationContext(), message);
                    }

                    @Override
                    public void onFailure(String message) {
                        myLoadingAlertDialogFrag.dismiss();
                        L.l(getApplicationContext(), message);
                    }
                });
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void sendPswdResetAction(final VolleyCallbackListener volleyCallbackListener){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Helpers.URL_STRING+"/password/reset", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException(jsonObject.getString("message"));
                    }
                    volleyCallbackListener.onSuccess("Successful");
                }
                catch (JSONException e) {
                    volleyCallbackListener.onFailure(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallbackListener.onFailure("Error: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("admin_id", Objects.requireNonNull(getSharedPreferences(
                        "MyDataSdch", MODE_PRIVATE).getString("admin_id", "")));

                params.put("oldpswd", oldPswd.getText().toString().trim());
                params.put("newpswd", newPswd.getText().toString().trim());
                params.put("apiKey", Helpers.API_KEY);
                params.put("apiId", Helpers.API_ID);
                return params;
            }
        };
        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, "resetPswd");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent accounts = new Intent(this, MainMenuBoard.class);
            accounts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(accounts);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.checkBoxShowPswd){
            if(!isChecked){
                newPswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                newPswd.setSelection(newPswd.getText().length());
            }
            else{
                newPswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                newPswd.setSelection(newPswd.getText().length());
            }
        }
    }


    private void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager   = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    interface VolleyCallbackListener{
        void onSuccess(String message);
        void onFailure(String message);
    }
}
