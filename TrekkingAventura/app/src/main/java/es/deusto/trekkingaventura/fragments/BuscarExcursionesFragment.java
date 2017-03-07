package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;

public class BuscarExcursionesFragment extends Fragment {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_BUSCAR_EXCURSIONES_NUMBER = "buscar_excursiones_number";
    public static final String ARG_MIS_EXCURSIONES = "mis_excursiones";

    private ArrayList<Excursion> arrExcursiones;

    private EditText edtName;
    private EditText edtLocation;
    private EditText edtDistance;
    private RadioGroup rdgLevel;
    private Button btnFind;

    public BuscarExcursionesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (MainActivity.appFirstTimeOpened) {
            // En este punto, se haría una búsqueda para mostrarle al usuario las excursiones más
            // destacadas o llamativas nada más abrir la aplicación. De momento, le mostramos todas.
            arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_MIS_EXCURSIONES);

            // En este punto habría que realizar el filtrado para que sólo queden esas excursiones que
            // creemos convenientes o creemos que pueden ser destacadas/atractivas. De momento, sólo le
            // metemos los banners a toda la lista de excursiones.
            ArrayList<Excursion> arrExcursionesBanner = new ArrayList<Excursion>(arrExcursiones);
            arrExcursionesBanner.add(new Excursion(-100,"Banner Ropa", "", "", 0,"",0,0,"Banner Ropa"));
            arrExcursionesBanner.add(new Excursion(-100,"Banner Transporte", "", "", 0,"",0,0,"Banner Transporte"));
            Collections.shuffle(arrExcursionesBanner);

            Fragment fragment = new ResultadoBusquedaFragment();
            Bundle args = new Bundle();
            args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Excursiones destacadas");
            args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursionesBanner);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

            MainActivity.appFirstTimeOpened = false;

            return null;
        } else {
            // Obtenemos el nombre del elemento de la lista seleccionado.
            View rootView = inflater.inflate(R.layout.fragment_buscar_excursiones, container, false);
            int i = getArguments().getInt(ARG_BUSCAR_EXCURSIONES_NUMBER);
            String nameItemSelected = getResources().getStringArray(R.array.Tags)[i];

            // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
            // a un método de la actividad llamado setTitle.
            getActivity().setTitle(nameItemSelected);

            // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
            setHasOptionsMenu(true);

            arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_MIS_EXCURSIONES);

            edtName = (EditText) rootView.findViewById(R.id.edName);
            edtLocation = (EditText) rootView.findViewById(R.id.edLocation);
            edtDistance = (EditText) rootView.findViewById(R.id.edDistance);
            rdgLevel = (RadioGroup) rootView.findViewById(R.id.rGroup);

            btnFind = (Button) rootView.findViewById(R.id.button_find);
            btnFind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    find(v);
                }
            });

            return rootView;
        }
    }

    public void find(View v) {
        // En este punto se realizaría la búsqueda y se le pasarían al fragment ResultadoBusqueda
        // todas las excursiones resultantes.

        // INICIO - Búsqueda provisional donde el resulado serían las 4 excursiones de prueba
        Fragment fragment = new ResultadoBusquedaFragment();
        Bundle args = new Bundle();
        args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Resultado de la búsqueda");
        args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursiones);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        // INICIO - Búsqueda provisional donde el resulado serían las 4 excursiones de prueba
    }
}
