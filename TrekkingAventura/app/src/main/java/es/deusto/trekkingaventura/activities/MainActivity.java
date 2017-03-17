package es.deusto.trekkingaventura.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.adapters.DrawerListAdapter;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.InternetAlarm;
import es.deusto.trekkingaventura.entitiesDB.UsuarioDB;
import es.deusto.trekkingaventura.fragments.EmptyAppFragment;
import es.deusto.trekkingaventura.fragments.EmptyFragment;
import es.deusto.trekkingaventura.fragments.BuscarExcursionesFragment;
import es.deusto.trekkingaventura.fragments.ExcursionFragment;
import es.deusto.trekkingaventura.fragments.AjustesFragment;
import es.deusto.trekkingaventura.fragments.FormExcursionesFragment;
import es.deusto.trekkingaventura.fragments.MisExcursionesFragment;
import es.deusto.trekkingaventura.entities.DrawerItem;
import es.deusto.trekkingaventura.restDatabaseAPI.RestClientManager;
import es.deusto.trekkingaventura.restDatabaseAPI.RestJSONParserManager;
import es.deusto.trekkingaventura.utilities.InternetAlarmManager;

public class MainActivity extends AppCompatActivity {

    /*
     DECLARACIONES
     */
    public static UsuarioDB usuario;

    private Toolbar toolbar;

    private boolean menuOptionEnabled = true;
    public static boolean appFirstTimeOpened = true;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence activityTitle;
    private CharSequence itemTitle;
    private String[] tagTitles;

    private ArrayList<Excursion> arrExcursiones;

    // Para cuando se accede desde una notificación
    public static final String ARG_NOTIFICATION_EXC = "arg_notification_exc";

