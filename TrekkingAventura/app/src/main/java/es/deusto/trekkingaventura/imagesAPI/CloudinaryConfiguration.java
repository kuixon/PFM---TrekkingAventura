package es.deusto.trekkingaventura.imagesAPI;

import java.util.HashMap;

/**
 * Created by salgu on 03/03/2017.
 */

public class CloudinaryConfiguration {

    public static HashMap getConfigs() {
        HashMap config = new HashMap();
        config.put("cloud_name", "trekkingaventura");
        config.put("api_key", "396424674486229");
        config.put("api_secret", "tVZWkJW87NOGvAiG3q-ieiBWmBY");

        return config;
    }
}
