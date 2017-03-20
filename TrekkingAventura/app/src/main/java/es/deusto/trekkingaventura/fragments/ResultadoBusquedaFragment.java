package es.deusto.trekkingaventura.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import static es.deusto.trekkingaventura.fragments.FormExcursionesFragment.ARG_FORM_EXCURSIONES_TITLE;

/**
 * Created by salgu on 28/02/2017.
 */

public class ResultadoBusquedaFragment extends Fragment {

    public static final String ARG_RESULTADO_BUSQUEDA_TITLE = "resultado_búsqueda_title";
    public static final String ARG_RESULTADO_BUSQUEDA = "resultado_búsqueda";

    private ListView listExcursiones;
    private ArrayList<Excursion> arrExcursiones;
    private ExcursionListAdapter adpExcursiones;

    public ResultadoBusquedaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_resultado_busqueda, container, false);
        String name = getArguments().getString(ARG_RESULTADO_BUSQUEDA_TITLE);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(name);

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        // Obtenemos la lista de excursiones del layout y la inicializamos.
        listExcursiones = (ListView) rootView.findViewById(R.id.listResultadoBusqueda);
        arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_RESULTADO_BUSQUEDA);

        // Inicializamos el list adapter personalizado y le cambiamos el adaptador a la lista por
        // el inicializado.
        adpExcursiones = new ExcursionListAdapter(getContext(), R.layout.excursion_list_adapter, arrExcursiones);
        listExcursiones.setAdapter(adpExcursiones);

        listExcursiones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrExcursiones.get(position).getName().equals("Banner Ropa")) {
                    Uri uri = Uri.parse("https://www.thenorthface.es/");
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                } else if (arrExcursiones.get(position).getName().equals("Banner Transporte")) {
                    Uri uri = Uri.parse("https://www.alsa.es/");
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                } else {
                    // En este punto, tendríamos que buscar todas las opiniones asociadas a la excursión
                    // clickada y pasárselas como parámetro al fragment de las opiniones. De momento,
                    // se le pasan algunas excursiones a modo de prueba a la variable ARG_OPINIONES
                    // (hasta que haya persistencia de datos).
                    ArrayList<Excursion> opiniones = new ArrayList<Excursion>();
                    opiniones.add(arrExcursiones.get(position));
                    if (position + 1 == arrExcursiones.size()) {
                        opiniones.add(arrExcursiones.get(position-1));
                    } else {
                        opiniones.add(arrExcursiones.get(position+1));
                    }

                    Fragment fragment = new OpinionesExcursionFragment();
                    Bundle args = new Bundle();
                    args.putSerializable(OpinionesExcursionFragment.ARG_RESULTADO_BUSQUEDA, arrExcursiones);
                    args.putSerializable(OpinionesExcursionFragment.ARG_OPINIONES, opiniones);
                    fragment.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.opiniones_excursiones, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mnu_back) {
            Fragment fragment = new BuscarExcursionesFragment();
            Bundle args = new Bundle();
            args.putInt(BuscarExcursionesFragment.ARG_BUSCAR_EXCURSIONES_NUMBER, 1);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
