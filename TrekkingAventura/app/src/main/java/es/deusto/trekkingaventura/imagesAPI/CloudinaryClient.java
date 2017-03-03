package es.deusto.trekkingaventura.imagesAPI;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;

import es.deusto.trekkingaventura.activities.MainActivity;

/**
 * Created by salgu on 03/03/2017.
 */

public class CloudinaryClient {

    public static void uploadImage(String imagePath, String imageCloudName, String imageKey) {
        Cloudinary cloudinary = new Cloudinary(CloudinaryConfiguration.getConfigs());
        try {
            cloudinary.uploader().upload(imagePath, ObjectUtils.asMap(imageKey, imageCloudName));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("INFO_CLOUDINARY", "No se ha podido subir la imagen");
        }
    }

}
