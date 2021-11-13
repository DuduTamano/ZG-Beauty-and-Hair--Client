package com.example.zgbeautyandhair.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Interface.IRecyclerItemSelectedListener;
import com.example.zgbeautyandhair.Model.Barber;
import com.example.zgbeautyandhair.Model.EventBus.EnableNextButton;
import com.example.zgbeautyandhair.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.MyViewHolder> {
    Context context;
    List< Barber > barberList;
    List<CardView> cardViewList;

    public MyBarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_barber,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        myViewHolder.txt_barber_name.setText(barberList.get(i).getName());
        if(barberList.get(i).getRatingTimes() != null)
            myViewHolder.ratingBar.setRating(barberList.get(i).getRating().floatValue() / barberList.get(i).getRatingTimes());
        else
            myViewHolder.ratingBar.setRating(0);

        if (!cardViewList.contains(myViewHolder.card_barber))
            cardViewList.add(myViewHolder.card_barber);

        myViewHolder.setiRecyclerItemSelectedListener((view, pos) -> {
            //set background for all item not choice
            for (CardView cardView:cardViewList)
            {
                cardView.setCardBackgroundColor(context.getResources()
                        .getColor(android.R.color.white));
            }

            //set background for choice
            myViewHolder.card_barber.setCardBackgroundColor(
                    context.getResources()
                            .getColor(R.color.homeBackground)
            );

            //Event Buss
            EventBus.getDefault().postSticky(new EnableNextButton(2,barberList.get(i)));
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_barber_name;
        RatingBar ratingBar;
        CardView card_barber;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_barber = itemView.findViewById(R.id.card_barber);
            txt_barber_name = itemView.findViewById(R.id.txt_barber_name);
            ratingBar = itemView.findViewById(R.id.rtb_barber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v,getAdapterPosition());
        }
    }
}