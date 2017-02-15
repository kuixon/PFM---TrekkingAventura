package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.deusto.trekkingaventura.R;

/**
 * Created by salgu on 15/02/2017.
 */

public class ExcursionFragment extends Fragment {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_EXCURSION = "excursion";

    public ExcursionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_excursion, container, false);
        String name = getArguments().getString(ARG_EXCURSION);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(name);

        return rootView;
    }
}
