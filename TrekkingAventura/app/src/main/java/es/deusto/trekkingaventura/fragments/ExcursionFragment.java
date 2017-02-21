package es.deusto.trekkingaventura.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import es.deusto.trekkingaventura.R;
import es.deusto.trekkingaventura.entities.Excursion;
import es.deusto.trekkingaventura.entities.Weather;
import es.deusto.trekkingaventura.utilities.JSONWeatherParser;
import es.deusto.trekkingaventura.utilities.WeatherHttpClient;

/**
 * Created by salgu on 15/02/2017.
 */

public class ExcursionFragment extends Fragment {

    public static final String EXCURSION_KEY = "excursion_key";

    private Excursion excursion;

    private ImageView imgExc;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtLocation;
    private TextView txtDistance;
    private ImageView imgLevel;
    private TextView txtLatitude;
    private TextView txtLongitude;

    // Parámetros situación meteorológica
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView hum;
    private ImageView imgView;
    private LinearLayout panelTiempo;
    private LinearLayout panelTiempoNoDisponible;

    public ExcursionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Obtenemos el nombre del elemento de la lista seleccionado.
        View rootView = inflater.inflate(R.layout.fragment_excursion, container, false);

        excursion = (Excursion) getArguments().getSerializable(EXCURSION_KEY);

        // Le cambiamos el título a la actividad (al cambiar el título, estaremos llamando
        // a un método de la actividad llamado setTitle.
        getActivity().setTitle(excursion.getName());

        // Ponemos esta opción a true para poder inflar el menu en la Toolbar.
        setHasOptionsMenu(true);

        imgExc = (ImageView) rootView.findViewById(R.id.excImg);
        txtName = (TextView) rootView.findViewById(R.id.excName);
        txtDescription = (TextView) rootView.findViewById(R.id.excDescription);
        txtLocation = (TextView) rootView.findViewById(R.id.excLocation);
        txtDistance = (TextView) rootView.findViewById(R.id.excDistance);
        imgLevel = (ImageView) rootView.findViewById(R.id.excLevel);
        txtLatitude = (TextView) rootView.findViewById(R.id.excLatitude);
        txtLongitude = (TextView) rootView.findViewById(R.id.excLongitude);

        // Parámetros meteorológicos
        cityText = (TextView) rootView.findViewById(R.id.cityText);
        condDescr = (TextView) rootView.findViewById(R.id.condDescr);
        temp = (TextView) rootView.findViewById(R.id.temp);
        hum = (TextView) rootView.findViewById(R.id.hum);
        press = (TextView) rootView.findViewById(R.id.press);
        windSpeed = (TextView) rootView.findViewById(R.id.windSpeed);
        windDeg = (TextView) rootView.findViewById(R.id.windDeg);
        imgView = (ImageView) rootView.findViewById(R.id.condIcon);
        panelTiempo = (LinearLayout) rootView.findViewById(R.id.panelTiempo);
        panelTiempoNoDisponible = (LinearLayout) rootView.findViewById(R.id.panelTiempoNoDisponible);

        switch (excursion.getImgPath()) {
            case "Cares":
                imgExc.setImageResource(R.drawable.rutadelcares);
                break;
            case "Relux":
                imgExc.setImageResource(R.drawable.ventanarelux);
                break;
            case "Caballo":
                imgExc.setImageResource(R.drawable.farodelcaballo);
                break;
            case "Gorbea":
                imgExc.setImageResource(R.drawable.gorbea);
                break;
        }

        txtName.setText(excursion.getName());
        txtDescription.setText(excursion.getOpinion());
        txtLocation.setText(excursion.getLocation());
        txtDistance.setText(Double.toString(excursion.getTravelDistance()) + " km");
        switch (excursion.getLevel()) {
            case "Facil":
                imgLevel.setImageResource(R.drawable.facil);
                break;
            case "Medio":
                imgLevel.setImageResource(R.drawable.medio);
                break;
            case "Dificil":
                imgLevel.setImageResource(R.drawable.dificil);
                break;
        }
        txtLatitude.setText(Float.toString(excursion.getLatitude()));
        txtLongitude.setText(Float.toString(excursion.getLongitude()));

