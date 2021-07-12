package com.example.app_schedule;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static com.example.app_schedule.Tool.concatenateDayInfoIdPlanId;

@Dao
public abstract class DayInfoDao {
    @Insert
    public abstract void insertAllDayInfo(List<DayInfo> dayInfoList);

    /*
    DayInfo 객체를 데이터베이스 DayInfo Entity 에 삽입함.
     */
    @Insert
    public abstract void insertDayInfo(DayInfo dayInfo);

    @Insert
    public abstract void insertPlanInfo(PlanInfo planInfo);

    /*
    Todo 객체를 데이터베이스 Todo Entity 에 삽입함.
     */
    @Insert
    public abstract void insertTodoList(List<PlanInfo> todoList);

    /*
    Done 객체를 데이터베이스 Done Entity 에 삽입함.
     */
    @Insert
    public abstract void insertDoneList(List<PlanInfo> doneList);

    @Query("DELETE FROM planInfo WHERE planId =:planId")
    public abstract void deletePlanInfo(long planId);

    @Query("UPDATE planInfo SET start =:start WHERE planId =:planId")
    public abstract void updatePlanInfoStart(long planId, double start);

    @Query("UPDATE planInfo SET endAngle =:end WHERE planId =:planId")
    public abstract void updatePlanInfoEnd(long planId, double end);

    @Query("UPDATE planInfo SET planId =:newPlanId WHERE planId =:planId")
    public abstract void updatePlanInfoPlanId(long newPlanId, long planId);

    /*
    id에 따라 id가 일치하는 DayInfo 엔티티의 모든 애트리뷰트를 get 함
     */
    @Query("SELECT * FROM DayInfo WHERE id =:id")
    public abstract DayInfo getDayInfo(int id);

    /*
    dayId에 따라 dayId가 일치하는 Todo 엔티티의 모든 애트리뷰트를 get 함
     */
    @Query("SELECT * FROM planInfo WHERE dayId =:dayId AND todoOrDone =:todoOrDone")
    public abstract List<PlanInfo> getTodoOrDoneList(int dayId, String todoOrDone);


    @Query("SELECT * FROM DayInfo WHERE year =:year AND month =:month")
    public abstract List<DayInfo> getOneMonth(int year, int month);

    @Update
    public abstract void updateAllDayInfo(DayInfo allDayInfo);

    public void updatePlanInfoStartWithPlanId(long planId, double start, int dayId) {
        updatePlanInfoStart(planId,start);
        updatePlanInfoPlanId(concatenateDayInfoIdPlanId(dayId, (int) start), planId);
    }

    /*
    지금 현재 가져온 AllDayInfo 에 들어있는 todo done 리스트에 dayId를 설정하고 entity 에 삽입.
     */
    public void insertDayInfoWithTodoDone(DayInfo dayInfo) {
        List<PlanInfo> todos = dayInfo.getTodo();
        List<PlanInfo> dones = dayInfo.getDone();
        for(PlanInfo todo : todos) {
            todo.setDayId(dayInfo.getId());
        }
        for(PlanInfo done : dones) {
            done.setDayId(dayInfo.getId());
        }
        insertTodoList(todos);
        insertDoneList(dones);
        insertDayInfo(dayInfo);
    }

    public DayInfo getDayInfoWithTodoDone(int id) {
        DayInfo allDayInfo =  getDayInfo(id);
        List<PlanInfo> todos = getTodoOrDoneList(id,"todo");
        List<PlanInfo> dones = getTodoOrDoneList(id,"done");
        if(todos !=null) {
            allDayInfo.setTodo(todos);
        }
        if(dones != null) {
            allDayInfo.setDone(dones);
        }
        return allDayInfo;
    }
}
