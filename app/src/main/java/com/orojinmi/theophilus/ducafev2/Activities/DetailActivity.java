package com.orojinmi.theophilus.ducafev2.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.orojinmi.theophilus.ducafev2.Adapters.DetailActivityRecyclerAdapter;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyFragNumDialogBox;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.Model.WalletActivity;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.AidlUtil;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;
import com.orojinmi.theophilus.ducafev2.bean.TableItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements MyFragNumDialogBox.Communicator {

    int stoffsetRow = 0;
    boolean isScrolling = false;
    private MyVolleySingleton myVolleySingleton;
    private RecyclerView recyclerView;
    private DetailActivityRecyclerAdapter activityRecyclerAdapter;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;
    private DetailActivity thisActivity;
    private Button cashWithDraw;
    private MyFragNumDialogBox myFragDialogBox;
    private String strFullname, registerId;
    int currentItems, totalItems, scrollOutItems;

    LinkedList<TableItem> datalist;

    private static final String PAYMENT_ROW_DETAIL              = "PayRowDetail";
    private static final String INTENT_EXTRA_AMOUNT_PAYABLE     = "AmountPayable";
    private static final String INTENT_EXTRA_WALLET_KEY_CODE    = "WalletKeyQrCode";
    private static final String DEFAULT_VALUE = "N/A";

    private ImageView activateImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        registerId  = getIntent().getStringExtra("register_id");
        strFullname = getIntent().getStringExtra("firstname")+" "+getIntent().getStringExtra("lastname");

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getIntent().getStringExtra("firstname")+" Activity");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        thisActivity    = this;
        datalist        = new LinkedList<>();
        addOneData(datalist);
        myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();

        init();

        activateImageView                   = findViewById(R.id.creditCard);
        recyclerView                        = findViewById(R.id.scrollRecyclerView);

        final LinearLayoutManager layoutManager   = new LinearLayoutManager(this);
        DividerItemDecoration decoration    = new DividerItemDecoration(this, layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);

        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "detailMyLoadFrag");
        setJsonTaskAction(new VolleyCallbackAction() {
            @Override
            public void onSuccess(ArrayList<WalletActivity> walletActivity) {
                activityRecyclerAdapter = new DetailActivityRecyclerAdapter(walletActivity, thisActivity);
                recyclerView.setAdapter(activityRecyclerAdapter);
                myLoadingAlertDialogFrag.dismiss();
            }

            @Override
            public void onFailure(String message) {
                myLoadingAlertDialogFrag.dismiss();
                L.l(getApplicationContext(), message);
            }
        }, stoffsetRow);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems    = layoutManager.getChildCount();
                totalItems      = layoutManager.getItemCount();
                scrollOutItems  = layoutManager.findFirstVisibleItemPosition();
                if(isScrolling && (currentItems + scrollOutItems == totalItems) && dy > 0){
                    isScrolling = false;
                    fetchNewJsonRow();
                }
            }
        });

        activateImageView.setOnClickListener(onClickImageView);
    }

    public void addOneData(LinkedList<TableItem> data) {
        TableItem tableItemHead = new TableItem();
        data.add(tableItemHead);
    }

    private void setTableItemLink(Bitmap bitmap, String[] p, String[] b, String customer, String date, String code){
        //Printing Task to show the Item Paid for and balance
        datalist.add(new TableItem(new String[]{p[0], p[1]}));
        datalist.add(new TableItem(new String[]{b[0], b[1]}));
        SharedPreferences sharedPreferences = getSharedPreferences("MyDataSdch", MODE_PRIVATE);
        String merchantAgent                = sharedPreferences.getString("fullname", DEFAULT_VALUE);
        AidlUtil.getInstance().printTextBitmap(bitmap, 0, datalist, customer, merchantAgent, date, code);
    }

    @SuppressLint("SetTextI18n")
    private void init(){
        TextView fullname       = findViewById(R.id.fullname);
        TextView cashAvailable  = findViewById(R.id.cashAvailableTxt);
        TextView actualCash     = findViewById(R.id.actualAmtAvailableTxt);
        cashWithDraw            = findViewById(R.id.withdrawBtn);

        Typeface myCustomTypefaceBold   = Typeface.createFromAsset(getAssets(), "fonts/DaxlinePro-Bold.ttf");
        Typeface myCustomTypeface       = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");
        Typeface myCustomTypefaceBlack  = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Black.ttf");
        actualCash.setText("N"+getIntent().getStringExtra("cash_balance"));
        fullname.setText(strFullname);

        fullname.setTypeface(myCustomTypeface);
        cashAvailable.setTypeface(myCustomTypeface);
        actualCash.setTypeface(myCustomTypefaceBlack);
        cashWithDraw.setTypeface(myCustomTypefaceBlack);
        myVolleySingleton   = MyVolleySingleton.getInstance(this);
        cashWithDraw.setOnClickListener(onClickListener);
    }

    private void fetchNewJsonRow(){
        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "detailMyLoadFrag");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stoffsetRow = stoffsetRow + 10;
                setJsonTaskAction(new VolleyCallbackAction() {
                    @Override
                    public void onSuccess(ArrayList<WalletActivity> walletActivity) {
                        if(activityRecyclerAdapter != null){
                            activityRecyclerAdapter.addActivityItemAction(walletActivity);
                            myLoadingAlertDialogFrag.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        myLoadingAlertDialogFrag.dismiss();
                        L.l(getApplicationContext(), message);
                    }
                }, stoffsetRow);
            }
        }, 2000);
    }

    /**
     * Set the click listnere into a variable
     */
    View.OnClickListener onClickListener    = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogMyFragDialogBox(v);
        }
    };

    //When the cardImage clicked to set the Activate Button
    View.OnClickListener onClickImageView   = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            accountReactivate();
        }
    };

    //DialogClickListener Interface Listener
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    startActivation();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    //Action to reactivate the account
    private void accountReactivate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage("Are you sure to reactivate this account?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    private void startActivation(){
        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "accActivateBox");
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        activateAccountRow(new VolleyCallbackResAction() {
                            @Override
                            public void onSuccess(String message) {
                                myLoadingAlertDialogFrag.dismiss();
                                L.l(thisActivity, message);
                            }

                            @Override
                            public void onFailure(String message) {
                                myLoadingAlertDialogFrag.dismiss();
                                L.l(thisActivity, message);
                            }
                        });
                    }
                }, 2000
        );
    }

    //Send request to activate the Account
    private void activateAccountRow(final VolleyCallbackResAction callbackResAction){
        String urlString    = Helpers.URL_STRING + "/users/activate";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject  = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException(jsonObject.getString("message"));
                    }
                    callbackResAction.onSuccess(jsonObject.getString("message"));
                } catch (JSONException e) {
                    callbackResAction.onFailure(e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackResAction.onFailure("Error: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> getParams   = new HashMap<>();
                getParams.put("register_id", registerId);
                getParams.put("apiId", Helpers.API_ID);
                getParams.put("apiKey", Helpers.API_KEY);
                return getParams;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        myVolleySingleton.addToRequestQueue(stringRequest, "activateAccount");
    }

    public void topUpPayToJsonAction(final String topupAmount, final PaymentDetail.VolleyCallBackAction volleyCallBackAction){
        String urlString          = Helpers.URL_STRING + "/wallet/topup";
        final String walletId     = getIntent().getStringExtra("wallet_id");
        final String registerId   = getIntent().getStringExtra("register_id");
        final SharedPreferences sharedP = getSharedPreferences("MyDataSdch", MODE_PRIVATE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException(jsonObject.getString("message"));
                    }
                    volleyCallBackAction.onSuccessListener(activityParseJson(jsonObject));
                } catch (JSONException e) {
                    volleyCallBackAction.onFailureListener(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallBackAction.onFailureListener("V error:"+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> getParams   = new HashMap<>();
                getParams.put("apiId", Helpers.API_ID);
                getParams.put("apiKey", Helpers.API_KEY);
                getParams.put("register_id", registerId);
                getParams.put("amountToUp", topupAmount);
                getParams.put("createdBy", Objects.requireNonNull(sharedP.getString("admin_id", "N/A")));
                getParams.put("purpose", "Topup Wallet");
                getParams.put("wallet_id", walletId);
                return getParams;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        myVolleySingleton.addToRequestQueue(stringRequest, "topUpRequest");
    }

    private WalletActivity activityParseJson(JSONObject jsonObject) throws JSONException {
        WalletActivity walletActivity   = new WalletActivity();
        JSONObject getData  = jsonObject.getJSONObject("data");
        walletActivity.setDateAdded(getData.getString("transaction_date"));
        walletActivity.setAmount(getData.getString("amount"));
        walletActivity.setBalance(getData.getString("balance"));
        walletActivity.setCodename(getData.getString("codename"));
        walletActivity.setFullname(getData.getString("fullname"));
        walletActivity.setPurpose(getData.getString("purpose"));
        return walletActivity;
    }

    public void setJsonTaskAction(final VolleyCallbackAction volleyCallbackAction, int offset){
        String walletId             = getIntent().getStringExtra("wallet_id");
        String registerId           = getIntent().getStringExtra("register_id");
        String urlStringParam       = "/wallet/getlist/"+registerId+"/"+walletId+"?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY+"&offset="+offset;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Helpers.URL_STRING + urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(jsonObject.getString("status").equals("OK")){
                        JSONObject dataFlow = jsonObject.getJSONObject("data");
                        volleyCallbackAction.onSuccess(parseJson(dataFlow));
                    }
                    else{
                        throw new JSONException("Error Parsing JSON Response");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    volleyCallbackAction.onFailure("JSONException:"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallbackAction.onFailure("Error Response: "+error);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, "detailActTag");
    }

    /**
     * @param response
     * @return ArrayList<WalletActivity></WalletActivity>
     */
    private ArrayList<WalletActivity> parseJson(JSONObject response){
        ArrayList<WalletActivity> usersArrayList = new ArrayList<>();
        try {
            JSONArray array = response.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                WalletActivity walletActivity = new WalletActivity();
                JSONObject current  = array.getJSONObject(i);
                if(current.has("register_id")){
                    walletActivity.setWalletId(current.getInt("wallet_id"));
                    walletActivity.setRegisterId(current.getInt("register_id"));
                    walletActivity.setWallettransactionId(current.getInt("wallettransactions_id"));
                    walletActivity.setDateAdded(current.getString("date_added"));
                    walletActivity.setPurpose(current.getString("purpose"));
                    walletActivity.setAmount(current.getString("amount"));
                }
                usersArrayList.add(walletActivity);
            }
        }
        catch(JSONException ex){
            L.l(getApplicationContext(), "Parse JSON error: "+ex.getMessage());
        }
        return usersArrayList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent accounts = new Intent(this, Accounts.class);
            accounts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(accounts);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param view
     */
    public void showDialogMyFragDialogBox(View view){
        FragmentManager manager = getSupportFragmentManager();
        myFragDialogBox         = new MyFragNumDialogBox();
        myFragDialogBox.show(manager, "MyDetailFragDialogBox");
    }

    /**
     * @param textString
     */
    @Override
    public void onDialogMsgTask(final String textString) {
        if(!textString.trim().equals("") || !textString.trim().isEmpty()){
            myFragDialogBox.dismiss();
            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.duilong);
            myLoadingAlertDialogFrag.show(getSupportFragmentManager(),"detailMyLoadFrag");
            topUpPayToJsonAction(textString.trim(), new PaymentDetail.VolleyCallBackAction() {
                @Override
                public void onSuccessListener(WalletActivity walletActivity) {

                    double formatAmt    = Double.parseDouble(walletActivity.getBalance());
                    @SuppressLint("DefaultLocale")
                    String[] balance    = new String[]{"Total Balance", String.format("%,.2f",formatAmt)};
                    String[] purpose    = new String[]{walletActivity.getPurpose(), walletActivity.getAmount()};
                    setTableItemLink(bitmap, purpose, balance, walletActivity.getFullname(), walletActivity.getDateAdded(), walletActivity.getCodename());
                    tableIntentAction(walletActivity);
                    myLoadingAlertDialogFrag.dismiss();
                }

                @Override
                public void onFailureListener(String message) {
                    myLoadingAlertDialogFrag.dismiss();
                    L.l(getApplicationContext(), message);
                }
            });

        }
    }

    @SuppressLint("DefaultLocale")
    private void tableIntentAction(WalletActivity walletActivity){
        double formatAmt    = Double.parseDouble(walletActivity.getAmount());
        Intent intent   = new Intent(getApplicationContext(), Success.class);
        intent.putExtra(PAYMENT_ROW_DETAIL, new Gson().toJson(datalist));
        intent.putExtra("datePay", walletActivity.getDateAdded());
        intent.putExtra("TransactCode", walletActivity.getCodename());
        intent.putExtra("PayerName", walletActivity.getFullname());
        intent.putExtra(INTENT_EXTRA_AMOUNT_PAYABLE, String.format("%,.2f", formatAmt));
        intent.putExtra(INTENT_EXTRA_WALLET_KEY_CODE, walletActivity.getWalletCode());
        startActivity(intent);
        finish();
    }

    interface VolleyCallbackAction{
        void onSuccess(ArrayList<WalletActivity> walletActivity);
        void onFailure(String message);
    }

    interface VolleyCallbackResAction{
        void onSuccess(String message);
        void onFailure(String message);
    }
}
