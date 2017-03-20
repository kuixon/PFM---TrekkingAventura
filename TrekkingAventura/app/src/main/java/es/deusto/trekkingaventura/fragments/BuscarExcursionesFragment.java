package es.deusto.trekkingaventura.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.ExcursionDestacada;
import es.deusto.trekkingaventura.entitiesDB.ExcursionDB;
import es.deusto.trekkingaventura.restDatabaseAPI.RestClientManager;
import es.deusto.trekkingaventura.restDatabaseAPI.RestJSONParserManager;

public class BuscarExcursionesFragment extends Fragment {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_BUSCAR_EXCURSIONES_NUMBER = "buscar_excursiones_number";

    private ArrayList<Excursion> arrExcursionesBusqueda;

    private EditText edtName;
    private EditText edtLocation;
    private EditText edtDistance;
    private RadioGroup rdgLevel;
    private Button btnFind;

    public BuscarExcursionesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (MainActivity.appFirstTimeOpened) {
            InicializarExcursionesBusquedaTask task = new InicializarExcursionesBusquedaTask();
            task.execute();

            return null;
        } else {
            // Obtenemos el nombre del elemento de la lista seleccionado.
            View rootView = inflater.inflate(R.layout.fragment_buscar_excursiones, container, false);
            int i = getArguments().getInt(ARG_BUSCAR_EXCURSIONES_NUMBER);
            String nameItemSelected = getResources().getStringArray(R.array.Tags)[i];

            // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
            // a un método de la actividad llamado setTitle.
            getActivity().setTitle(nameItemSelected);

            // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
            setHasOptionsMenu(true);

            edtName = (EditText) rootView.findViewById(R.id.edName);
            edtLocation = (EditText) rootView.findViewById(R.id.edLocation);
            edtDistance = (EditText) rootView.findViewById(R.id.edDistance);
            rdgLevel = (RadioGroup) rootView.findViewById(R.id.rGroup);

            btnFind = (Button) rootView.findViewById(R.id.button_find);
            btnFind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    find(v);
                }
            });

            return rootView;
        }
    }

    public void find(View v) {
        String nombre = edtName.getText().toString().equals("") ? "nulo" : edtName.getText().toString();
        String lugar = edtLocation.getText().toString().equals("") ? "nulo" : edtLocation.getText().toString();
        String distancia = edtDistance.getText().toString().equals("") ? "nulo" : edtDistance.getText().toString();;
        String nivel = "nulo";
        switch (rdgLevel.getCheckedRadioButtonId()) {
            case R.id.b_level_low:
                nivel = "Facil";
                break;
            case R.id.b_level_medium:
                nivel = "Medio";
                break;
            case R.id.b_level_high:
                nivel = "Dificil";
                break;
        }

        BuscarExcursionesTask task = new BuscarExcursionesTask();
        task.execute(new String[] {nombre, lugar, distancia, nivel});
    }

    private class InicializarExcursionesBusquedaTask extends AsyncTask<Void, Void, ArrayList<ExcursionDestacada>> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Cargando excursiones destacadas...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<ExcursionDestacada> doInBackground(Void... params) {
            ArrayList<ExcursionDestacada> aled = null;

            String data = (new RestClientManager()).obtenerExcursionesDestacadas();
            if (data != null) {
                try {
                    aled = RestJSONParserManager.getExcursionesDestacadas(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return aled;
        }

        @Override
        protected void onPostExecute(ArrayList<ExcursionDestacada> aled) {
            super.onPostExecute(aled);
            if (aled != null) {
                // Hay excursiones en la base de datos
                Log.i("EXCURSIONES_BUSQUEDA", "Hay excursiones en la BD");

                // Se muestran las 4 excursiones que tienen más opiniones, las destacadas.
                arrExcursionesBusqueda = new ArrayList<Excursion>();

                int[] posiciones = new int[aled.size()];
                for (int i = 0; i < posiciones.length; i++) {
                    posiciones[i] = aled.get(i).getNumOpiniones();
                }
                Arrays.sort(posiciones);

                int aux = 1;
                for (ExcursionDestacada ed : aled) {
                    if (ed.getNumOpiniones() == posiciones[posiciones.length - aux]) {
                        arrExcursionesBusqueda.add(new Excursion(ed.getIdExcursion(), ed.getNombre(), "",
                                ed.getNivel(), ed.getDistancia(), ed.getLugar(), ed.getLatitud(), ed.getLongitud(), ed.getFoto()));
                        aux++;

                        if (aux == 5) {
                            break;
                        }
                    }
                }

                ArrayList<Excursion> arrExcursionesBanner = new ArrayList<Excursion>(arrExcursionesBusqueda);
                arrExcursionesBanner.add(new Excursion(-100,"Banner Ropa", "", "", 0,"",0,0,"Banner Ropa"));
                arrExcursionesBanner.add(new Excursion(-100,"Banner Transporte", "", "", 0,"",0,0,"Banner Transporte"));
                Collections.shuffle(arrExcursionesBanner);

                Fragment fragment = new ResultadoBusquedaFragment();
                Bundle args = new Bundle();
                args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Excursiones destacadas");
                args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursionesBanner);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

                MainActivity.appFirstTimeOpened = false;
            } else {
                // NO hay excursiones en la base de datos
                arrExcursionesBusqueda = new ArrayList<Excursion>();
                Log.i("EXCURSIONES_BUSQUEDA", "No hay excursiones en la BD");

                Fragment fragment = new ResultadoBusquedaFragment();
                Bundle args = new Bundle();
                args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Excursiones destacadas");
                args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursionesBusqueda);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }

            progressDialog.dismiss();
        }
    }

    private class BuscarExcursionesTask extends AsyncTask<String, Void, ArrayList<ExcursionDB>> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Buscando excursiones...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<ExcursionDB> doInBackground(String... params) {
            ArrayList<ExcursionDB> ale = null;

            String data = (new RestClientManager()).buscarExcursionesPorCriterio(params[0], params[1], params[2], params[3]);
            if (data != null) {
                try {
                    ale = RestJSONParserManager.getExcursionesBusqueda(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return ale;
        }

        @Override
        protected void onPostExecute(ArrayList<ExcursionDB> ale) {
            super.onPostExecute(ale);
            if (ale != null) {
                // Hay resultado de búsqueda
                Log.i("EXCURSIONES_BUSQUEDA", "Hay resultado de búsqueda");
                ArrayList<Excursion> arrExcursiones = new ArrayList<Excursion>();
                for (ExcursionDB e : ale) {

                    arrExcursiones.add(new Excursion(e.getIdExcursion(), e.getNombre(), "", e.getNivel(),
                            e.getDistancia(), e.getLugar(), e.getLatitud(), e.getLongitud(), e.getFoto()));
                }

                Fragment fragment = new ResultadoBusquedaFragment();
                Bundle args = new Bundle();
                args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Resultado de la búsqueda");
                args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursiones);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else {
                // NO hay resultado de búsqueda
                Log.i("EXCURSIONES_BUSQUEDA", "NO hay resultado de búsqueda");

                arrExcursionesBusqueda = new ArrayList<Excursion>();

                Fragment fragment = new ResultadoBusquedaFragment();
                Bundle args = new Bundle();
                args.putString(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA_TITLE, "Resultado de la búsqueda");
                args.putSerializable(ResultadoBusquedaFragment.ARG_RESULTADO_BUSQUEDA, arrExcursionesBusqueda);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }

            progressDialog.dismiss();
        }
    }
}
