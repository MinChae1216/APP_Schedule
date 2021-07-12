package com.example.app_schedule.listSection;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_schedule.PlanInfo;
import com.example.app_schedule.R;
import com.example.app_schedule.Tool;
import com.ramotion.foldingcell.FoldingCell;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.SettingDialog.cancelAlarm;
import static com.example.app_schedule.SettingDialog.setAlarm;
import static com.example.app_schedule.Tool.add0SmallerThan10;
import static com.example.app_schedule.Tool.angleToHour;
import static com.example.app_schedule.Tool.angleToMinute;

//todo 여기는 리스트 프래그먼트 어댑터고 폴딩셀로 클릭하면 펴지는 식으로 뷰를 완성시킬거임 지금 레이아웃보면 대충 틀 잡아놨고 텍스트뷰 onBindViewHolder설정좀 해놓고 contentView 안에 꾸미면 될듯 그 안에는 메모 같은걸 할 수 있게 .
public class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentAdapter.ViewHolder> {
    private Context mContext;
    private List<PlanInfo> planInfoList;

    public ListFragmentAdapter(List<PlanInfo> dayInfoList) {
        this.planInfoList = dayInfoList;
    }

    public void setPlanInfoList(List<PlanInfo> planInfoList) {
        this.planInfoList = planInfoList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_foldingcell, parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_time.setText(angleToHour(planInfoList.get(position).getStart()) + Tool.angleToMinute(planInfoList.get(position).getStart())
                + " ~ " + angleToHour(planInfoList.get(position).getEndAngle()) + Tool.angleToMinute(planInfoList.get(position).getEndAngle()));
        holder.tv_planName.setText(planInfoList.get(position).getPlanName());
        holder.tv_planMemo.setText(planInfoList.get(position).getPlanMemo());
    }

    public List<PlanInfo> getPlanInfoList() {
        return planInfoList;
    }

    @Override
    public int getItemCount() {
        return planInfoList.size();
    }

    @SuppressLint("NonConstantResourceId")
    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.folding_cell)
        FoldingCell foldingCell;
        @BindView(R.id.tv_time_listFragment)
        TextView tv_time;
        @BindView(R.id.iv_alarmButton_listFragment)
        ImageView tv_alarmButton;
        @BindView(R.id.tv_planName_listFragment)
        TextView tv_planName;
        @BindView(R.id.tv_planExplain)
        TextView tv_planMemo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            foldingCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    foldingCell.toggle(true);
                }
            });
            tv_alarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tv_alarmButton.getBackground() == ContextCompat.getDrawable(mContext,R.drawable.alarm)) {
                        setAlarm(String.valueOf(planInfoList.get(getAdapterPosition()).getPlanId())
                                ,add0SmallerThan10(angleToHour(planInfoList.get(getAdapterPosition()).getStart())).concat(add0SmallerThan10(angleToMinute(planInfoList.get(getAdapterPosition()).getStart())))
                                ,planInfoList.get(getAdapterPosition()).getPlanName(), mContext);
                        tv_alarmButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.alarm_green));
                    } else {
                        tv_alarmButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.alarm));
                        cancelAlarm(String.valueOf(planInfoList.get(getAdapterPosition()).getPlanId())
                                ,add0SmallerThan10(angleToHour(planInfoList.get(getAdapterPosition()).getStart())).concat(add0SmallerThan10(angleToMinute(planInfoList.get(getAdapterPosition()).getStart())))
                                ,mContext);
                    }
                }
            });

        }
    }
}
