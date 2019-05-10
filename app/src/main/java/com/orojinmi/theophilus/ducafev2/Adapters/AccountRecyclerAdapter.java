package com.orojinmi.theophilus.ducafev2.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.Activities.DetailActivity;
import com.orojinmi.theophilus.ducafev2.Model.Users;
import com.orojinmi.theophilus.ducafev2.Network.MyVolleySingleton;
import com.orojinmi.theophilus.ducafev2.R;

import java.util.ArrayList;
import java.util.Random;

public class AccountRecyclerAdapter extends RecyclerView.Adapter<AccountRecyclerAdapter.AccountViewHolder> {

    private LayoutInflater layoutInflater;
    private MyVolleySingleton volleySingleton;
    private ArrayList<Users> usersArrayList;
    private Context context;

    public AccountRecyclerAdapter(ArrayList<Users> usersArrayList, Context context) {
        this.context        = context;
        this.usersArrayList = usersArrayList;
        layoutInflater      = LayoutInflater.from(context);
        volleySingleton     = MyVolleySingleton.getInstance(context);
    }

    public void addArrayAccountListAction(ArrayList<Users> usersArrayList){
        this.usersArrayList.addAll(usersArrayList);
        notifyDataSetChanged();
    }

    private int getRandomColor(){
        Random random   = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public void clearUserList(){
        usersArrayList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = layoutInflater.inflate(R.layout.account_item_row, viewGroup, false);
        return new AccountViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder accountViewHolder, int i) {
        final Users userTransact  = usersArrayList.get(i);
        accountViewHolder.phoneEmailText.setText(userTransact.getEmail());
        accountViewHolder.circularTextView.setText(userTransact.getFirstLetter().toUpperCase());
        accountViewHolder.textReceiver.setText(userTransact.getFirstname()+" "+userTransact.getLastname());
        accountViewHolder.circularTextView.setBackgroundColor(getRandomColor());
        accountViewHolder.openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent   = new Intent(context, DetailActivity.class);
                intent.putExtra("firstname", userTransact.getFirstname());
                intent.putExtra("lastname", userTransact.getLastname());
                intent.putExtra("date_created", userTransact.getDateCreated());
                intent.putExtra("cash_balance", userTransact.getCashBalance());
                intent.putExtra("date_registered", userTransact.getDateRegister());
                intent.putExtra("register_id", userTransact.getRegisterId());
                intent.putExtra("wallet_id", userTransact.getWalletId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder{
        TextView phoneEmailText, circularTextView, textReceiver;
        private Typeface myCustomTypeface, myCustomTypefaceBold, myCustomTypefaceBlack;
        private Button openBtn;

        AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            textReceiver        = itemView.findViewById(R.id.textReceiver);
            circularTextView    = itemView.findViewById(R.id.circularTextView);
            openBtn             = itemView.findViewById(R.id.openUserAccount);
            phoneEmailText      = itemView.findViewById(R.id.phoneTag);
            setTypeFaceTask(itemView);
        }

        private void setTypeFaceTask(View itemView){
            myCustomTypefaceBold    = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/hurme-geometric-bold.ttf");
            myCustomTypefaceBlack   = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/ProximaNova-Black.ttf");
            circularTextView.setTypeface(myCustomTypefaceBlack);
            textReceiver.setTypeface(myCustomTypefaceBold);
            phoneEmailText.setTypeface(myCustomTypefaceBold);
            openBtn.setTypeface(myCustomTypefaceBlack);
        }
    }
}
