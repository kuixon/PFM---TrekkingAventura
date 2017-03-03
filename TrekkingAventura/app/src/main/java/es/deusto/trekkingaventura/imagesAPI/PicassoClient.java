package es.deusto.trekkingaventura.imagesAPI;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import es.deusto.trekkingaventura.R;

/**
 * Created by salgu on 03/03/2017.
 */

public class PicassoClient {

    public static void downloadImage(Context context, String url, ImageView img) {
        if ((url != null) && (url.length() > 0)) {
            Picasso.with(context).load(url).placeholder(R.drawable.imgnotavailable).into(img);
        } else {
            Picasso.with(context).load(R.drawable.imgnotavailable).into(img);
        }
    }
}
