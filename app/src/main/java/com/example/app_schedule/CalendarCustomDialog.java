package com.example.app_schedule;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


import static com.example.app_schedule.MainActivity.changeSELECTED_DATE;
import static com.example.app_schedule.SettingDialog.showToast;

@SuppressLint("NonConstantResourceId")
public class CalendarCustomDialog {
    private Dialog mDialog;
    private Context mContext;
    private CalendarDay mCalendarDay;
    private final MainActivity mainActivity;
    private final MainActivity.RefreshFragmentInst refreshFragmentInst;

    @BindView(R.id.calendarView)
    MaterialCalendarView materialCalendarView;
    @BindView(R.id.bt_calendarOK)
    TextView bt_calendarOK;
    @BindView(R.id.bt_calendarNO)
    TextView bt_calendarNO;


    public CalendarCustomDialog(Context context, MainActivity mainActivity, MainActivity.RefreshFragmentInst refreshFragmentInst) {
        this.mContext = context;
        this.mainActivity = mainActivity;
        this.refreshFragmentInst = refreshFragmentInst;
    }

    public void callDialog() {
        setDialog();
        bindViews();
        setCalendarView();
        setOnClickedListener();
    }

    private void bindViews() {
        ButterKnife.bind(this,mDialog);
    }

    private void setOnClickedListener() {
        bt_calendarOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCalendarDay != null) {
                    //이게 1월이 0월로 나오고 3월이 2월로 나옴
                    changeSELECTED_DATE(mCalendarDay.getYear(),Integer.parseInt(Tool.add0SmallerThan10(mCalendarDay.getMonth() + 1)),Integer.parseInt(Tool.add0SmallerThan10(mCalendarDay.getDay())));
                    mainActivity.changeTextView();
                    mainActivity.refreshSelectDayAdapterList();
                    mainActivity.scrollToSelectedItem2();
                    refreshFragmentInst.refreshCircleFragment();
                    refreshFragmentInst.refreshListFragment();
                    mDialog.dismiss();
                } else {
                    showToast(mContext,"날짜를 선택해주세요");
                }
            }
        });
        bt_calendarNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    private void setCalendarView() {
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2020, 0, 1))
                .setMaximumDate(CalendarDay.from(2023, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                materialCalendarView.setSelectedDate(date);
                mCalendarDay = date;
            }
        });
    }


    private void setDialog() {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_calendar);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setAttributes(lp);
    }
}
