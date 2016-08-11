package com.stairway.spotlight.screens.register.signup;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.stairway.data.manager.Logger;
import com.stairway.spotlight.R;
import com.stairway.spotlight.core.BaseFragment;
import com.stairway.spotlight.core.di.component.ComponentContainer;
import com.stairway.spotlight.screens.register.verifyotp.VerifyOtpFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by vidhun on 22/07/16.
 */
public class SignUpFragment extends BaseFragment{

    @Bind(R.id.dropdown_register_country_code)
    Spinner countryCodeSpinner;

    @Bind(R.id.et_signup_mobile)
    EditText mobileEditText;

    @Bind(R.id.btn_register_send_confirmation)
    Button confirmationButton;

    public static SignUpFragment getInstance() {
        SignUpFragment signUpFragment = new SignUpFragment();
        return signUpFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        confirmationButton.setAlpha(0.2f);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mobileEditText.getText().length() >= 10)
            confirmationButton.setAlpha(1);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @OnClick(R.id.btn_register_send_confirmation)
    public void onConfirmationClicked() {
        int countryCodeStart = countryCodeSpinner.getSelectedItem().toString().lastIndexOf("+");
        int countrCodeEnd = countryCodeSpinner.getSelectedItem().toString().length();

        String countryCode = countryCodeSpinner.getSelectedItem().toString().substring(countryCodeStart, countrCodeEnd);
        String mobileNumber = mobileEditText.getText().toString();

        Logger.d("Country Code  = "+countryCode);
        Logger.d("Mobile Number = "+mobileNumber);

        if(mobileEditText.getText().toString().length()>=10) {
            FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.register_FragmentContainer, VerifyOtpFragment.getInstance(mobileNumber, countryCode));
//            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.addToBackStack("SignUpFragment");
            fragmentTransaction.commit();
        }
    }

    @OnTextChanged(R.id.et_signup_mobile)
    public void onMobileTextChanged() {
        if(mobileEditText.getText().toString().length()>=10) {
            confirmationButton.setAlpha(1);
        } else {
            confirmationButton.setAlpha(.2f);
        }

    }

    @Override
    protected void injectComponent(ComponentContainer componentContainer) {

    }
}