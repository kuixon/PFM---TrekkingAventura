package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
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
