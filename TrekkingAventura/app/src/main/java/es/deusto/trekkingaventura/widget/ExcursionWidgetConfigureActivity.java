package es.deusto.trekkingaventura.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.OpinionExtendida;
import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;
import es.deusto.trekkingaventura.restDatabaseAPI.RestClientManager;
import es.deusto.trekkingaventura.restDatabaseAPI.RestJSONParserManager;

/**
 * The configuration screen for the {@link ExcursionWidget ExcursionWidget} AppWidget.
 */
public class ExcursionWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "es.deusto.trekkingaventura.widget.ExcursionWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public static UsuarioDB usuario;

    public static ArrayList<Excursion> arrExcursiones;

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

        usuario = new UsuarioDB(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        ObtenerUsuarioTask task = new ObtenerUsuarioTask();
        task.execute(new String[] {usuario.getIdUsuario()});

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.excursion_widget_configure);

        spnExcursiones = (Spinner) findViewById(R.id.excursionesSpiner);


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
    }

    private class ObtenerUsuarioTask extends AsyncTask<String, Void, UsuarioDB> {
        ProgressDialog progressDialog = new ProgressDialog(ExcursionWidgetConfigureActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Obteniendo datos del usuario...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected UsuarioDB doInBackground(String... params) {
            UsuarioDB usuario = null;

            String data = ((new RestClientManager()).obtenerUsuarioPorId(params[0]));
            if (data != null) {
                try {
                    usuario = RestJSONParserManager.getUsuarioDB(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return usuario;
        }

        @Override
        protected void onPostExecute(UsuarioDB usuarioDB) {
            super.onPostExecute(usuarioDB);
            if (usuarioDB != null) {
                usuario = usuarioDB;
                // Inicializamos la lista de excursiones
                InicializarExcursionesTask task = new InicializarExcursionesTask();
                task.execute(new String[]{usuario.getIdUsuario()});
            } else {
                // Insertar el usuario.
                Log.i("USUARIO", "Insertar usuario");
                InsertarUsuarioTask task = new InsertarUsuarioTask();
                task.execute(new UsuarioDB[]{new UsuarioDB(usuario.getIdUsuario())});
            }
            progressDialog.dismiss();
        }
    }

    private class InsertarUsuarioTask extends AsyncTask<UsuarioDB, Void, UsuarioDB> {
        ProgressDialog progressDialog = new ProgressDialog(ExcursionWidgetConfigureActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Almacenando datos del usuario...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected UsuarioDB doInBackground(UsuarioDB... params) {
            UsuarioDB usuario = null;

            String data = ((new RestClientManager()).insertarUsuario(params[0]));
            if (data != null) {
                try {
                    usuario = RestJSONParserManager.getUsuarioDB(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return usuario;
        }

        @Override
        protected void onPostExecute(UsuarioDB usuarioDB) {
            super.onPostExecute(usuarioDB);
            if (usuarioDB != null) {
                // Se ha insertado correctamente el usuario.
                Log.i("USUARIO", "El usuario '" + usuarioDB.getIdUsuario() + "' se ha insertado correctamente");

                // Inicializamos la lista de excursiones
                InicializarExcursionesTask taskExcursiones = new InicializarExcursionesTask();
                taskExcursiones.execute(new String[]{usuarioDB.getIdUsuario()});
            } else {
                // Insertar el usuario.
                Log.i("USUARIO", "No se ha podido insertar el usuario");
            }
            progressDialog.dismiss();
        }
    }

    private class InicializarExcursionesTask extends AsyncTask<String, Void, ArrayList<OpinionExtendida>> {
        ProgressDialog progressDialog = new ProgressDialog(ExcursionWidgetConfigureActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Cargando excursiones del usuario...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

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
                Log.i("EXCURSIONES", "El usuario '" + usuario.getIdUsuario() + "' tiene excursiones");
                arrExcursiones = new ArrayList<Excursion>();
                for (OpinionExtendida oe : aloe) {
                    arrExcursiones.add(new Excursion(oe.getIdOpinion(), oe.getExcursion().getIdExcursion(),
                            oe.getExcursion().getNombre(), oe.getOpinion(), oe.getExcursion().getNivel(),
                            oe.getExcursion().getDistancia(), oe.getExcursion().getLugar(), oe.getExcursion().getLatitud(),
                            oe.getExcursion().getLongitud(), oe.getImgPath()));
                }

                String[] items = new String[arrExcursiones.size()];
                for (int i = 0; i < arrExcursiones.size(); i++) {
                    items[i] = arrExcursiones.get(i).getName();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ExcursionWidgetConfigureActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnExcursiones.setAdapter(adapter);

                final String selectedExcursionName = loadSelectedExcursionName(ExcursionWidgetConfigureActivity.this, mAppWidgetId);
                for (int i = 0; i < arrExcursiones.size(); i++) {
                    if (selectedExcursionName.equals(arrExcursiones.get(i).getName())) {
                        spnExcursiones.setSelection(i);
                    }
                }
            } else {
                // El usuario no tiene excursiones
                arrExcursiones = new ArrayList<Excursion>();
                Log.i("EXCURSIONES", "El usuario no tiene excursiones");
            }

            progressDialog.dismiss();
        }
    }
}

