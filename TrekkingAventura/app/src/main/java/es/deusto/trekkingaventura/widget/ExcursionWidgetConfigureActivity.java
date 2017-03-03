package es.deusto.trekkingaventura.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;

/**
 * The configuration screen for the {@link ExcursionWidget ExcursionWidget} AppWidget.
 */
public class ExcursionWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "es.deusto.trekkingaventura.widget.ExcursionWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private ArrayList<Excursion> arrExcursiones;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner spnExcursiones;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ExcursionWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String selectedExcursionName = spnExcursiones.getSelectedItem().toString();
            saveSelectedExcursionName(context, mAppWidgetId, selectedExcursionName);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ExcursionWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public ExcursionWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveSelectedExcursionName(Context context, int appWidgetId, String selectedExcursionName) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, selectedExcursionName);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadSelectedExcursionName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String selectedExcursionNameValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (selectedExcursionNameValue != null) {
            return selectedExcursionNameValue;
        } else {
            return context.getString(R.string.widget_default_text);
        }
    }

    static void deleteSelectedExcursionName(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.excursion_widget_configure);

        // Como todavía no tenemos persistencia de los datos, obtenemos de esta forma las excursiones de prueba de la App.
        createExcursionList();

        spnExcursiones = (Spinner) findViewById(R.id.excursionesSpiner);
        String[] items = new String[arrExcursiones.size()];
        for (int i = 0; i < arrExcursiones.size(); i++) {
            items[i] = arrExcursiones.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExcursionWidgetConfigureActivity.this,
                android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExcursiones.setAdapter(adapter);

        findViewById(R.id.btnAddWidget).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        final String selectedExcursionName = loadSelectedExcursionName(ExcursionWidgetConfigureActivity.this, mAppWidgetId);
        for (int i = 0; i < arrExcursiones.size(); i++) {
            if (selectedExcursionName.equals(arrExcursiones.get(i).getName())) {
                spnExcursiones.setSelection(i);
            }
        }
    }

    private void createExcursionList() {

        // Creamos tres excursiones de prueba y las metemos al array de Excursiones.
        Excursion exc1 = new Excursion(1,"Ruta del Cares", "Un sitio espectacular con unas vistas impresionantes. Ideal para ir con la familia y para sacar fotos de los acantilados.", "Medio", 12,"Arenas de Cabrales",Float.parseFloat("43.2551652"),Float.parseFloat("-4.8366377"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_1_1.jpg");
        Excursion exc2 = new Excursion(2,"Ventana Relux", "Unas vistas impresionantes desde la ventana. Una caída libre espectacular que merece ser fotografiada. Ideal para la familia.", "Facil", 2.7,"Karrantza Harana",Float.parseFloat("43.2499237"),Float.parseFloat("-3.4108149"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_2_2.jpg");
        Excursion exc3 = new Excursion(3,"Faro del Caballo", "Excursión muy bonita para ver todos los acantilados del monte Buciero de Santoña. Ideal para ir en pareja y para pasar el día.", "Medio", 12,"Santoña",Float.parseFloat("43.4514626"),Float.parseFloat("-3.4256904"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_3_3.jpg");
        Excursion exc4 = new Excursion(4,"Gorbea", "Subida preciosa a uno de los montes más característicos de Bizkaia. Recorrido un poco duro pero el paisaje merece la pena.", "Dificil", 12,"Areatza",Float.parseFloat("43.0350000"),Float.parseFloat("-2.7798800"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_4_4.jpg");

        arrExcursiones = new ArrayList<Excursion>();
        arrExcursiones.add(exc1);
        arrExcursiones.add(exc2);
        arrExcursiones.add(exc3);
        arrExcursiones.add(exc4);
    }
}

