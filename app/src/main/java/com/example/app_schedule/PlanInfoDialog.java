package com.example.app_schedule;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.MainActivity.SELECTED_DATE;
import static com.example.app_schedule.MainActivity.appDataBase;
import static com.example.app_schedule.Tool.angleToHour;
import static com.example.app_schedule.Tool.angleToMinute;
import static com.example.app_schedule.Tool.timeToAngle;

@SuppressLint("NonConstantResourceId")
public class PlanInfoDialog {//todo 여기서 시간수정 제목수정 등등을 작업할 수 있게 하고 삭제할시 selectedDATE에서 삭제할 수 있도록 시간변경은 그냥 planInfo내에서 start시간만 바꾸면 되니까 어려울거 없음.
    private final Context mContext;
    private Dialog mDialog;
    private PlanInfo mPlanInfo;
    private final MainActivity.RefreshFragmentInst refreshFragmentInst;


    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.tv_content_planInfoDialog)
    TextView tv_content;
    @BindView(R.id.tv_title_planInfoDialog)
    TextView tv_title;
    @BindView(R.id.tv_delete_planInfoDialog)
    TextView tv_delete;
    @BindView(R.id.tv_setting_planInfoDialog)
    TextView tv_setting;
    @BindView(R.id.tv_ok)
    TextView tv_ok;

    public PlanInfoDialog(Context context, PlanInfo planInfo, MainActivity.RefreshFragmentInst refreshFragmentInst) {
        this.mContext = context;
        this.mPlanInfo = planInfo;
        this.refreshFragmentInst = refreshFragmentInst;
    }

    public void callDialog() {
        setDialog();
        bindViews();
        setTextView();
        setOnClickListener();
    }

    private void setDialog() {
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_planinfo);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        Window window = mDialog.getWindow();
        window.setAttributes(lp);
    }

    private void bindViews() {
        ButterKnife.bind(this,mDialog);
    }

    private void setTextView() {
        tv_title.setText(mPlanInfo.getPlanName());
        tv_content.setText(mPlanInfo.getPlanMemo());
    }

    private void setOnClickListener() {
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingDialog settingDialog = new SettingDialog(mPlanInfo.getStart(), mPlanInfo.getEndAngle(), mPlanInfo.getTodoOrDone(), refreshFragmentInst, mPlanInfo.getPlanName(), mContext);
                settingDialog.callDialog();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2021-07-07 circleCanvasView 에서 list 를 가져왔으니 이게 삭제되면 그것도 맞게 초기화를 해줘야함
                appDataBase.dayInfoDao().deletePlanInfo(mPlanInfo.getPlanId());
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2021-07-07 이건 todo 에만 있게 해야하는데 이걸 누를시 done 에도 추가가 되게
                appDataBase.dayInfoDao().insertPlanInfo(new PlanInfo(mPlanInfo.getPlanId(), mPlanInfo.getDayId(), "done", mPlanInfo.getStart(), mPlanInfo.getEndAngle(), mPlanInfo.getPlanName()
                , mPlanInfo.getPlanMemo(), mPlanInfo.getColor()));
            }
        });
    }

}
