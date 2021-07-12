package com.example.app_schedule;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_schedule.circleSection.CircleCanvasView;
import com.example.app_schedule.circleSection.CircleFragment;
import com.example.app_schedule.listSection.ListFragmentAdapter;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.MainActivity.DEVICE_HEIGHT;
import static com.example.app_schedule.MainActivity.DEVICE_WIDTH;
import static com.example.app_schedule.MainActivity.alarmManager;
import static com.example.app_schedule.MainActivity.appDataBase;
import static com.example.app_schedule.Tool.add0SmallerThan10;
import static com.example.app_schedule.Tool.concatenateDayInfoIdPlanId;
import static com.example.app_schedule.Tool.getAMOrPM;
import static com.example.app_schedule.Tool.getMonthsEnglishName;
import static com.example.app_schedule.Tool.angleToHour;
import static com.example.app_schedule.Tool.angleToMinute;
import static com.example.app_schedule.Tool.timeToAngle;
import static com.example.app_schedule.MainActivity.SELECTED_DATE;
import static com.example.app_schedule.Tool.toHourOf12;

@SuppressLint("NonConstantResourceId")
public class SettingDialog {


    @BindView(R.id.tv_engMonthDay)
    TextView tv_engMonthDay;
    @BindView(R.id.tv_year)
    TextView tv_year;
    @BindView(R.id.filledEt_Name)
    TextInputLayout et_planName; //todo 이거 fragment 에서 planName 입력하던거 그대로 가져오기 아직 설정 안함
    @BindView(R.id.iv_addButton)
    ImageView iv_addButton;
    @BindView(R.id.rv_setting)
    RecyclerView rv_setting;
    @BindView(R.id.filledEt_Memo)
    TextInputLayout et_planMemo;//todo 이거 아직 설정안하고 여러줄로 바꾸기.
    @BindView(R.id.rv_colorSelect)
    RecyclerView rv_colorSelect;
    @BindView(R.id.tv_startTime)
    TextView tv_startTime;
    @BindView(R.id.tv_endTime)
    TextView tv_endTime;
    @BindView(R.id.tv_cancelButton)
    TextView tv_cancelButton;
    @BindView(R.id.tv_setButton)
    TextView tv_okButton;



    private final Context mContext;
    private Dialog mDialog;
    private String todoOrDone;
    private double mStartAngle;//circleFragment 에서 받아온 시작 각도
    private double mEndAngle;//circleFragment 에서 받아온 끝 각도
    private List<PlanInfo> mPlanList;//선택된 날의 planList todo 거나 done 이거나
    public List<String> bookMarkList = new ArrayList<>();//북마크 리스트
    public BookMarkAdapter bookMarkAdapter;
    private String mPlanName;
    private boolean isClickable = true; //시작시간이 끝시간보다 클경우 ok 버튼이 비활성화
    private CircleCanvasView circleCanvasView;
    private boolean isAlarm;
    public static Toast toast;
    public String detailSetting_color = "#000000";//이 일정을 구성할 색깔
    public ListFragmentAdapter listFragmentAdapter;
    public SelectColorAdapter selectColorAdapter;

    private final MainActivity.RefreshFragmentInst refreshFragmentInst;
    private GregorianCalendar mCalender;

    //todo 그리고 알람버튼 및 메모 쓸때 줄이 자동으로 띄어쓰기 되면서 계속 써지게게

   /**
     * settingDialog construct
    * @param startAngle 시작 각도
    * @param endAngle 끝 각도
    * @param context context
    */
    public SettingDialog(double startAngle, double endAngle, String todoOrDone, MainActivity.RefreshFragmentInst refreshFragmentInst, String planName, Context context) {
        this.mContext = context;
        this.mStartAngle = startAngle;
        this.mEndAngle = endAngle;
        this.todoOrDone = todoOrDone;
        this.refreshFragmentInst = refreshFragmentInst;
        this.mPlanName = planName;
        this.mPlanList = appDataBase.dayInfoDao().getTodoOrDoneList(SELECTED_DATE.getId(), todoOrDone);
        bookMarkList.add("잠");
        setStringArrayPref(mContext,"bookMark",bookMarkList);
        bookMarkList = getStringArrayPref(mContext,"bookMark");
    }

    public void callDialog() {
        setDialog();
        bindViews();
        setTextView();
        setBookMarkRecyclerView();
        setSelectColorRecyclerView();
        viewOnclickListener();
    }



