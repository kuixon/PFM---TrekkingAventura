package es.deusto.trekkingaventura.restDatabaseAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.ExcursionDestacada;
import es.deusto.trekkingaventura.entities.OpinionExtendida;
import es.deusto.trekkingaventura.entitiesDB.ExcursionDB;
import es.deusto.trekkingaventura.entitiesDB.OpinionDB;
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

    public static OpinionDB getOpinionDB(String data) throws JSONException {
        OpinionDB opinion = new OpinionDB();

        JSONObject jObj = new JSONObject(data);

        opinion.setIdOpinion(jObj.getInt("idOpinion"));
        opinion.setIdUsuario(jObj.getString("idUsuario"));
        opinion.setIdExcursion(jObj.getInt("idExcursion"));
        opinion.setOpinion(jObj.getString("opinion"));
        opinion.setFoto(jObj.getString("foto"));

        return opinion;
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

    public static ArrayList<ExcursionDestacada> getExcursionesDestacadas(String data) throws JSONException {
        ArrayList<ExcursionDestacada> aled = new ArrayList<ExcursionDestacada>();

        JSONObject root = new JSONObject(data);
        JSONArray excursionesDestacadas = root.getJSONArray("excursionDestacada");

        ExcursionDestacada ed;
        JSONObject excursionDestacada;
        for (int i=0; i < excursionesDestacadas.length(); i++) {
            ed = new ExcursionDestacada();

            excursionDestacada = excursionesDestacadas.getJSONObject(i);

            ed.setIdExcursion(excursionDestacada.getInt("idExcursion"));
            ed.setNombre(excursionDestacada.getString("nombre"));
            ed.setNivel(excursionDestacada.getString("nivel"));
            ed.setLugar(excursionDestacada.getString("lugar"));
            ed.setDistancia(excursionDestacada.getDouble("distancia"));
            ed.setFoto(excursionDestacada.getString("foto"));
            ed.setLatitud(Float.parseFloat(excursionDestacada.getString("latitud")));
            ed.setLongitud(Float.parseFloat(excursionDestacada.getString("longitud")));
            ed.setNumOpiniones(excursionDestacada.getInt("numOpiniones"));

            aled.add(ed);
        }

        return aled;
    }

    public static ArrayList<ExcursionDB> getExcursionesBusqueda(String data) throws JSONException {
        ArrayList<ExcursionDB> ale = new ArrayList<ExcursionDB>();

        JSONObject root = new JSONObject(data);
        JSONArray excursiones = root.getJSONArray("excursion");

        ExcursionDB e;
        JSONObject excursion;
        for (int i = 0; i < excursiones.length(); i++) {
            e = new ExcursionDB();

            excursion = excursiones.getJSONObject(i);

            e.setIdExcursion(excursion.getInt("idExcursion"));
            e.setNombre(excursion.getString("nombre"));
            e.setNivel(excursion.getString("nivel"));
            e.setLugar(excursion.getString("lugar"));
            e.setDistancia(excursion.getDouble("distancia"));
            e.setFoto(excursion.getString("foto"));
            e.setLatitud(Float.parseFloat(excursion.getString("latitud")));
            e.setLongitud(Float.parseFloat(excursion.getString("longitud")));

            ale.add(e);
        }

        return ale;
    }

    public static ArrayList<OpinionDB> getOpinionesPorIdExcursion(String data) throws JSONException {
        ArrayList<OpinionDB> alo = new ArrayList<OpinionDB>();

        JSONObject root = new JSONObject(data);
        JSONArray opiniones = root.getJSONArray("opinion");

        OpinionDB o;
        JSONObject opinion;
        for (int i = 0; i < opiniones.length(); i++) {
            o = new OpinionDB();

            opinion = opiniones.getJSONObject(i);

            o.setIdOpinion(opinion.getInt("idOpinion"));
            o.setIdUsuario(opinion.getString("idUsuario"));
            o.setIdExcursion(opinion.getInt("idExcursion"));
            o.setOpinion(opinion.getString("opinion"));
            o.setFoto(opinion.getString("foto"));

            alo.add(o);
        }

        return alo;
    }
}
