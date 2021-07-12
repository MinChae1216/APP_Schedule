package com.example.app_schedule;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DayInfo.class, PlanInfo.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase{
    public abstract DayInfoDao dayInfoDao();
}
