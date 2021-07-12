package com.example.app_schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.MainActivity.DEVICE_WIDTH;
import static com.example.app_schedule.MainActivity.changeSELECTED_DATE;
import static com.example.app_schedule.MainActivity.SELECTED_DATE;

public class SelectDayAdapter extends RecyclerView.Adapter<SelectDayAdapter.ViewHolder> {
    private List<DayInfo> mList_oneMonth;
    private final MainActivity.RefreshFragmentInst refreshFragmentInst;
    private final SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    public SelectDayAdapter(List<DayInfo> list, MainActivity.RefreshFragmentInst refreshFragmentInst) {
        this.mList_oneMonth = list;
        this.refreshFragmentInst = refreshFragmentInst;
        setBooleanArray_selectedDATEAsTrue();
    }

    public void setList_oneMonth(List<DayInfo> mList_oneMonth) {
        this.mList_oneMonth = mList_oneMonth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_selectday, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_day.setText(setTextColor(mList_oneMonth.get(position).getDay() + "  " + mList_oneMonth.get(position).getDayOfWeek(), mList_oneMonth.get(position).getDayOfWeek()));
        if ( sparseBooleanArray.get(position,false) ){
            holder.tv_day.setBackgroundColor(Color.parseColor("#cfd8dc"));
        } else {
            holder.tv_day.setBackgroundColor(Color.parseColor("#EFFAFF"));
        }
    }

    private SpannableStringBuilder setTextColor(String str, String dayOfWeek) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        switch (dayOfWeek) {
            case "토":
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#0067A3")), str.length() - 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "일":
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), str.length() - 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default :
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#616161")), str.length() - 1, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
        }
        return ssb;
    }

    @Override
    public int getItemCount() {
        return mList_oneMonth.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_day_selectDay)
        TextView tv_day;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tv_day.setWidth(DEVICE_WIDTH / 7);
            tv_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeSELECTED_DATE(mList_oneMonth.get(getAdapterPosition()).getYear(),
                            mList_oneMonth.get(getAdapterPosition()).getMonth(),
                            mList_oneMonth.get(getAdapterPosition()).getDay());
                    if (!sparseBooleanArray.get(getAdapterPosition(),false)){
                        sparseBooleanArray.clear();
                        sparseBooleanArray.put(getAdapterPosition(), true);
                    }
                    refreshFragmentInst.refreshCircleFragment();
                    refreshFragmentInst.refreshListFragment();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setBooleanArray_selectedDATEAsTrue() {
        sparseBooleanArray.clear();
        sparseBooleanArray.put(SELECTED_DATE.getDay()-1, true);
    }
}
