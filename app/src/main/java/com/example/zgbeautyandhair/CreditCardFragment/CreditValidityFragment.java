package com.example.zgbeautyandhair.CreditCardFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.zgbeautyandhair.CardFrontFragment;
import com.example.zgbeautyandhair.PaymentActivity;
import com.example.zgbeautyandhair.R;
import com.example.zgbeautyandhair.Utils.CreditCardEditText;
import com.example.zgbeautyandhair.Utils.CreditCardExpiryTextWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditValidityFragment extends Fragment {

    @BindView(R.id.et_validity)
    CreditCardEditText et_validity;
    TextView tv_validity;

    PaymentActivity activity;
    CardFrontFragment cardFrontFragment;

    public CreditValidityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_credit_validity, container, false);
        ButterKnife.bind(this, view);

        activity = (PaymentActivity) getActivity();
        cardFrontFragment = activity.cardFrontFragment;


        tv_validity = cardFrontFragment.getValidity();
        et_validity.addTextChangedListener(new CreditCardExpiryTextWatcher(et_validity, tv_validity));

        et_validity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (activity != null) {
                        activity.nextClick();
                        return true;
                    }

                }
                return false;
            }
        });

        et_validity.setOnBackButtonListener(new CreditCardEditText.BackButtonListener() {
            @Override
            public void onBackClick() {
                if(activity!=null)
                    activity.onBackPressed();
            }
        });


        return view;
    }

    public String getValidity()
    {
        if(et_validity!=null)
            return et_validity.getText().toString().trim();

        return null;
    }
}