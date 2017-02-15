package es.deusto.trekkingaventura.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;

/**
 * Created by salgu on 15/02/2017.
 */

public class ExcursionFragment extends Fragment {

    public static final String EXCURSION_KEY = "excursion_key";
    private Excursion excursion;

    private ImageView imgExc;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtLocation;
    private TextView txtDistance;
    private ImageView imgLevel;
    private TextView txtLatitude;
    private TextView txtLongitude;

    public ExcursionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_excursion, container, false);

        excursion = (Excursion) getArguments().getSerializable(EXCURSION_KEY);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(excursion.getName());

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        imgExc = (ImageView) rootView.findViewById(R.id.excImg);
        txtName = (TextView) rootView.findViewById(R.id.excName);
        txtDescription = (TextView) rootView.findViewById(R.id.excDescription);
        txtLocation = (TextView) rootView.findViewById(R.id.excLocation);
        txtDistance = (TextView) rootView.findViewById(R.id.excDistance);
        imgLevel = (ImageView) rootView.findViewById(R.id.excLevel);
        txtLatitude = (TextView) rootView.findViewById(R.id.excLatitude);
        txtLongitude = (TextView) rootView.findViewById(R.id.excLongitude);

        imgExc.setImageResource(R.drawable.mountain);
        txtName.setText(excursion.getName());
        txtDescription.setText(excursion.getOpinion());
        txtLocation.setText(excursion.getLocation());
        txtDistance.setText(Double.toString(excursion.getTravelDistance()));
        switch (excursion.getLevel()) {
            case "Facil":
                imgLevel.setImageResource(R.drawable.facil);
                break;
            case "Medio":
                imgLevel.setImageResource(R.drawable.medio);
                break;
            case "Dificil":
                imgLevel.setImageResource(R.drawable.dificil);
                break;
        }
        txtLatitude.setText(Float.toString(excursion.getLatitude()));
        txtLongitude.setText(Float.toString(excursion.getLongitude()));

        return rootView;
    }
}
