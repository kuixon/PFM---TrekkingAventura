package es.deusto.trekkingaventura.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.activities.MainActivity;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.utilities.ImageHelper;

import static android.app.Activity.RESULT_OK;

public class FormExcursionesFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final String ARG_FORM_EXCURSIONES_TITLE = "form_excursiones_title";
    public static final String ARG_FORM_EXCURSIONES_SOURCE = "form_excursiones_source";
    public static final String FORM_EXCURSION_KEY = "form_excursion_key";
    public static final int IMG_FROM_CAMERA = 1;
    public static final int IMG_FROM_GALLERY = 2;

    private GoogleApiClient mGoogleApiClient;

    private Excursion excursion;

    private EditText edtName;
    private EditText edtDescription;
    private EditText edtLocation;
    private EditText edtDistance;
    private RadioGroup rdgLevel;
    private EditText edtLatitude;
    private EditText edtLongitude;
    private Button btnGeolocate;
    private Button btnAddImage;

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
        String name = getArguments().getString(ARG_FORM_EXCURSIONES_TITLE);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(name);

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        edtName = (EditText) rootView.findViewById(R.id.edtName);
        edtDescription = (EditText) rootView.findViewById(R.id.edtDescription);
        edtLocation = (EditText) rootView.findViewById(R.id.edtLocation);
        edtDistance = (EditText) rootView.findViewById(R.id.edtDistance);
        rdgLevel = (RadioGroup) rootView.findViewById(R.id.radioGroup);
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

        excursion = (Excursion) getArguments().getSerializable(FORM_EXCURSION_KEY);
        if (excursion != null) {
            initializeFields(excursion);
        }

        return rootView;
    }

    private void initializeFields(Excursion excursion) {
        edtName.setText(excursion.getName());
        edtDescription.setText(excursion.getOpinion());
        edtLocation.setText(excursion.getLocation());
        edtDistance.setText(Double.toString(excursion.getTravelDistance()));
        switch (excursion.getLevel()) {
            case "Facil":
                rdgLevel.check(R.id.button_level_low);
                break;
            case "Medio":
                rdgLevel.check(R.id.button_level_medium);
                break;
            case "Dificil":
                rdgLevel.check(R.id.button_level_high);
                break;
        }
        edtLatitude.setText(Float.toString(excursion.getLatitude()));
        edtLongitude.setText(Float.toString(excursion.getLongitude()));
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

        int id = item.getItemId();

        if (id == R.id.mnu_cancel_create_exc) {
            if (getArguments().getString(ARG_FORM_EXCURSIONES_SOURCE) != null &&
                    getArguments().getString(ARG_FORM_EXCURSIONES_SOURCE).equals("Mis Excursiones")) {
                Fragment fragment = new MisExcursionesFragment();
                Bundle args = new Bundle();
                args.putInt(MisExcursionesFragment.ARG_MIS_EXCURSIONES_NUMBER, 0);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else {
                Fragment fragment = new ExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(ExcursionFragment.EXCURSION_KEY, excursion);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }

            return true;
        } else if (id == R.id.mnu_create_exc) {

            // En este punto, se tendría que crear la excursión y almacenarla en la BDD del servidor.

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void geolocate(View v) {
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            Log.i("Error", "No se ha podido obtener la dirección.");
            e.printStackTrace();
        }

        Log.i("Latitude", Double.toString(loc.getLatitude()));
        Log.i("Longitude", Double.toString(loc.getLongitude()));
        Log.i("City", addresses.get(0).getLocality());
        edtLatitude.setText(Double.toString(loc.getLatitude()));
        edtLongitude.setText(Double.toString(loc.getLongitude()));
        edtLocation.setText(addresses.get(0).getLocality());
    }

    private void addImage(View v) {

        final String[] menu_tags = getResources().getStringArray(R.array.Add_Image_Menu);
        final CharSequence[] options = {menu_tags[0], menu_tags[1], menu_tags[2]};

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle(R.string.add_menu_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(options[0])) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        File image = null;
                        try {
                            image = ImageHelper.createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (image != null) {
                            imageUri = Uri.fromFile(image);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, IMG_FROM_CAMERA);
                        }
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
                // Obtenemos la imagen sacada con la cámara y de momento no hacemos nada con ella
                getContext().getContentResolver().notifyChange(imageUri, null);
                ContentResolver cr = getContext().getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
                } catch (Exception e) {
                    Log.d("APP", "Failed to load", e);
                }

                // Cambiamos el el TextView con el Path de la foto.
                txtImage.setText(imageUri.getPath());
                txtImage.setVisibility(View.VISIBLE);
                deleteSelectedImg.setVisibility(View.VISIBLE);
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
