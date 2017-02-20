package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.adapters.ExcursionListAdapter;
import es.deusto.trekkingaventura.entities.Excursion;

public class MisExcursionesFragment extends Fragment {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_MIS_EXCURSIONES_NUMBER = "mis_excursiones_number";

    private ListView listExcursiones;
    private ArrayList<Excursion> arrExcursiones;
    private ExcursionListAdapter adpExcursiones;

    public MisExcursionesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_mis_excursiones, container, false);
        int i = getArguments().getInt(ARG_MIS_EXCURSIONES_NUMBER);
        String nameItemSelected = getResources().getStringArray(R.array.Tags)[i];

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(nameItemSelected);

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        // Obtenemos la lista de excursiones del layout y la inicializamos.
        listExcursiones = (ListView) rootView.findViewById(R.id.listExcursiones);
        createExcursionList();

        // Inicializamos el list adapter personalizado y le cambiamos el adaptador a la lista por
        // el inicializado.
        adpExcursiones = new ExcursionListAdapter(getContext(), R.layout.excursion_list_adapter, arrExcursiones);
        listExcursiones.setAdapter(adpExcursiones);

        listExcursiones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(ExcursionFragment.EXCURSION_KEY, arrExcursiones.get(position));
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mis_excursiones, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void createExcursionList() {

        // Creamos tres excursiones de prueba y las metemos al array de Excursiones.
        Excursion exc1 = new Excursion(1,"Ruta del Cares", "Un sitio espectacular con unas vistas impresionantes. Ideal para ir con la familia y para sacar fotos de los acantilados.", "Medio", 12,"Arenas de Cabrales",Float.parseFloat("43.2551652"),Float.parseFloat("-4.8366377"),"Cares");
        Excursion exc2 = new Excursion(2,"Ventana Relux", "Unas vistas impresionantes desde la ventana. Una caída libre espectacular que merece ser fotografiada. Ideal para la familia.", "Facil", 2.7,"Karrantza Harana",Float.parseFloat("43.2499237"),Float.parseFloat("-3.4108149"),"Relux");
        Excursion exc3 = new Excursion(3,"Faro del Caballo", "Excursión muy bonita para ver todos los acantilados del monte Buciero de Santoña. Ideal para ir en pareja y para pasar el día.", "Medio", 12,"Santoña",Float.parseFloat("43.4514626"),Float.parseFloat("-3.4256904"),"Caballo");
        Excursion exc4 = new Excursion(4,"Gorbea", "Subida preciosa a uno de los montes más característicos de Bizkaia. Recorrido un poco duro pero el paisaje merece la pena.", "Dificil", 12,"Areatza",Float.parseFloat("43.0350000"),Float.parseFloat("-2.7798800"),"Gorbea");
        arrExcursiones = new ArrayList<Excursion>();
        arrExcursiones.add(exc1);
        arrExcursiones.add(exc2);
        arrExcursiones.add(exc3);
        arrExcursiones.add(exc4);
    }
}
