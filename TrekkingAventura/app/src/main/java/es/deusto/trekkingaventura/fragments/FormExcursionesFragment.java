package es.deusto.trekkingaventura.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.utilities.ImageHelper;

import static android.app.Activity.RESULT_OK;

public class FormExcursionesFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_FORM_EXCURSIONES = "form_excursiones";
    public static final int IMG_FROM_CAMERA = 1;
    public static final int IMG_FROM_GALLERY = 2;

    private GoogleApiClient mGoogleApiClient;

    private Button btnGeolocate;
    private Button btnAddImage;
    private EditText edtLatitude;
    private EditText edtLongitude;
    private TextView txtImage;
    private ImageButton deleteSelectedImg;

    private Uri imageUri;

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

        edtLatitude = (EditText) rootView.findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) rootView.findViewById(R.id.edtLongitude);
        txtImage = (TextView) rootView.findViewById(R.id.txtImage);

        btnGeolocate = (Button) rootView.findViewById(R.id.button_geolocate);
        btnGeolocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geolocate(v);
            }
        });

        btnAddImage = (Button) rootView.findViewById(R.id.buttonAddImg);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage(v);
            }
        });

        deleteSelectedImg = (ImageButton) rootView.findViewById(R.id.deleteSelectedImg);
        deleteSelectedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtImage.setText("");
                txtImage.setVisibility(View.GONE);
                deleteSelectedImg.setVisibility(View.GONE);
            }
        });

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.form_excursiones, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void geolocate(View v) {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i("Latitude", Double.toString(loc.getLatitude()));
        Log.i("Longitude", Double.toString(loc.getLongitude()));
        edtLatitude.setText(Double.toString(loc.getLatitude()));
        edtLongitude.setText(Double.toString(loc.getLongitude()));
    }

    private void addImage(View v) {

        final CharSequence[] options = {"Take image", "Import from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMG_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContext().getContentResolver().query(selectedImage, filePath, null, null, null);
                if(c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Log.i("Ruta foto", picturePath);
                    txtImage.setText(picturePath);
                    txtImage.setVisibility(View.VISIBLE);
                    deleteSelectedImg.setVisibility(View.VISIBLE);
                }
                Log.i("INFO", "Se resuelve el intent correctamente");
            } else {
                Log.i("ERROR", "No se resuleve el intent correctamente");
            }
        } else if (requestCode == IMG_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContext().getContentResolver().query(selectedImage, filePath, null, null, null);
                if(c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Log.i("Ruta foto", picturePath);
                    txtImage.setText(picturePath);
                    txtImage.setVisibility(View.VISIBLE);
                    deleteSelectedImg.setVisibility(View.VISIBLE);
                }
            } else {
                Log.i("Error:", "No se ha seleccionado ninguna imagen.");
            }
        } else {
            Log.i("Error:", "No se ha seleccionado ningun elemento del menu");
        }

        super.onActivityResult(requestCode, resultCode, data);
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
}
