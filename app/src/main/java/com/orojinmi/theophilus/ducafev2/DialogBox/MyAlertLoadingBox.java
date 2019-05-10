package com.orojinmi.theophilus.ducafev2.DialogBox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.orojinmi.theophilus.ducafev2.R;

/**
 * Created by Theophilus on 1/1/2018.
 */

public class MyAlertLoadingBox extends DialogFragment implements View.OnClickListener {
    Button yes, no;
    Typeface myCustomBlackTypeface, customTypeface;
    View view;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        view   = inflater.inflate(R.layout.loading, null);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        return view;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater     = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading, null));
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((AlertDialog)dialog).getButton(which).setVisibility(View.INVISIBLE);
            }
        });
        Dialog dialog   = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {

    }
}
