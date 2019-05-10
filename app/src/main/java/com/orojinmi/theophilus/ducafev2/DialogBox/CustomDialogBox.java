package com.orojinmi.theophilus.ducafev2.DialogBox;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orojinmi.theophilus.ducafev2.R;
import com.orojinmi.theophilus.ducafev2.Utils.L;

/**
 * Created by Theophilus on 2/19/2018.
 */

public class CustomDialogBox extends DialogFragment implements View.OnClickListener {

    Button yes, no;
    Typeface myCustomBoldTypeface, myCustomTypeface;
    EditText editText;
    TextView amountToPay, payer, pemail;
    Bundle mArgs;

    OnDialogFragmentBoxClicked onDialogFragmentBoxClicked;

    private static final int IMG_HEIGHT = 500;
    private static final int IMG_WIDTH  = 500;
    private LinearLayout viewProgessLayout;
    private Button acceptButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.topup_layout_fragment, null);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        acceptButton            = view.findViewById(R.id.acceptBtn);
        viewProgessLayout       = view.findViewById(R.id.progressBarLay);
        myCustomTypeface        = Typeface.createFromAsset(view.getResources().getAssets(), "fonts/slatepro-bk.ttf");
        myCustomBoldTypeface    = Typeface.createFromAsset(view.getResources().getAssets(), "fonts/OpenSans-ExtraBold.ttf");
        setCancelable(false);
        init(view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try{
//            onDialogFragmentBoxClicked  = (OnDialogFragmentBoxClicked) getTargetFragment();
//        }
//        catch (ClassCastException ex){
//            L.l(context, "ClassCastException: "+ex.getMessage());
//        }
//    }

    public OnDialogFragmentBoxClicked getOnDialogFragmentBoxClicked() {
        return onDialogFragmentBoxClicked;
    }

    public void setOnDialogFragmentBoxClicked(OnDialogFragmentBoxClicked onDialogFragmentBoxClicked) {
        this.onDialogFragmentBoxClicked = onDialogFragmentBoxClicked;
    }

    public interface OnDialogFragmentBoxClicked{
        void sendDialogBoxMsg(String textString);
    }

    @Override
    public void onClick(final View view) {
        if(view.getId() == R.id.acceptBtn){
            //EditText amountRowTask  = (EditText) view.findViewById(R.id.amountTaskRow);
            if(!editText.getText().toString().equals("") && !editText.getText().toString().isEmpty()) {
                onDialogFragmentBoxClicked.sendDialogBoxMsg(editText.getText().toString().trim());
                acceptButton.setEnabled(false);
                acceptButton.setText("Please Wait");
            }
            else {
                L.l(getContext(), "Empty Amount Field");
            }
        }
        else{
            dismiss();
        }
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public LinearLayout getViewProgessLayout() {
        return viewProgessLayout;
    }

    private void init(View view){
        mArgs       = getArguments();
        yes         = view.findViewById(R.id.acceptBtn);
        no          = view.findViewById(R.id.cancelBtn);
        editText    = view.findViewById(R.id.passwordTyped);
        //amountToPay = view.findViewById(R.id.textAmount);
        pemail      = view.findViewById(R.id.p_email);
        payer       = view.findViewById(R.id.payer);

        no.setTypeface(myCustomBoldTypeface);
        yes.setTypeface(myCustomBoldTypeface);
        payer.setText(mArgs.getString("receiver"));
        pemail.setText(mArgs.getString("pemail"));
        payer.setTypeface(myCustomBoldTypeface);
        pemail.setTypeface(myCustomTypeface);

        no.setOnClickListener(this);
        yes.setOnClickListener(this);
    }

    public interface DialogBoxCallBack{
        void onProcessing();
        void onSuccess();
        void onFailure();
    }
}
