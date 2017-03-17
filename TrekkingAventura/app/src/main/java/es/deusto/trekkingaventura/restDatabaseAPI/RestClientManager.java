package es.deusto.trekkingaventura.restDatabaseAPI;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;

/**
 * Created by salgu on 16/03/2017.
 */

public class RestClientManager {

    public static String obtenerUsuarioPorId(String id) {
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/usuarios/usuario/" + id);

            URLConnection connection = url.openConnection();

            // Let's read the response
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }

            return sb.toString();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String insertarUsuario(UsuarioDB usuario){
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/usuarios/insertar/" + usuario.getIdUsuario());

            URLConnection connection = url.openConnection();

            // Let's read the response
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }

            return sb.toString();
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
