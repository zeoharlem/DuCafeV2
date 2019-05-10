package com.orojinmi.theophilus.ducafev2.DialogBox;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.L;

import org.json.JSONObject;

/**
 * Created by Theophilus on 12/20/2017.
 */

public class MyDialogBox extends DialogFragment implements View.OnClickListener{

    Button yes, no;
    Typeface myCustomBlackTypeface, customTypeface;
    EditText editText;
    TextView amountToPay, payer, pemail;
    Bundle mArgs;

    OnDialogFragmentBoxClicked onDialogFragmentBoxClicked;

    private static final int IMG_HEIGHT = 500;
    private static final int IMG_WIDTH  = 500;
    JSONObject printJson = new JSONObject();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view               = inflater.inflate(R.layout.my_dialog, null);
        customTypeface          = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
        myCustomBlackTypeface   = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-ExtraBold.ttf");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        init(view);
        return view;
    }

    private void init(View view){
        mArgs                   = getArguments();
        yes                     = view.findViewById(R.id.submitPswd);
        no                      = view.findViewById(R.id.cancelPswd);
        editText                = view.findViewById(R.id.passwordDialogBox);

        amountToPay             = view.findViewById(R.id.amountToPay);
        payer                   = view.findViewById(R.id.payer);
        pemail                  = view.findViewById(R.id.p_email);

        payer.setText(mArgs.getString("payer"));
        pemail.setText(mArgs.getString("pemail"));
        amountToPay.setText(mArgs.getString("toPay"));

        yes.setTypeface(myCustomBlackTypeface);
        no.setTypeface(myCustomBlackTypeface);

        payer.setTypeface(customTypeface);
        amountToPay.setTypeface(myCustomBlackTypeface);
        pemail.setTypeface(customTypeface);

        no.setOnClickListener(this);
        yes.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.submitPswd){
            if(!editText.getText().toString().equals("") || !editText.getText().toString().isEmpty()) {
                onDialogFragmentBoxClicked.sendDialogBoxMsg(editText.getText().toString().trim());
            }
        }
        else{
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onDialogFragmentBoxClicked  = (OnDialogFragmentBoxClicked) getTargetFragment();
        }
        catch (ClassCastException ex){
            L.l(context, "ClassCastException: "+ex.getMessage());
        }
    }

    public interface OnDialogFragmentBoxClicked{
        void sendDialogBoxMsg(String textString);
    }
}
