package es.deusto.trekkingaventura.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.StringTokenizer;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.Weather;
import es.deusto.trekkingaventura.utilities.JSONWeatherParser;
import es.deusto.trekkingaventura.utilities.WeatherHttpClient;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ExcursionWidgetConfigureActivity ExcursionWidgetConfigureActivity}
 */
public class ExcursionWidget extends AppWidgetProvider {

    static RemoteViews views;
    static int widgetId;
    static Context cntx;
    static AppWidgetManager widgetManager;

    static ArrayList<Excursion> arrExcursiones;
    static Excursion excursionWidget;
    static PendingIntent pendingIntentWidget;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        widgetId = appWidgetId;
        cntx = context;
        widgetManager = appWidgetManager;

        // Como todavía no tenemos persistencia de los datos, obtenemos de esta forma las excursiones de prueba de la App.
        createExcursionList();

        String selectedExcursionName = ExcursionWidgetConfigureActivity.loadSelectedExcursionName(cntx, widgetId);

        excursionWidget = getExcursionByName(selectedExcursionName);

        if (excursionWidget != null) {
            // Se realiza la petición a la API del tiempo.
            String city = excursionWidget.getLocation();
            String cityWithoutSpaces = city.replace(" ", "%20");
            JSONWeatherTask task = new JSONWeatherTask();
            if (cityWithoutSpaces.contains("%20")) {
                task.execute(new String[]{cityWithoutSpaces});
            } else {
                task.execute(new String[]{city});
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            ExcursionWidgetConfigureActivity.deleteSelectedExcursionName(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static Excursion getExcursionByName(String name) {
        for (Excursion e : arrExcursiones) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static void createExcursionList() {

        // Creamos tres excursiones de prueba y las metemos al array de Excursiones.
        Excursion exc1 = new Excursion(1,"Ruta del Cares", "Un sitio espectacular con unas vistas impresionantes. Ideal para ir con la familia y para sacar fotos de los acantilados.", "Medio", 12,"Arenas de Cabrales",Float.parseFloat("43.2551652"),Float.parseFloat("-4.8366377"),"/storage/emulated/0/Download/tour-ruta-del-cares.jpg");
        Excursion exc2 = new Excursion(2,"Ventana Relux", "Unas vistas impresionantes desde la ventana. Una caída libre espectacular que merece ser fotografiada. Ideal para la familia.", "Facil", 2.7,"Karrantza Harana",Float.parseFloat("43.2499237"),Float.parseFloat("-3.4108149"),"/storage/emulated/0/Download/408152_211118512316132_100002539549108_438902_18443862_n.jpg");
        Excursion exc3 = new Excursion(3,"Faro del Caballo", "Excursión muy bonita para ver todos los acantilados del monte Buciero de Santoña. Ideal para ir en pareja y para pasar el día.", "Medio", 12,"Santoña",Float.parseFloat("43.4514626"),Float.parseFloat("-3.4256904"),"/storage/emulated/0/Download/186669-944-517.jpg");
        Excursion exc4 = new Excursion(4,"Gorbea", "Subida preciosa a uno de los montes más característicos de Bizkaia. Recorrido un poco duro pero el paisaje merece la pena.", "Dificil", 12,"Areatza",Float.parseFloat("43.0350000"),Float.parseFloat("-2.7798800"),"/storage/emulated/0/Download/parque_natural_gorbea.jpg");

        arrExcursiones = new ArrayList<Excursion>();
        arrExcursiones.add(exc1);
        arrExcursiones.add(exc2);
        arrExcursiones.add(exc3);
        arrExcursiones.add(exc4);
    }

    private static class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

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
            Intent intent = new Intent(cntx, MainActivity.class);
            intent.putExtra(MainActivity.ARG_NOTIFICATION_EXC, excursionWidget);
            intent.setAction("OPEN_EXC_FRAGMENT");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            pendingIntentWidget = PendingIntent.getActivity(cntx, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Construct the RemoteViews object
            views = new RemoteViews(cntx.getPackageName(), R.layout.excursion_widget);

            views.setTextViewText(R.id.excNameWid, excursionWidget.getName());
            views.setTextViewText(R.id.excLocationWid, excursionWidget.getLocation());
            views.setTextViewText(R.id.excDistanceWid, Double.toString(excursionWidget.getTravelDistance()) + " km");

            switch (excursionWidget.getLevel()) {
                case "Facil":
                    views.setImageViewResource(R.id.excLevelWid, R.drawable.facil);
                    break;
                case "Medio":
                    views.setImageViewResource(R.id.excLevelWid, R.drawable.medio);
                    break;
                case "Dificil":
                    views.setImageViewResource(R.id.excLevelWid, R.drawable.dificil);
                    break;
            }

            if (weather != null) {
                if (weather.currentCondition.getIcon() != null) {
                    switch (weather.currentCondition.getIcon()) {
                        case "01d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img01d);
                            break;
                        case "01n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img01n);
                            break;
                        case "02d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img02d);
                            break;
                        case "02n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img02n);
                            break;
                        case "03d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img03d);
                            break;
                        case "03n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img03n);
                            break;
                        case "04d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img04d);
                            break;
                        case "04n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img04n);
                            break;
                        case "09d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img09d);
                            break;
                        case "09n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img09n);
                            break;
                        case "10d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img10d);
                            break;
                        case "10n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img10n);
                            break;
                        case "11d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img11d);
                            break;
                        case "11n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img11n);
                            break;
                        case "13d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img13d);
                            break;
                        case "13n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img13n);
                            break;
                        case "50d":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img50d);
                            break;
                        case "50n":
                            views.setImageViewResource(R.id.condIconWid, R.drawable.img50n);
                            break;
                        default:
                            views.setImageViewResource(R.id.condIconWid, R.drawable.imgnotavailable);
                            break;
                    }
                } else {
                    views.setImageViewResource(R.id.condIconWid, R.drawable.imgnotavailable);
                }
            } else {
                views.setImageViewResource(R.id.condIconWid, R.drawable.imgnotavailable);
            }

            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntentWidget);

            // Instruct the widget manager to update the widget
            widgetManager.updateAppWidget(widgetId, views);
        }
    }
}

