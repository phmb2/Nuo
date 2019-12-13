package com.phmb2.nuo.broadcastReceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by phmb2 on 24/07/17.
 */

//Classe que reinicializa o serviço de notificações
public class Boot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if(calendar.get(Calendar.HOUR_OF_DAY) >= 10)
        {
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24*60*60*1000);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 10);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent aIntent = new Intent(context, Notificacao.class);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, aIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.cancel(alarmIntent);

        Log.v("scheduled", SimpleDateFormat.getInstance().format(calendar.getTime()));
        alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}
