package es.deusto.trekkingaventura.entities;

import java.io.Serializable;

public class Excursion implements Serializable{

    private int idOpinion;
    private int idExcursion;
    private String name;
    private String opinion;
    private String level;
    private double travelDistance;
    private String location;
    private float latitude;
    private float longitude;
    private String imgPath;

    public Excursion() {

    }

    public Excursion(int idOpinion, int idExcursion, String name, String opinion, String level, double travelDistance, String location, float latitude, float longitude, String imgPath) {
        this.idOpinion = idOpinion;
        this.idExcursion = idExcursion;
        this.name = name;
        this.opinion = opinion;
        this.level = level;
        this.travelDistance = travelDistance;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgPath = imgPath;
    }

    public int getIdOpinion() {
        return idOpinion;
    }

    public void setIdOpinion(int idOpinion) {
        this.idOpinion = idOpinion;
    }

    public int getIdExcursion() {
        return idExcursion;
    }

    public void setIdExcursion(int idExcursion) {
        this.idExcursion = idExcursion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getTravelDistance() {
        return travelDistance;
    }

    public void setTravelDistance(double travelDistance) {
        this.travelDistance = travelDistance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public String toString() {
        return "Excursion{" +
                "idOpinion=" + idOpinion +
                ", idExcursion=" + idExcursion +
                ", name='" + name + '\'' +
                ", opinion='" + opinion + '\'' +
                ", level='" + level + '\'' +
                ", travelDistance=" + travelDistance +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }
}
