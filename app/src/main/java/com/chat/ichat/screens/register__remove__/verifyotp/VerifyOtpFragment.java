//package com.stairway.spotlight.screens.register.verifyotp;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentTransaction;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.stairway.spotlight.api.ApiManager;
//import com.stairway.spotlight.models.UserSession;
//import com.stairway.spotlight.AccessTokenManager;
//import com.stairway.spotlight.R;
//import com.stairway.spotlight.core.BaseFragment;
//import com.stairway.spotlight.screens.register.initialize.InitializeFragment;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.OnTextChanged;
//
///**
// * Created by vidhun on 22/07/16.
// */
//public class VerifyOtpFragment extends BaseFragment implements VerifyOtpContract.View {
//
//    private static String KEY_MOBILE = "MOBILE";
//    private static String KEY_COUNTRY_CODE = "COUNTRY_CODE";
//    @Bind(R.id.et_otp_otp)
//    EditText otpEditText;
//    @Bind(R.id.tv_otp_mobilenumber)
//    TextView mobileNumberTextView;
//    @Bind(R.id.btn_otp_continue)
//    Button continueButton;
//    VerifyOtpPresenter verifyOtpPresenter;
//    int OTP_LENGTH = 4;
//
//    public VerifyOtpFragment() {
//    }
//
//    public static VerifyOtpFragment getInstance(String mobile, String countryCode) {
//        Bundle bundle = new Bundle();
//        bundle.putString(KEY_MOBILE, mobile);
//        bundle.putString(KEY_COUNTRY_CODE, countryCode);
//
//        VerifyOtpFragment verifyOtpFragment = new VerifyOtpFragment();
//        verifyOtpFragment.setArguments(bundle);
//
//        return verifyOtpFragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        verifyOtpPresenter = new VerifyOtpPresenter(ApiManager.getUserApi(), AccessTokenManager.getInstance());
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_register_otp, container, false);
//        ButterKnife.bind(this, view);
//
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        String mobile = getArguments().getString(KEY_MOBILE);
//        String countryCode = getArguments().getString(KEY_COUNTRY_CODE);
//
//        mobileNumberTextView.setText(countryCode + "-" + mobile);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        continueButton.setAlpha(0.2f);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        verifyOtpPresenter.attachView(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    public void navigateToInitializeFragment(UserSession accessToken) {
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.register_FragmentContainer, InitializeFragment.getInstance());
//        fragmentTransaction.commit();
//    }
//
//    @Override
//    public void invalidOtpError() {
//        Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_LONG).show();
//        otpEditText.setText("");
//    }
//
//    @OnTextChanged(R.id.et_otp_otp)
//    public void onMobileTextChanged() {
//        if (otpEditText.getText().toString().length() == OTP_LENGTH) {
//            continueButton.setAlpha(1);
//        } else {
//            continueButton.setAlpha(.2f);
//        }
//    }
//
//    @OnClick(R.id.btn_otp_continue)
//    public void onContinueClicked() {
//        if (otpEditText.getText().toString().length() == OTP_LENGTH) {
//            verifyOtpPresenter.registerUser(
//                    getArguments().getString(KEY_COUNTRY_CODE),
//                    getArguments().getString(KEY_MOBILE),
//                    otpEditText.getText().toString());
//        }
//    }
//}