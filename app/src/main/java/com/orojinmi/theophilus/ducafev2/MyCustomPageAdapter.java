package com.orojinmi.theophilus.ducafev2;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Theophilus on 10/28/2018.
 */

public class MyCustomPageAdapter extends PagerAdapter {

    private int[] layouts;
    private LayoutInflater layoutInflater;
    private Context context;


    public MyCustomPageAdapter(int[] layouts, Context context) {
        this.layouts    = layouts;
        this.context    = context;
        layoutInflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override

    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view   = layoutInflater.inflate(layouts[position], container, false);
        init(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view   = (View) object;
        container.removeView(view);
    }

    private void init(View view){
        Typeface typeface   = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/hurme-geometric-bold.ttf");
        TextView title      = view.findViewById(R.id.moremiTitle);
        TextView subtitle   = view.findViewById(R.id.todaysWallet);
        title.setTypeface(typeface);
        subtitle.setTypeface(typeface);
    }
}
