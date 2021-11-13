package com.example.zgbeautyandhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.zgbeautyandhair.Model.Salon;
import com.example.zgbeautyandhair.ViewModel.ViewContact;

import java.util.List;

import butterknife.BindView;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.galit_instagram)
    ImageView galit_instagram;

    @BindView(R.id.galit_facebook)
    ImageView galit_facebook;

    @BindView(R.id.galit_whatsapp)
    ImageView galit_whatsapp;

    @BindView(R.id.zehava_instagram)
    ImageView zehava_instagram;

    @BindView(R.id.zehava_facebook)
    ImageView zehava_facebook;

    @BindView(R.id.zehava_whatsapp)
    ImageView zehava_whatsapp;

    String GalitPhoneNumber = "+972524557708";

    String ZehavaPhoneNumber = "+972549843747";

    private ViewContact viewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_contact );

        viewContact = new ViewModelProvider(this).get(ViewContact.class);

        viewContact.getMutableLiveDataContact().observe(this, new Observer<List<Salon>>() {
            @Override
            public void onChanged(List<Salon> salonList) {
            }
        } );

        init();

        OpenUrlGalit();

        OpenUrlZehava();
    }

    private void init() {
        galit_instagram = findViewById(R.id.galit_instagram);
        galit_facebook = findViewById(R.id.galit_facebook);
        galit_whatsapp = findViewById(R.id.galit_whatsapp);

        zehava_instagram = findViewById(R.id.zehava_instagram);
        zehava_facebook = findViewById(R.id.zehava_facebook);
        zehava_whatsapp = findViewById(R.id.zehava_whatsapp);
    }

    private void OpenUrlZehava() {
        zehava_instagram.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoInstagram("https://www.instagram.com/zehavat10/");
            }
        } );

        zehava_facebook.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFacebook("https://www.facebook.com/zehava370");
            }
        } );

        zehava_whatsapp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWhattsapp("https://api.whatsapp.com/send?phone="+ZehavaPhoneNumber);
            }
        } );
    }

    private void OpenUrlGalit() {
        galit_instagram.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoInstagram("https://www.instagram.com/galit_tadasa/");
            }
        } );

        galit_facebook.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFacebook("https://www.facebook.com/profile.php?id=100000811071800");
            }
        } );

        galit_whatsapp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWhattsapp("https://api.whatsapp.com/send?phone="+GalitPhoneNumber);
            }
        } );
    }

    private void gotoWhattsapp(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri) );
    }

    private void goToFacebook(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri) );
    }

    private void gotoInstagram(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri) );
    }

    public void BackToHome(View view) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }
}