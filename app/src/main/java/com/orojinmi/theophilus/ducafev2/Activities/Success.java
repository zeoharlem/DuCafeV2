package com.orojinmi.theophilus.ducafev2.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orojinmi.theophilus.ducafev2.MainMenuBoard;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.AidlUtil;
import com.orojinmi.theophilus.ducafev2.Utils.L;
import com.orojinmi.theophilus.ducafev2.bean.TableItem;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import cdflynn.android.library.checkview.CheckView;

public class Success extends AppCompatActivity {

    CheckView checkView;
    Button doneBtn, printAgainBtn;
    TextView textSuccess, payedAmount;
    private static final String DEFAULT_VALUE   = "N/A";
    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;
    private TableItem tableItem;
    private LinkedList<TableItem> tableItemLinkedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        AidlUtil.getInstance().initPrinter();

        mTypeface           = Typeface.createFromAsset(getAssets(),"fonts/hurme-geometric-bold.ttf");
        mTypefaceRegular    = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Reg.ttf");
        mTypefaceBlack      = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");
        setInit();
        checkView.check();
    }

    private void setInit(){
        doneBtn         = findViewById(R.id.doneBtn);
        printAgainBtn   = findViewById(R.id.printAgain);
        textSuccess     = findViewById(R.id.textSuccess);
        payedAmount     = findViewById(R.id.payedAmount);
        checkView       = findViewById(R.id.checkMarker);
        TextView tagM   = findViewById(R.id.merchantTag);

        doneBtn.setTypeface(mTypefaceBlack);
        textSuccess.setTypeface(mTypeface);
        printAgainBtn.setTypeface(mTypefaceBlack);
        tagM.setTypeface(mTypeface);

        payedAmount.setText(getIntent().getStringExtra("AmountPayable"));

        clickDoneButton();
        clickPrintAgain();
    }

    private void getTableItemGson(){
        String payIntent   = getIntent().getStringExtra("PayRowDetail");
        if(payIntent != null && !payIntent.equals("")){
            Type type   = new TypeToken<LinkedList<TableItem>>(){}.getType();
            tableItemLinkedList = new Gson().fromJson(payIntent, type);
            String balKeyValue  = tableItemLinkedList.get(1).getTextKeyInt(1);
            payedAmount.setText(balKeyValue);
            //Iterate the tableItemLinkedList testing purpose
            iterateItemLinkedList(tableItemLinkedList);
        }
    }

    //OnClick Here will Clear Current WalletActivity Off Stack
    private void clickPrintAgain(){
        printAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTableItemGson();
                String datePay  = getIntent().getStringExtra("datePay");
                String payerStr = getIntent().getStringExtra("PayerName");
                String transact = getIntent().getStringExtra("TransactCode");
                Bitmap header   = BitmapFactory.decodeResource(getResources(), R.mipmap.duilong);
                String merchant = getSharedPreferences("MyDataSdch", MODE_PRIVATE).getString("fullname", DEFAULT_VALUE);
                AidlUtil.getInstance().printTextBitmap(header, 0, tableItemLinkedList,payerStr, merchant, datePay, transact);
                mainMenuBoardSetIntent();

            }
        });
    }

    private void clickDoneButton(){
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenuBoardSetIntent();
            }
        });
    }

    private void mainMenuBoardSetIntent(){
        Intent intent   = new Intent(getApplicationContext(), MainMenuBoard.class);
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void iterateItemLinkedList(Collection<TableItem> tableItems){
        Iterator<TableItem> tableItemIterator   = tableItems.iterator();
        while (tableItemIterator.hasNext()){
            L.l(getApplicationContext(), Arrays.toString(tableItemIterator.next().getText()));
        }
    }
}
