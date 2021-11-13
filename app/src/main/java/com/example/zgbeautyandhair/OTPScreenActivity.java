package com.example.zgbeautyandhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.zgbeautyandhair.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OTPScreenActivity extends AppCompatActivity {
    public String Number_entered_by_user,code_by_system;
    public CardView verify;
    public TextView resend;
    public PinView otp;

    TextView your_phone_number;

    private FirebaseAuth firebaseAuth;

    ConstraintLayout otp_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_otpscreen);


        Intent intent = getIntent();
        Number_entered_by_user = intent.getStringExtra("number");

        verify = findViewById(R.id.verifybutton);
        resend = findViewById(R.id.resend);
        your_phone_number = findViewById(R.id.your_phone_number);
        otp_activity = findViewById(R.id.otp_activity);

        your_phone_number.setText(Number_entered_by_user);

        otp=findViewById(R.id.pinview);
        send_code_to_user(Number_entered_by_user);

        firebaseAuth = FirebaseAuth.getInstance();

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend_otp(Number_entered_by_user);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_code();
            }
        });

    }

    private void resend_otp(String number_entered_by_user) {
        send_code_to_user(number_entered_by_user);
    }

    private void check_code() {
        String user_entered_otp =otp.getText().toString();
        if (user_entered_otp.isEmpty() || user_entered_otp.length() < 6){
            Snackbar.make(otp_activity, "קוד שגוי", Snackbar.LENGTH_SHORT).show();
            return;
        }
        finish_everything(user_entered_otp);
    }

    private void send_code_to_user(String number_entered_by_user) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+972" + number_entered_by_user,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );
    }
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            code_by_system=s;

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code !=null){
                finish_everything(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPScreenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void finish_everything(String code) {
        otp.setText(code);
        PhoneAuthCredential credential =PhoneAuthProvider.getCredential(code_by_system,code);
        sign_in(credential);
    }

    private void sign_in(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(OTPScreenActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast mToast = new Toast(OTPScreenActivity.this);
                    mToast.setDuration(Toast.LENGTH_LONG);
                    View toastView = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                    TextView textView = toastView.findViewById(R.id.txt_message);
                    mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                    textView.setText("ברוכה הבאה");
                    mToast.setView(toastView);
                    mToast.show();

                    if (task.isComplete())
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            FirebaseInstanceId.getInstance()
                                    .getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<InstanceIdResult> task) {
                                            if (task.isSuccessful()) {
                                                Common.updateToken(OTPScreenActivity.this.getBaseContext(), task.getResult().getToken());
                                                Log.d("Z&G Tokens", task.getResult().getToken());
                                                Intent intent = new Intent(OTPScreenActivity.this, HomeActivity.class);
                                                intent.putExtra(Common.IS_LOGIN, true);
                                                OTPScreenActivity.this.startActivity(intent);
                                                OTPScreenActivity.this.finish();
                                            }
                                        }
                                    });
                        }
                    }
                }
                else{
                    Snackbar.make(otp_activity, "קוד שגוי", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }
}