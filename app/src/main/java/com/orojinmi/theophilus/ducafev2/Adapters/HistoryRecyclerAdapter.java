package com.orojinmi.theophilus.ducafev2.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.Model.HistoryActivity;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;

import java.util.ArrayList;
import java.util.Random;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {

    private LayoutInflater layoutInflater;
    private MyVolleySingleton volleySingleton;
    private ArrayList<HistoryActivity> usersArrayList;
    private Context context;

    public HistoryRecyclerAdapter(ArrayList<HistoryActivity> usersArrayList, Context context) {
        this.context        = context;
        this.usersArrayList = usersArrayList;
        layoutInflater      = LayoutInflater.from(context);
        volleySingleton     = MyVolleySingleton.getInstance(context);
    }

    public void addArrayListHistory(ArrayList<HistoryActivity> usersArrayList){
        this.usersArrayList.addAll(usersArrayList);
        notifyDataSetChanged();
    }

    public ArrayList<HistoryActivity> getUsersArrayList() {
        return usersArrayList;
    }

    public void setUsersArrayList(ArrayList<HistoryActivity> usersArrayList) {
        this.usersArrayList = usersArrayList;
    }

    private int getRandomColor(){
        Random random   = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = layoutInflater.inflate(R.layout.history_item_row, viewGroup, false);
        return new HistoryViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int i) {
        final HistoryActivity userTransact  = usersArrayList.get(i);
        historyViewHolder.amountPaid.setText(userTransact.getAmount());
        historyViewHolder.purposeTag.setText(userTransact.getPurpose());
        historyViewHolder.transactionDate.setText(userTransact.getDateAdded());
        historyViewHolder.textReceiver.setText(userTransact.getFullname());
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder{
        TextView purposeTag, transactionDate, textReceiver, amountPaid;
        private Typeface myCustomTypeface, myCustomTypefaceBold, myCustomTypefaceBlack;
        private Button openBtn;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            amountPaid          = itemView.findViewById(R.id.amountPaid);
            textReceiver        = itemView.findViewById(R.id.textReceiver);
            purposeTag          = itemView.findViewById(R.id.purposeTag);
            transactionDate     = itemView.findViewById(R.id.dateHistoryTag);
            setTypeFaceTask(itemView);
        }

        private void setTypeFaceTask(View itemView){
            myCustomTypefaceBold    = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/hurme-geometric-bold.ttf");
            myCustomTypeface        = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/DaxlinePro-Regular.ttf");
            myCustomTypefaceBlack   = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/ProximaNova-Black.ttf");
            amountPaid.setTypeface(myCustomTypefaceBlack);
            textReceiver.setTypeface(myCustomTypefaceBold);
        }
    }
}