        // Se realiza la petición a la API del tiempo.
        String city = excursion.getLocation();
        String cityWithoutSpaces = city.replace(" ", "%20");
        JSONWeatherTask task = new JSONWeatherTask();
        if (cityWithoutSpaces.contains("%20")) {
            task.execute(new String[]{cityWithoutSpaces});
        } else {
            task.execute(new String[]{city});
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.excursion, menu);

        MenuItem mnuShare = menu.findItem(R.id.mnu_share);

        String shareText = "*" + excursion.getName() + "*\n" + excursion.getOpinion();

        ShareActionProvider shareProv = (ShareActionProvider) MenuItemCompat.getActionProvider(mnuShare);
        shareProv.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareProv.setShareIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mnu_edit_exc) {
            Fragment fragment = new FormExcursionesFragment();
            Bundle args = new Bundle();
            args.putString(FormExcursionesFragment.ARG_FORM_EXCURSIONES, "Formulario");
            args.putSerializable(FormExcursionesFragment.FORM_EXCURSION_KEY, excursion);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            return true;
        } else if (id == R.id.mnu_delete_exc) {

            // Se elimina en este punto la excursión en cuestión

            Toast.makeText(getContext(), "La excursión '" + excursion.getName() + "' ha sido eliminada.", Toast.LENGTH_SHORT).show();

            Fragment fragment = new MisExcursionesFragment();
            Bundle args = new Bundle();
            args.putInt(MisExcursionesFragment.ARG_MIS_EXCURSIONES_NUMBER, 0);
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            if (data != null) {
                try {
                    weather = JSONWeatherParser.getWeather(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Error", "la API no ha devuelto ningun valor");
                weather = null;
            }

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather != null) {
                if (weather.currentCondition.getIcon() != null) {
                    switch (weather.currentCondition.getIcon()) {
                        case "01d":
                            imgView.setImageResource(R.drawable.img01d);
                            break;
                        case "01n":
                            imgView.setImageResource(R.drawable.img01n);
                            break;
                        case "02d":
                            imgView.setImageResource(R.drawable.img02d);
                            break;
                        case "02n":
                            imgView.setImageResource(R.drawable.img02n);
                            break;
                        case "03d":
                            imgView.setImageResource(R.drawable.img03d);
                            break;
                        case "03n":
                            imgView.setImageResource(R.drawable.img03n);
                            break;
                        case "04d":
                            imgView.setImageResource(R.drawable.img04d);
                            break;
                        case "04n":
                            imgView.setImageResource(R.drawable.img04n);
                            break;
                        case "09d":
                            imgView.setImageResource(R.drawable.img09d);
                            break;
                        case "09n":
                            imgView.setImageResource(R.drawable.img09n);
                            break;
                        case "10d":
                            imgView.setImageResource(R.drawable.img10d);
                            break;
                        case "10n":
                            imgView.setImageResource(R.drawable.img10n);
                            break;
                        case "11d":
                            imgView.setImageResource(R.drawable.img11d);
                            break;
                        case "11n":
                            imgView.setImageResource(R.drawable.img11n);
                            break;
                        case "13d":
                            imgView.setImageResource(R.drawable.img13d);
                            break;
                        case "13n":
                            imgView.setImageResource(R.drawable.img13n);
                            break;
                        case "50d":
                            imgView.setImageResource(R.drawable.img50d);
                            break;
                        case "50n":
                            imgView.setImageResource(R.drawable.img50n);
                            break;
                        default:
                            imgView.setImageResource(R.drawable.imgnotavailable);
                            break;
                    }
                } else {
                    imgView.setImageResource(R.drawable.imgnotavailable);
                }

                cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "ºC");
                hum.setText("" + weather.currentCondition.getHumidity() + "%");
                press.setText("" + weather.currentCondition.getPressure() + " hPa");
                windSpeed.setText("" + weather.wind.getSpeed() + " mps");
                if (weather.wind.getDeg() != -1111) {
                    windDeg.setText("" + weather.wind.getDeg() + "º");
                } else {
                    windDeg.setText("");
                }

                panelTiempo.setVisibility(View.VISIBLE);
            } else {
                panelTiempoNoDisponible.setVisibility(View.VISIBLE);
            }
        }
    }
}
