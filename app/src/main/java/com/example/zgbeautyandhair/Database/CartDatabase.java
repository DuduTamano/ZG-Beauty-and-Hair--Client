package com.example.zgbeautyandhair.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 4, entities = CartItem.class, exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {
    public abstract CartDAO cartDAO();

    private static CartDatabase instance;

    public static CartDatabase getInstance(Context context) {
        if(instance == null)
            instance = Room.databaseBuilder(context, CartDatabase.class, "MyBarberDB")
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }
}