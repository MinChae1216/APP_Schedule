package com.example.app_schedule.statisticFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_schedule.PlanInfo;
import com.example.app_schedule.R;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;


import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;


import static com.example.app_schedule.MainActivity.appDataBase;
import static com.example.app_schedule.Tool.getNow;
import static com.example.app_schedule.Tool.minusDayFromTheNow;

public class PieChartFragment extends Fragment {
    private int lastDayBefore;
    ArrayList<PieEntry> entries = new ArrayList<>();

    @BindView(R.id.mp_pieChart)
    PieChart pieChart;

    public PieChartFragment(int lastDayBefore) {
        this.lastDayBefore = lastDayBefore;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_piechart, container, false);
        bindViews(view);
        setPieChart();
        setData();
        return view;
    }

    public void setLastDayBefore(int lastDayBefore) {
        this.lastDayBefore = lastDayBefore;
        setData();
    }

    private void bindViews(View view) {
        ButterKnife.bind(this,view);
    }

    private void setPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
    }

    private float getAllTimeToDone(double start, double end) {
        return (float) (end - start);
    }

    private void setData() {
        switch (lastDayBefore) {
            case 1 :
                for(PlanInfo p : appDataBase.dayInfoDao().getTodoOrDoneList(getNow(),"done")) {
                    entries.add(new PieEntry((getAllTimeToDone(p.getStart(),p.getEndAngle())), p.getPlanName()));
                }
                break;
            case 7:
                for(int i=0; i<7; i++) {
                    for(PlanInfo p : appDataBase.dayInfoDao().getTodoOrDoneList(minusDayFromTheNow(i),"done")) {
                        entries.add(new PieEntry((getAllTimeToDone(p.getStart(),p.getEndAngle())), p.getPlanName()));
                    }
                }
                break;
            case 30:
                for(int i=0; i<30; i++) {
                    for(PlanInfo p : appDataBase.dayInfoDao().getTodoOrDoneList(minusDayFromTheNow(i),"done")) {
                        entries.add(new PieEntry((getAllTimeToDone(p.getStart(),p.getEndAngle())), p.getPlanName()));
                    }
                }
        }


        PieDataSet dataSet = new PieDataSet(entries,"일정 퍼센트");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));

        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();



        for (int c : ColorTemplate.VORDIPLOM_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)

            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)

            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());



        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        // undo all highlights

        pieChart.highlightValues(null);
        pieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {



        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");

        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);

        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);

        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);

        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);

        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);

        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);

        return s;

    }
}
