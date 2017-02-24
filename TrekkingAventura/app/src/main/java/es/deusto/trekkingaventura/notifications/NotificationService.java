package es.deusto.trekkingaventura.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.deusto.trekkingaventura.entities.Excursion;

/**
 * Created by salgu on 24/02/2017.
 */

public class NotificationService {
    private static int TIME = 15;
    private static NotificationService instance;
    private Context context;
    private AlarmManager alarmManager;
    private PendingIntent sender;
    private Excursion excursion;

    public static NotificationService getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationService();
            instance.context = context;
            instance.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return instance;
    }

    public Excursion getExcursion() {
        return excursion;
    }

    public void setExcursion(Excursion excursion) {
        this.excursion = excursion;
    }

    public void startAlarm(){
        Log.i("INFO_NOT", "Se inicia la alarma desde NotificationService");
        Intent intent = new Intent(context, AppReceiver.class);
        intent.putExtra(AppReceiver.ALARM_SERVICE, AppReceiver.ALARM_SERVICE_CONTENT);
        intent.putExtra(AppReceiver.ARG_EXCURSION, getExcursion());
        instance.sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        instance.alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TIME * 1000, 10, sender);
    }

    public void stopAlarm(){
        if(instance.sender != null) {
            Log.i("INFO_NOT", "Se para la alarma desde NotificationService");
            instance.sender.cancel();
            instance.alarmManager.cancel(sender);
        }
    }
}
