package com.stairway.spotlight.screens.sign_up;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.stairway.spotlight.R;
import com.stairway.spotlight.core.Logger;
import com.stairway.spotlight.screens.register.signup.SignUpFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity{
    @Bind(R.id.tb_sign_up)
    Toolbar toolbar;

    @Bind(R.id.sign_up_name)
    TextView nameView;

    @Bind(R.id.sign_up_country_code)
    TextView countryCodeView;

    @Bind(R.id.sign_up_mobile_number)
    TextView mobileNumberView;

    @Bind(R.id.sign_up_password)
    TextView password;

    public static Intent callingIntent(Context context) {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Permissions
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int permission = PackageManager.PERMISSION_GRANTED;

        return result1 == permission && result2 == permission;
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
