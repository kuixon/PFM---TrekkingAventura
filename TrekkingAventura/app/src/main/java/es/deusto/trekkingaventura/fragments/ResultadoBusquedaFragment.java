package es.deusto.trekkingaventura.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.adapters.ExcursionListAdapter;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entitiesDB.OpinionDB;
import es.deusto.trekkingaventura.restDatabaseAPI.RestClientManager;
import es.deusto.trekkingaventura.restDatabaseAPI.RestJSONParserManager;

import static es.deusto.trekkingaventura.fragments.FormExcursionesFragment.ARG_FORM_EXCURSIONES_TITLE;

/**
 * Created by salgu on 28/02/2017.
 */

public class ResultadoBusquedaFragment extends Fragment {

    public static final String ARG_RESULTADO_BUSQUEDA_TITLE = "resultado_búsqueda_title";
    public static final String ARG_RESULTADO_BUSQUEDA = "resultado_búsqueda";

    private ListView listExcursiones;
    private ArrayList<Excursion> arrExcursiones;
    private ExcursionListAdapter adpExcursiones;

    public static boolean firstTime = true;

    public ResultadoBusquedaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_resultado_busqueda, container, false);
        String name = getArguments().getString(ARG_RESULTADO_BUSQUEDA_TITLE);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(name);

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        // Obtenemos la lista de excursiones del layout y la inicializamos.
        listExcursiones = (ListView) rootView.findViewById(R.id.listResultadoBusqueda);
        arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_RESULTADO_BUSQUEDA);

        // Inicializamos el list adapter personalizado y le cambiamos el adaptador a la lista por
        // el inicializado.
        adpExcursiones = new ExcursionListAdapter(getContext(), R.layout.excursion_list_adapter, arrExcursiones);
        listExcursiones.setAdapter(adpExcursiones);

        listExcursiones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrExcursiones.get(position).getName().equals("Banner Ropa")) {
                    Uri uri = Uri.parse("https://www.thenorthface.es/");
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                } else if (arrExcursiones.get(position).getName().equals("Banner Transporte")) {
                    Uri uri = Uri.parse("https://www.alsa.es/");
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                } else {
                    InicializarOpinionesTask task = new InicializarOpinionesTask();
                    task.execute(new Excursion[] {arrExcursiones.get(position)});
                }
            }
        });

        return rootView;
    }

    private class InicializarOpinionesTask extends AsyncTask<Excursion, Void, ArrayList<OpinionDB>> {
        String nombreExcursion = "";
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Cargando opiniones...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<OpinionDB> doInBackground(Excursion... params) {
            nombreExcursion = params[0].getName();

            ArrayList<OpinionDB> alo = null;

            String data = (new RestClientManager()).obtenerOpinionesPorIdExcursion(params[0].getIdExcursion());
            if (data != null) {
                try {
                    alo = RestJSONParserManager.getOpinionesPorIdExcursion(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return alo;
        }

        @Override
        protected void onPostExecute(ArrayList<OpinionDB> alo) {
            super.onPostExecute(alo);
            if (alo != null) {
                // Hay resultado de búsqueda
                Log.i("OPINIONES_EXCURSION", "La excursión tiene opiniones");

                ArrayList<Excursion> opiniones = new ArrayList<Excursion>();
                for (OpinionDB o : alo) {
                    opiniones.add(new Excursion(o.getIdOpinion(), o.getIdExcursion(), nombreExcursion, o.getOpinion(), "", 0, "", 0f, 0f, o.getFoto()));
                }

                Fragment fragment = new OpinionesExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(OpinionesExcursionFragment.ARG_RESULTADO_BUSQUEDA, arrExcursiones);
                args.putSerializable(OpinionesExcursionFragment.ARG_OPINIONES, opiniones);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else {
                // NO hay resultado de búsqueda
                Log.i("OPINIONES_EXCURSION", "La excursión NO tiene opiniones");

                ArrayList<Excursion> opiniones = new ArrayList<Excursion>();

                Fragment fragment = new OpinionesExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(OpinionesExcursionFragment.ARG_RESULTADO_BUSQUEDA, arrExcursiones);
                args.putSerializable(OpinionesExcursionFragment.ARG_OPINIONES, opiniones);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }

            progressDialog.dismiss();
        }
    }
}
