package com.example.app_schedule;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "planInfo")
@Getter @Setter
public class PlanInfo implements Comparable<PlanInfo>{
    @PrimaryKey
    public long planId;
    public int dayId;
    private String todoOrDone;
    private double start;
    private double endAngle;
    private String planName;
    private String planMemo;
    private String color;

    public PlanInfo(long planId, int dayId, String todoOrDone, double start, double endAngle, String planName, String planMemo, String color) {
        this.planId = planId;
        this.dayId = dayId;
        this.todoOrDone = todoOrDone;
        this.start = start;
        this.endAngle = endAngle;
        this.planName = planName;
        this.planMemo = planMemo;
        this.color = color;
    }

    @Override
    public int compareTo(PlanInfo o) {
        return (int) (this.start - o.start);
    }
}
