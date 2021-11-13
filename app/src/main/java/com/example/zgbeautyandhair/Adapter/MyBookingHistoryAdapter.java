package com.example.zgbeautyandhair.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.BookingHistoryActivity;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Fragments.HomeFragment;
import com.example.zgbeautyandhair.Model.BookingInformation;
import com.example.zgbeautyandhair.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.paperdb.Paper;

import static com.example.zgbeautyandhair.Common.Common.currentBarber;

public class MyBookingHistoryAdapter extends RecyclerView.Adapter<MyBookingHistoryAdapter.MyViewHolder> {

    Context context;
    List<BookingInformation> bookingInformationList;
    SimpleDateFormat simpleDateFormat;

    CardView history_layout;

    public MyBookingHistoryAdapter(Context context, List<BookingInformation> bookingInformationList) {
        this.context = context;
        this.bookingInformationList = bookingInformationList;
        this.history_layout = history_layout;
        this.simpleDateFormat = new SimpleDateFormat("HH:mm - dd-MM-yyyy");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_booking_history,viewGroup,false);

        history_layout = itemView.findViewById(R.id.history_layout);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        myViewHolder.txt_booking_time_text.setText(new StringBuilder().append(bookingInformationList.get(i).getBookingDate())
                .append(" בשעה: ").append(bookingInformationList.get(i).getBookingTime()));

        myViewHolder.txt_booking_date.setText(simpleDateFormat.format(new Date(bookingInformationList.get(i).getTimestamp().toDate().toString())));

        myViewHolder.txt_order_status.setText(new StringBuilder("").append(Common.convertBookingStatusToText(bookingInformationList.get(i).getBookingStatus())));


        myViewHolder.delete_change.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDeleteOrChange(bookingInformationList.get(i), myViewHolder, i);
            }
        } );
    }

    private void showDialogDeleteOrChange(BookingInformation bookingInformation, MyViewHolder myViewHolder, int i) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_delete_booking, null);
        Dialog builder = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        builder.setContentView(layout_dialog);

        TextView txt_salon_name = (TextView) layout_dialog.findViewById(R.id.txt_salon_name);
        TextView txt_customer_name = (TextView) layout_dialog.findViewById(R.id.txt_customer_name);
        TextView txt_customer_phone = (TextView) layout_dialog.findViewById(R.id.txt_customer_phone);
        TextView txt_salon_address = (TextView) layout_dialog.findViewById(R.id.txt_salon_address);
        TextView txt_time = (TextView) layout_dialog.findViewById(R.id.txt_time);
        Button btn_change_booking = (Button) layout_dialog.findViewById(R.id.btn_change_booking);
        Button btn_delete_booking = (Button) layout_dialog.findViewById(R.id.btn_delete_booking);
        ImageView back_pressed = (ImageView) layout_dialog.findViewById(R.id.back_pressed);


        txt_customer_name.setText(bookingInformation.getCustomerName());
        txt_salon_name.setText(bookingInformation.getSalonName());
        txt_customer_phone.setText(bookingInformation.getCustomerPhone().replaceAll("[+]972", "0"));
        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_time.setText(bookingInformation.getBookingTime());

        //Show dialog
        builder.create();
        builder.show();

        back_pressed.setOnClickListener(v -> builder.dismiss());

        btn_delete_booking.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("מחיקת תור קיים")
                        .setMessage("את עומדת למחוק את התור, האם את בטוחה?")
                        .setNegativeButton("ביטול", (dialog1, which) ->
                                dialog1.dismiss())
                        .setPositiveButton("מאשרת", (dialog12, which) -> {
                            deleteBookingFromBarber(false, bookingInformationList.get(i), myViewHolder);

                            builder.dismiss();

                            dialog12.dismiss();
                        }).create();
                dialog.show();


            }
        });

        btn_change_booking.setOnClickListener(v ->
                ChangeBookingFromUser(i, myViewHolder));

    }

    private void ChangeBookingFromUser(int i, MyViewHolder myViewHolder) {
        //Show dialog confirm
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("היי!")
                .setMessage("האם את רוצה לשנות את התור שלך?\n אם כן אנו נמחק את התור הקיים\n האם את מאשרת")
                .setNegativeButton("ביטול", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                }).setPositiveButton("מאשרת", (dialogInterface, which) -> {
                    deleteBookingFromBarber(true, bookingInformationList.get(i), myViewHolder);
                });
        confirmDialog.show();
    }

    private void deleteBookingFromBarber(boolean isChange, BookingInformation bookingInformation, MyViewHolder myViewHolder) {
        //we need load Common.currentBooking because we need some data from BookingInformation
        int i = myViewHolder.getAdapterPosition();

        //get booking information in barber project
        DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(bookingInformation.getCityBook())
                .collection("Branch")
                .document(bookingInformation.getSalonId())
                .collection("Barbers")
                .document(bookingInformation.getBarberId())
                .collection(bookingInformation.getBookingDate())
                .document(bookingInformation.getSlot().toString());

        //When we document, just delete it
        barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                deleteBookingFromUser(isChange, bookingInformation.getBookingTime(), i, bookingInformation);
            }
        });
    }

    private void deleteBookingFromUser(boolean isChange, String bookingTime, int i, BookingInformation bookingInformation) {

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking")
                .whereEqualTo("bookingTime", bookingTime)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                for (DocumentSnapshot historySnapshot:list) {
                    String Id = historySnapshot.getId();

                    DocumentReference bookingDelete = FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(Common.currentUser.getPhoneNumber())
                            .collection("Booking")
                            .document(Id);

                    bookingDelete.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        //After delete from "User", just delete from calendar
                                        //First, we need save Uri of event we just add
                                        Paper.init( context );
                                        if (Paper.book().read( Common.EVENT_URI_CACHE ) != null) {
                                            String eventString = Paper.book().read( Common.EVENT_URI_CACHE ).toString();
                                            Uri eventUri = null;
                                            if (eventString != null && !TextUtils.isEmpty( eventString ))
                                                eventUri = Uri.parse( eventString );
                                            if (eventUri != null)
                                                context.getContentResolver().delete( eventUri, null, null );
                                        }

                                        Toast mToast = new Toast(context);
                                        mToast.setDuration(Toast.LENGTH_LONG);
                                        View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity)context).findViewById(R.id.root_layout));
                                        TextView textView = toastView.findViewById(R.id.txt_message);
                                        mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                        textView.setText("תור נמחק");
                                        mToast.setView(toastView);
                                        mToast.show();

                                        bookingInformationList.remove(bookingInformationList.get(i));
                                        notifyDataSetChanged();

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingInformationList  == null ? 0 :bookingInformationList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        Unbinder unbinder;

        @BindView(R.id.txt_order_status)
        TextView txt_order_status;

        @BindView(R.id.txt_booking_time_text)
        TextView txt_booking_time_text;

        @BindView(R.id.txt_booking_date)
        TextView txt_booking_date;

        @BindView(R.id.delete_change)
        ImageView delete_change;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

        }
    }
}
