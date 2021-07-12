package com.example.app_schedule;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.Display;

import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_schedule.circleSection.CircleFragment;
import com.example.app_schedule.listSection.ListFragment;
import com.example.app_schedule.statisticFragment.StatisticFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;

import static com.example.app_schedule.Tool.DateToDayOfWeek;
import static com.example.app_schedule.Tool.add0SmallerThan10;
import static com.example.app_schedule.Tool.concatenateDateFormat;
import static com.example.app_schedule.Tool.getMonthOfNow;
import static com.example.app_schedule.Tool.getMonthsEnglishName;
import static com.example.app_schedule.Tool.getNow;
import static com.example.app_schedule.Tool.getYearOfNow;
import static com.example.app_schedule.Tool.isLeapYear;

@SuppressLint("NonConstantResourceId")
// TODO: 2021-06-24 이제 해야 할것 앱을 정리하는 거는 나중에 주말에하고 지금부터는 리스트 프래그먼트 다시 디지인하고 실행할 수 있도록 그다음 원형에서 plan을 눌렀을때 삭제 할건지 세부설정 할건지 완료할건지 뜨는 알림창 만들고
// TODO: 2021-06-24 그다음 통계 프래그먼트도 정리하고 circleFragment 부분 디자인 참고해서 그 날짜 부분 특히 더바꾸고 하면서 해야될거 발견하면 여기에 적어놓기 
// TODO: 2021-06-24 이거 selectDayAdapter 이거 MainActivity에다 놓고 눌릴때마다 fragment.refresh 하면 안됨? 이거 메일에 해답 비슷한거 내게보내기 해놓음
public class MainActivity extends AppCompatActivity {
    public static int DEVICE_WIDTH; //디바이스 화면 넓이
    public static int DEVICE_HEIGHT;//디바이스 화면 높이
    public static String MAIN_COLOR = "#D97898";
    public static List<DayInfo> mListOfSelectedMonths;//selectDay 어댑터로 넘길 한달객체
    public static DayInfo SELECTED_DATE;
    private CircleFragment circleFragment;
    private ListFragment listFragment;
    private StatisticFragment statisticFragment;
    private FragmentManager fragmentManager;
    public static AppDataBase appDataBase;
    private SelectDayAdapter selectDayAdapter;
    public RefreshFragmentInst refreshFragmentInst;
    public static AlarmManager alarmManager;

