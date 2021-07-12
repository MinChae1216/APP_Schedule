package com.example.app_schedule.calendarDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.app_schedule.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarDialog  {
    private Context context;
    final private Dialog dialog;
    private CalendarDay mDate;

    @BindView(R.id.calendarView)
    MaterialCalendarView materialCalendarView;

    @BindView(R.id.bt_calendarNO)
    TextView bt_calendarNO;

    @BindView(R.id.bt_calendarOK)
    TextView bt_calendarOK;

    public CalendarDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
    }

    private void bindView() {
        ButterKnife.bind(dialog);
        //날짜를 클릭했을 때
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mDate = date;
                //여기에 이제 날짜를 클릭했을때 변하는 날짜 데코레이터를 만들면됨
            }
        });
        //취소버튼을 눌렀을 때
        bt_calendarNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //확인 버튼을 눌렀을 때
        bt_calendarOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectedDATE.setDayInfoForDate(mDate);
            }
        });
    }

    public void callDialog() {
        bindView();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_calendar);
        dialog.show();
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2021,01,01))
                .setMaximumDate(CalendarDay.from(2023,12,31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(//일요일은 빨간색 토요일은 파란색으로 변경해주는 클래스
                new SundayDecorator(),
                new SaturdayDecorator());



    }
}
