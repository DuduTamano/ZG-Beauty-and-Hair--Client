package com.example.zgbeautyandhair.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.zgbeautyandhair.Model.Barber;
import com.example.zgbeautyandhair.Model.BookingInformation;
import com.example.zgbeautyandhair.Model.MyToken;
import com.example.zgbeautyandhair.Model.Order;
import com.example.zgbeautyandhair.Model.Salon;
import com.example.zgbeautyandhair.Model.ShoppingItem;
import com.example.zgbeautyandhair.Model.User;
import com.example.zgbeautyandhair.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;

public class Common {
    public static final String IS_LOGIN = "IsLogin";
    public static final String IS_NEWS = "IS_NEWS";
    public static final String NEWS_TOPIC = "news";
    public static final String IS_SEND_IMAGE = "IS_SEND_IMAGE";
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final double DEFAULT_PRICE = 0;
    public static User currentUser;
    public static Salon currentSalon;
    public static final int TIME_SLOT_TOTAL = 9;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String LOGGED_KEY = "UserLogged";
    public static final String RATING_INFORMATION_KEY = "RATING_INFORMATION";
    public static final String RATING_STATE_KEY = "RATING_STATE";
    public static final String RATING_SALON_ID = "RATING_SALON_ID";
    public static final String RATING_SALON_NAME = "RATING_SALON_NAME";
    public static final String RATING_BARBER_ID = "RATING_BARBER_ID";
    public static int step = 0;
    public static String city = "";
    public static int currentTimeSlot = -1;
    public static Calendar bookingDate = Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static BookingInformation currentBooking;
    public static String currentBookingId = "";
    public static Barber currentBarber;
    public static Order currentOrder;
    public static int size = -1;
    public static ShoppingItem currentShopping;

    private static boolean enabledButton = false;

    private Common() {
        //Do not instantiate
    }

    public static Barber getCurrentBarber() {
        return currentBarber;
    }

    public static void setCurrentBarber(Barber currentBarber) {
        Common.currentBarber = currentBarber;
    }

    public static void EnableNextButton(String[] time, Context context) {
        String[] startEnd = time[0].split("-");
        String[] startTime = startEnd[0].split(":");
        int startHour = Integer.parseInt(startTime[0]);
        int startMin = Integer.parseInt(startTime[1]);
        String[] endTime = startEnd[1].split(":");


        int hourOfDay = bookingDate.get(Calendar.HOUR_OF_DAY);
        int minOfDay = bookingDate.get(Calendar.MINUTE);
        String today = simpleDateFormat.format(bookingDate.getTime());
        String booking = simpleDateFormat.format(bookingDate.getTime());

        if(!today.equals(booking))
            enabledButton = true;
        else {
            if(hourOfDay < startHour)
                enabledButton = true;
            else if(hourOfDay == startHour)
                enabledButton = minOfDay < startMin;
        }
    }

    public static boolean isEnabledButton() {
        return enabledButton;
    }

    public static SimpleDateFormat getSimpleFormatDate() {
        return simpleDateFormat;
    }

    public static Calendar getBookingDate() {
        return bookingDate;
    }

