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
import es.deusto.trekkingaventura.entities.OpinionExtendida;
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
    private ArrayList<OpinionExtendida> arrOpinionesExtendidas;

    // Para cuando se accede desde una notificación
    public static final String ARG_NOTIFICATION_EXC = "arg_notification_exc";

    private static InternetAlarm internetAlarm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = new UsuarioDB();
        usuario.setIdUsuario(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
        Log.i("INFO_USER", "Id: " + usuario.getIdUsuario());

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
                // Comprobamos si el usuario existe en la BD y si no existe lo insertamos.
                ObtenerUsuarioTask task = new ObtenerUsuarioTask();
                task.execute(new String[]{usuario.getIdUsuario()});
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

                // Inicializamos la lista de excursiones
                InicializarExcursionesTask task = new InicializarExcursionesTask();
                task.execute(new String[]{usuario.getIdUsuario()});
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

                // Inicializamos la lista de excursiones
                InicializarExcursionesTask taskExcursiones = new InicializarExcursionesTask();
                taskExcursiones.execute(new String[]{usuarioDB.getIdUsuario()});
            } else {
                // Insertar el usuario.
                Log.i("USUARIO", "No se ha podido insertar el usuario");
            }
            progressDialog.dismiss();
        }
    }

    private class InicializarExcursionesTask extends AsyncTask<String, Void, ArrayList<OpinionExtendida>> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Cargando excursiones del usuario...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<OpinionExtendida> doInBackground(String... params) {
            ArrayList<OpinionExtendida> aloe = null;

            String data = ((new RestClientManager()).obtenerOpinionesUsuario(params[0]));
            if (data != null) {
                try {
                    aloe = RestJSONParserManager.getOpinionesExtendidas(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return aloe;
        }

        @Override
        protected void onPostExecute(ArrayList<OpinionExtendida> aloe) {
            super.onPostExecute(aloe);
            if (aloe != null) {
                // El usuario tiene excursiones
                Log.i("EXCURSIONES", "El usuario '" + usuario.getIdUsuario() + "' tiene excursiones");
                arrExcursiones = new ArrayList<Excursion>();
                for (OpinionExtendida oe : aloe) {
                    arrExcursiones.add(new Excursion(oe.getExcursion().getIdExcursion(), oe.getExcursion().getNombre(),
                            oe.getOpinion(), oe.getExcursion().getNivel(), oe.getExcursion().getDistancia(),
                            oe.getExcursion().getLugar(), oe.getExcursion().getLatitud(), oe.getExcursion().getLongitud(),
                            oe.getImgPath()));
                }
            } else {
                // El usuario no tiene excursiones
                arrExcursiones = new ArrayList<Excursion>();
                Log.i("EXCURSIONES", "El usuario no tiene excursiones");
            }

            selectItem(0);

            progressDialog.dismiss();
        }
    }
}
