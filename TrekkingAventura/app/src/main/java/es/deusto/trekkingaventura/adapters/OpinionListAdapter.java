package es.deusto.trekkingaventura.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;

/**
 * Created by salgu on 28/02/2017.
 */

public class OpinionListAdapter extends ArrayAdapter<Excursion> {

    public OpinionListAdapter(Context context, int resource, List<Excursion> opiniones) {
        super(context, resource, opiniones);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.opinion_list_adapter, null);
        }

        Excursion opinion = getItem(position);

        if (opinion != null) {
            ImageView imageExcursion = (ImageView) v.findViewById(R.id.excursionImage);
            TextView txtOpinion = (TextView) v.findViewById(R.id.excursionOpinion);

            // Método temporal para cargar las imágenes de prueba.
            switch (opinion.getImgPath()) {
                case "Cares":
                    imageExcursion.setImageResource(R.drawable.rutadelcares);
                    break;
                case "Relux":
                    imageExcursion.setImageResource(R.drawable.ventanarelux);
                    break;
                case "Caballo":
                    imageExcursion.setImageResource(R.drawable.farodelcaballo);
                    break;
                case "Gorbea":
                    imageExcursion.setImageResource(R.drawable.gorbea);
                    break;
            }

            txtOpinion.setText(opinion.getOpinion());
        }

        return v;
    }
}
