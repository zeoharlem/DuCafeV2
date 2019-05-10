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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.orojinmi.theophilus.ducafev2.Adapters.HistoryRecyclerAdapter;
import com.orojinmi.theophilus.ducafev2.DialogBox.MyLoadingAlertDialogFrag;
import com.orojinmi.theophilus.ducafev2.MainMenuBoard;
import com.orojinmi.theophilus.ducafev2.Model.HistoryActivity;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.Helpers;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class History extends AppCompatActivity {

    int stoffsetRow = 0;
    String sMonth, sDay, sYear;
    List<String> monthsRow  = new ArrayList<>();
    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    ArrayList<HistoryActivity> historyActivityArrayList;
    HistoryRecyclerAdapter historyRecyclerAdapter;
    private History thisAccount;
    private RecyclerView recyclerView;
    private TextView textTotal;
    private TextView textTotalAmt;
    private MyLoadingAlertDialogFrag myLoadingAlertDialogFrag;
    Boolean isScrolling         = false;
    int currentItems, totalItems, scrollOutItems;
    private String urlParams    = "";
    private Button historyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("HistoryActivity");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myLoadingAlertDialogFrag    = new MyLoadingAlertDialogFrag();

        monthsRowAction();

        init();
        thisAccount     = this;
        historyActivityArrayList    = new ArrayList<>();
        recyclerView    = findViewById(R.id.historyRecycler);

        final LinearLayoutManager layoutManager   = new LinearLayoutManager(this);
        DividerItemDecoration decoration    = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);

        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "historyAlertBox");

        setJsonRowAction(stoffsetRow, new VolleyCallBackAction() {
            @Override
            public void OnSuccess(ArrayList<HistoryActivity> historyActivities, String totalAmt) {
                historyRecyclerAdapter  = new HistoryRecyclerAdapter(historyActivities, thisAccount);
                recyclerView.setAdapter(historyRecyclerAdapter);
                textTotalAmt.setText(totalAmt+":00");
                myLoadingAlertDialogFrag.dismiss();
            }

            @Override
            public void OnFailure(String message) {
                L.l(getApplicationContext(), message);
                myLoadingAlertDialogFrag.dismiss();
            }
        }, "");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                    //clickSubmitDateAction();
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
                    //clickSubmitDateAction();
                    fetchNewJsonRow(urlParams);
                }
            }
        });
    }

    private void init(){
        sMonth = "January"; sDay = "01"; sYear = "2019";
        Spinner daysRow         = findViewById(R.id.daysRow);
        Spinner monthSpinner    = findViewById(R.id.monthsRow);
        textTotal               = findViewById(R.id.totalText);
        textTotalAmt            = findViewById(R.id.totalAmount);

        mTypefaceBlack          = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        mTypefaceBold           = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");
        textTotalAmt.setTypeface(mTypefaceBold);
        textTotal.setTypeface(mTypefaceBold);

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthsRow);
        ArrayAdapter stringDaysRowAdapt         = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);

        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        monthSpinner.setAdapter(stringArrayAdapter);
        daysRow.setAdapter(stringDaysRowAdapt);

        historyBtn  = findViewById(R.id.historyBtn);
        historyBtn.setTypeface(mTypefaceBlack);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView mySelectedText = (TextView) view;
                sMonth  = mySelectedText.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        daysRow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView mySelectedText = (TextView) view;
                sDay    = mySelectedText.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clickSubmitDateAction();
    }

    /**
     * History Date Click Event
     */
    private void clickSubmitDateAction(){
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "historyAlertBox");
                int currYear        = Calendar.getInstance().get(Calendar.YEAR);
                urlParams    = "&dateTrans="+currYear+"-"+sMonth.substring(sMonth.indexOf("-")+1)+"-"+sDay;
                setJsonRowAction(stoffsetRow, new VolleyCallBackAction() {
                    @Override
                    public void OnSuccess(ArrayList<HistoryActivity> historyActivities, String totalAmt) {
                        historyRecyclerAdapter.setUsersArrayList(historyActivities);
                        historyRecyclerAdapter.notifyDataSetChanged();
                        textTotalAmt.setText(totalAmt+":00");
                        myLoadingAlertDialogFrag.dismiss();
                    }

                    @Override
                    public void OnFailure(String message) {
                        L.l(getApplicationContext(), message);
                        myLoadingAlertDialogFrag.dismiss();
                    }
                }, urlParams);
            }
        });
    }

    private List<String> monthsRowAction(){
        monthsRow.add("January-01");
        monthsRow.add("February-02");
        monthsRow.add("March-03");
        monthsRow.add("April-04");
        monthsRow.add("May-05");
        monthsRow.add("June-06");
        monthsRow.add("July-07");
        monthsRow.add("August-08");
        monthsRow.add("September-09");
        monthsRow.add("October-10");
        monthsRow.add("November-11");
        monthsRow.add("December-12");
        return monthsRow;
    }

    private void fetchNewJsonRow(final String urlParams){
        myLoadingAlertDialogFrag.show(getSupportFragmentManager(), "historyAlertBox");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stoffsetRow = stoffsetRow + 10;
                setJsonRowAction(stoffsetRow, new VolleyCallBackAction() {
                    @Override
                    public void OnSuccess(ArrayList<HistoryActivity> historyActivities, String totalAmt) {
                        if(historyRecyclerAdapter != null){
                            historyRecyclerAdapter.addArrayListHistory(historyActivities);
                            myLoadingAlertDialogFrag.dismiss();
                        }
                    }

                    @Override
                    public void OnFailure(String message) {
                        myLoadingAlertDialogFrag.dismiss();
                        L.l(getApplicationContext(), message);
                    }
                }, urlParams);
            }
        }, 2000);
    }

    private void setJsonRowAction(int offset, final VolleyCallBackAction volleyCallBackAction, String urlParams){
        String urlStringParam   = Helpers.URL_STRING + "/history/"+offset+"?apiId="+Helpers.API_ID+"&apiKey="+Helpers.API_KEY+urlParams;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlStringParam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject   = new JSONObject(response);
                    if(!jsonObject.getString("status").equals("OK")){
                        throw new JSONException("Status Not OK");
                    }
                    JSONObject dataFlow = jsonObject.getJSONObject("data");
                    volleyCallBackAction.OnSuccess(parseJson(dataFlow), jsonObject.getString("total"));
                }
                catch (JSONException e) {
                    volleyCallBackAction.OnFailure(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallBackAction.OnFailure("Server: "+error);
            }
        });
        MyVolleySingleton.getInstance(this).addToRequestQueue(stringRequest, "historyReq");
    }

    private ArrayList<HistoryActivity> parseJson(JSONObject response){
        ArrayList<HistoryActivity> usersArrayList = new ArrayList<>();
        try {
            JSONArray array = response.getJSONArray("data");
            for(int i = 0; i < array.length(); i++){
                HistoryActivity userRow = new HistoryActivity();
                JSONObject current      = array.getJSONObject(i);
                if(current.has("register_id")){
                    userRow.setFullname(current.getString("fullname"));
                    userRow.setWalletTransactionId(current.getString("wallettransactions_id"));
                    userRow.setWalletId(current.getString("wallet_id"));
                    userRow.setAmount(current.getString("amount"));
                    userRow.setDateAdded(current.getString("date_added"));
                    userRow.setPurpose(current.getString("purpose"));
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
        else if(id == android.R.id.home){
            Intent accounts = new Intent(this, MainMenuBoard.class);
            accounts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(accounts);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    interface VolleyCallBackAction{
        void OnSuccess(ArrayList<HistoryActivity> historyActivities, String totalAmt);
        void OnFailure(String message);
    }
}
