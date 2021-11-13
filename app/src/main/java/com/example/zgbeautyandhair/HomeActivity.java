package com.example.zgbeautyandhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Fragments.HomeFragment;
import com.example.zgbeautyandhair.Fragments.ShoppingFragment;
import com.example.zgbeautyandhair.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

import static com.example.zgbeautyandhair.Common.Common.bookingDate;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;

    AlertDialog dialog;

    @Override
    protected void onResume() {
        super.onResume();

        //Check rating dialog
        checkRatingDialog();
    }

    private void checkRatingDialog() {
        Paper.init(this);
        String dataSerialized = Paper.book().read(Common.RATING_INFORMATION_KEY, "");
        if (!TextUtils.isEmpty(dataSerialized)) {
            Map<String, String> dataReceived = new Gson()
                    .fromJson(dataSerialized, new TypeToken<Map<String, String>>() {
                    }.getType());

            if (dataReceived != null) {
                Common.showRatingDialog(HomeActivity.this,
                        dataReceived.get(Common.RATING_STATE_KEY),
                        dataReceived.get(Common.RATING_SALON_ID),
                        dataReceived.get(Common.RATING_SALON_NAME),
                        dataReceived.get(Common.RATING_BARBER_ID));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(HomeActivity.this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Calendar bookingDateWithoutHouse = Calendar.getInstance();
        bookingDateWithoutHouse.setTimeInMillis(bookingDate.getTimeInMillis());

        //Create Timestamp object and apply to BookingInformation
        Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());


        //init
        userRef = FirebaseFirestore.getInstance().collection("Users");
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();


        //Check intent, if is login = true, enable full access
        //if is login = false, just let user around shopping to view
        if (getIntent() != null) {
            // Check if user is exists
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                dialog.show();

                //Save user phone by paper
                Paper.init(HomeActivity.this);
                Paper.book().write(Common.LOGGED_KEY, user.getPhoneNumber());

                DocumentReference currentUser = userRef.document(user.getPhoneNumber());
                currentUser.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userSnapShot = task.getResult();
                                    if (!userSnapShot.exists()) {
                                        HomeActivity.this.showUpdateDialog(user.getPhoneNumber(), timestamp);
                                    } else {
                                        //if user already available in our system
                                        Common.currentUser = userSnapShot.toObject(User.class);
                                        bottomNavigationView.setSelectedItemId(R.id.action_home);
                                    }
                                    if (dialog.isShowing())
                                        dialog.dismiss();

                                }
                            }
                        });
            }
        }

        //view
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_home)
                    fragment = new HomeFragment();
                else if (menuItem.getItemId() == R.id.action_shopping)
                    fragment = new ShoppingFragment();


                switch (menuItem.getItemId()){
                    case R.id.card_view_booking:
                        startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.card_view_history:
                        startActivity(new Intent(getApplicationContext(), BookingHistoryActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return loadFragment(fragment);
            }
        });


    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();

            return true;

        }
        return false;
    }

    private void showUpdateDialog(String PhoneNumber, Timestamp timestamp) {
        //Init dialog
        bottomSheetDialog = new BottomSheetDialog(this, R.style.DialogTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information, null);

        Button btnUpdate = sheetView.findViewById(R.id.btnUpdate);
        TextInputEditText edtFirstName = sheetView.findViewById(R.id.edtFirstName);
        TextInputEditText edtLastName = sheetView.findViewById(R.id.edtLastName);
        TextInputEditText edtPhoneNumber = sheetView.findViewById(R.id.edtPhoneNumber);
        TextInputEditText edtAddress = sheetView.findViewById(R.id.edtAddress);


        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null &&
        !TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
            edtPhoneNumber.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        btnUpdate.setOnClickListener(view -> {

            if (!dialog.isShowing())
                dialog.show();

            User user = new User(edtFirstName.getText().toString(),
                    edtLastName.getText().toString(),
                    edtAddress.getText().toString(),
                    PhoneNumber);

            user.setTimestamp(timestamp);

            userRef.document(PhoneNumber)
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        bottomSheetDialog.dismiss();
                        if (dialog.isShowing())
                            dialog.dismiss();

                        Common.currentUser = user;
                        bottomNavigationView.setSelectedItemId(R.id.action_home);

                        Toast.makeText(HomeActivity.this, "תודה רבה פרטיך נקלטו", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                bottomSheetDialog.dismiss();
                if (dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}