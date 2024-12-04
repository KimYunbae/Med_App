package com.android.medicinenotification.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.medicinenotification.data.MedicineModel;

import java.util.Calendar;

public class NotificationSchedule {

    private Context ctx;
    private String notificationID = "";
    private String channelID = "";


    public NotificationSchedule(Context context) {
        this.ctx = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    public void scheduleNotification(MedicineModel medicineModel) {
        Intent intent = new Intent(ctx, NotificationReceiver.class);
        String notificationTitle = medicineModel.getMedicineName();
        String notificationMessage = "약을 복용하실 시간입니다.";

        // 알림을 여러개 설정하기 위해서 NotificationID를 다르게 설정
        // notification ID 는 Index의 뒤에서 6자리까지만 사용
        notificationID = medicineModel.getIndex();
        channelID = "medicineNotification";

        createNotificationChannel(channelID);
        intent.putExtra("title", notificationTitle);
        intent.putExtra("message", notificationMessage);
        intent.putExtra("channelID", channelID);
        intent.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                1001,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        long time = getTime(medicineModel.getDoseDate() + "/" + medicineModel.getDoseTime());
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelID) {
        String name = "notif channel";
        String desc = "A Description of the Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(desc);
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void cancelNotification(int notificationID) {
        Intent intent = new Intent(ctx, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                notificationID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Cancel the alarm
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

//        // Delete the notification channel
        deleteNotificationChannel(notificationID);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void deleteNotificationChannel(int notificationID) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.deleteNotificationChannel(notificationID + "");
        }
    }

    private long getTime(String date) {
        // date = "2021-07-01/16:10"
        Log.i("##INFO", "getTime(): date = " + date);
        String dateString = date.split("/")[0];
        String timeString = date.split("/")[1];

        String[] dateArray = dateString.split("-");
        int year = Integer.parseInt(dateArray[0]);
        int month = Integer.parseInt(dateArray[1]);
        int day = Integer.parseInt(dateArray[2]);

        String[] timeArray = timeString.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int minute = Integer.parseInt(timeArray[1]);

        int newMont = month - 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, newMont); // 월은 0부터 시작하므로 1을 빼야 합니다.
        cal.set(Calendar.DAY_OF_MONTH, day);

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);



        return cal.getTimeInMillis();
    }
}
