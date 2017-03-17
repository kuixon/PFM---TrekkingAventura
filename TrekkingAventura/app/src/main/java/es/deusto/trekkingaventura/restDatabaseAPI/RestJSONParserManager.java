package es.deusto.trekkingaventura.restDatabaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.deusto.trekkingaventura.entities.OpinionExtendida;
import es.deusto.trekkingaventura.entitiesDB.ExcursionDB;
import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;

/**
 * Created by salgu on 16/03/2017.
 */

public class RestJSONParserManager {

    public static UsuarioDB getUsuarioDB(String data) throws JSONException {
        UsuarioDB usuario = new UsuarioDB();

        JSONObject jObj = new JSONObject(data);

        usuario.setIdUsuario(jObj.getString("idUsuario"));

        return usuario;
    }

    public static ArrayList<OpinionExtendida> getOpinionesExtendidas(String data) throws JSONException {
        ArrayList<OpinionExtendida> loe = new ArrayList<OpinionExtendida>();

        JSONObject root = new JSONObject(data);
        JSONArray opinionesExtendidas = root.getJSONArray("opinionExtendida");

        OpinionExtendida oe;
        JSONObject opinionExtendida;
        JSONObject excursion;
        JSONObject usuario;
        for (int i=0; i < opinionesExtendidas.length(); i++) {
            oe = new OpinionExtendida();

            opinionExtendida = opinionesExtendidas.getJSONObject(i);
            excursion = opinionExtendida.getJSONObject("excursion");
            usuario = opinionExtendida.getJSONObject("usuario");

            oe.setIdOpinion(opinionExtendida.getInt("idOpinion"));
            oe.setUsuario(new UsuarioDB(usuario.getString("idUsuario")));
            oe.setExcursion(new ExcursionDB(excursion.getInt("idExcursion"), excursion.getString("nombre"), excursion.getString("nivel"),
                    excursion.getString("lugar"), excursion.getDouble("distancia"), excursion.getString("foto"),
                    Float.parseFloat(excursion.getString("latitud")), Float.parseFloat(excursion.getString("longitud"))));
            oe.setOpinion(opinionExtendida.getString("opinion"));
            oe.setImgPath(opinionExtendida.getString("imgPath"));

            loe.add(oe);
        }

        return loe;
    }
}
