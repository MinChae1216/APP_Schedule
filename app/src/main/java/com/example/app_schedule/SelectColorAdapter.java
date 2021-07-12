package com.example.app_schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;



//todo 이거 색깔 고르는 어댑터 하기
public class SelectColorAdapter extends RecyclerView.Adapter<SelectColorAdapter.ViewHolder> {
    public List<String> colorList = new ArrayList<>();
    private final SettingDialog settingDialog;
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    public SelectColorAdapter(SettingDialog settingDialog) {
        initColorList();
        this.settingDialog = settingDialog;
        sparseBooleanArray.put(0,true);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_color, parent, false);
        return new ViewHolder(view);
    }

    //todo 다이얼로그를 켰을때 첫번째걸로 설정이 되있게 다른 색깔을 누르면 다른색깔로 원이 바뀜.
    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectColorAdapter.ViewHolder holder, int position) {
        holder.iv_color.setBackgroundColor(Color.parseColor(colorList.get(position)));
        if ( sparseBooleanArray.get(position, false) ){
            holder.iv_colorSelected.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            holder.iv_colorSelected.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public void initColorList() {
        colorList.add("#FF5675");
        colorList.add("#FF9E7D");
        colorList.add("#2ABCB4");
        colorList.add("#369F36");
        colorList.add("#0A9696");
        colorList.add("#CD853F");
        colorList.add("#6495ED");
        colorList.add("#FFA500");
        colorList.add("#960a96");
        colorList.add("#828282");
        colorList.add("#D27328");
        colorList.add("#2828CD");
    }
       public class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.circleIv_color)
        CircleImageView iv_color;
        @BindView(R.id.circleIv_colorSelected)
        CircleImageView iv_colorSelected;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            iv_color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingDialog.detailSetting_color = colorList.get(getAdapterPosition());
                    if ( sparseBooleanArray.get(getAdapterPosition(), false) ){
                    } else {
                        sparseBooleanArray.clear();
                        sparseBooleanArray.put(getAdapterPosition(), true);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}
