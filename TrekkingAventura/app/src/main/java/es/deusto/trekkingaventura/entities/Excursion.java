package es.deusto.trekkingaventura.entities;

import java.io.Serializable;

public class Excursion implements Serializable{

    private int id;
    private String name;
    private String opinion;
    private String level;
    private double travelDistance;
    private String location;
    private float latitude;
    private float longitude;
    private String imgPath;
    private boolean notifications;

    public Excursion(int id, String name, String opinion, String level, double travelDistance, String location, float latitude, float longitude, String imgPath, boolean notifications) {
        this.id = id;
        this.name = name;
        this.opinion = opinion;
        this.level = level;
        this.travelDistance = travelDistance;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgPath = imgPath;
        this.notifications = notifications;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "Excursion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", opinion='" + opinion + '\'' +
                ", level='" + level + '\'' +
                ", travelDistance=" + travelDistance +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", imgPath='" + imgPath + '\'' +
                ", notifications=" + notifications +
                '}';
    }
}
