package es.deusto.trekkingaventura.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.adapters.ExcursionListAdapter;
import es.deusto.trekkingaventura.entities.Excursion;

public class MisExcursionesFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final String ARG_MIS_EXCURSIONES_NUMBER = "mis_excursiones_number";
    public static final String ARG_MIS_EXCURSIONES = "mis_excursiones";

    private ListView listExcursiones;
    private ArrayList<Excursion> arrExcursiones;
    private ArrayList<Excursion> arrExcursionesFiltered;
    private ExcursionListAdapter adpExcursiones;

    private SharedPreferences sharedPref;
    private double distance;
    private boolean filter;

    private GoogleApiClient mGoogleApiClient;

    public MisExcursionesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_mis_excursiones, container, false);
        int i = getArguments().getInt(ARG_MIS_EXCURSIONES_NUMBER);
        String nameItemSelected = getResources().getStringArray(R.array.Tags)[i];

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(nameItemSelected);

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        // Obtenemos la lista de excursiones del layout y la inicializamos.
        listExcursiones = (ListView) rootView.findViewById(R.id.listExcursiones);
        arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(ARG_MIS_EXCURSIONES);
        arrExcursionesFiltered = new ArrayList<Excursion>();
        for (Excursion e : arrExcursiones) {
            arrExcursionesFiltered.add(e);
        }

        // Inicializamos el list adapter personalizado y le cambiamos el adaptador a la lista por
        // el inicializado.
        adpExcursiones = new ExcursionListAdapter(getContext(), R.layout.excursion_list_adapter, arrExcursionesFiltered);
        listExcursiones.setAdapter(adpExcursiones);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        listExcursiones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(ExcursionFragment.EXCURSION_KEY, arrExcursionesFiltered.get(position));
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        String distanceText = sharedPref.getString("distance", "");
        if (distanceText != null && !distanceText.equals("")) {
            distance = Double.parseDouble(distanceText) * 1000;
        } else {
            distance = 0;
        }

        filter = sharedPref.getBoolean("filter", false);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mis_excursiones, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.mnu_search).getActionView();
        final MenuItem addItem = menu.findItem(R.id.mnu_add);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                doMySearch(newText);
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
    }

    private void doMySearch(String query) {
        if (query.isEmpty()) {
            arrExcursionesFiltered = new ArrayList<Excursion>(arrExcursiones);
        } else {
            arrExcursionesFiltered = new ArrayList<Excursion>();

            for (int i = 0; i < arrExcursiones.size(); i++) {
                if (arrExcursiones.get(i).getName().toLowerCase().contains(query.toLowerCase())) {
                    arrExcursionesFiltered.add(arrExcursiones.get(i));
                }
            }
        }

        adpExcursiones = new ExcursionListAdapter(getContext(), R.layout.excursion_list_adapter, arrExcursionesFiltered);

        listExcursiones.setAdapter(adpExcursiones);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Location getUserLocation() {
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        return userLocation;
    }

    private void applyFilter(Location userLocation) {
        final ArrayList<Excursion> arrFilteredExcursiones = arrExcursionesFiltered;
        arrExcursiones = new ArrayList<Excursion>();

        Location excursionLocation = new Location("");
        for (Excursion e : arrFilteredExcursiones) {
            excursionLocation.setLatitude(e.getLatitude());
            excursionLocation.setLongitude(e.getLongitude());
            Log.i("Distancia", Float.toString(excursionLocation.distanceTo(userLocation)));
            if (excursionLocation.distanceTo(userLocation) <= distance) {
                arrExcursiones.add(e);
            }
        }

        arrExcursionesFiltered = new ArrayList<Excursion>(arrExcursiones);

        adpExcursiones = new ExcursionListAdapter(getContext(), R.layout.excursion_list_adapter, arrExcursionesFiltered);

        listExcursiones.setAdapter(adpExcursiones);
    }

    private boolean connectToGooglePlayServices(){
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext()) != ConnectionResult.SUCCESS){
            return false;
        }

        mGoogleApiClient =  new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
        return true;
    }

    private void disconnectFromGooglePlayServices(){
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
            Log.i("Location client", "Disconnected");
        }
    }

    public void showMessageDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!connectToGooglePlayServices()) {
            showMessageDialog("Google Play Services are not available in this device");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnectFromGooglePlayServices();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Location client", "Connected");

        if (filter && distance > 0) {
            Location userLocation = getUserLocation();
            applyFilter(userLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Location client", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showMessageDialog("Internet connection is not available");
    }
}
