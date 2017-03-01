package es.deusto.trekkingaventura.entitiesDB;

import java.io.Serializable;

/**
 * Created by salgu on 01/03/2017.
 */

public class UsuarioDB implements Serializable {

    private String idUsuario;

    public UsuarioDB() {

    }

    public UsuarioDB(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "UsuarioDB{" +
                "idUsuario='" + idUsuario + '\'' +
                '}';
    }
}
