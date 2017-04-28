//package com.stairway.spotlight.screens.register;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//
//import com.stairway.spotlight.R;
//import com.stairway.spotlight.core.Logger;
//import com.stairway.spotlight.screens.register.signup.SignUpFragment;
//
//import butterknife.ButterKnife;
//
//public class RegisterActivity extends AppCompatActivity{
//
//    public static Intent callingIntent(Context context) {
//        Intent intent = new Intent(context, RegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        return intent;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Logger.d(this);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//        ButterKnife.bind(this);
//
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.register_FragmentContainer, SignUpFragment.getInstance());
//        fragmentTransaction.commit();
//
//        // Permissions
//        int MyVersion = Build.VERSION.SDK_INT;
//        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            if (!checkIfAlreadyhavePermission()) {
//                requestForSpecificPermission();
//            }
//        }
//    }
//
//    private boolean checkIfAlreadyhavePermission() {
//        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
//        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
//        int result3 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        int result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permission = PackageManager.PERMISSION_GRANTED;
//
//        return result1 == permission && result2 == permission && result3 == permission && result4 == permission;
//    }
//
//    private void requestForSpecificPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 101:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //granted
//                } else {
//                    //not granted
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//}