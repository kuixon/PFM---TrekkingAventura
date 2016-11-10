package es.deusto.trekkingaventura.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;

public class FormExcursionesFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_FORM_EXCURSIONES = "form_excursiones";

    private GoogleApiClient mGoogleApiClient;

    private Button btnGeolocate;
    private EditText edtLatitude;
    private EditText edtLongitude;

    public FormExcursionesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_form_excursiones, container, false);
        String name = getArguments().getString(ARG_FORM_EXCURSIONES);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(name);

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        btnGeolocate = (Button) rootView.findViewById(R.id.button_geolocate);
        btnGeolocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geolocate(v);
            }
        });

        edtLatitude = (EditText) rootView.findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) rootView.findViewById(R.id.edtLongitude);

        return rootView;
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

    public void geolocate(View v) {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("Latitude", Double.toString(loc.getLatitude()));
        Log.i("Longitude", Double.toString(loc.getLongitude()));
        edtLatitude.setText(Double.toString(loc.getLatitude()));
        edtLongitude.setText(Double.toString(loc.getLongitude()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.form_excursiones, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
    public void onConnected(Bundle bundle) {
        Log.i("Location client", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Location client", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showMessageDialog("Internet connection is not available");
    }
    /*
    private void addImage() {

        final CharSequence[] options = {"Take image", "Import from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AnimalFormActivity.this);
        builder.setTitle("Add image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(options[0])) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = null;
                    try {
                        image = ImageHelper.createImageFile();
                        imageUri = Uri.fromFile(image);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, IMG_FROM_CAMERA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (options[item].equals(options[1])) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMG_FROM_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    */
}
