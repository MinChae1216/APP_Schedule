package com.example.app_schedule.circleSection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_schedule.MainActivity;
import com.example.app_schedule.PlanInfo;
import com.example.app_schedule.PlanInfoDialog;
import com.example.app_schedule.R;
import com.example.app_schedule.SettingDialog;
import com.example.app_schedule.Tool;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.MainActivity.DEVICE_WIDTH;
import static com.example.app_schedule.MainActivity.appDataBase;
import static com.example.app_schedule.MainActivity.getLastDay;
import static com.example.app_schedule.MainActivity.SELECTED_DATE;
import static com.example.app_schedule.SettingDialog.showToast;
import static com.example.app_schedule.Tool.concatenateDayInfoIdPlanId;
import static com.example.app_schedule.Tool.getAMOrPM;
import static com.example.app_schedule.Tool.timeToAngle;
import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD;

@SuppressLint("NonConstantResourceId")
public class CircleFragment extends Fragment{
    private Context mContext;
    public static double mCurrOfAngle_first;
    public static double mCurrOfAngle_second;
    private String todoOrDone;
    private MaterialTimePicker materialTimePicker;
    private final MainActivity mainActivity;

    @BindView(R.id.tv_done)
    TextView tv_done;
    @BindView(R.id.tv_todo)
    TextView tv_todo;
    @BindView(R.id.canvasView)
    CircleCanvasView circleCanvasView;
    @BindView(R.id.iv_stick)
    ImageView iv_startHand;
    @BindView(R.id.iv_stick2)
    ImageView iv_endHand;
    @BindView(R.id.tv_detailedSettings)
    TextView tv_settingButton;
    @BindView(R.id.tv_startTime)
    TextView tv_startTime;
    @BindView(R.id.tv_endTime)
    TextView tv_endTime;
    @BindView(R.id.tv_checkButton)
    TextView tv_checkButton;
    @BindView(R.id.filledEt_Name)
    TextInputLayout et_planName;