    @BindView(R.id.rv_selectDay)
    RecyclerView rv_selectDay;
    @BindView(R.id.tv_engMonthDay)
    TextView tv_engMonthDay;//가장위에 뜨는 년도와 달
    @BindView(R.id.tv_year)
    TextView tv_year;
    @BindView(R.id.iv_calendarButton)
    ImageView iv_calendarButton;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDeviceSize();
        bindView();
        provideDataBase();
        setAlarmManager();
        setSelectedDateToNow();
        setFragment();
        setRefreshFragmentInst();
        setRecyclerView();
        setBottomNavigationView();
        setOnClickListener();
    }

    public void setAlarmManager() {
        alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void setDeviceSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        DEVICE_WIDTH = size.x;
        DEVICE_HEIGHT = size.y;
    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    /**
     * SharedPreferences 에 버전을 input 함.
     */
    private void setVersion() {
        SharedPreferences sharedPreferences = getSharedPreferences("Version_db7",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("version",2);
        editor.commit();
    }

    /**
     * SharedPreferences 에 저장되어있는 버전을 가져옴 앱을 처음 받았을 경우 default 값 1을 가져옴.
     * @return int version
     */
    private int getVersion() {
        SharedPreferences sharedPreferences = getSharedPreferences("Version_db7",MODE_PRIVATE);
        return sharedPreferences.getInt("version",1);
    }

    /**
     * 앱을 처음 다운받았을 경우에는 데이터베이스를 초기화 시키고 그렇지 않은 경우에는 데이터베이스에 있는 데이터를 불러옴.
     * @throws Exception 아직 안함.
     */
    private void provideDataBase() throws Exception {
        if(getVersion() == 1) {
            appDataBase = Room.databaseBuilder(this, AppDataBase.class, "dayInfo7-db")
                    .allowMainThreadQueries()
                    .build();
            appDataBase.dayInfoDao().insertAllDayInfo(getList());
            setVersion();
        } else {
            appDataBase = Room.databaseBuilder(this, AppDataBase.class, "dayInfo7-db")
                    .allowMainThreadQueries()
                    .build();
        }
    }

    /**
     * //SELECTED_DATE 에 현재 날짜를 초기화 해줌 yyyyMMdd.
     */
    @SuppressLint("SetTextI18n")
    private void setSelectedDateToNow() {
        SELECTED_DATE = appDataBase.dayInfoDao().getDayInfoWithTodoDone(getNow());
        setListOfSelectedMonths(getYearOfNow(),getMonthOfNow());
        changeTextView();
    }

    private List<DayInfo> getList() throws Exception {
        List<DayInfo> allDayInfoList = new ArrayList<>();
        for(int year=2020; year<=2023; year++) {
            for(int month=1; month<=12; month++) {
                int lastDayOfMonth = getLastDay(month,year);
                for(int day=1; day<=lastDayOfMonth; day++) {
                    allDayInfoList.add(new DayInfo(concatenateDateFormat(year, month, day),
                            year, month, day, DateToDayOfWeek(Integer.toString(year)
                            .concat(add0SmallerThan10(month).concat(add0SmallerThan10(day))))));
                }
            }
        }
        return allDayInfoList;
    }

    /**
     * 리사이클러뷰 세팅하기
     */
    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv_selectDay.setLayoutManager(linearLayoutManager);
        if(refreshFragmentInst == null) {
            setRefreshFragmentInst();
        }
        selectDayAdapter = new SelectDayAdapter(appDataBase.dayInfoDao().getOneMonth(SELECTED_DATE.getYear(), SELECTED_DATE.getMonth()), refreshFragmentInst);
        rv_selectDay.setAdapter(selectDayAdapter);
        scrollToSelectedItem();
    }

    private void initFragment() {
        circleFragment = new CircleFragment(MainActivity.this);
        listFragment = new ListFragment(MainActivity.this);
        statisticFragment = new StatisticFragment();
    }

    private void setFragment() {
        initFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, circleFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void setRefreshFragmentInst() {
        if(circleFragment == null || listFragment == null || statisticFragment == null) {
            setFragment();
        }
        refreshFragmentInst = new RefreshFragmentInst(circleFragment, listFragment);
    }

    private void setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.page_1:
                        fragmentTransaction.replace(R.id.fragment_container,circleFragment).commitAllowingStateLoss();
                        break;
                    case R.id.page_2:
                        fragmentTransaction.replace(R.id.fragment_container,listFragment).commitAllowingStateLoss();
                        break;
                    case R.id.page_3:
                        fragmentTransaction.replace(R.id.fragment_container,statisticFragment).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });
    }

    private void setOnClickListener() {
        iv_calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarCustomDialog calendarCustomDialog = new CalendarCustomDialog(MainActivity.this,MainActivity.this, refreshFragmentInst);
                calendarCustomDialog.callDialog();
            }
        });
    }

    /**
     * selectedDATE 를 변경하고 selectedDATE 가 속해있는 달을 바꾼다
     * @param year 년도
     * @param month 달
     * @param day 날
     */
    public static void changeSELECTED_DATE(int year, int month, int day) {
        SELECTED_DATE = appDataBase.dayInfoDao().getDayInfoWithTodoDone(concatenateDateFormat(year,month,day));
        setListOfSelectedMonths(year,month);
    }

    /**
     * 메인액티비티 년과 달 정보 textView 를 SELECTED_DATE 에 맞춰서 바꿔줌.
     */
    @SuppressLint("SetTextI18n")
    public void changeTextView() {
        tv_engMonthDay.setText(getMonthsEnglishName(SELECTED_DATE.getMonth()) + " " + SELECTED_DATE.getDay());//그달 이름을 setText함
        tv_year.setText(Integer.toString(SELECTED_DATE.getYear()));
    }

    //todo 그리고 main 액티비티서 메뉴바 조금 작고 요일리사이클러뷰 크고 circleFragment 에서 시간 안보임 시간 짤림 settingDialog 도 아예 뭉겨져 버림


    /**
     * 리사이클러뷰를 선택된 날짜로 스크롤해줌
     */
    public void scrollToSelectedItem() {
        if(SELECTED_DATE.getDay() <= 4) {
            rv_selectDay.scrollToPosition(0);
        } else if(SELECTED_DATE.getDay() > 4 && SELECTED_DATE.getDay() <= getLastDay(SELECTED_DATE.getMonth(), SELECTED_DATE.getYear()) - 4) {
            rv_selectDay.scrollToPosition(SELECTED_DATE.getDay() - 4);
        } else {
            rv_selectDay.scrollToPosition(getLastDay(SELECTED_DATE.getMonth(), SELECTED_DATE.getYear()) - 3);
        }
    }

    public void scrollToSelectedItem2() {
        if(SELECTED_DATE.getDay() <= 4) {
            rv_selectDay.scrollToPosition(0);
        } else if(SELECTED_DATE.getDay() > 4 && SELECTED_DATE.getDay() <= getLastDay(SELECTED_DATE.getMonth(), SELECTED_DATE.getYear()) - 4) {
            rv_selectDay.scrollToPosition(1);
            rv_selectDay.scrollToPosition(SELECTED_DATE.getDay() + 2);
        } else {
            rv_selectDay.scrollToPosition(getLastDay(SELECTED_DATE.getMonth(), SELECTED_DATE.getYear()) - 1);
        }
    }

    public void refreshSelectDayAdapterList() {
        selectDayAdapter.setList_oneMonth(appDataBase.dayInfoDao().getOneMonth(SELECTED_DATE.getYear(),SELECTED_DATE.getMonth()));
        selectDayAdapter.setBooleanArray_selectedDATEAsTrue();
        selectDayAdapter.notifyDataSetChanged();
        changeTextView();
    }

    /**
     * 선택된 날짜의 달정보를  mListOfSelectedMonth 에 담기
     */
    public static void setListOfSelectedMonths(int year, int month) {
        mListOfSelectedMonths = appDataBase.dayInfoDao().getOneMonth(year,month);
    }


    /**
     * 입력받은 그 연도 그 달의 마지막 날짜를 구함
     * @param month
     * @param year
     * @return
     */
    public static int getLastDay(int month, int year) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if (month == 2 && !isLeapYear(year)) {
            return 29;
        } else if (month == 2 && isLeapYear(year)) {
            return 28;
        } else {
            return 31;
        }
    }

    public static class RefreshFragmentInst {
        public CircleFragment circleFragment;
        public ListFragment listFragment;

        public RefreshFragmentInst(CircleFragment circleFragment, ListFragment listFragment) {
            this.circleFragment = circleFragment;
            this.listFragment = listFragment;
        }

        public void refreshCircleFragment() {
            this.circleFragment.refresh();
        }

        public void refreshListFragment() {
            this.listFragment.refresh();
        }
    }
}