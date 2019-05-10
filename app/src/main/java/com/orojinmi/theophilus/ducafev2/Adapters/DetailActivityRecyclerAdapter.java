package com.orojinmi.theophilus.ducafev2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.Model.WalletActivity;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.CircularTextView;

import java.util.ArrayList;
import java.util.Random;

public class DetailActivityRecyclerAdapter extends RecyclerView.Adapter<DetailActivityRecyclerAdapter.DetailActivityViewHolder>{

    private LayoutInflater layoutInflater;
    private MyVolleySingleton volleySingleton;
    private ArrayList<WalletActivity> walletActivityArrayList;
    private Context context;

    public DetailActivityRecyclerAdapter(ArrayList<WalletActivity> walletActivityArrayList, Context context) {
        this.context            = context;
        this.walletActivityArrayList = walletActivityArrayList;
        layoutInflater          = LayoutInflater.from(context);
        volleySingleton         = MyVolleySingleton.getInstance(context);
    }

    @NonNull
    @Override
    public DetailActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = layoutInflater.inflate(R.layout.item_detail_activity_row, viewGroup, false);
        return new DetailActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailActivityViewHolder detailActivityViewHolder, int i) {
        WalletActivity walletActivity = walletActivityArrayList.get(i);
        detailActivityViewHolder.priceTagAct.setText(walletActivity.getAmount());
        detailActivityViewHolder.purposeTitle.setText(walletActivity.getPurpose());
        detailActivityViewHolder.datePurposeAct.setText(walletActivity.getDateAdded());
        //detailActivityViewHolder.circularTextView.setBackgroundColor(getRandomColor());
    }

    @Override
    public int getItemCount() {
        int activityArraySise   = 0;
        if(walletActivityArrayList != null){
            activityArraySise   = walletActivityArrayList.size();
        }
        return activityArraySise;
    }

    public void addActivityItemAction(ArrayList<WalletActivity> walletActivityArrayList){
        this.walletActivityArrayList.addAll(walletActivityArrayList);
        notifyDataSetChanged();
    }

    private int getRandomColor(){
        Random random   = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public ArrayList<WalletActivity> getWalletActivityArrayList(){
        return this.walletActivityArrayList;
    }

    class DetailActivityViewHolder extends RecyclerView.ViewHolder{

        TextView purposeTitle, datePurposeAct, priceTagAct;
        Typeface mTypefaceRegular, mTypefaceBold, mTypefaceBlack;
        CircularTextView circularTextView;

        DetailActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            purposeTitle    = itemView.findViewById(R.id.purposeTitle);
            datePurposeAct  = itemView.findViewById(R.id.datePurposeAct);
            priceTagAct     = itemView.findViewById(R.id.priceTagAct);
            //circularTextView= itemView.findViewById(R.id.bulletPoints);

            mTypefaceBold   = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/hurme-geometric-bold.ttf");
            mTypefaceBlack  = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/ProximaNova-Black.ttf");
            mTypefaceRegular= Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/DaxlinePro-Regular.ttf");

            purposeTitle.setTypeface(mTypefaceBold);
            //circularTextView.setTypeface(mTypefaceBlack);
            //datePurposeAct.setTypeface(mTypefaceBold);
            priceTagAct.setTypeface(mTypefaceBlack);
        }
    }
}
