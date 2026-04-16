package com.jorge.viajes;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jorge.viajes.entity.Trip;
import com.jorge.viajes.util.Constantes;

public class SelectedTripsActivity extends AppCompatActivity {

    private SelectedTripAdapter adapter;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_trips);

        emptyText = findViewById(R.id.emptyText);
        RecyclerView recycler = findViewById(R.id.selectedRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SelectedTripAdapter(Constantes.SELECCIONADOS, this::onBuy);
        recycler.setAdapter(adapter);

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());

        actualizarVista();
    }

    private void onBuy(Trip trip, int position) {
        Toast.makeText(this, trip.getCiudad() + " comprado", Toast.LENGTH_SHORT).show();
        trip.setSeleccionado(false);
        Constantes.SELECCIONADOS.remove(position);
        adapter.notifyItemRemoved(position);
        actualizarVista();
    }

    private void actualizarVista() {
        emptyText.setVisibility(Constantes.SELECCIONADOS.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