    public CircleFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // TODO: 6/7/2021 scrollAdpater 두개로 나눠져 있는 이유는 하나는 앱 실행할때 설정이고 하나는 캘린더로 날짜를 바꿨을때 실행하는건데 이게 26일에서 25일로 이동할때는 스크롤이 안됨 이걸 고쳐야함
    // TODO: 6/7/2021 firstAngle이 더 클때 리스트 갔다오면 텍스트뷰가 초기화가 안되는 현상 수정
    // TODO: 6/7/2021 check버튼 누를때도 그 전 리스트 가져와서 없앨지 각도를 바꿀지 다 설정 하기
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle, container, false);// Inflate the layout for this fragment
        mContext = container.getContext();
        bindView(view);
        setCircleCanvasViewAttr();
        setListOfCircleCanvasView("todo");
        initHand();
        initTextView();
        setOnClickListener();
        setOnTouchListener();
        imageViewOnclickListener();
        return view;
    }

    private void bindView(View view) {
        ButterKnife.bind(this,view);
    }

    private void setCircleCanvasViewAttr() {
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.oval);
        circleCanvasView.setBackground(drawable);
    }

    /**
     * CircleCanvasView 의 리스트를(todoOrDone) 설정하기
     */
    public void setListOfCircleCanvasView(String todoOrDone) {
        if(todoOrDone.equals("todo") || todoOrDone.equals("") || todoOrDone == null) {
            this.todoOrDone = "todo";
        } else {
            this.todoOrDone = "done";
        }
        circleCanvasView.setTodoOrDoneList(appDataBase.dayInfoDao().getTodoOrDoneList(SELECTED_DATE.getId(), todoOrDone));
    }

    private void initHand() { // TODO: 6/7/2021 이거 start end 관계 없이 min max 로 다시 설정하기.
        rotateView(iv_startHand, (float) mCurrOfAngle_first);
        mCurrOfAngle_second = 90;
        rotateView(iv_endHand, (float) mCurrOfAngle_second);
    }

    @SuppressLint("SetTextI18n")
    private void initTextView() {
        if(mCurrOfAngle_first == 0) {
            tv_startTime.setText("AM" + 12 +  " : " + Tool.angleToMinute(mCurrOfAngle_first));
        } else {
            tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                    Tool.angleToMinute(mCurrOfAngle_first));
        }
        if(mCurrOfAngle_second == 0) {
            tv_endTime.setText("AM" + 12 +  " : " + 30);
            mCurrOfAngle_second = timeToAngle(0,30);
        }
        tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                Tool.angleToMinute(mCurrOfAngle_second));
    }

    public void refresh() {
        setListOfCircleCanvasView(todoOrDone);
    }
    /*
    이게 두개로 나눠져 있는 이유는 하나는 앱 실행할때 설정이고 하나는 캘린더로 날짜를 바꿨을때 실행하는건데 이게 26일에서 25일로 이동할때는 스크롤이 안됨 이걸 고쳐야함
     */
    private void imageViewOnclickListener() {
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(todoOrDone.equals("todo")) {
                    tv_done.setBackgroundResource(R.drawable.background_filled_blue);
                    tv_done.setTextColor(Color.parseColor("#FFFFFF"));
                    tv_todo.setBackgroundResource(R.drawable.background_filled_radius_white);
                    tv_todo.setTextColor(Color.parseColor("#000000"));
                    setListOfCircleCanvasView("done");
                }
            }
        });

        tv_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(todoOrDone.equals("done")) {
                    tv_todo.setBackgroundResource(R.drawable.background_filled_blue);
                    tv_todo.setTextColor(Color.parseColor("#FFFFFF"));
                    tv_done.setBackgroundResource(R.drawable.background_filled_radius_white);
                    tv_done.setTextColor(Color.parseColor("#000000"));
                    setListOfCircleCanvasView("todo");
                }
            }
        });

        tv_settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //여기에 커스텁 다이얼 로그 실행
                SettingDialog settingDialog = new SettingDialog(Math.min(mCurrOfAngle_first, mCurrOfAngle_second),
                       Math.max(mCurrOfAngle_first, mCurrOfAngle_second), todoOrDone, mainActivity.refreshFragmentInst
                        , et_planName.getEditText().getText().toString(), mContext);
                settingDialog.setCircleCanvasView(circleCanvasView);
                settingDialog.callDialog();
            }
        });

        tv_checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_planName.getEditText().getText().toString().equals("")) {
                    showToast(mContext,"일정을 입력해 주세요");
                } else {
                    appDataBase.dayInfoDao().insertPlanInfo(new PlanInfo(concatenateDayInfoIdPlanId(SELECTED_DATE.getId(), (int) mCurrOfAngle_first), SELECTED_DATE.getId(),
                            todoOrDone, mCurrOfAngle_first, mCurrOfAngle_second,
                            getEditText_planName(), "","#369F36"));
                    //여기서 사실 selectedDate도 초기화 해줘야하는데 저거 의존도 낮출려고 데이터베이스에 직접접근 하는 방향으로 코딩중.
                    setListOfCircleCanvasView(todoOrDone);
                }
            }
        });
    }

    //todo 이게 지금 timePicker 에서 material 로 수정한건데 안되면 setOnTimeSelected 로 다시 해야됨 designError 그 파일 전거에 이거 수정 전 거 있음
    private void showTimePicker(View view) {
        switch (view.getId()) {
            case R.id.tv_startTime:
                materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .setInputMode(INPUT_MODE_KEYBOARD)
                        .build();
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        int hourOfDay = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        if(mCurrOfAngle_first < mCurrOfAngle_second) {
                            if(hourOfDay == 23 && minute >= 55){
                                mCurrOfAngle_first = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_first >= mCurrOfAngle_second) {
                                    mCurrOfAngle_second = (float) Tool.timeToAngle(24, 0);
                                    rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                    tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_second));
                                }
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                            } else if(hourOfDay == 24) {
                                mCurrOfAngle_first = (float) Tool.timeToAngle(23, 59);
                                mCurrOfAngle_second = (float) Tool.timeToAngle(24, 0);
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            } else {
                                mCurrOfAngle_first = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_first >= mCurrOfAngle_second) {
                                    mCurrOfAngle_second = (float) Tool.timeToAngle(hourOfDay, minute + 5);
                                    rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                    tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_second));
                                }
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                                //
                            }
                        } else {
                            if(hourOfDay == 23 && minute >= 55){
                                mCurrOfAngle_second = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_second >= mCurrOfAngle_first) {
                                    mCurrOfAngle_first = (float) Tool.timeToAngle(24, 0);
                                    rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                    tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_first));
                                }
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            } else if(hourOfDay == 24) {
                                mCurrOfAngle_first = (float) Tool.timeToAngle(24, 0);
                                mCurrOfAngle_second = (float) Tool.timeToAngle(23, 59);
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            } else {
                                mCurrOfAngle_second = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_second >= mCurrOfAngle_first) {
                                    mCurrOfAngle_first = (float) Tool.timeToAngle(hourOfDay, minute + 5);
                                    rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                    tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_first));
                                }
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            }
                        }
                    }
                });
                materialTimePicker.show(requireActivity().getSupportFragmentManager(),"fragment_tag");
                break;
            case R.id.tv_endTime:
                materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(10)
                        .setTitleText("Select Appointment time")
                        .setInputMode(INPUT_MODE_KEYBOARD)
                        .build();
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        int hourOfDay = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        if(mCurrOfAngle_first > mCurrOfAngle_second) {
                            if(hourOfDay == 0 && minute <= 5 && minute != 0){
                                mCurrOfAngle_first = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_first <= mCurrOfAngle_second) {
                                    mCurrOfAngle_second = (float) Tool.timeToAngle(0, 0);
                                    rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                    tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_second));
                                }
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                            } else if(hourOfDay == 0 && minute == 0) {
                                mCurrOfAngle_first = (float) Tool.timeToAngle(0, 5);
                                mCurrOfAngle_second = (float) Tool.timeToAngle(0, 0);
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            } else {
                                mCurrOfAngle_first = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_first <= mCurrOfAngle_second) {
                                    mCurrOfAngle_second = (float) Tool.timeToAngle(hourOfDay, minute - 5);
                                    rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                    tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_second));
                                }
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                            }
                        } else {
                            if(hourOfDay == 0 && minute <= 5 && minute != 0){
                                mCurrOfAngle_second = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_second <= mCurrOfAngle_first) {
                                    mCurrOfAngle_first = (float) Tool.timeToAngle(0, 0);
                                    rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                    tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_first));
                                }
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            } else if(hourOfDay == 0 && minute == 0) {
                                mCurrOfAngle_first = (float) Tool.timeToAngle(0, 0);
                                mCurrOfAngle_second = (float) Tool.timeToAngle(0, 1);
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_first));
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            } else {
                                mCurrOfAngle_second = (float) Tool.timeToAngle(hourOfDay, minute);
                                if(mCurrOfAngle_second <= mCurrOfAngle_first) {
                                    mCurrOfAngle_first = (float) Tool.timeToAngle(hourOfDay, minute - 5);
                                    rotateView(iv_startHand, (float) mCurrOfAngle_first);
                                    tv_startTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_first)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_first)) +  " : " +
                                            Tool.angleToMinute(mCurrOfAngle_first));
                                }
                                rotateView(iv_endHand, (float) mCurrOfAngle_second);
                                tv_endTime.setText(getAMOrPM(Tool.angleToHour(mCurrOfAngle_second)) + Tool.toHourOf12(Tool.angleToHour(mCurrOfAngle_second)) +  " : " +
                                        Tool.angleToMinute(mCurrOfAngle_second));
                            }
                        }
                    }
                });
                materialTimePicker.show(requireActivity().getSupportFragmentManager(),"fragment_tag");
        }
    }

    private void setOnClickListener() {
        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(tv_startTime);
            }
        });
        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(tv_endTime);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {


        iv_startHand.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final float centerOfHeight = circleCanvasView.getHeight() / 2;
                final float x = event.getRawX() - (DEVICE_WIDTH / 2);
                int[] location = new int[2];
                circleCanvasView.getLocationOnScreen(location);
                final float y = ((centerOfHeight) + location[1]) - event.getRawY();
                double touchedAngle = Tool.pieTo360Angle(Math.toDegrees(Math.atan2(x, y)));

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        //iv_startHand.setPivotY(circleCanvasView.getHeight() / 2);
                        mCurrOfAngle_first = touchedAngle;
                        rotateView(iv_startHand, (float) touchedAngle);
                        tv_startTime.setText(getAMOrPM(Tool.angleToHour(Math.min(mCurrOfAngle_first, mCurrOfAngle_second))) + " " + Tool.toHourOf12(Tool.angleToHour(Math.min(mCurrOfAngle_first, mCurrOfAngle_second))) +  " : " +
                                Tool.angleToMinute(Math.min(mCurrOfAngle_first, mCurrOfAngle_second)));
                        tv_endTime.setText(getAMOrPM(Tool.angleToHour(Math.max(mCurrOfAngle_first, mCurrOfAngle_second))) + " " + Tool.toHourOf12(Tool.angleToHour(Math.max(mCurrOfAngle_first, mCurrOfAngle_second))) +  " : " +
                                Tool.angleToMinute(Math.max(mCurrOfAngle_first, mCurrOfAngle_second)));
                        Log.d("pivot", String.valueOf(circleCanvasView.getHeight()));
                        break;
                }
                return true;
            }
        });

        iv_endHand.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final float centerOfHeight = circleCanvasView.getHeight() / 2;
                final float x = event.getRawX() - (DEVICE_WIDTH / 2);
                int[] location = new int[2];
                circleCanvasView.getLocationOnScreen(location);
                final float y = ((centerOfHeight) + location[1]) - event.getRawY();
                double touchedAngle = Tool.pieTo360Angle(Math.toDegrees(Math.atan2(x, y)));

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_MOVE:
                        mCurrOfAngle_second = touchedAngle;
                        Log.d("angle", String.valueOf(touchedAngle));
                        rotateView(iv_endHand, (float) touchedAngle);
                        tv_startTime.setText(getAMOrPM(Tool.angleToHour(Math.min(mCurrOfAngle_first, mCurrOfAngle_second))) + " " + Tool.toHourOf12(Tool.angleToHour(Math.min(mCurrOfAngle_first, mCurrOfAngle_second))) +  " : " +
                                Tool.angleToMinute(Math.min(mCurrOfAngle_first, mCurrOfAngle_second)));
                        tv_endTime.setText(getAMOrPM(Tool.angleToHour(Math.max(mCurrOfAngle_first, mCurrOfAngle_second))) + " " + Tool.toHourOf12(Tool.angleToHour(Math.max(mCurrOfAngle_first, mCurrOfAngle_second))) +  " : " +
                                Tool.angleToMinute(Math.max(mCurrOfAngle_first, mCurrOfAngle_second)));
                        break;
                }
                return true;
            }
        });

        circleCanvasView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final float centerOfHeight = circleCanvasView.getHeight() / 2;
                final float x = event.getRawX() - (DEVICE_WIDTH / 2);
                int[] location = new int[2];
                circleCanvasView.getLocationOnScreen(location);
                final float y = ((centerOfHeight) + location[1]) - event.getRawY();
                double touchedAngle = Tool.pieTo360Angle(Math.toDegrees(Math.atan2(x, y)));

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;

                    case MotionEvent.ACTION_UP:
                        for(PlanInfo p : circleCanvasView.getTodoOrDoneList()) {
                            if(p.getStart() >= touchedAngle && p.getEndAngle() < touchedAngle) {
                                PlanInfoDialog planInfoDialog = new PlanInfoDialog(mContext,p, mainActivity.refreshFragmentInst);
                                planInfoDialog.callDialog();
                            }
                        }

                }

                return true;
            }
        });
    }

    private String getEditText_planName() {
        return et_planName.getEditText().getText().toString();
    }

    /**
     * 뷰를 회전시킴
     * @param view 회전시킬 뷰
     * @param angle 회전시킬 각도
     */
    public void rotateView(View view, float angle) {
        if(angle >= 180) {
            view.setRotation(angle - 180);
        } else {
            view.setRotation(angle + 180);
        }

    }


}