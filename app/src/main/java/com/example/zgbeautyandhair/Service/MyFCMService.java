package com.example.zgbeautyandhair.Service;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.zgbeautyandhair.Common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class MyFCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Common.updateToken(this, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //     dataSend.put("update_done", "true");
        if(remoteMessage.getData() != null)
        {
            if (remoteMessage.getData().get(Common.IS_SEND_IMAGE) != null &&
                    remoteMessage.getData().get(Common.IS_SEND_IMAGE).equals("true"))
            {
                Glide.with(this)
                        .asBitmap()
                        .load(remoteMessage.getData().get(Common.IMAGE_URL))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Common.showNotificationBigStyle(MyFCMService.this,
                                        new Random().nextInt(),
                                        remoteMessage.getData().get(Common.TITLE_KEY),
                                        remoteMessage.getData().get(Common.CONTENT_KEY),
                                        resource,
                                        null);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
            else
            {
                if(remoteMessage.getData().get("update_done") != null){
                    updateLastBooking();
                    Map<String,String> dataReceived = remoteMessage.getData();
                    Paper.init(this);
                    Paper.book().write(Common.RATING_INFORMATION_KEY, new Gson().toJson(dataReceived));

                }

                if(remoteMessage.getData().get(Common.TITLE_KEY) != null &&
                        remoteMessage.getData().get(Common.CONTENT_KEY) != null){

                    Common.showNotification(this,
                            new Random().nextInt(),
                            remoteMessage.getData().get(Common.TITLE_KEY),
                            remoteMessage.getData().get(Common.CONTENT_KEY),
                            null);
                }
            }
        }
    }

    private void updateLastBooking() {

        //Here We need current User Login
        //Because App maybe run on background so we need get from paper

        CollectionReference userBooking;

        //If app running
        if(Common.currentUser != null)
        {
            userBooking = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking");
        }else{
            //If app is not running.
            Paper.init(this);
            String user = Paper.book().read(Common.LOGGED_KEY);


            userBooking = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(user)
                    .collection("Booking");
        }

        //Check if Exist by get Current Date
        //Why we are working only for current date as we only load appoinemtn for current date and next 3 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.MINUTE, 0);

        Timestamp timestamp = new Timestamp(calendar.getTime());
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", timestamp)//Get ONly booking Information with time today or next day
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyFCMService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().size() > 0)
                    {
                        //Update
                        DocumentReference userBookingCurrentDocument = null;
                        for(DocumentSnapshot documentSnapshot : task.getResult())
                        {
                            userBookingCurrentDocument = userBooking.document(documentSnapshot.getId());
                        }
                        if(userBookingCurrentDocument!=null){
                            Map<String, Object> dataUpdate = new HashMap<>();
                            dataUpdate.put("done", true);
                            userBookingCurrentDocument.update(dataUpdate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MyFCMService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            }
        });


    }
}
