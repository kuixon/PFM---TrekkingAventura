package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.adapters.OpinionListAdapter;
import es.deusto.trekkingaventura.entities.Excursion;

/**
 * Created by salgu on 28/02/2017.
 */

public class OpinionesExcursionFragment extends Fragment {

    public static final String ARG_OPINIONES = "opiniones";
    public static final String ARG_RESULTADO_BUSQUEDA = "resultado_busqueda";

    private ArrayList<Excursion> arrExcursiones;

    private ListView listOpiniones;
    private ArrayList<Excursion> arrOpiniones;
    private OpinionListAdapter adpOpiniones;

    public OpinionesExcursionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_opiniones_excursion, container, false);

        arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_RESULTADO_BUSQUEDA);

        arrOpiniones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_OPINIONES);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle).
        getActivity().setTitle(arrOpiniones.get(0).getName());

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        // Obtenemos la lista de excursiones del layout y la inicializamos.
        listOpiniones = (ListView) rootView.findViewById(R.id.listOpiniones);

        // Inicializamos el list adapter personalizado y le cambiamos el adaptador a la lista por
        // el inicializado.
        adpOpiniones = new OpinionListAdapter(getContext(), R.layout.opinion_list_adapter, arrOpiniones);
        listOpiniones.setAdapter(adpOpiniones);

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
            Fragment fragment = new ResultadoBusquedaFragment();
            Bundle args = new Bundle();
            if (ResultadoBusquedaFragment.firstTime) {
                args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Excursiones destacadas");
            } else {
                args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Resultado de la búsqueda");
            }
            args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursiones);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
