package com.orojinmi.theophilus.ducafev2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.Activities.Accounts;
import com.orojinmi.theophilus.ducafev2.Activities.CheckBalance;
import com.orojinmi.theophilus.ducafev2.Activities.CreateAccount;
import com.orojinmi.theophilus.ducafev2.Activities.History;
import com.orojinmi.theophilus.ducafev2.Activities.PayNow;
import com.orojinmi.theophilus.ducafev2.Activities.Profile;
import com.orojinmi.theophilus.ducafev2.Activities.Settings;
import com.orojinmi.theophilus.ducafev2.Adapters.MainDrawerListAdapter;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.Model.Users;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;
import com.orojinmi.theophilus.ducafev2.Utils.NavItems;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainMenuBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView mDrawerList;
    ArrayList<NavItems> navItems;

    private static final int REQUEST_CODE_SCANCODE_KEY      = 3;
    private static final int REQUEST_CODE_CHECK_BALANCE_KEY = 4;
    private Button mPayNow, vTransfer, scanQr, mCreateAccount, mCheckBal;
    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.app_name);

        FloatingActionButton fab    = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTypeface          = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Thin.ttf");
        mTypefaceRegular   = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Reg.ttf");
        mTypefaceBlack     = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");
        mTypefaceBold      = Typeface.createFromAsset(getAssets(), "fonts/DaxlinePro-Bold.ttf");

        //Call the setButtonRow for the customTypeface call and OnclickListener
        setTaskButtonRow();

        //Set the custome sidebar view for the Navigation view here
        navItems    = new ArrayList<>();
        navItems.add(new NavItems("Dashboard", "Meet up Destination", R.drawable.ic_new_email_outline));
        navItems.add(new NavItems("Accounts", "Users on the Platform", R.drawable.ic_account));
        navItems.add(new NavItems("History Activity", "Get Log of Transactions", R.drawable.ic_lock));
        //navItems.add(new NavItems("Qr Code", "Generate QrCode to be Scanned", R.drawable.ic_qr_code_light));
        navItems.add(new NavItems("Profile", "Outline of Merchant", R.drawable.ic_profile));
        navItems.add(new NavItems("Settings", "Change Password/Username", R.drawable.ic_settings));
        navItems.add(new NavItems("Sign Out", "Close Application", R.drawable.ic_power_button));

        DrawerLayout drawer             = findViewById(R.id.drawer_layout);
        mDrawerList                     = findViewById(R.id.navList);
        MainDrawerListAdapter mDrawAdapt= new MainDrawerListAdapter(this, navItems);
        mDrawerList.setAdapter(mDrawAdapt);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        finishStartIntent(MainMenuBoard.class);
                        break;
                    case 1:
                        startIntentTask(Accounts.class);
                        break;
                    case 2:
                        startIntentTask(History.class);
                        break;
                    case 3:
                        startIntentTask(Profile.class);
                        break;
                    case 4:
                        startIntentTask(Settings.class);
                        break;
                    case 5:
                        finishStartIntent(MainActivity.class);
                        break;
                }
            }
        });
        ActionBarDrawerToggle toggle    = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView   = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void startIntentTask(Class className){
        Intent intent   = new Intent(getApplicationContext(), className);
        startActivity(intent);
    }

    private void finishStartIntent(Class className){
        Intent intent   = new Intent(getApplicationContext(), className);
        startActivity(intent);
        finish();
    }

    private void setTaskButtonRow(){
        mPayNow     = findViewById(R.id.paynow);
        vTransfer   = findViewById(R.id.transfer);
        mCreateAccount  = findViewById(R.id.createAccount);
        mCheckBal       = findViewById(R.id.checkBalance);
        scanQr          = findViewById(R.id.qrcodeScan);

        //Set the custom Typeface to each Button
        mPayNow.setTypeface(mTypefaceBlack);
        vTransfer.setTypeface(mTypefaceBlack);
        mCreateAccount.setTypeface(mTypefaceBlack);
        mCheckBal.setTypeface(mTypefaceBlack);
        scanQr.setTypeface(mTypefaceBlack);

        //mPayNow set the click listerner
        mPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeKeyToPayNowActivity();
            }
        });

        vTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                L.l(getApplicationContext(), "Virtual Transfer Not Available Yet");
            }
        });

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeKeyToCreateActivity();
            }
        });

        mCheckBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeKeyToCheckBalActivity();
            }
        });

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeKeyToScanCodeActivity();
            }
        });
    }

    //OnActivityResult test whether this method triggered the call
    private void takeKeyToScanCodeActivity() {
        Intent intent   = new Intent(getApplicationContext(), ScRow.class);
        startActivityForResult(intent, REQUEST_CODE_SCANCODE_KEY);
    }

    //OnActivityResult test whether this method triggered the call
    private void takeKeyToCheckBalActivity() {
        Intent intent   = new Intent(getApplicationContext(), ScRow.class);
        startActivityForResult(intent, REQUEST_CODE_CHECK_BALANCE_KEY);
    }

    private void takeKeyToPayNowActivity() {
        Intent intent   = new Intent(getApplicationContext(), PayNow.class);
        startActivity(intent);
    }

    private void takeKeyToCreateActivity() {
        Intent intent   = new Intent(getApplicationContext(), CreateAccount.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        final String nData    = data.getStringExtra("keyQrCode");
        if(requestCode == REQUEST_CODE_SCANCODE_KEY && resultCode == RESULT_OK){
            L.l(getApplicationContext(), "Scan Code results" + nData);
        }
        else if(requestCode == REQUEST_CODE_CHECK_BALANCE_KEY && resultCode == RESULT_OK){
            //Request for checking Balance. Perform a Volley singleton call
            myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();
            myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "MyLoadingAlertBox");
            myLoadingAlertDialogFrag.callAlertLoadingTaskCallback(new MyLoadingAlertDialogFrag.AlertLoadingTaskCallback() {
                @Override
                public void CallbackTask(final MyLoadingAlertDialogFrag myLoadingAlertDialogFrag) {
                    new Handler(getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myLoadingAlertDialogFrag.dismiss();
                            checkBalanceRequestAction(nData, new VolleyCheckBalanceActionListener() {
                                @Override
                                public void onSuccess(Users users) {
                                    Intent intent   = new Intent(getApplicationContext(), CheckBalance.class);
                                    intent.putExtra("fullname", users.getFirstname());
                                    intent.putExtra("balance", users.getCashBalance());
                                    intent.putExtra("todaysDate", users.getDateCreated());
                                    intent.putExtra("register_id", users.getRegisterId());
                                    intent.putExtra("expiryDate", users.getExpiryDate());
                                    intent.putExtra("userState", users.getUserState());
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(String message) {
                                    L.l(getApplicationContext(), message);
                                }
                            });
                            //L.l(getApplicationContext(), "Check Balance should run volley | "+nData);
                        }
                    }, 3000);
                }
            });
        }
    }

    private void checkBalanceRequestAction(final String walletcode, final VolleyCheckBalanceActionListener balanceActionListener){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Helpers.URL_STRING + "/balance", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException(jsonObject.getString("message"));
                    }
                    balanceActionListener.onSuccess(parseJson(jsonObject));
                } catch (JSONException e) {
                    balanceActionListener.onFailure("JSON ERROR:"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                balanceActionListener.onFailure("V-Error:"+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params  = new HashMap<>();
                params.put("apiKey", Helpers.API_KEY);
                params.put("apiId", Helpers.API_ID);
                params.put("wallet", walletcode);
                return params;
            }
        };
        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, "checkBalTag");
    }

    private Users parseJson(JSONObject jsonObject) throws JSONException {
        Users users = new Users();
        JSONObject getData  = jsonObject.getJSONObject("data");
        if(getData.has("register_id") && !getData.getString("register_id").isEmpty()){
            users.setFirstname(getData.getString("fullname"));
            users.setRegisterId(getData.getString("register_id"));
            users.setCashBalance(getData.getString("cashbalance"));
            users.setDateCreated(getData.getString("todays_date"));
            users.setExpiryDate(getData.getString("expiry_date"));
            users.setUserState(getData.getString("user_state"));
        }
        return users;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    interface VolleyCheckBalanceActionListener{
        void onSuccess(Users users);
        void onFailure(String message);
    }
}