    public static void setAlarm(String id, String time, String contextTitle, Context context) {
        int alarmId = Integer.parseInt(id.substring(4,7).concat(time));
        Intent receiverIntent = new Intent(context, NotificationHelper.class);
        receiverIntent.putExtra("alarmId", alarmId);
        receiverIntent.putExtra("contentText", contextTitle);
        receiverIntent.setAction("알람 설정");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String from = id.concat(time).concat("00"); //임의로 날짜와 시간을 지정

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date datetime = null;
        try {
            datetime = dateFormat.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelAlarm(String id, String time, Context context) {
        int alarmId = Integer.parseInt(id.substring(4,7).concat(time));
        Intent receiverIntent = new Intent(context, NotificationHelper.class);
        receiverIntent.setAction("알람 설정");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    @SuppressLint("SetTextI18n")
    private void setTextView() {
        tv_engMonthDay.setText(getMonthsEnglishName(SELECTED_DATE.getMonth()) + " " + SELECTED_DATE.getDay());
        tv_year.setText(Integer.toString(SELECTED_DATE.getYear()));
        if(mStartAngle == 0) {
            tv_startTime.setText( 12 + " : " + angleToMinute(mStartAngle) + " " + "AM"  );
        } else {
            tv_startTime.setText(getAMOrPM(angleToHour(mStartAngle)) + " " + toHourOf12(angleToHour(mStartAngle)) + " : " +angleToMinute(mStartAngle));
        }
        tv_endTime.setText(getAMOrPM(angleToHour(mEndAngle)) + " " + toHourOf12(angleToHour(mEndAngle)) + " : " +angleToMinute(mEndAngle));
        Objects.requireNonNull(et_planName.getEditText()).setText(mPlanName);
    }

    private String getmEditText_memo() {
        if(Objects.requireNonNull(et_planMemo.getEditText()).getText().toString().equals("")) {
            return "";
        } else {
            return et_planMemo.getEditText().getText().toString();
        }
    }

    private void setDialog() {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_setting);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = (DEVICE_WIDTH * 9) / 10;
        lp.height = (DEVICE_HEIGHT * 8) / 10;
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setAttributes(lp);
    }

    private void bindViews() {
        ButterKnife.bind(this,mDialog);
    }

    private void setBookMarkRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mDialog.getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv_setting.setLayoutManager(linearLayoutManager);
        bookMarkAdapter = new BookMarkAdapter(bookMarkList,et_planName);//즐겨찾기 목록을 누르면 et_planName 에 즐겨찾기한 String 이 입력됨.
        rv_setting.setAdapter(bookMarkAdapter);
    }

    private void setSelectColorRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mDialog.getContext(),2);
        gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rv_colorSelect.setLayoutManager(gridLayoutManager);
        selectColorAdapter = new SelectColorAdapter(this);
        rv_colorSelect.setAdapter(selectColorAdapter);
    }

    public void setCircleCanvasView(CircleCanvasView circleCanvasView) {
        this.circleCanvasView = circleCanvasView;
    }

    public void setRecyclerView(ListFragmentAdapter listFragmentAdapter) {
        this.listFragmentAdapter = listFragmentAdapter;
    }

    /**
     * 리스트에 저장된 북마크 목록 sharedPreferences 이용하여 저장하기
     * @param context MainActivity context
     * @param key sharedPreferences 저장소의 키
     * @param values 저장 시킬 북마크 리스트
     */
    private void setStringArrayPref(Context context, String key, List<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }



    /**
     * 저장소에 저장된 리스트 불러오기
     * @param context MainActivity context
     * @param key 불러올 저장소의 키
     * @return 키와 맞는 value
     */
    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    private String getPlanName() {
        String s = Objects.requireNonNull(et_planName.getEditText()).getText().toString();
        if(s.equals("")) {
            return mPlanName;
        } else {
            return s;
        }
    }

    private boolean isExistPlanName() {
        String s = Objects.requireNonNull(et_planName.getEditText()).getText().toString();
        if(s.equals("")) {
            return false;
        } else {
            return true;
        }
    }


    private Calendar getCalendarDay(int day,int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        return c;
    }

    public static void showToast(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.show();
    }

    // TODO: 6/5/2021 이거 circleCanvasView.invalidate() 하기전에 circleCanvasView의 리스트를 초기화 시켜야됨. 
    private void viewOnclickListener() {
        tv_okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClickable && isExistPlanName()) {
                    for(PlanInfo p : mPlanList) {
                        if(mStartAngle <= p.getStart()) {
                            if(mEndAngle > p.getStart() && mEndAngle < p.getEndAngle()) {
                                appDataBase.dayInfoDao().updatePlanInfoStartWithPlanId(p.getPlanId(), mEndAngle, p.getDayId());
                            } else if(mEndAngle >= p.getEndAngle()) {
                                appDataBase.dayInfoDao().deletePlanInfo(p.getPlanId());
                            } else {
                                continue;
                            }
                        } else if(mStartAngle > p.getStart() && mStartAngle < p.getEndAngle()) {
                            if (mEndAngle < p.getEndAngle()) {
                                appDataBase.dayInfoDao().updatePlanInfoEnd(p.getDayId(), mEndAngle);
                                appDataBase.dayInfoDao().insertPlanInfo(new PlanInfo(concatenateDayInfoIdPlanId(SELECTED_DATE.getId(), (int) mEndAngle)
                                        , SELECTED_DATE.getId(), todoOrDone, mEndAngle, p.getEndAngle(), p.getPlanName(), p.getPlanMemo(), p.getColor()));
                            } else {
                                appDataBase.dayInfoDao().updatePlanInfoEnd(p.getDayId(), mStartAngle);
                            }
                        } else {
                            continue;
                        }
                    }
                    appDataBase.dayInfoDao().insertPlanInfo(new PlanInfo(concatenateDayInfoIdPlanId(SELECTED_DATE.getId(),(int) mStartAngle)
                            ,SELECTED_DATE.getId()
                            ,todoOrDone, mStartAngle, mEndAngle
                            ,getPlanName(), getmEditText_memo(), detailSetting_color));
                    SELECTED_DATE = appDataBase.dayInfoDao().getDayInfoWithTodoDone(SELECTED_DATE.getId());
                    setAlarm(String.valueOf(concatenateDayInfoIdPlanId(SELECTED_DATE.getId(), (int) mStartAngle))
                            ,add0SmallerThan10(angleToHour(mStartAngle)).concat(add0SmallerThan10(angleToMinute(mStartAngle)))
                            ,getPlanName(), mContext);
                    refreshFragmentInst.refreshCircleFragment();
                    refreshFragmentInst.refreshListFragment();
                    mDialog.dismiss();
                } else if(!isClickable && !isExistPlanName()){
                    showToast(mContext,"시작시간이 종료시간보다 더 이릅니다.\n 일정 제목이 비어있습니다");
                }  else if(!isExistPlanName()) {
                    showToast(mContext,"일정 제목이 비어있습니다");
                } else {
                    showToast(mContext,"시작시간이 종료시간보다 더 이릅니다");
                }
            }
        });

        tv_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mStartAngle = timeToAngle(hourOfDay,minute);
                                if(mStartAngle >= mEndAngle) {
                                    isClickable = false;
                                    tv_okButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.radius_disabled));
                                } else {
                                    isClickable = true;
                                    tv_okButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.background_filled_yellow));
                                }
                                tv_startTime.setText(getAMOrPM(angleToHour(mStartAngle)) + " " + angleToHour(mStartAngle) + " : " +angleToMinute(mStartAngle));
                            }
                        },  angleToHour(mStartAngle),
                        angleToMinute(mStartAngle),false);
                timePickerDialog.setTitle("시작 시간");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                        android.R.style.Theme_Holo_Light_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mEndAngle = timeToAngle(hourOfDay,minute);
                                if(mStartAngle >= mEndAngle) {
                                    isClickable = false;
                                    tv_okButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.radius_disabled));
                                } else {
                                    isClickable = true;
                                    tv_okButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.background_filled_yellow));
                                }
                                tv_endTime.setText(getAMOrPM(angleToHour(mEndAngle)) + " " + angleToHour(mEndAngle) + " : " +angleToMinute(mEndAngle));
                            }
                        },  angleToHour(mEndAngle),
                        angleToMinute(mEndAngle),false);
                timePickerDialog.setTitle("시작 시간");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });




        //todo 아직 이것도 안함 북마크 리스트 추가하는 플러스 버튼
        iv_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(mContext);

                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext,R.style.MyAlertDialogStyle);

                alt_bld.setTitle("닉네임 변경")

                        .setMessage("변경할 닉네임을 입력하세요")

                        .setIcon(R.drawable.ic_launcher_background)

                        .setCancelable(false)

                        .setView(et)

                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                String value = et.getText().toString();
                                if(!value.equals("")) {
                                    bookMarkList.add(value);
                                    bookMarkAdapter.notifyDataSetChanged();
                                }

                            }

                        });

                AlertDialog alert = alt_bld.create();

                alert.show();


            }
        });
    }
}
