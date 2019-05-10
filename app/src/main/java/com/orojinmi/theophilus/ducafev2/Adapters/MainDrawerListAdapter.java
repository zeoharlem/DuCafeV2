package com.orojinmi.theophilus.ducafev2.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.NavItems;

import java.util.ArrayList;

public class MainDrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItems> mNavItems;
    Typeface mTypeface, mTypefaceBlack, mTypefaceBold, mTypefaceRegular;

    public MainDrawerListAdapter(Context mContext, ArrayList<NavItems> mNavItems) {
        this.mContext = mContext;
        this.mNavItems  = mNavItems;
        mTypeface           = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Thin-webfont.ttf");
        mTypefaceRegular    = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Alt-Regular-webfont.ttf");
        mTypefaceBold       = Typeface.createFromAsset(mContext.getAssets(), "fonts/hurme-geometric-bold.ttf");
        mTypefaceBlack      = Typeface.createFromAsset(mContext.getAssets(), "fonts/ProximaNova-Black.ttf");
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            LayoutInflater layoutInflater   = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view                            = layoutInflater.inflate(R.layout.drawer_item, parent, false);
        }
        else{
            view    = convertView;
        }
        TextView titleView      = view.findViewById(R.id.title);
        TextView subtitleView   = view.findViewById(R.id.subTitle);

        ImageView iconView      = view.findViewById(R.id.icon);

        titleView.setText(mNavItems.get(position).getmTitle() );
        subtitleView.setText(mNavItems.get(position).getmSubtitle() );
        iconView.setImageResource(mNavItems.get(position).getmIcon());

        subtitleView.setTypeface(mTypefaceRegular);
        titleView.setTypeface(mTypefaceBold);

        return view;
    }
}
