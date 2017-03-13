package es.deusto.trekkingaventura.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.utilities.ExcursionNotificationManager;

/**
 * Created by salgu on 24/02/2017.
 */

public class NotificationService {

    private static int TIME = 900;
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

    public void setExcursion(Excursion excursion) {
        this.excursion = excursion;
    }

    public void startAlarm(){
        Log.i("INFO_NOT", "Se inicia la alarma desde NotificationService");
        (new ExcursionNotificationManager(context)).deleteFile();
        (new ExcursionNotificationManager(context)).saveExcursionNotification(excursion);

        Intent intent = new Intent(context, AppReceiver.class);
        instance.sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        instance.alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, TIME * 1000, 10, sender);
    }

    public void stopAlarm(){
        if(instance.sender != null) {
            Log.i("INFO_NOT", "Se para la alarma desde NotificationService");
            (new ExcursionNotificationManager(context)).deleteFile();

            instance.sender.cancel();
            instance.alarmManager.cancel(sender);
        }
    }
}
