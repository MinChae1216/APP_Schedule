package com.example.app_schedule;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(tableName = "DayInfo")
@Setter @Getter
public class DayInfo {
    @PrimaryKey
    private int id; //ex) 20210711
    private int year;
    private int month;
    private int day;
    private String dayOfWeek;
    @Ignore
    private List<PlanInfo> todo;
    @Ignore
    private List<PlanInfo> done;



    public DayInfo(int id, int year, int month, int day, String dayOfWeek) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.dayOfWeek = dayOfWeek;
        this.todo = new ArrayList<>();
        this.done = new ArrayList<>();
    }
}
