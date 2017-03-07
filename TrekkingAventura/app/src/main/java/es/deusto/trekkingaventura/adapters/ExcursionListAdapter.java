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
import es.deusto.trekkingaventura.imagesAPI.PicassoClient;

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

            if (excursion.getImgPath() == null || excursion.getImgPath().isEmpty()) {
                imageExcursion.setImageResource(R.drawable.imgnotavailable);
            } else {
                if (excursion.getImgPath().equals("Banner Ropa")) {
                    imageExcursion.setImageResource(R.drawable.banner_ropa);
                } else if (excursion.getImgPath().equals("Banner Transporte")) {
                    imageExcursion.setImageResource(R.drawable.banner_transporte);
                } else {
                    PicassoClient.downloadImage(getContext(), excursion.getImgPath(), imageExcursion);
                }
            }

            if (excursion.getName().equals("Banner Ropa")) {
                txtName.setText("");
            } else if(excursion.getName().equals("Banner Transporte")) {
                txtName.setText("");
            } else {
                txtName.setText(excursion.getName());
            }

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
                default:
                    imageLevel.setImageResource(android.R.color.transparent);
                    break;
            }

            if (excursion.getLocation().equals("")) {
                txtLocation.setText("Publicidad");
            } else {
                txtLocation.setText(excursion.getLocation());
            }

            if (excursion.getTravelDistance() == 0) {
                txtDistance.setText("");
            } else {
                txtDistance.setText(Double.toString(excursion.getTravelDistance()) + " km");
            }
        }

        return v;
    }
}
