package com.orojinmi.theophilus.ducafev2.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.orojinmi.theophilus.ducafev2.R;

/**
 * Created by Theophilus on 10/29/2018.
 */

public class PreferencesMgr {

    private Context context;
    private SharedPreferences sharedPreferences;

    public PreferencesMgr(Context context) {
        this.context    = context;
        getSharedPreference();
    }

    private void getSharedPreference(){
        sharedPreferences   = context.getSharedPreferences(context.getString(R.string.my_pref), Context.MODE_PRIVATE);
    }

    public void writePreference(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.my_pref),"INIT_OK");
        editor.apply();
    }

    public boolean checkPreference(){
        boolean status;
        status  = !sharedPreferences.getString(context.getString(R.string.my_pref), "null").equals("null");
        return status;
    }

    public void clearPreference(){
        sharedPreferences.edit().clear().apply();
    }
}
