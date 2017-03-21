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

import es.deusto.trekkingaventura.entitiesDB.OpinionDB;
import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;

/**
 * Created by salgu on 16/03/2017.
 */

public class RestClientManager {

    // USUARIOS
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

    // EXCURSIONES
    public static String obtenerExcursionesDestacadas() {
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/excursiones/destacadas");

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

    public static String buscarExcursionesPorCriterio(String nombre, String lugar, String distancia, String nivel) {
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/excursiones/criterio/nombre=" + nombre + "&lugar=" + lugar +
                    "&distancia=" + distancia + "&nivel=" + nivel);

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

    // OPINIONES
    public static String obtenerOpinionesUsuario(String idUsuario) {
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/opiniones/usuario/" + idUsuario);

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

    public static String obtenerOpinionesPorIdExcursion(int idExcursion) {
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/opiniones/excursion/" + idExcursion);

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

    public static String editarOpinion(OpinionDB opinion) {
        BufferedReader br = null;

        URL url;
        try {
            final String op = opinion.getOpinion().replace(" ", "%20");

            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/opiniones/editar/" +
                    "idopinion=" + opinion.getIdOpinion() + "&idusuario=" + opinion.getIdUsuario() +
                    "&idexcursion=" + opinion.getIdExcursion() + "&opinion=" + op +
                    "&imgpath=" + opinion.getFoto());

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

    public static String eliminarOpinion(int idOpinion) {
        BufferedReader br = null;

        URL url;
        try {
            url = new URL("http://www.trekkingaventura-160709.appspot.com/rest/8JTFVFQX/opiniones/eliminar/" + idOpinion);

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
