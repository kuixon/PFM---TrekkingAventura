package es.deusto.trekkingaventura.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.notifications.NotificationService;

/**
 * Created by salgu on 22/02/2017.
 */

public class AjustesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String ARG_MIS_EXCURSIONES = "mis_excursiones";

    private SharedPreferences sharedPreferences;
    private ArrayList<Excursion> arrExcursiones;
    private boolean notifications_enabled;
    private Excursion excursion;

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        activity = getActivity();

        arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_MIS_EXCURSIONES);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Filtro distancia
        if (sharedPreferences.getString("distance","").isEmpty()) {
            this.findPreference("distance").setSummary("Introduce la distancia máxima");
        } else {
            this.findPreference("distance").setSummary(sharedPreferences.getString("distance", ""));
        }

        // Notificaciones
        CharSequence[] entries = new CharSequence[arrExcursiones.size()];
        CharSequence[] entryValues = new CharSequence[arrExcursiones.size()];

        for (int i = 0; i < arrExcursiones.size(); i++) {
            entries[i] = arrExcursiones.get(i).getName();
            entryValues[i] = Integer.toString(arrExcursiones.get(i).getId());
        }

        ListPreference listExcursiones = (ListPreference) this.findPreference("excursiones");
        listExcursiones.setEntries(entries);
        listExcursiones.setEntryValues(entryValues);

        if (sharedPreferences.getString("excursiones","").isEmpty()) {
            this.findPreference("excursiones").setSummary("Selecciona una excursión...");
        } else {
            int id = Integer.parseInt(sharedPreferences.getString("excursiones",""));
            Excursion excursion = getExcursionById(id);
            if (excursion != null) {
                this.findPreference("excursiones").setSummary(excursion.getName());
            } else {
                this.findPreference("excursiones").setSummary("Selecciona una excursión...");
                ((ListPreference) this.findPreference("excursiones")).setValue("0");
            }
        }

        notifications_enabled = sharedPreferences.getBoolean("notifications", false);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("distance")) {
            if (sharedPreferences.getString(key,"").equals("")) {
                this.findPreference(key).setSummary("Introduce la distancia máxima");
            } else {
                this.findPreference(key).setSummary(sharedPreferences.getString(key, "Introduce la distancia máxima"));
            }
        } else if(key.equals("excursiones")) {
            int id = Integer.parseInt(sharedPreferences.getString("excursiones", ""));
            Excursion excursion = getExcursionById(id);
            if (excursion != null) {
                this.findPreference("excursiones").setSummary(excursion.getName());
            } else {
                this.findPreference("excursiones").setSummary("Selecciona una excursión...");
                ((ListPreference) this.findPreference("excursiones")).setValue("0");
            }
        } else if(key.equals("notifications")) {
            if(sharedPreferences.getBoolean("notifications", false) && !notifications_enabled) {
                notifications_enabled = true;
                int id = Integer.parseInt(sharedPreferences.getString("excursiones", ""));
                excursion = getExcursionById(id);
                if (excursion != null) {
                    NotificationService.getInstance(getActivity()).setExcursion(excursion);
                    NotificationService.getInstance(getActivity()).startAlarm();
                    Toast.makeText(activity,
                            "*Notificaciones ACTIVADAS* \nA partir de ahora recibiras una notificación cuando haga mal tiempo en" +
                                    " el lugar de la excursión seleccionada.", Toast.LENGTH_LONG).show();
                }
            } else {
                notifications_enabled = false;
                NotificationService.getInstance(getActivity()).stopAlarm();
                Toast.makeText(activity,
                        "*Notificaciones DESACTIVADAS* \nA partir de ahora NO recibiras una notificación cuando haga mal tiempo en" +
                                " el lugar de la excursión seleccionada.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Excursion getExcursionById(int id) {
        for (Excursion e : arrExcursiones) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
