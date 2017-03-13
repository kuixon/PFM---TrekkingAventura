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

import es.deusto.trekkingaventura.entities.Excursion;

/**
 * Created by salgu on 13/03/2017.
 */

public class ExcursionNotificationManager {

    private static final String FILENAME = "ExcursionNotification";
    private Context mContext;

    public ExcursionNotificationManager(Context c) {
        mContext = c;
    }

    public Excursion loadExcursionNotification(){
        try {
            FileInputStream fis = mContext.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            @SuppressWarnings("unchecked")
            Excursion excursion = (Excursion) ois.readObject();
            ois.close();
            fis.close();
            return excursion;
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

    public void saveExcursionNotification(Excursion excursion){
        try {
            FileOutputStream fos = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(excursion);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
