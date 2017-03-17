package es.deusto.trekkingaventura.entities;

import java.io.Serializable;

import es.deusto.trekkingaventura.entitiesDB.ExcursionDB;
import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;

/**
 * Created by salgu on 17/03/2017.
 */

public class OpinionExtendida implements Serializable {

    private int idOpinion;
    private UsuarioDB usuario;
    private ExcursionDB excursion;
    private String opinion;
    private String imgPath;

    public OpinionExtendida() {

    }

    public OpinionExtendida(int idOpinion, UsuarioDB usuario, ExcursionDB excursion, String opinion, String imgPath) {
        this.idOpinion = idOpinion;
        this.usuario = usuario;
        this.excursion = excursion;
        this.opinion = opinion;
        this.imgPath = imgPath;
    }

    public int getIdOpinion() {
        return idOpinion;
    }

    public void setIdOpinion(int idOpinion) {
        this.idOpinion = idOpinion;
    }

    public UsuarioDB getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDB usuario) {
        this.usuario = usuario;
    }

    public ExcursionDB getExcursion() {
        return excursion;
    }

    public void setExcursion(ExcursionDB excursion) {
        this.excursion = excursion;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "OpinionExtendida{" +
                "idOpinion=" + idOpinion +
                ", usuario=" + usuario +
                ", excursion=" + excursion +
                ", opinion='" + opinion + '\'' +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
