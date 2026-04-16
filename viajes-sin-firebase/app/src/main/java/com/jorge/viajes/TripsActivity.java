package com.jorge.viajes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jorge.viajes.entity.Filtro;
import com.jorge.viajes.entity.Trip;
import com.jorge.viajes.util.Constantes;

import java.util.ArrayList;
import java.util.List;

public class TripsActivity extends AppCompatActivity implements TripAdapter.OnTripClickListener {

    private RecyclerView tripsRecycler;
    private TripAdapter adapter;
    private TextView tripsCount;
    private List<Trip> viajesMostrar = new ArrayList<>();
    private Filtro filtro = new Filtro();

    private final ActivityResultLauncher<Intent> filterLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    filtro = result.getData().getParcelableExtra("FILTRO");
                    aplicarFiltro();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        tripsRecycler = findViewById(R.id.tripsRecycler);
        tripsCount = findViewById(R.id.tripsCount);
        Button btnFilter = findViewById(R.id.btnFilter);
        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        Switch switchColumns = findViewById(R.id.switchColumns);

        tripsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter(viajesMostrar, this);
        tripsRecycler.setAdapter(adapter);

        switchColumns.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                tripsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            } else {
                tripsRecycler.setLayoutManager(new LinearLayoutManager(this));
            }
        });

        btnFilter.setOnClickListener(v ->
                filterLauncher.launch(new Intent(this, FilterActivity.class)));

        aplicarFiltro();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void aplicarFiltro() {
        viajesMostrar.clear();
        for (Trip trip : Constantes.VIAJES) {
            if (trip.getPrecio() >= filtro.getPrecioMin()
                    && trip.getPrecio() <= filtro.getPrecioMax()
                    && trip.getFechaSalida() >= filtro.getFechaIni()
                    && trip.getFechaLlegada() <= filtro.getFechaFin()) {
                viajesMostrar.add(trip);
            }
        }
        adapter.notifyDataSetChanged();
        tripsCount.setText("Hay " + viajesMostrar.size() + " viajes con las condiciones de filtro");
    }

    @Override
    public void onTripClick(Trip trip, int position) {
        Intent intent = new Intent(this, TripDetailActivity.class);
        intent.putExtra("TRIP", trip);
        startActivity(intent);
    }

    @Override
    public void onStarClick(Trip trip, int position) {
        trip.setSeleccionado(!trip.isSeleccionado());
        if (trip.isSeleccionado()) {
            if (!Constantes.SELECCIONADOS.contains(trip)) {
                Constantes.SELECCIONADOS.add(trip);
            }
            Toast.makeText(this, trip.getCiudad() + " añadido a seleccionados", Toast.LENGTH_SHORT).show();
        } else {
            Constantes.SELECCIONADOS.remove(trip);
            Toast.makeText(this, trip.getCiudad() + " eliminado de seleccionados", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyItemChanged(position);
    }
}