    private static InternetAlarm internetAlarm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos el ID del Usuario y lo insertamos en la BD si es que no existe aun.
        usuario = new UsuarioDB();
        usuario.setIdUsuario(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
        Log.i("INFO_USER", "Id: " + usuario.getIdUsuario());
        ObtenerUsuarioTask task = new ObtenerUsuarioTask();
        task.execute(new String[]{usuario.getIdUsuario()});

        // Inicializamos la lista de excursiones
        createExcursionList();

        // Ponemos nuestra Toolbar personalizada como action bar.
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Obtenemos el títulos de la actividad.
        itemTitle = activityTitle = getTitle();

        // Obtenemos los títulos que tendrá nuestro drawer.
        tagTitles = getResources().getStringArray(R.array.Tags);

        // Inicializamos nuestro drawerList y nuestro drawerLayout.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Ponemos una sombra sobre el contenido principal cuando el drawer se despliegue
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Creamos una lista con los elementos que tendrá nuestro menú desplegable.
        ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
        items.add(new DrawerItem(tagTitles[0],R.drawable.ic_terrain_white_24dp));
        items.add(new DrawerItem(tagTitles[1],R.drawable.ic_search_white_24dp));
        items.add(new DrawerItem(tagTitles[2],R.drawable.ic_settings_white_24dp));

        // Le cambiamos el adaptador a la lista desplegable por un adaptador personalizado
        // pasándole la lista de elementos anterior. Por último, le añadimos un click listener
        // denominado DrawerItemClickListener (clase definida más abajo).
        drawerList.setAdapter(new DrawerListAdapter(this, items));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Habilitamos el icono de la app por si hay algún estilo que lo deshabilitó
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                // Inhabilitamos las opciones del menu.
                menuOptionEnabled = false;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                // Habilitamos las opciones del menu.
                menuOptionEnabled = true;
            }
        };

        // Le cambiamos la escucha al layout.
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            if (getIntent().getSerializableExtra(ARG_NOTIFICATION_EXC) != null) {
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyAppFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyFragment()).commit();

                Fragment fragment = new ExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(ExcursionFragment.EXCURSION_KEY, getIntent().getSerializableExtra(ARG_NOTIFICATION_EXC));
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else {
                selectItem(1);
            }
        }

        // Cuando llegamos a esta actividad desde una notificación
        if(getIntent().getAction().equals("OPEN_EXC_FRAGMENT")) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyAppFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyFragment()).commit();

            Fragment fragment = new ExcursionFragment();
            Bundle args = new Bundle();
            args.putSerializable(ExcursionFragment.EXCURSION_KEY, getIntent().getSerializableExtra(ARG_NOTIFICATION_EXC));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        // Se comprueba la alarma para saber si hay que lanzarla o no.
        internetAlarm = (new InternetAlarmManager(this)).loadInternetAlarm();
        if (internetAlarm == null) {
            showMessageDialog("¡RECUERDA!" +
            "\nLa app necesita conexión a Internet para poder desplegar todo su potencial:" +
            "\n\t- Almacenamiento y recuperación de fotografías." +
            "\n\t- Geolocalización." +
            "\n\t- Conexión con nuestros servidores.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (menuOptionEnabled) {

            int id = item.getItemId();

            if (id == R.id.mnu_add) {
                Fragment fragment = new FormExcursionesFragment();
                Bundle args = new Bundle();
                args.putSerializable(FormExcursionesFragment.FORM_EXCURSION_KEY, null);
                args.putSerializable(FormExcursionesFragment.FORM_EXCURSIONES, arrExcursiones);
                args.putString(FormExcursionesFragment.ARG_FORM_EXCURSIONES_TITLE, "Formulario");
                args.putString(FormExcursionesFragment.ARG_FORM_EXCURSIONES_SOURCE, "Mis Excursiones");
                fragment.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincronizamos el estado del drawer.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Cambiamos las configuraciones del drawer si hubo modificaciones
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position) {

        // Declaramos el fragment.
        Fragment fragment;

        // Declaramos los argumentos que le pasaremos como parámetro al fragment.
        Bundle args = new Bundle();

        // Inicializamos los paneles vacios (este paso sirve para borrar/eliminar el fragment que estaba en uso.
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyAppFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyFragment()).commit();

        // Inicializamos el fragment dependiendo de la posición de la lista desplegable que se
        // haya clickado. Además, le pasamos como parámetro al fragment la posición del elemento
        // de la lista desplegable clickado.
        switch (position) {
            case 0:
                fragment = new MisExcursionesFragment();
                args.putInt(MisExcursionesFragment.ARG_MIS_EXCURSIONES_NUMBER, position);
                args.putSerializable(MisExcursionesFragment.ARG_MIS_EXCURSIONES, arrExcursiones);
                fragment.setArguments(args);
                break;
            case 1:
                fragment = new BuscarExcursionesFragment();
                args.putInt(BuscarExcursionesFragment.ARG_BUSCAR_EXCURSIONES_NUMBER, position);
                args.putSerializable(BuscarExcursionesFragment.ARG_MIS_EXCURSIONES, arrExcursiones);
                fragment.setArguments(args);
                break;
            case 2:
                AjustesFragment ajustesFragment = new AjustesFragment();
                args.putSerializable(AjustesFragment.ARG_MIS_EXCURSIONES, arrExcursiones);
                ajustesFragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, ajustesFragment).commit();
                fragment = null;
                break;
            default: fragment = null;
        }

        if (fragment != null) {
            // Reemplazamos el contenido principal del layout de la actividad principal
            // por el del fragment.
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        // Se actualiza el item seleccionado y el título, después de cerrar el drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    private void createExcursionList() {

        // Creamos tres excursiones de prueba y las metemos al array de Excursiones.
        Excursion exc1 = new Excursion(1,"Ruta del Cares", "Un sitio espectacular con unas vistas impresionantes. Ideal para ir con la familia y para sacar fotos de los acantilados.", "Medio", 12,"Arenas de Cabrales",Float.parseFloat("43.253143"),Float.parseFloat("-4.844181"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_1_1.jpg");
        Excursion exc2 = new Excursion(2,"Ventana Relux", "Unas vistas impresionantes desde la ventana. Una caída libre espectacular que merece ser fotografiada. Ideal para la familia.", "Facil", 2.7,"Karrantza Harana",Float.parseFloat("43.250062"),Float.parseFloat("-3.411184"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_2_2.jpg");
        Excursion exc3 = new Excursion(3,"Faro del Caballo", "Excursión muy bonita para ver todos los acantilados del monte Buciero de Santoña. Ideal para ir en pareja y para pasar el día.", "Medio", 12,"Santoña",Float.parseFloat("43.451673"),Float.parseFloat("-3.425712"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_3_3.jpg");
        Excursion exc4 = new Excursion(4,"Gorbea", "Subida preciosa a uno de los montes más característicos de Bizkaia. Recorrido un poco duro pero el paisaje merece la pena.", "Dificil", 12,"Areatza",Float.parseFloat("43.034984"),Float.parseFloat("-2.779891"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_4_4.jpg");
        Excursion exc5 = new Excursion(5,"Ruta del Río Borosa", "Espectacular ruta que nos permite apreciar toda la belleza del Rio Borosa y del Parque Nacional de la Sierra de Cazorla.", "Facil", 20,"Jaén",Float.parseFloat("38.009718"),Float.parseFloat("-2.858513"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_5_5.jpg");
        Excursion exc6 = new Excursion(6,"Chachorros del Río Chiller", "Explorarás un paisaje en el que el agua es tan protagonista que lo mejor que puedes hacer es llevar un calzado que no te importe que se moje.", "Facil", 15,"Nerja",Float.parseFloat("36.831615"),Float.parseFloat("-3.853639"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_6_6.jpg");
        Excursion exc7 = new Excursion(7,"Ruta de los Pantaneros", "Fabuloso descenso de 80 metros del cañón y cruzar varios puentes colgantes que te llevarán a través de paisajes de Bosque de Ribera y Matorral Mediterráneo.", "Dificil", 5,"Chulilla",Float.parseFloat("39.670969"),Float.parseFloat("-0.888563"),"http://res.cloudinary.com/trekkingaventura/image/upload/c2a61b1cd1ac1d22_7_7.jpg");

        arrExcursiones = new ArrayList<Excursion>();
        arrExcursiones.add(exc1);
        arrExcursiones.add(exc2);
        arrExcursiones.add(exc3);
        arrExcursiones.add(exc4);
        arrExcursiones.add(exc5);
        arrExcursiones.add(exc6);
        arrExcursiones.add(exc7);
    }

    public void showMessageDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setPositiveButton("No volver a mostrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final InternetAlarm internetAlarm = new InternetAlarm(usuario.getIdUsuario(), false);
                (new InternetAlarmManager(MainActivity.this)).deleteFile();
                (new InternetAlarmManager(MainActivity.this)).saveInternetAlarm(internetAlarm);
            }
        });
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                (new InternetAlarmManager(MainActivity.this)).deleteFile();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Este listener redireccionará las peticiones que se realicen sobre cada uno de los
    // elementos de la lista despegable al método selectItem; indicándole el elemento de
    // la lista que fue clicado.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private class ObtenerUsuarioTask extends AsyncTask<String, Void, UsuarioDB> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Obteniendo datos del usuario...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected UsuarioDB doInBackground(String... params) {
            UsuarioDB usuario = null;

            String data = ((new RestClientManager()).obtenerUsuarioPorId(params[0]));
            if (data != null) {
                try {
                    usuario = RestJSONParserManager.getUsuarioDB(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return usuario;
        }

        @Override
        protected void onPostExecute(UsuarioDB usuarioDB) {
            super.onPostExecute(usuarioDB);
            if (usuarioDB != null) {
                // Si el usuario ya existe, inicializamos la variable 'usuario' de la clase
                usuario = usuarioDB;
                Log.i("USUARIO", "El usuario ya existe");
                Log.i("USUARIO", "Id Usuario existente: " + usuario.getIdUsuario());
            } else {
                // Insertar el usuario.
                Log.i("USUARIO", "Insertar usuario");
                InsertarUsuarioTask task = new InsertarUsuarioTask();
                task.execute(new UsuarioDB[]{new UsuarioDB(usuario.getIdUsuario())});
            }
            progressDialog.dismiss();
        }
    }

    private class InsertarUsuarioTask extends AsyncTask<UsuarioDB, Void, UsuarioDB> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Almacenando datos del usuario...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected UsuarioDB doInBackground(UsuarioDB... params) {
            UsuarioDB usuario = null;

            String data = ((new RestClientManager()).insertarUsuario(params[0]));
            if (data != null) {
                try {
                    usuario = RestJSONParserManager.getUsuarioDB(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return usuario;
        }

        @Override
        protected void onPostExecute(UsuarioDB usuarioDB) {
            super.onPostExecute(usuarioDB);
            if (usuarioDB != null) {
                // Se ha insertado correctamente el usuario.
                Log.i("USUARIO", "El usuario '" + usuarioDB.getIdUsuario() + "' se ha insertado correctamente");
            } else {
                // Insertar el usuario.
                Log.i("USUARIO", "No se ha podido insertar el usuario");
            }
            progressDialog.dismiss();
        }
    }
}
