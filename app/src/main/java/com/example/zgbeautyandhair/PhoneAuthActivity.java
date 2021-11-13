package com.example.zgbeautyandhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;


public class PhoneAuthActivity extends AppCompatActivity {
    public CardView continuebutton;
    public EditText phone_input;
    CountryCodePicker ccp;
    String number;
    ConstraintLayout phone_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_phone_auth );

        phone_auth = findViewById(R.id.phone_auth);
        continuebutton=findViewById(R.id.next);
        phone_input=findViewById(R.id.phone);
        ccp=findViewById(R.id.ccp);

        ccp.registerCarrierNumberEditText(phone_input);

        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ccp.isValidFullNumber()) {

                    Intent intent =new Intent(PhoneAuthActivity.this,OTPScreenActivity.class);
                    number = phone_input.getText().toString();
                    intent.putExtra("number",number);
                    startActivity(intent);

                } else {
                    Snackbar.make(phone_auth, "אנא הזיני מספר נייד על מנת שישלח קוד אימות", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void BackToMain(View view) {
        startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class));
    }
}