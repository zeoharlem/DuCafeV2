package com.orojinmi.theophilus.ducafev2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.Model.Admin;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox showHidePassword;
    private EditText editTextEmailAdd;
    private EditText editTextPassword;
    private Button buttonLogin;

    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEmailAdd    = findViewById(R.id.login_emailid);
        editTextPassword    = findViewById(R.id.login_password);
        showHidePassword    = findViewById(R.id.show_hide_password);
        buttonLogin         = findViewById(R.id.loginBtn);

        buttonLogin.setOnClickListener(this);
        showHidePassword.setOnCheckedChangeListener(this);

        //Set the OnFocused Listener when the Layout is clicked
        editTextEmailAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideSoftKeyBoard(v);
                }
            }
        });
        setTypefaceInit();
    }

    private void setTypefaceInit(){
        mTypeface          = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Thin.ttf");
        mTypefaceRegular   = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Reg.ttf");
        mTypefaceBlack     = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        mTypefaceBold      = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");

        editTextEmailAdd.setTypeface(mTypefaceBold);
        editTextPassword.setTypeface(mTypefaceBold);
        showHidePassword.setTypeface(mTypefaceBold);
        buttonLogin.setTypeface(mTypefaceBlack);
    }

    private boolean validateRow(){
        String textEmailId  = editTextEmailAdd.getText().toString().trim();
        String textPassword = editTextPassword.getText().toString().trim();
        Pattern pattern     = Pattern.compile(Helpers.regEx);
        Matcher matcher     = pattern.matcher(textEmailId);
        if(textEmailId.equals("") || textEmailId.isEmpty()){
            L.l(getApplicationContext(), "Your Username Field is Empty");
            return false;
        }
        else if(textPassword.equals("") || textPassword.isEmpty()){
            L.l(getApplicationContext(), "Your Password Field is Empty");
            return false;
        }
        else if(!matcher.find()){
            L.l(getApplicationContext(), "Your Email Address is Invalid Format");
            return false;
        }
        return true;
    }

    private void hideSoftKeyBoard(View view) {
        InputMethodManager inputMethodManager   = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                checkLoginTaskRow();
                break;
        }
    }

    private void createSharedPrefAction(Admin admin){
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataSdch", MODE_PRIVATE);
        SharedPreferences.Editor editor     = sharedPreferences.edit();
        editor.putString("email", admin.getUsername());
        editor.putString("fullname", admin.getFullname());
        editor.putString("admin_id", admin.getRegister_id());
        editor.putString("privy", String.valueOf(admin.getPrivy()));
        editor.putString("codename", admin.getCodename());
        editor.putString("role", admin.getRole());
        editor.apply();
    }

    private void checkLoginTaskRow() {
        //L.l(getApplicationContext(), "Login Button Lcikced");
        if(validateRow()) {
            String textEmailId  = editTextEmailAdd.getText().toString().trim();
            String textPassword = editTextPassword.getText().toString().trim();
            myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();
            myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "MainActivityAlertBox");
            setLoginAction(textEmailId, textPassword, new VolleyCallbackAction() {
                @Override
                public void onFailure(String message) {
                    myLoadingAlertDialogFrag.dismiss();
                    L.l(getApplicationContext(), message);
                }

                @Override
                public void onSuccess(Admin admin) {
                    myLoadingAlertDialogFrag.dismiss();
                    createSharedPrefAction(admin);
                    Intent intent = new Intent(getApplicationContext(), MainMenuBoard.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void setLoginAction(final String username, final String password, final VolleyCallbackAction volleyCallbackAction){
        String urlStringParam       = "/login/"+username+"/"+password+"?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Helpers.URL_STRING+urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException(jsonObject.getString("message"));
                    }
                    volleyCallbackAction.onSuccess(parseJson(jsonObject));
                } catch (JSONException e) {
                    volleyCallbackAction.onFailure("JSON:"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallbackAction.onFailure("Error: "+error.getMessage());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, "loginTag");
    }

    /**
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    private Admin parseJson(JSONObject jsonObject) throws JSONException {
        Admin admin         = new Admin();
        JSONObject getData  = jsonObject.getJSONObject("data");
        if(checkApiToken(getData, admin)){
            admin.setPrivy(getData.getInt("privy"));
            admin.setFullname(getData.getString("fullname"));
            admin.setRegister_id(getData.getString("register_id"));
            admin.setCodename(getData.getString("codename"));
            admin.setUsername(getData.getString("username"));
            admin.setRole(getData.getString("role"));
        }
        return admin;
    }

    /**
     * @param getData
     * @param admin
     * @return
     * @throws JSONException
     */
    private boolean checkApiToken(JSONObject getData, Admin admin) throws JSONException {
        boolean typeCheckRow    = false;
        if(getData.has("apiTokenKey") && getData.has("accessKeyToken")){
            JSONObject currentApiKey    = getData.getJSONObject("apiTokenKey");
            String currentAccessKey     = getData.getString("accessKeyToken");
            if(!currentApiKey.getString("appKeyId").isEmpty() && !currentApiKey.getString("appKeySecret").isEmpty() && !currentAccessKey.isEmpty()){
                if(currentApiKey.getString("appKeySecret").equals(Helpers.API_KEY) && currentApiKey.getString("appKeyId").equals(Helpers.API_ID)) {
                    typeCheckRow = true;
                    admin.setAccessKeyToken(currentAccessKey);
                    admin.setAppSecretKey(currentApiKey.getString("appKeySecret"));
                    admin.setApiKeyId(currentApiKey.getString("appKeyId"));
                }
            }
        }
        return typeCheckRow;
    }

    private void setPreferenceAction(){

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.show_hide_password){
            if(!isChecked){
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
            else{
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        }
    }

    private interface VolleyCallbackAction{
        void onFailure(String message);
        void onSuccess(Admin admin);
    }
}
