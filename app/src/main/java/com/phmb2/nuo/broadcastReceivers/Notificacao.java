package com.phmb2.nuo.broadcastReceivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.phmb2.nuo.R;
import com.phmb2.nuo.activities.MainActivity;
import com.phmb2.nuo.activities.SplashScreenActivity;
import com.phmb2.nuo.basicas.Foto;
import com.phmb2.nuo.bd.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by phmb2 on 24/07/17.
 */

public class Notificacao extends BroadcastReceiver
{
    public List<Foto> fotosNulas = new ArrayList<>();
    public List<String> fotosLines = new ArrayList<>();
    public int temFotoNaoConcluida;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String contentTitle = context.getResources().getString(R.string.app_name);
        String contentText = context.getResources().getString(R.string.texto_notificacao);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        c.setTimeInMillis(c.getTimeInMillis() + 24*60*60*1000);

        temFotoNaoConcluida = Database.getDatabase(context).temFotoInseridaNaoConcluida();

        if(temFotoNaoConcluida > 0)
        {
            fotosNulas = Database.getDatabase(context).listarFotosNaoConcluidas();

            int size = fotosNulas.size();

            for (int i = 0; i < size; i++) {
                fotosLines.add(context.getResources().getString(R.string.titulo_detalhes_foto) + " " + fotosNulas.get(i).getId());
            }

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(contentTitle);

            for (String s : fotosLines) {
                inboxStyle.addLine(s);
            }

            inboxStyle.setSummaryText(contentText);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_transparent)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.nuo))
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setNumber(size)
                    .setStyle(inboxStyle)
                    .setAutoCancel(true);

            Intent resultIntent = new Intent(context, MainActivity.class);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            Notification note = builder.build();
            note.defaults |= Notification.DEFAULT_VIBRATE;
            NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.notify(1, note);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent aIntent = new Intent(context, Notificacao.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 1, aIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);
        }
    }
}
