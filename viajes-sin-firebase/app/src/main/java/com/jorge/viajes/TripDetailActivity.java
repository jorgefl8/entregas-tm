package com.jorge.viajes;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jorge.viajes.entity.Trip;
import com.jorge.viajes.util.Constantes;
import com.jorge.viajes.util.UtilFecha;

public class TripDetailActivity extends AppCompatActivity {

    private Trip trip;
    private ImageView detailStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        trip = getIntent().getParcelableExtra("TRIP");
        if (trip == null) { finish(); return; }

        Trip source = findTripInGlobal(trip);
        if (source != null) trip = source;

        ((TextView) findViewById(R.id.detailCity)).setText(trip.getCiudad());
        ((TextView) findViewById(R.id.detailPrice)).setText(trip.getPrecio() + "€");
        ((TextView) findViewById(R.id.detailDepartureDate)).setText(UtilFecha.formateaFecha(trip.getFechaSalida()));
        ((TextView) findViewById(R.id.detailArrivalDate)).setText(UtilFecha.formateaFecha(trip.getFechaLlegada()));
        ((TextView) findViewById(R.id.detailDeparturePlace)).setText(trip.getLugarSalida());
        ((TextView) findViewById(R.id.detailDescription)).setText(trip.getDescripcion());

        detailStar = findViewById(R.id.detailStar);
        actualizarEstrella();

        detailStar.setOnClickListener(v -> toggleSeleccion());
        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
    }

    private Trip findTripInGlobal(Trip parcelado) {
        for (Trip t : Constantes.VIAJES) {
            if (t.getCiudad().equals(parcelado.getCiudad())
                    && t.getFechaSalida() == parcelado.getFechaSalida()
                    && t.getPrecio() == parcelado.getPrecio()) {
                return t;
            }
        }
        return null;
    }

    private void toggleSeleccion() {
        trip.setSeleccionado(!trip.isSeleccionado());
        if (trip.isSeleccionado()) {
            if (!Constantes.SELECCIONADOS.contains(trip)) {
                Constantes.SELECCIONADOS.add(trip);
            }
            Toast.makeText(this, trip.getCiudad() + " seleccionado", Toast.LENGTH_SHORT).show();
        } else {
            Constantes.SELECCIONADOS.remove(trip);
            Toast.makeText(this, trip.getCiudad() + " deseleccionado", Toast.LENGTH_SHORT).show();
        }
        actualizarEstrella();
    }

    private void actualizarEstrella() {
        detailStar.setImageResource(trip.isSeleccionado()
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
    }
}
