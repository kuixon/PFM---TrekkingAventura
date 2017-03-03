package es.deusto.trekkingaventura.entities;

import java.io.Serializable;

/**
 * Created by salgu on 03/03/2017.
 */

public class InternetAlarm implements Serializable {

    private String userId;
    private boolean alarmOn;

    public InternetAlarm() {

    }

    public InternetAlarm(String userId, boolean alarmOn) {
        this.userId = userId;
        this.alarmOn = alarmOn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAlarmOn() {
        return alarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    @Override
    public String toString() {
        return "InternetAlarm{" +
                "userId='" + userId + '\'' +
                ", alarmOn=" + alarmOn +
                '}';
    }
}
