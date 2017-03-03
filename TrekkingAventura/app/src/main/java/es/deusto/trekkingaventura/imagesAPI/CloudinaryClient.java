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

    public static void uploadImage(String imagePath, String imageKey) {
        Cloudinary cloudinary = new Cloudinary(CloudinaryConfiguration.getConfigs());
        try {
            cloudinary.uploader().upload(imagePath, ObjectUtils.asMap("public_id", imageKey));
            Log.i("INFO_IMG_UPLOAD", "La imagen se ha subido correctamente");
        } catch (IOException e) {
            Log.i("INFO_IMG_UPLOAD", "La imagen NO se ha subido correctamente");
            e.printStackTrace();
        }
    }

    public static String downloadImage(String imageKey) {
        Cloudinary cloudinary = new Cloudinary(CloudinaryConfiguration.getConfigs());
        Log.i("INFO_IMG_DOWNLOAD", "URL de la imagen: " + cloudinary.url().generate(imageKey));
        return cloudinary.url().generate(imageKey);
    }
}
