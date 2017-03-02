package es.deusto.trekkingaventura.activities;

import android.content.res.Configuration;
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

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.adapters.DrawerListAdapter;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.fragments.EmptyAppFragment;
import es.deusto.trekkingaventura.fragments.EmptyFragment;
import es.deusto.trekkingaventura.fragments.BuscarExcursionesFragment;
import es.deusto.trekkingaventura.fragments.ExcursionFragment;
import es.deusto.trekkingaventura.fragments.AjustesFragment;
import es.deusto.trekkingaventura.fragments.FormExcursionesFragment;
import es.deusto.trekkingaventura.fragments.MisExcursionesFragment;
import es.deusto.trekkingaventura.entities.DrawerItem;

public class MainActivity extends AppCompatActivity {

    /*
     DECLARACIONES
     */
    public static String USER_ID;

    private Toolbar toolbar;

    private boolean menuOptionEnabled = true;

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence activityTitle;
    private CharSequence itemTitle;
    private String[] tagTitles;

    private ArrayList<Excursion> arrExcursiones;

    // Para cuando se accede desde una notificación
    public static final String ARG_NOTIFICATION_EXC = "arg_notification_exc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos el ID del Usuario
        USER_ID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

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
            selectItem(0);
        }

        // Cuando llegamos a esta actividad desde una notificación
        if(getIntent().getAction().equals("OPEN_EXC_FRAGMENT")) {
            Log.i("INFO_NOT", "Se entra a MainActivity tras clickar la notificacion");
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyAppFragment()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyFragment()).commit();

            Fragment fragment = new ExcursionFragment();
            Bundle args = new Bundle();
            args.putSerializable(ExcursionFragment.EXCURSION_KEY, getIntent().getSerializableExtra(ARG_NOTIFICATION_EXC));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
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
        Excursion exc1 = new Excursion(1,"Ruta del Cares", "Un sitio espectacular con unas vistas impresionantes. Ideal para ir con la familia y para sacar fotos de los acantilados.", "Medio", 12,"Arenas de Cabrales",Float.parseFloat("43.2551652"),Float.parseFloat("-4.8366377"),null);
        Excursion exc2 = new Excursion(2,"Ventana Relux", "Unas vistas impresionantes desde la ventana. Una caída libre espectacular que merece ser fotografiada. Ideal para la familia.", "Facil", 2.7,"Karrantza Harana",Float.parseFloat("43.2499237"),Float.parseFloat("-3.4108149"),null);
        Excursion exc3 = new Excursion(3,"Faro del Caballo", "Excursión muy bonita para ver todos los acantilados del monte Buciero de Santoña. Ideal para ir en pareja y para pasar el día.", "Medio", 12,"Santoña",Float.parseFloat("43.4514626"),Float.parseFloat("-3.4256904"),null);
        Excursion exc4 = new Excursion(4,"Gorbea", "Subida preciosa a uno de los montes más característicos de Bizkaia. Recorrido un poco duro pero el paisaje merece la pena.", "Dificil", 12,"Areatza",Float.parseFloat("43.0350000"),Float.parseFloat("-2.7798800"),null);

        arrExcursiones = new ArrayList<Excursion>();
        arrExcursiones.add(exc1);
        arrExcursiones.add(exc2);
        arrExcursiones.add(exc3);
        arrExcursiones.add(exc4);
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
}
