package es.deusto.trekkingaventura.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.imagesAPI.PicassoClient;

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

            if(opinion.getImgPath() == null || opinion.getImgPath().isEmpty()) {
                imageExcursion.setImageResource(R.drawable.imgnotavailable);
            } else {
                PicassoClient.downloadImage(getContext(), "http://res.cloudinary.com/trekkingaventura/image/upload/" + opinion.getImgPath(), imageExcursion);
            }

            txtOpinion.setText(opinion.getOpinion());
        }

        return v;
    }
}