    public static String[] convertTimeSlotToString(int slot) {
        String[] timeSlots = new String[2];
        switch (slot)
        {
            case 0:
                timeSlots[0] = "10:00-11:00";
                timeSlots[1] = "10:00-11:00";
                break;
            case 1:
                timeSlots[0] = "11:00-12:00";
                timeSlots[1] = "11:00-12:00";
                break;
            case 2:
                timeSlots[0] = "12:00-13:00";
                timeSlots[1] = "12:00-13:00";
                break;
            case 3:
                timeSlots[0] = "13:00-14:00";
                timeSlots[1] = "13:00-14:00";
                break;
            case 4:
                timeSlots[0] = "14:00-15:00";
                timeSlots[1] = "14:00-15:00";
                break;
            case 5:
                timeSlots[0] = "15:00-16:00";
                timeSlots[1] = "15:00-16:00";
                break;
            case 6:
                timeSlots[0] = "16:00-17:00";
                timeSlots[1] = "16:00-17:00";
                break;
            case 7:
                timeSlots[0] = "17:00-18:00";
                timeSlots[1] = "17:00-18:00";
                break;
            case 8:
                timeSlots[0] = "18:00-19:00";
                timeSlots[1] = "18:00-19:00";
                break;

            default:
                timeSlots[0] = "סגור";
                timeSlots[1] =  "סגור";
        }
        return timeSlots;
    }

    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatItemShoppingName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0,10)).append("...").toString():name;
    }

    public static void showNotification(Context context, int noti_id, String title, String content, Intent intent) {

        //copy code from staff app
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL_ID = "Z&G Client App";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);

            //configure the notification channel
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.zglogo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.noti_logo));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        Notification mNotification = builder.build();

        notificationManager.notify(noti_id, mNotification);
    }

    public static void showRatingDialog(final Context context, String stateName, String salonID,
                                        final String salonName, String barberID)
    {
        final DocumentReference barberNeedRateRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document("ראשון לציון")
                .collection("Branch")
                .document(salonID)
                .collection("Barbers")
                .document(barberID);

        barberNeedRateRef.get()
                .addOnFailureListener(e ->
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
                    if(task.isSuccessful())
                    {
                        final Barber barberRate = task.getResult().toObject(Barber.class);
                        barberRate.setBarberId(task.getResult().getId());

                        //create view for dialog
                        View view = LayoutInflater.from(context)
                                .inflate(R.layout.layout_rating_dialog, null);

                        //Widget
                        TextView txt_salon_name = view.findViewById(R.id.txt_salon_name);
                        TextView txt_barber_name = view.findViewById(R.id.txt_barber_name);
                        final RatingBar ratingBar = view.findViewById(R.id.rating);

                        //set info
                        txt_barber_name.setText(barberRate.getName());
                        txt_salon_name.setText(salonName);

                        //create dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setView(view)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //if select ok, we will update
                                        //rating information to firestore
                                        Double original_rating = barberRate.getRating();
                                        Long ratingTimes = barberRate.getRatingTimes();

                                        float userRating = ratingBar.getRating();

                                        Double finalRating = (original_rating+userRating);

                                        //Update barber
                                        Map<String, Object> data_update = new HashMap<>();
                                        data_update.put("rating", finalRating);
                                        data_update.put("ratingTimes", ++ratingTimes);

                                        barberNeedRateRef.update(data_update)
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show())
                                                .addOnCompleteListener(task1 -> {
                                                    if(task1.isSuccessful())
                                                    {
                                                        Toast.makeText(context, "תודה רבה על חוות הדעת <3", Toast.LENGTH_SHORT).show();
                                                        //Remove key
                                                        Paper.init(context);
                                                        Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("SKIP", (dialogInterface, i) -> {
                                    //if select skip, we just dismiss dialog
                                    dialogInterface.dismiss();
                                })
                                .setNeutralButton("NEVER", (dialogInterface, i) -> {
                                    //if select never,
                                    //that mean no rating, we will delete key
                                    Paper.init(context);
                                    Paper.book().delete(Common.RATING_INFORMATION_KEY);

                                });

                        final AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
    }

    public static String createOrderNumber() {
        return new StringBuilder()
                //.append(Math.abs(new Short().intV))
                .append(Math.abs(new Random().nextInt()))
                .toString();
    }

    public static String convertStatusToText(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "הזמנה במספרה";
            case 1:
                return "הזמנה נשלחה";
            case 2:
                return "הזמנה הגיעה ליעד";
            case -1:
                return "הזמנה בוטלה";
            default:
                return "Error";
        }
    }

    public static String convertBookingStatusToText(int bookingStatus) {
        switch (bookingStatus) {
            case 0:
                return "תור ממתין לאישור";
            case 1:
                return "תור מאושר";
            case -1:
                return "תור מבוטל";
            default:
                return "Unk";
        }
    }

    public static void showNotificationBigStyle(Context context, int noti_id, String title, String content, Bitmap bitmap, Intent intent) {

        //copy code from staff app
        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(
                    context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL_ID = "Z&G Client App";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);

            //configure the notification channel
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 100, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.zglogo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.zglogo))
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        Notification mNotification = builder.build();

        notificationManager.notify(noti_id, mNotification);
    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }

    public enum TOKEN_TYPE {
        CLIENT,
        BARBER,
        MANAGER
    }

    public static void updateToken(Context context, final String token) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            MyToken myToken = new MyToken();
            myToken.setToken(token);
            //because token come from client app
            myToken.setTokenType(TOKEN_TYPE.CLIENT);
            myToken.setUserPhone(user.getPhoneNumber());

            FirebaseFirestore.getInstance()
                    .collection("Z&G Tokens")
                    .document(user.getPhoneNumber())
                    .set(myToken)
                    .addOnCompleteListener(task -> {

                    });
        }
        else
        {
            Paper.init(context);
            String localUser = Paper.book().read(Common.LOGGED_KEY);
            if (localUser != null)
            {
                if (!TextUtils.isEmpty(localUser))
                {
                    MyToken myToken = new MyToken();
                    myToken.setToken(token);
                    //because token come from client app
                    myToken.setTokenType(TOKEN_TYPE.CLIENT);
                    myToken.setUserPhone(localUser);

                    FirebaseFirestore.getInstance()
                            .collection("Z&G Tokens")
                            .document(localUser)
                            .set(myToken)
                            .addOnCompleteListener(task -> {

                            });
                }

            }
        }
    }
}
