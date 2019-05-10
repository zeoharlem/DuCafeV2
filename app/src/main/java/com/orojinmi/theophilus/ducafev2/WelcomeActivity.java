package com.orojinmi.theophilus.ducafev2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.Manager.PreferencesMgr;


public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager myViewPager;
    private int[] layouts   = {
            R.layout.first_slide,
            R.layout.second_slide,
            R.layout.third_slide
    };
    private ImageView[] dots;
    private LinearLayout dottedLayouts;
    private MyCustomPageAdapter myCustomPageAdapter;

    private Button btnNext, btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new PreferencesMgr(this).checkPreference()){
            //loadHomePageRow();
        }

        if(Build.VERSION.SDK_INT >= 19){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_welcome);
        myViewPager         = findViewById(R.id.viewPagerRow);
        myCustomPageAdapter = new MyCustomPageAdapter(layouts, this);
        myViewPager.setAdapter(myCustomPageAdapter);

        dottedLayouts       = findViewById(R.id.dotsLayout);
        btnNext             = findViewById(R.id.btnNext);
        btnSkip             = findViewById(R.id.btnSkip);

        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

        createDots(0);

        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                createDots(position);
                if(position == layouts.length - 1){
                    btnNext.setText("Login");
                    btnSkip.setVisibility(View.INVISIBLE);
                }
                else{
                    btnNext.setText("Next");
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //init();
            }
        });
    }

    private void init(){
        Typeface typeface   = Typeface.createFromAsset(getAssets(), "fonts/hurme-geometric-bold.ttf");
        TextView title      = findViewById(R.id.moremiTitle);
        TextView subtitle   = findViewById(R.id.todaysWallet);
        title.setTypeface(typeface);
        subtitle.setTypeface(typeface);
    }

    private void createDots(int currentPosition){
        if(dottedLayouts != null){
            dottedLayouts.removeAllViews();
        }
        dots    = new ImageView[layouts.length];
        for (int i = 0; i < layouts.length; i++){
            dots[i] = new ImageView(this);
            if(i == currentPosition){
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_brightness_1_black_12dp));
            }
            else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_brightness_1_black_12dp));
            }
            LinearLayout.LayoutParams params    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            dottedLayouts.addView(dots[i], params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNext:
                loadNextSlide();
                break;
            case R.id.btnSkip:
                loadHomePageRow();
                new PreferencesMgr(this).writePreference();
                break;
        }
    }

    private void loadHomePageRow(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void loadNextSlide(){
        int nextSlide   = myViewPager.getCurrentItem() + 1;
        if(nextSlide < layouts.length){
            myViewPager.setCurrentItem(nextSlide);
        }
        else{
            loadHomePageRow();
            new PreferencesMgr(this).writePreference();
        }
    }
}
