package es.deusto.trekkingaventura.imagesAPI;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;

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

    public static String getImageUrl(String imageKey) {
        Cloudinary cloudinary = new Cloudinary(CloudinaryConfiguration.getConfigs());
        return cloudinary.url().generate(imageKey);
    }
}
