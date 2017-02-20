package es.deusto.trekkingaventura.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;

public class ExcursionListAdapter extends ArrayAdapter<Excursion> {

    public ExcursionListAdapter(Context context, int resource, List<Excursion> excursiones) {
        super(context, resource, excursiones);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.excursion_list_adapter, null);
        }

        Excursion excursion = getItem(position);

        if (excursion != null) {
            ImageView imageExcursion = (ImageView) v.findViewById(R.id.excursionImage);
            TextView txtName = (TextView) v.findViewById(R.id.excursionName);
            ImageView imageLevel = (ImageView) v.findViewById(R.id.excursionLevel);
            TextView txtLocation = (TextView) v.findViewById(R.id.excursionLocation);
            TextView txtDistance = (TextView) v.findViewById(R.id.excursionDistance);

            switch (excursion.getImgPath()) {
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

            txtName.setText(excursion.getName());

            switch (excursion.getLevel()) {
                case "Facil":
                    imageLevel.setImageResource(R.drawable.facil);
                    break;
                case "Medio":
                    imageLevel.setImageResource(R.drawable.medio);
                    break;
                case "Dificil":
                    imageLevel.setImageResource(R.drawable.dificil);
                    break;
            }
            txtLocation.setText(excursion.getLocation());
            txtDistance.setText(Double.toString(excursion.getTravelDistance()) + " km");
        }

        return v;
    }
}
