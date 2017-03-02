package es.deusto.trekkingaventura.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.utilities.ImageHelper;

import static android.app.Activity.RESULT_OK;

public class FormExcursionesFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Este atributo nos servirá para saber la posición del item seleccionado de la lista
    // desplegable.
    public static final int REQUEST_LOCATION_ENABLE = 1;
    public static final String ARG_FORM_EXCURSIONES_TITLE = "form_excursiones_title";
    public static final String ARG_FORM_EXCURSIONES_SOURCE = "form_excursiones_source";
    public static final String FORM_EXCURSION_KEY = "form_excursion_key";
    public static final String FORM_EXCURSIONES = "form_excursiones";
    public static final int IMG_FROM_CAMERA = 1;
    public static final int IMG_FROM_GALLERY = 2;

    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Excursion> arrExcursiones;
    private Excursion excursion;

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutDescription;
    private TextInputLayout inputLayoutLocation;
    private TextInputLayout inputLayoutDistance;
    private TextInputLayout inputLayoutLevel;
    private TextInputLayout inputLayoutLatitude;
    private TextInputLayout inputLayoutLongitude;

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

        arrExcursiones = (ArrayList<Excursion>) getArguments().getSerializable(FORM_EXCURSIONES);

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.input_layout_name);
        inputLayoutDescription = (TextInputLayout) rootView.findViewById(R.id.input_layout_description);
        inputLayoutLocation = (TextInputLayout) rootView.findViewById(R.id.input_layout_location);
        inputLayoutDistance = (TextInputLayout) rootView.findViewById(R.id.input_layout_distance);
        inputLayoutLevel = (TextInputLayout) rootView.findViewById(R.id.input_layout_level);
        inputLayoutLatitude = (TextInputLayout) rootView.findViewById(R.id.input_layout_latitude);
        inputLayoutLongitude = (TextInputLayout) rootView.findViewById(R.id.input_layout_longitude);

        edtName = (EditText) rootView.findViewById(R.id.edtName);
        edtDescription = (EditText) rootView.findViewById(R.id.edtDescription);
        edtLocation = (EditText) rootView.findViewById(R.id.edtLocation);
        edtDistance = (EditText) rootView.findViewById(R.id.edtDistance);

        rdgLevel = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        rdgLevel.check(R.id.button_level_low);

        edtLatitude = (EditText) rootView.findViewById(R.id.edtLatitude);
        edtLongitude = (EditText) rootView.findViewById(R.id.edtLongitude);

        txtImage = (TextView) rootView.findViewById(R.id.txtImage);

        btnGeolocate = (Button) rootView.findViewById(R.id.button_geolocate);
        btnGeolocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobamos los permisos de Geolocalización
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.i("INFO_GEOLOC", "Es necesaria una explicación de por qué se solicitan los permisos");
                        Toast.makeText(getActivity(), "La aplicación utiliza tu localización para ayudarte a" +
                                " rellenar los campos 'Lugar', 'Latitud' y 'Longitud' del formulario.", Toast.LENGTH_LONG).show();
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ENABLE);
                    } else {
                        Log.i("INFO_GEOLOC", "Se solicitan los permisos");
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ENABLE);
                    }
                } else {
                    Log.i("INFO_GEOLOC", "El usuario tiene permisos");
                    geolocate();
                }
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
                args.putSerializable(MisExcursionesFragment.ARG_MIS_EXCURSIONES, arrExcursiones);
                args.putInt(MisExcursionesFragment.ARG_MIS_EXCURSIONES_NUMBER, 0);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            } else {
                Fragment fragment = new ExcursionFragment();
                Bundle args = new Bundle();
                args.putSerializable(ExcursionFragment.EXCURSION_KEY, excursion);
                args.putSerializable(ExcursionFragment.ARG_EXCURSIONES, arrExcursiones);
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            }

            return true;
        } else if (id == R.id.mnu_create_exc) {
            // En este punto, se tendría que crear la excursión y almacenarla en la BDD del servidor. Por el momento,
            // añadimos/editamos la excursión y la cambiamos en memoria.
            if (validateFields()) {
                // Todo está bien validado
                showMessageDialog("Todo esta correcto");
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
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
                //excursion.setImgPath(imageUri.getPath());
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
                    //excursion.setImgPath(picturePath);
                }
            } else {
                Log.i("Error:", "No se ha seleccionado ninguna imagen.");
            }
        } else {
            Log.i("Error:", "No se ha seleccionado ningun elemento del menu");
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i("INFO_GEOLOC", "Se reciben las solicitudes de permisos.");
        switch (requestCode) {
            case REQUEST_LOCATION_ENABLE: {
                Log.i("INFO_GEOLOC", "Se entra al case de REQUEST_LOCATION_ENABLE");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("INFO_GEOLOC", "Se conceden los permisos");
                    geolocate();
                } else {
                    Log.i("INFO_GEOLOC", "Se deniegan los permisos");
                    Toast.makeText(getActivity(), "La app no puede utilizar esta funcionalidad porque no se han concedido" +
                            " permisos de localización.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
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

    private boolean validateFields() {
        if (!validateName()) {
            return false;
        }

        if (!validateDescription()) {
            return false;
        }

        if (!validateLocation()) {
            return false;
        }

        if (!validateDistance()) {
            return false;
        }

        if (!validateLatitude()) {
            return false;
        }

        if (!validateLongitude()) {
            return false;
        }

        return true;
    }

    private boolean validateName() {
        if (edtName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.error_required_name));
            requestFocus(edtName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDescription() {
        if (edtDescription.getText().toString().trim().isEmpty()) {
            inputLayoutDescription.setError(getString(R.string.error_required_description));
            requestFocus(edtDescription);
            return false;
        } else {
            inputLayoutDescription.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLocation() {
        if (edtLocation.getText().toString().trim().isEmpty()) {
            inputLayoutLocation.setError(getString(R.string.error_required_location));
            requestFocus(edtLocation);
            return false;
        } else {
            inputLayoutLocation.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDistance() {
        if (edtDistance.getText().toString().trim().isEmpty()) {
            inputLayoutDistance.setError(getString(R.string.error_required_distance));
            requestFocus(edtDistance);
            return false;
        } else {
            final double distance = Double.parseDouble(edtDistance.getText().toString().trim());

            if (distance == 0) {
                inputLayoutDistance.setError(getString(R.string.error_distance_value));
                requestFocus(edtDistance);
                return false;
            } else {
                inputLayoutDistance.setErrorEnabled(false);
            }
        }

        return true;
    }

    private boolean validateLatitude() {
        if (edtLatitude.getText().toString().trim().isEmpty()) {
            inputLayoutLatitude.setError(getString(R.string.error_required_latitude));
            requestFocus(edtLatitude);
            return false;
        } else {
            inputLayoutLatitude.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLongitude() {
        if (edtLongitude.getText().toString().trim().isEmpty()) {
            inputLayoutLongitude.setError(getString(R.string.error_required_longitude));
            requestFocus(edtLongitude);
            return false;
        } else {
            inputLayoutLongitude.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void geolocate() {
        Log.i("INFO_GEOLOC", "Se entra a geolocalizar.");
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            Log.i("Error", "No se ha podido obtener la dirección.");
            e.printStackTrace();
        }

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
}
