package es.deusto.trekkingaventura.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city});

        return rootView;
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            if (data != null) {
                try {
                    weather = JSONWeatherParser.getWeather(data);

                    // Let's retrieve the icon
                    weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i("Error", "la API no ha devuelto ningun valor");
            }

            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather != null) {
                if (weather.iconData != null && weather.iconData.length > 0) {
                    Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                    imgView.setImageBitmap(img);
                }

                cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
                condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
                temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "ºC");
                hum.setText("" + weather.currentCondition.getHumidity() + "%");
                press.setText("" + weather.currentCondition.getPressure() + " hPa");
                windSpeed.setText("" + weather.wind.getSpeed() + " mps");
                windDeg.setText("" + weather.wind.getDeg() + "º");

                panelTiempo.setVisibility(View.VISIBLE);
            } else {
                panelTiempoNoDisponible.setVisibility(View.VISIBLE);
            }
        }
    }
}
