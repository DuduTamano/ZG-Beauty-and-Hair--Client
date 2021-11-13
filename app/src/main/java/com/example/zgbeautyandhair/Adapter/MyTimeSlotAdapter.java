package com.example.zgbeautyandhair.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Interface.IRecyclerItemSelectedListener;
import com.example.zgbeautyandhair.Model.EventBus.EnableNextButton;
import com.example.zgbeautyandhair.Model.TimeSlot;
import com.example.zgbeautyandhair.R;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(i)[0]).toString());

        String time = Common.convertTimeSlotToString(i)[0];
        String[] startEnd = time.split("-");
        String[] startTime = startEnd[0].split(":");
        int startHour = Integer.parseInt(startTime[0]);
        int startMin = Integer.parseInt(startTime[1]);

        String today = Common.getSimpleFormatDate().format(Calendar.getInstance().getTime());
        String bookingDate = Common.getSimpleFormatDate().format(Common.getBookingDate().getTime());

        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minOfDay = Calendar.getInstance().get(Calendar.MINUTE);

        if(!timeSlotList.isEmpty()) {

            if(today.equals(bookingDate)) {
                if(hourOfDay <= startHour && minOfDay < startMin) {
                    myViewHolder.card_time_slot.setEnabled(true);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white, null));
                    myViewHolder.txt_time_slot_description.setText("תור פנוי");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black, null));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black, null));
                }
                else if(hourOfDay > startHour) {
                    myViewHolder.card_time_slot.setEnabled(false);
                    myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray, null));
                    myViewHolder.txt_time_slot_description.setText("תור לא פנוי");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white, null));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white, null));
                }
            }
            else {
                myViewHolder.card_time_slot.setEnabled(true);
                myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white, null));
                myViewHolder.txt_time_slot_description.setText("תור פנוי");
                myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black, null));
                myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black, null));
            }

            for(TimeSlot slotValue:timeSlotList) {
                int slot = Integer.parseInt(slotValue.getSlot().toString());

                if(slot == i) {
                    myViewHolder.card_time_slot.setEnabled(false);
                    myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray, null));
                    myViewHolder.txt_time_slot_description.setText("תפוס");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white, null));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white, null));
                }
            }
        }
        else {
            //if time if left
            if(today.equals(bookingDate)) {
                if(hourOfDay <= startHour && minOfDay < startMin) {
                    myViewHolder.card_time_slot.setEnabled(true);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white, null));
                    myViewHolder.txt_time_slot_description.setText("זמן חלף");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black, null));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black, null));
                }
                //if hourOfDay is currect day, hour is left
                else if(hourOfDay > startHour) {
                    myViewHolder.card_time_slot.setEnabled(false);
                    myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.background_dark, null));
                    myViewHolder.txt_time_slot_description.setText("לא פנוי");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white, null));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white, null));
                }
            }
            //hourOfDay is currect day, time is available
            else {
                myViewHolder.card_time_slot.setEnabled(true);
                myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white, null));
                myViewHolder.txt_time_slot_description.setText(" פנוי");
                myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black, null));
                myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black, null));
            }
        }

        if(!cardViewList.contains(myViewHolder.card_time_slot))
            cardViewList.add(myViewHolder.card_time_slot);

        if (!timeSlotList.contains(i))
        {
            myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {
                    for (CardView cardView : cardViewList) {
                        if (cardView.getTag() == null) {
                            cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white, null));
                            Common.EnableNextButton(Common.convertTimeSlotToString(i), context);
                        }
                    }

                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.homeBackground, null));

                    EventBus.getDefault().postSticky(new EnableNextButton(3, i, Common.isEnabledButton()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_time_slot,txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_time_slot = itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = itemView.findViewById(R.id.txt_time_slot_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}