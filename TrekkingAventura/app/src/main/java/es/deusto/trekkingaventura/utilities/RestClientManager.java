package es.deusto.trekkingaventura.utilities;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import es.deusto.trekkingaventura.entitiesDB.ExcursionDB;
import es.deusto.trekkingaventura.entitiesDB.OpinionDB;
import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;

/**
 * Created by salgu on 13/03/2017.
 */

public class RestClientManager {

    public static RestClientManager instance;

    public static int SUCCESS_CODE = 201;
    public static int EDIT_SUCCESS_CODE = 204;
    public static int ERROR_CODE = 205;
    public static int ALREADY_EXIST_CODE = 206;

    private static String URL = "http://www.trekkingaventura-160709.appspot.com/";

    private DefaultClientConfig config;
    private Client client;
    private WebResource service;

    public RestClientManager() {
        config = new DefaultClientConfig();
        client = Client.create(config);
        service = client.resource(UriBuilder.fromUri(URL).build());
    }

    public static RestClientManager getInstance() {
        if (instance == null) {
            instance = new RestClientManager();
        }
        return instance;
    }

    // USUARIOS
    public UsuarioDB obtenerUsuarioPorId(String id) {
        return service.path("rest").path("usuarios").path(id).get(UsuarioDB.class);
    }

    public boolean insertarUsuario(UsuarioDB u) {
        ClientResponse response = service.path("rest").path("usuarios").type(MediaType.APPLICATION_JSON).post(ClientResponse.class, u);
        return response.getStatus() == SUCCESS_CODE ? true : false;
    }

    // EXCURSIONES
    public ExcursionDB obtenerExcursionPorId(int id) {
        return service.path("rest").path("excursiones").path("excursion").path(Integer.toString(id)).get(ExcursionDB.class);
    }

    public ExcursionDB obtenerExcursionPorNombre(String nombre) {
        return service.path("rest").path("excursiones").path("nombre").path(nombre).get(ExcursionDB.class);
    }

    public ArrayList<ExcursionDB> obtenerExcursionesPorCriterio(String criterio) {
        ExcursionDB[] array = service.path("rest").path("excursiones").path("criterio").path(criterio).get(ExcursionDB[].class);
        return new ArrayList<ExcursionDB>(Arrays.asList(array));
    }

    public boolean insertarExcursion(ExcursionDB e) {
        ClientResponse response = service.path("rest").path("excursiones").type(MediaType.APPLICATION_JSON).post(ClientResponse.class, e);
        return response.getStatus() == SUCCESS_CODE ? true : false;
    }

    public void eliminarExcursion(int id) {
        service.path("rest").path("excursiones").path("excursion").path(Integer.toString(id)).delete();
    }

    // OPINIONES
    public ArrayList<OpinionDB> obtenerOpinionesUsuario(String idusuario) {
        OpinionDB[] array = service.path("rest").path("opiniones").path("usuario").path(idusuario).get(OpinionDB[].class);
        return new ArrayList<OpinionDB>(Arrays.asList(array));
    }

    public ArrayList<OpinionDB> obtenerOpinionesExcursion(int idexcursion) {
        OpinionDB[] array = service.path("rest").path("opiniones").path("excursion").path(Integer.toString(idexcursion)).get(OpinionDB[].class);
        return new ArrayList<OpinionDB>(Arrays.asList(array));
    }

    public boolean insertarOpinion(OpinionDB o) {
        ClientResponse response = service.path("rest").path("opiniones").type(MediaType.APPLICATION_JSON).post(ClientResponse.class, o);
        return response.getStatus() == SUCCESS_CODE ? true : false;
    }

    public boolean editarOpinion(OpinionDB o) {
        ClientResponse response = service.path("rest").path("opiniones").path("opinion").path(Integer.toString(o.getIdOpinion()))
                .type(MediaType.APPLICATION_JSON).put(ClientResponse.class, o);
        return response.getStatus() == EDIT_SUCCESS_CODE ? true : false;
    }

    public void eliminarOpinion(int id) {
        service.path("rest").path("opiniones").path("opinion").path(Integer.toString(id)).delete();
    }
}
