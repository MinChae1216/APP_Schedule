package com.example.app_schedule.statisticFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.app_schedule.PlanInfo;
import com.example.app_schedule.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.MainActivity.SELECTED_DATE;

public class StatisticFragment extends Fragment {
    @BindView(R.id.sp_time)
    Spinner sp_time;

    private Context mContext;
    private FragmentManager fragmentManager;
    private PieChartFragment pieChartFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_statistic, container,false);
        bindViews(view);
        setFragment();
        //setSpinner();
        return view;
    }

    private void bindViews(View view) {
        ButterKnife.bind(this,view);
    }

    private void setFragment() {
        pieChartFragment = new PieChartFragment(7);
        fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_statisticFragment, pieChartFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(mContext,R.array.my_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_time.setAdapter(arrayAdapter);
        sp_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:  pieChartFragment.setLastDayBefore(1);
                    break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
