package com.example.app_schedule;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationHelper extends BroadcastReceiver {
    public static final String CHANNEL_ID = "channel1ID";
    public static final String CHANNEL_NAME = "channel 1 ";

    @Override
    public void onReceive(Context context, Intent intent) {
       NotificationManager mManager;
       NotificationCompat.Builder builder;
       AlarmManager alarmManager;
       PendingIntent pendingIntent_alarm;

        alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        String action = intent.getAction();
        switch (action) {
            case "알람 설정" :
                builder = null;
                mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    mManager.createNotificationChannel(
                            new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                    );
                    builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                } else {
                    builder = new NotificationCompat.Builder(context);
                }

                Intent activityIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,0, activityIntent ,PendingIntent.FLAG_CANCEL_CURRENT);
                //알림창 클릭 시 activity 화면 부름

                //알림창 제목
                builder.setContentTitle("알람");
                builder.setContentText(intent.getStringExtra("contentText"));
                //알림창 아이콘
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                //알림창 터치시 자동 삭제
                builder.setAutoCancel(true);

                builder.setContentIntent(pendingIntent);

                Notification notification = builder.build();
                mManager.notify(1, notification);

                pendingIntent_alarm = PendingIntent.getBroadcast(context,intent.getIntExtra("alarmId",1), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent_alarm);
                break;

            case "알람 취소" :
                pendingIntent_alarm = PendingIntent.getBroadcast(context,intent.getIntExtra("alarmId",1), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingIntent_alarm);
                break;

            default:
                break;
        }

    }
}
