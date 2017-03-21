package es.deusto.trekkingaventura.widget;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.OpinionExtendida;
import es.deusto.trekkingaventura.entities.Weather;
import es.deusto.trekkingaventura.restDatabaseAPI.RestClientManager;
import es.deusto.trekkingaventura.restDatabaseAPI.RestJSONParserManager;
import es.deusto.trekkingaventura.weatherAPI.JSONWeatherParser;
import es.deusto.trekkingaventura.weatherAPI.WeatherHttpClient;

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

        InicializarExcursionesTask task = new InicializarExcursionesTask();
        task.execute(new String[] {Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)});
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

    private static class InicializarExcursionesTask extends AsyncTask<String, Void, ArrayList<OpinionExtendida>> {

        @Override
        protected ArrayList<OpinionExtendida> doInBackground(String... params) {
            ArrayList<OpinionExtendida> aloe = null;

            String data = ((new RestClientManager()).obtenerOpinionesUsuario(params[0]));
            if (data != null) {
                try {
                    aloe = RestJSONParserManager.getOpinionesExtendidas(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return aloe;
        }

        @Override
        protected void onPostExecute(ArrayList<OpinionExtendida> aloe) {
            super.onPostExecute(aloe);
            if (aloe != null) {
                // El usuario tiene excursiones
                arrExcursiones = new ArrayList<Excursion>();
                for (OpinionExtendida oe : aloe) {
                    arrExcursiones.add(new Excursion(oe.getIdOpinion(), oe.getExcursion().getIdExcursion(),
                            oe.getExcursion().getNombre(), oe.getOpinion(), oe.getExcursion().getNivel(),
                            oe.getExcursion().getDistancia(), oe.getExcursion().getLugar(), oe.getExcursion().getLatitud(),
                            oe.getExcursion().getLongitud(), oe.getImgPath()));
                }

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
            } else {
                // El usuario no tiene excursiones
                arrExcursiones = new ArrayList<Excursion>();
                Log.i("EXCURSIONES", "El usuario no tiene excursiones");
            }
        }
    }
}

