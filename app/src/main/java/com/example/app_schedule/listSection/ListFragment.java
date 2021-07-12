package com.example.app_schedule.listSection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_schedule.MainActivity;
import com.example.app_schedule.R;
import com.example.app_schedule.SettingDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.app_schedule.MainActivity.SELECTED_DATE;
import static com.example.app_schedule.MainActivity.appDataBase;

@SuppressLint("NonConstantResourceId")
public class ListFragment extends Fragment {
    private Context mContext;
    public String todoOrDone;
    public ItemTouchHelper.SimpleCallback simpleCallback;
    private ListFragmentAdapter listFragmentAdapter;
    private final MainActivity mainActivity;

    @BindView(R.id.rv_listFragment)
    RecyclerView rv_listFragment;
    @BindView(R.id.tv_todo_listFragment)
    TextView tv_todo;
    @BindView(R.id.tv_done_listFragment)
    TextView tv_done;
    @BindView(R.id.bt_floating)
    FloatingActionButton floatingActionButton;



    public ListFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.todoOrDone = "todo";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View v = inflater.inflate(R.layout.fragment_list, container,false);
        bindViews(v);
        setRecyclerView();
        setOnClickListener();
        return v;
    }

    private void bindViews(View view) {
        ButterKnife.bind(this,view);
    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv_listFragment.setLayoutManager(linearLayoutManager);
        listFragmentAdapter = new ListFragmentAdapter(appDataBase.dayInfoDao().getTodoOrDoneList(SELECTED_DATE.getId(), todoOrDone));
        rv_listFragment.setAdapter(listFragmentAdapter);
        setItemTouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_listFragment);
    }

    public void refresh() {
        if(listFragmentAdapter != null) {
            listFragmentAdapter.setPlanInfoList(appDataBase.dayInfoDao().getTodoOrDoneList(SELECTED_DATE.getId(), todoOrDone));
        }
    }

    private void setOnClickListener() {
        tv_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_todo.setBackgroundResource(R.drawable.background_filled_blue);
                tv_todo.setTextColor(Color.parseColor("#FFFFFF"));
                tv_done.setBackgroundResource(R.drawable.background_filled_radius_white);
                tv_done.setTextColor(Color.parseColor("#000000"));
                setListFragmentPlanList("todo");
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_done.setBackgroundResource(R.drawable.background_filled_blue);
                tv_done.setTextColor(Color.parseColor("#FFFFFF"));
                tv_todo.setBackgroundResource(R.drawable.background_filled_radius_white);
                tv_todo.setTextColor(Color.parseColor("#000000"));
                setListFragmentPlanList("done");
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingDialog settingDialog = new SettingDialog(90,150, todoOrDone, mainActivity.refreshFragmentInst, "", mContext);
                settingDialog.callDialog();
            }
        });
    }

    private void setItemTouchHelper() {
        simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if(todoOrDone.equals("todo")) {
                    SELECTED_DATE.getTodo().remove(position);
                } else {
                    SELECTED_DATE.getDone().remove(position);
                }
                listFragmentAdapter.notifyDataSetChanged();
            }
        };
    }

    private void setListFragmentPlanList(String todoOrDone) {
        if(todoOrDone.equals("todo")) {
            this.todoOrDone = "todo";
        } else {
            this.todoOrDone = "done";
        }
        listFragmentAdapter.setPlanInfoList(appDataBase.dayInfoDao().getTodoOrDoneList(SELECTED_DATE.getId(),todoOrDone));
    }




}
