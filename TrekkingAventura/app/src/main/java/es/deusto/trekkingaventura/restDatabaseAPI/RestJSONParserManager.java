package es.deusto.trekkingaventura.restDatabaseAPI;

import org.json.JSONException;
import org.json.JSONObject;

import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;

/**
 * Created by salgu on 16/03/2017.
 */

public class RestJSONParserManager {

    public static UsuarioDB getUsuarioDB(String data) throws JSONException {
        UsuarioDB usuario = new UsuarioDB();

        // We create out JSONObject from the data
        JSONObject jObj = new JSONObject(data);

        usuario.setIdUsuario(getString("idUsuario", jObj));

        return usuario;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }
}
