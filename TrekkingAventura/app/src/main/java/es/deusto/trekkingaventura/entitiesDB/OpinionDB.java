package es.deusto.trekkingaventura.entitiesDB;

import java.io.Serializable;

/**
 * Created by salgu on 01/03/2017.
 */

public class OpinionDB implements Serializable {

    private int idOpinion;
    private String idUsuario;
    private int idExcursion;
    private String opinion;
    private String foto;

    public OpinionDB() {

    }

    public OpinionDB(int idOpinion, String idUsuario, int idExcursion, String opinion, String foto) {
        this.idOpinion = idOpinion;
        this.idUsuario = idUsuario;
        this.idExcursion = idExcursion;
        this.opinion = opinion;
        this.foto = foto;
    }

    public OpinionDB(String idUsuario, int idExcursion, String opinion, String foto) {
        this.idUsuario = idUsuario;
        this.idExcursion = idExcursion;
        this.opinion = opinion;
        this.foto = foto;
    }

    public int getIdOpinion() {
        return idOpinion;
    }

    public void setIdOpinion(int idOpinion) {
        this.idOpinion = idOpinion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdExcursion() {
        return idExcursion;
    }

    public void setIdExcursion(int idExcursion) {
        this.idExcursion = idExcursion;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "OpinionDB{" +
                "idOpinion=" + idOpinion +
                ", idUsuario='" + idUsuario + '\'' +
                ", idExcursion=" + idExcursion +
                ", opinion='" + opinion + '\'' +
                ", foto='" + foto + '\'' +
                '}';
    }
}
