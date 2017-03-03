package es.deusto.trekkingaventura.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.Weather;
import es.deusto.trekkingaventura.weatherAPI.JSONWeatherParser;
import es.deusto.trekkingaventura.weatherAPI.WeatherHttpClient;

/**
 * Created by salgu on 24/02/2017.
 */

public class AppReceiver extends BroadcastReceiver {

    private Context context;

    public static final String ARG_EXCURSION = "arg_excursion";
    public static final String ALARM_SERVICE = "alarm_service";
    public static final String ALARM_SERVICE_CONTENT = "alarm_service_content";

    private Excursion excursion;
    private String currentCondition;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPref.getBoolean("notifications",false)) {
            if(intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                NotificationService.getInstance(context).startAlarm();
            } else if (intent.getStringExtra(ALARM_SERVICE).equals(ALARM_SERVICE_CONTENT)) {
                Log.i("INFO_NOT", "Se reciben las alarmas desde AppReceiver");
                excursion = (Excursion) intent.getSerializableExtra(ARG_EXCURSION);

                // Se realiza la petición a la API del tiempo.
                String city = excursion.getLocation();
                String cityWithoutSpaces = city.replace(" ", "%20");
                AppReceiver.JSONWeatherTask task = new AppReceiver.JSONWeatherTask();
                if (cityWithoutSpaces.contains("%20")) {
                    task.execute(new String[]{cityWithoutSpaces});
                } else {
                    task.execute(new String[]{city});
                }
            }
        }
    }

    private void sendNotification(Excursion excursion) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.ARG_NOTIFICATION_EXC, excursion);
        intent.setAction("OPEN_EXC_FRAGMENT");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification  = new NotificationCompat.Builder(this.context)
                .setContentTitle("Mal tiempo en '" + excursion.getLocation() + "'.")
                .setContentText("El tiempo actual en '" + excursion.getLocation() + "' es: '" + currentCondition + "'.")
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#A1CD5C"))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notificationicon);


        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(this.context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification.build());
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            if (data != null) {
                try {
                    weather = JSONWeatherParser.getWeather(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Error", "la API no ha devuelto ningun valor");
                weather = null;
            }

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            if (weather != null) {
                if ((weather).currentCondition.getCondition().contains("Rain") ||
                        (weather.currentCondition.getCondition().contains("rain")) ||
                        (weather.currentCondition.getCondition().equals("Thunderstorm"))) {
                    Log.i("INFO_NOT", "Se envía la notificación de mal tiempo desde AppReceiver");
                    currentCondition = weather.currentCondition.getCondition();
                    sendNotification(excursion);
                }
            }
        }
    }
}
