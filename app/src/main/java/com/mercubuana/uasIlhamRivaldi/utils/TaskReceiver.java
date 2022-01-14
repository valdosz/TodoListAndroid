package com.mercubuana.uasIlhamRivaldi.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.mercubuana.uasIlhamRivaldi.R;
import com.mercubuana.uasIlhamRivaldi.helper.Helper;

import java.util.Calendar;

public class TaskReceiver extends BroadcastReceiver {

    private static final String E_MESSAGE = "message";
    public static final String E_TYPE = "type";
    private static final String E_DATE = "date";
    private static final String E_TIME = "time";
    private static final String E_NOTIF = "E_NOTIF";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(E_MESSAGE);
        String title = intent.getStringExtra(E_TYPE);
        title = title != null ? title: E_TYPE;
        int notifId = intent.getIntExtra(E_NOTIF, 1);

        if (message != null) showAlarmNotif(context, title, message, notifId);

        showToast(context, "Ada Notif Terbaru", message);

    }

    private void showAlarmNotif(Context context, String title, String message, int notifId) {
        String CHANNEL_ID = "ChannelID";
        String CHANNEL_NAME = "Alarm To Do";
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notif);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.todoicon)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(soundUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            channel.setSound(soundUri, audioAttributes);
            builder.setChannelId(CHANNEL_ID);
            if (notificationManagerCompat != null) {
                notificationManagerCompat.createNotificationChannel(channel);
            }
        }
        Notification notification = builder.build();
        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, notification);
        }
    }

    private void showToast(Context context, String title, String message) {
        Toast.makeText(context, title+ " "+message+"", Toast.LENGTH_LONG).show();
    }

    public void setAlarmNotif(Context context, String type, String date, String time, String title, String message) {
        String DATE_FORMAT = "yyyy-MM-dd";
        String TIME_FORMAT = "HH:mm";
        if (Helper.isDateInvalid(date, DATE_FORMAT) || Helper.isDateInvalid(time, TIME_FORMAT)) return;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TaskReceiver.class);
        intent.putExtra("ONETIME", title);
        intent.putExtra(E_MESSAGE, message);
        intent.putExtra(E_TYPE, title);
        intent.putExtra(E_DATE, date);
        intent.putExtra(E_TIME, time);
        Log.e("ONE TIME", date + " " + time);
        String[] dateArray = date.split("-");
        String[] timeArray = time.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);
        int notifId = Integer.parseInt(dateArray[0]) + Integer.parseInt(dateArray[1]) - 1 + Integer.parseInt(dateArray[2]) + Integer.parseInt(timeArray[0]) + Integer.parseInt(timeArray[1]);
        intent.putExtra(E_NOTIF, notifId);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        Toast.makeText(context, "One time alarm set up", Toast.LENGTH_SHORT).show();
    }
}
