package com.orojinmi.theophilus.ducafev2.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.Adapters.AccountRecyclerAdapter;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.MainMenuBoard;
import com.orojinmi.theophilus.ducafev2.Model.Users;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Accounts extends AppCompatActivity implements SearchView.OnQueryTextListener {

    int stoffsetRow = 0;
    boolean isScrolling = false;
    private ProgressBar progressBar;
    private MyVolleySingleton myVolleySingleton;
    int currentItems, totalItems, scrollOutItems;
    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;
    private AccountRecyclerAdapter accountRecyclerAdapter;
    private Accounts thisAccount;
    private RecyclerView recyclerView;
    private boolean stopFetchingRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        thisAccount             = this;
        stopFetchingRows        = true;
        TextView merchantText   = findViewById(R.id.merchantTxt);
        recyclerView            = findViewById(R.id.accountRecylerView);

        myVolleySingleton       = MyVolleySingleton.getInstance(getApplicationContext());
        mTypefaceBlack          = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        merchantText.setTypeface(mTypefaceBlack);

        myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();
        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "myAccountDialogBox");

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration decoration  = new DividerItemDecoration(this, layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);

        getRegisterRowAction(new VolleyCallbackListener() {
            @Override
            public void onSuccess(ArrayList<Users> usersArrayList) {
                myLoadingAlertDialogFrag.dismiss();
                accountRecyclerAdapter  = new AccountRecyclerAdapter(usersArrayList, thisAccount);
                recyclerView.setAdapter(accountRecyclerAdapter);
            }

            @Override
            public void onFailure(String message) {
                myLoadingAlertDialogFrag.dismiss();
                L.l(getApplicationContext(), "Failure Message: "+message);
            }
        }, stoffsetRow, "");

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
                    fetchNewJsonRow("");
                }
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu, menu);
        MenuItem menuItem       = menu.findItem(R.id.action_search);
        SearchView searchView   = (SearchView) menuItem.getActionView();

        searchView.setOnCloseListener(onCloseListener);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void fetchNewJsonRow(final String searchQuery){
        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "myAccountDialogBox");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stoffsetRow = stoffsetRow + 10;
                getRegisterRowAction(new VolleyCallbackListener() {
                    @Override
                    public void onSuccess(ArrayList<Users> usersArrayList) {
                        if(accountRecyclerAdapter != null){
                            accountRecyclerAdapter.addArrayAccountListAction(usersArrayList);
                            myLoadingAlertDialogFrag.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        myLoadingAlertDialogFrag.dismiss();
                        L.l(getApplicationContext(), message);
                    }
                },stoffsetRow, searchQuery);
            }
        }, 5000);
    }

    private void getRegisterRowAction(final VolleyCallbackListener callbackListener, int offset, String searchQuery){
        String urlStringParam   = Helpers.URL_STRING + "/users/list?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY+"&offset="+offset+"&search="+searchQuery;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(jsonObject.getString("status").equals("OK")){
                        JSONObject dataFlow = jsonObject.getJSONObject("data");
                        stopFetchingRows    = jsonObject.getBoolean("end");
                        callbackListener.onSuccess(parseJson(dataFlow));
                    }
                } catch (JSONException e) {
                    L.l(getApplicationContext(), "JSON Error:"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callbackListener.onFailure("ERROR: "+error);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest,"userList");
    }

    private ArrayList<Users> parseJson(JSONObject response){
        ArrayList<Users> usersArrayList = new ArrayList<>();
        try {
            JSONArray array = response.getJSONArray("data");
            for(int i = 0; i < array.length(); i++){
                Users userRow       = new Users();
                JSONObject current  = array.getJSONObject(i);
                if(current.has("register_id")){
                    userRow.setPhone(current.getString("phone"));
                    userRow.setFirstname(current.getString("firstname"));
                    userRow.setLastname(current.getString("lastname"));
                    userRow.setRegisterId(current.getString("register_id"));
                    userRow.setFirstLetter(current.getString("firstLetter"));
                    userRow.setCashBalance(current.getString("cashBalance"));
                    userRow.setWalletId(current.getString("wallet_id"));
                    userRow.setWalletCode(current.getString("walletcode"));
                    userRow.setDateCreated(current.getString("date_created"));
                    userRow.setEmail(current.getString("email"));
                }
                usersArrayList.add(userRow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            L.l(getApplicationContext(), "JSON Response Error: "+e.getMessage());
        }
        return usersArrayList;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String textInput    = s.toLowerCase();
        if(textInput.length() > 4){
            if(accountRecyclerAdapter != null){
                accountRecyclerAdapter.clearUserList();
            }
            fetchNewJsonRow(textInput);
            return false;
        }
        return false;
    }

    SearchView.OnCloseListener onCloseListener  = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            stoffsetRow = 0;
            fetchNewJsonRow("");
            return false;
        }
    };

    private interface VolleyCallbackListener{
        void onSuccess(ArrayList<Users> usersArrayList);
        void onFailure(String message);
    }
}
