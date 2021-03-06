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
    TextInputLayout et_planName; //todo ?????? fragment ?????? planName ??????????????? ????????? ???????????? ?????? ?????? ??????
    @BindView(R.id.iv_addButton)
    ImageView iv_addButton;
    @BindView(R.id.rv_setting)
    RecyclerView rv_setting;
    @BindView(R.id.filledEt_Memo)
    TextInputLayout et_planMemo;//todo ?????? ?????? ??????????????? ???????????? ?????????.
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
    private double mStartAngle;//circleFragment ?????? ????????? ?????? ??????
    private double mEndAngle;//circleFragment ?????? ????????? ??? ??????
    private List<PlanInfo> mPlanList;//????????? ?????? planList todo ?????? done ?????????
    public List<String> bookMarkList = new ArrayList<>();//????????? ?????????
    public BookMarkAdapter bookMarkAdapter;
    private String mPlanName;
    private boolean isClickable = true; //??????????????? ??????????????? ????????? ok ????????? ????????????
    private CircleCanvasView circleCanvasView;
    private boolean isAlarm;
    public static Toast toast;
    public String detailSetting_color = "#000000";//??? ????????? ????????? ??????
    public ListFragmentAdapter listFragmentAdapter;
    public SelectColorAdapter selectColorAdapter;

    private final MainActivity.RefreshFragmentInst refreshFragmentInst;
    private GregorianCalendar mCalender;

    //todo ????????? ???????????? ??? ?????? ?????? ?????? ???????????? ???????????? ????????? ?????? ????????????

   /**
     * settingDialog construct
    * @param startAngle ?????? ??????
    * @param endAngle ??? ??????
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
        bookMarkList.add("???");
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
        receiverIntent.setAction("?????? ??????");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        String from = id.concat(time).concat("00"); //????????? ????????? ????????? ??????

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
        receiverIntent.setAction("?????? ??????");
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
        bookMarkAdapter = new BookMarkAdapter(bookMarkList,et_planName);//???????????? ????????? ????????? et_planName ??? ??????????????? String ??? ?????????.
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
     * ???????????? ????????? ????????? ?????? sharedPreferences ???????????? ????????????
     * @param context MainActivity context
     * @param key sharedPreferences ???????????? ???
     * @param values ?????? ?????? ????????? ?????????
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
     * ???????????? ????????? ????????? ????????????
     * @param context MainActivity context
     * @param key ????????? ???????????? ???
     * @return ?????? ?????? value
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

    // TODO: 6/5/2021 ?????? circleCanvasView.invalidate() ???????????? circleCanvasView??? ???????????? ????????? ????????????. 
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
                    showToast(mContext,"??????????????? ?????????????????? ??? ????????????.\n ?????? ????????? ??????????????????");
                }  else if(!isExistPlanName()) {
                    showToast(mContext,"?????? ????????? ??????????????????");
                } else {
                    showToast(mContext,"??????????????? ?????????????????? ??? ????????????");
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
                timePickerDialog.setTitle("?????? ??????");
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
                timePickerDialog.setTitle("?????? ??????");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });




        //todo ?????? ????????? ?????? ????????? ????????? ???????????? ????????? ??????
        iv_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(mContext);

                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext,R.style.MyAlertDialogStyle);

                alt_bld.setTitle("????????? ??????")

                        .setMessage("????????? ???????????? ???????????????")

                        .setIcon(R.drawable.ic_launcher_background)

                        .setCancelable(false)

                        .setView(et)

                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {

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
