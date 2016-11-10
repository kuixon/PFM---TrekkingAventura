package es.deusto.trekkingaventura.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.deusto.trekkingaventura.R;

public class AjustesFragment extends Fragment {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_AJUSTES_NUMBER = "ajustes_number";

    public AjustesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_ajustes, container, false);
        int i = getArguments().getInt(ARG_AJUSTES_NUMBER);
        String nameItemSelected = getResources().getStringArray(R.array.Tags)[i];

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(nameItemSelected);

        return rootView;
    }
}
