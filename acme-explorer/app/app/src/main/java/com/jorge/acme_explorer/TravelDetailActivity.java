package com.jorge.acme_explorer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jorge.acme_explorer.entity.Travel;
import com.jorge.acme_explorer.entity.Weather;
import com.jorge.acme_explorer.entity.WeatherConditions;
import com.jorge.acme_explorer.entity.WeatherResponse;
import com.jorge.acme_explorer.service.FirebaseDatabaseService;
import com.jorge.acme_explorer.service.WeatherService;
import com.jorge.acme_explorer.util.UtilFecha;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_TRAVEL_ID = "extra_travel_id";
    private static final int REQ_LOCATION_PERMISSION = 2001;
    private static final int CAMERA_PADDING_PX = 200;

    private ImageView travelDetailImage;
    private TextView travelDetailTitle;
    private TextView travelDetailRoute;
    private TextView travelDetailDates;
    private TextView travelDetailPrice;
    private TextView travelDetailDescription;

    private CardView travelDetailWeatherCard;
    private ImageView travelDetailWeatherIcon;
    private TextView travelDetailWeatherCity;
    private TextView travelDetailWeatherTemp;
    private TextView travelDetailWeatherDescription;
    private TextView travelDetailWeatherHumidity;

    private GoogleMap map;
    private Travel currentTravel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);

        String travelId = getIntent().getStringExtra(EXTRA_TRAVEL_ID);
        if (travelId == null) {
            finish();
            return;
        }

        travelDetailImage = findViewById(R.id.travelDetailImage);
        travelDetailTitle = findViewById(R.id.travelDetailTitle);
        travelDetailRoute = findViewById(R.id.travelDetailRoute);
        travelDetailDates = findViewById(R.id.travelDetailDates);
        travelDetailPrice = findViewById(R.id.travelDetailPrice);
        travelDetailDescription = findViewById(R.id.travelDetailDescription);
        travelDetailWeatherCard = findViewById(R.id.travelDetailWeatherCard);
        travelDetailWeatherIcon = findViewById(R.id.travelDetailWeatherIcon);
        travelDetailWeatherCity = findViewById(R.id.travelDetailWeatherCity);
        travelDetailWeatherTemp = findViewById(R.id.travelDetailWeatherTemp);
        travelDetailWeatherDescription = findViewById(R.id.travelDetailWeatherDescription);
        travelDetailWeatherHumidity = findViewById(R.id.travelDetailWeatherHumidity);

        ImageButton backButton = findViewById(R.id.travelDetailBack);
        backButton.setOnClickListener(v -> finish());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.travelDetailMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        FirebaseDatabaseService.getInstance().getTravelRef(travelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Travel travel = snapshot.getValue(Travel.class);
                        if (travel == null) {
                            finish();
                            return;
                        }
                        currentTravel = travel;
                        bind(travel);
                        drawTravelOnMap();
                        loadWeatherForDestination(travel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        finish();
                    }
                });
    }

    private void bind(Travel travel) {
        travelDetailTitle.setText(travel.getTitulo());
        travelDetailRoute.setText(getString(R.string.travel_item_route,
                travel.getCiudadOrigen(), travel.getCiudadDestino()));
        travelDetailDates.setText(getString(R.string.travel_item_dates,
                UtilFecha.formateaFecha(travel.getFechaSalida()),
                UtilFecha.formateaFecha(travel.getFechaLlegada())));
        travelDetailPrice.setText(getString(R.string.travel_item_price, travel.getPrecio()));
        travelDetailDescription.setText(travel.getDescripcion());

        Glide.with(this)
                .load(travel.getImagenUrl())
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(travelDetailImage);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        drawTravelOnMap();
        enableMyLocation();
    }

    private void drawTravelOnMap() {
        if (map == null || currentTravel == null) return;

        LatLng origen = new LatLng(currentTravel.getLatOrigen(), currentTravel.getLngOrigen());
        LatLng destino = new LatLng(currentTravel.getLatDestino(), currentTravel.getLngDestino());

        map.clear();
        map.addMarker(new MarkerOptions()
                .position(origen)
                .title(currentTravel.getCiudadOrigen())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        map.addMarker(new MarkerOptions()
                .position(destino)
                .title(currentTravel.getCiudadDestino())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        map.addPolyline(new PolylineOptions()
                .add(origen, destino)
                .width(8f)
                .color(ContextCompat.getColor(this, R.color.primaryButton)));

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(origen)
                .include(destino)
                .build();
        map.setOnMapLoadedCallback(() ->
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_PADDING_PX)));
    }

    private void enableMyLocation() {
        if (map == null) return;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } catch (SecurityException ignored) {
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQ_LOCATION_PERMISSION);
        }
    }

    private void loadWeatherForDestination(Travel travel) {
        Call<WeatherResponse> call = WeatherService.getInstance().getCurrentWeather(
                travel.getLatDestino(),
                travel.getLngDestino(),
                getString(R.string.open_weather_map_api_key)
        );
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                bindWeather(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void bindWeather(WeatherResponse weatherResponse) {
        WeatherConditions main = weatherResponse.getMain();
        if (main == null) return;

        String city = currentTravel != null ? currentTravel.getCiudadDestino() : weatherResponse.getName();
        travelDetailWeatherCity.setText(getString(R.string.travel_detail_weather_title, city));
        travelDetailWeatherTemp.setText(getString(R.string.travel_detail_weather_temp, main.getTemp()));
        travelDetailWeatherHumidity.setText(getString(R.string.travel_detail_weather_humidity, main.getHumidity()));

        if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
            Weather first = weatherResponse.getWeather().get(0);
            String desc = first.getDescription();
            if (desc != null && !desc.isEmpty()) {
                travelDetailWeatherDescription.setText(
                        Character.toUpperCase(desc.charAt(0)) + desc.substring(1));
            }
            String iconCode = first.getIcon();
            if (iconCode != null && !iconCode.isEmpty()) {
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                Glide.with(this).load(iconUrl).into(travelDetailWeatherIcon);
            }
        }

        travelDetailWeatherCard.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQ_LOCATION_PERMISSION) return;

        boolean granted = false;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                granted = true;
                break;
            }
        }
        if (granted) {
            enableMyLocation();
        } else {
            Toast.makeText(this, R.string.travel_detail_location_denied, Toast.LENGTH_LONG).show();
        }
    }
}
