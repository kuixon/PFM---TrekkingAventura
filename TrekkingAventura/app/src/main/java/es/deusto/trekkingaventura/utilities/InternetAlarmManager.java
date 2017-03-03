package es.deusto.trekkingaventura.utilities;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import es.deusto.trekkingaventura.entities.InternetAlarm;

/**
 * Created by salgu on 03/03/2017.
 */

public class InternetAlarmManager {

    private static final String FILENAME = "AlarmState";
    private Context mContext;

    public InternetAlarmManager(Context c) {
        mContext = c;
    }

    public InternetAlarm loadInternetAlarm(){
        try {
            FileInputStream fis = mContext.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked")
            InternetAlarm internetAlarm = (InternetAlarm) ois.readObject();
            ois.close();
            fis.close();
            return internetAlarm;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFile() {
        mContext.deleteFile(FILENAME);
    }

    public void saveInternetAlarm(InternetAlarm internetAlarm){
        try {
            FileOutputStream fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(internetAlarm);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
