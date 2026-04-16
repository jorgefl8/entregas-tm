package com.jorge.viajes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jorge.viajes.entity.Trip;
import com.jorge.viajes.util.UtilFecha;

import java.util.List;

public class SelectedTripAdapter extends RecyclerView.Adapter<SelectedTripAdapter.ViewHolder> {

    public interface OnBuyClickListener {
        void onBuy(Trip trip, int position);
    }

    private final List<Trip> trips;
    private final OnBuyClickListener listener;

    public SelectedTripAdapter(List<Trip> trips, OnBuyClickListener listener) {
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.city.setText(trip.getCiudad());
        holder.info.setText(trip.getPrecio() + "€  Salida: " + UtilFecha.formateaFecha(trip.getFechaSalida()));
        holder.btnBuy.setOnClickListener(v -> listener.onBuy(trip, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return trips.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView city, info;
        Button btnBuy;

        ViewHolder(View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.selectedCity);
            info = itemView.findViewById(R.id.selectedInfo);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}
