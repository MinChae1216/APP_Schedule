package com.example.app_schedule.circleSection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.app_schedule.PlanInfo;

import java.util.ArrayList;
import java.util.List;

public class CircleCanvasView extends View {
    private List<PlanInfo> todoOrDoneList = new ArrayList<>();
    private final List<String> color = new ArrayList<>();
    private Paint draw_arcPnt = new Paint();
    private Paint draw_arcStroke = new Paint();
    private Paint draw_text = new Paint();

    public CircleCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        color.add("#B57FB3");
        color.add("#D18063");
        color.add("#AEDDEF");
        color.add("#D5CB8E");
        color.add("#C9CBE0");
    }

    @Override
    protected void onDraw(Canvas canvas) {// 여기에 부채꼴을 그려야함
        super.onDraw(canvas);
        RectF rect = new RectF();//정사각형
        rect.set(5, 5, this.getWidth() - 5 , this.getHeight() -5);//정사각형 양 모서리 좌표

        setArcPnt();
        setArcStrokePnt();
        setTextPnt();

        if(!todoOrDoneList.isEmpty()) {
            for(PlanInfo p : todoOrDoneList) {
                draw_arcPnt.setColor(Color.parseColor(p.getColor()));//부채꼴 색깔 정하기
                canvas.drawArc(rect, (float) convertToArcAngle(p.getStart()), (float) angleToMove(p.getStart(),p.getEndAngle()), true, draw_arcPnt); //부채꼴 그리기
                canvas.drawArc(rect, (float) convertToArcAngle(p.getStart()), (float) angleToMove(p.getStart(),p.getEndAngle()), true, draw_arcStroke);//부채꼴 가장자리 그리기

                canvas.rotate(getMiddleAngleToRotate(p.getStart(),p.getEndAngle()),this.getWidth() / 2, this.getHeight() / 2); // 부채꼴의 중간각도를 구하기 2,3 사분면하고 1,4 사분면 다르게
                float middle = (float) ((p.getStart() + p.getEndAngle()) / 2);
                if(middle <= 180) {
                    canvas.drawText(p.getPlanName(), (float) (this.getWidth() / 1.4),this.getHeight() / 2, draw_text); //중각각도의 따라 왼쪽에다 놓고 돌리기, 오른쪽에다 놓고 돌리기
                } else {
                    canvas.drawText(p.getPlanName(), (float) (this.getWidth() / 7.5),this.getHeight() / 2, draw_text); //중각각도의 따라 왼쪽에다 놓고 돌리기, 오른쪽에다 놓고 돌리기
                }
                canvas.rotate(-getMiddleAngleToRotate(p.getStart(),p.getEndAngle()), this.getWidth() / 2, this.getHeight() / 2);
            }
        }
        
    }

    private void setArcPnt() {
        draw_arcPnt = new Paint();
        draw_arcPnt.setStrokeWidth(9f);
        draw_arcPnt.setStyle(Paint.Style.STROKE);
        //canvas.drawArc(rect,0 ,360, true, draw_arcPnt);
        draw_arcPnt.setStyle(Paint.Style.FILL);
    }

    private void setArcStrokePnt() {
        draw_arcStroke = new Paint();
        draw_arcStroke.setColor(Color.parseColor("#000000"));
        draw_arcStroke.setStyle(Paint.Style.STROKE);
        draw_arcStroke.setStrokeWidth(7f);
    }
    private void setTextPnt() {
        draw_text = new Paint();
        draw_text.setColor(Color.parseColor("#FFFFFF"));
        draw_text.setStyle(Paint.Style.FILL);
        draw_text.setTextSize(50);
    }

    public void setTodoOrDoneList(List<PlanInfo> todoOrDoneList) {
        this.todoOrDoneList = todoOrDoneList;
        this.invalidate();
    }

    public List<PlanInfo> getTodoOrDoneList() {
        return todoOrDoneList;
    }

    private double convertToArcAngle(double angle) {
        if(angle >= 90) {
            return angle - 90;
        } else {
            return angle + 270;
        }
    }

    private double angleToMove(double start, double end) {
        if(start <= 360 && end < start) {
            return (360 - start) + end;
        } else {
            return end - start;
        }
    }

    private float getMiddleAngleToRotate(double start, double end) {
        float middle = (float) ((start + end) / 2);
        if(middle <= 180) {
            return middle - 87;
        } else {
            return middle - 275;
        }
    }

    private float getProperTextSize(double start, double end) {
        double size = end - start;
        if (size >= 0 && size < 30) {
            return 20;
        } else {
            return 40;
        }
    }

}
