package com.jorge.viajes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jorge.viajes.entity.Trip;
import com.jorge.viajes.util.UtilFecha;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public interface OnTripClickListener {
        void onTripClick(Trip trip, int position);
        void onStarClick(Trip trip, int position);
    }

    private final List<Trip> trips;
    private final OnTripClickListener listener;

    public TripAdapter(List<Trip> trips, OnTripClickListener listener) {
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.city.setText(trip.getCiudad());
        holder.info.setText(trip.getPrecio() + "€  Salida: " + UtilFecha.formateaFecha(trip.getFechaSalida())
                + "  Llegada: " + UtilFecha.formateaFecha(trip.getFechaLlegada()));
        holder.star.setImageResource(trip.isSeleccionado()
                ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);

        holder.itemView.setOnClickListener(v -> listener.onTripClick(trip, holder.getAdapterPosition()));
        holder.star.setOnClickListener(v -> listener.onStarClick(trip, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return trips.size(); }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView city, info;
        ImageView star;

        TripViewHolder(View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.tripCity);
            info = itemView.findViewById(R.id.tripInfo);
            star = itemView.findViewById(R.id.tripStar);
        }
    }
}
