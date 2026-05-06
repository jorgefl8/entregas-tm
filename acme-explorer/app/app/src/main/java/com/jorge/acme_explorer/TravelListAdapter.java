package com.jorge.acme_explorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jorge.acme_explorer.entity.Travel;
import com.jorge.acme_explorer.util.UtilFecha;

import java.util.ArrayList;
import java.util.List;

public class TravelListAdapter extends RecyclerView.Adapter<TravelListAdapter.TravelViewHolder> {

    public interface OnTravelClickListener {
        void onTravelClick(String travelId, Travel travel);
    }

    private final List<String> travelIds = new ArrayList<>();
    private final List<Travel> travels = new ArrayList<>();
    private final OnTravelClickListener listener;

    public TravelListAdapter(OnTravelClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<String> ids, List<Travel> items) {
        this.travelIds.clear();
        this.travels.clear();
        this.travelIds.addAll(ids);
        this.travels.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel, parent, false);
        return new TravelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelViewHolder holder, int position) {
        Travel travel = travels.get(position);
        String id = travelIds.get(position);

        holder.title.setText(travel.getTitulo());
        holder.route.setText(holder.itemView.getContext().getString(
                R.string.travel_item_route, travel.getCiudadOrigen(), travel.getCiudadDestino()));
        holder.dates.setText(holder.itemView.getContext().getString(
                R.string.travel_item_dates,
                UtilFecha.formateaFecha(travel.getFechaSalida()),
                UtilFecha.formateaFecha(travel.getFechaLlegada())));
        holder.price.setText(holder.itemView.getContext().getString(
                R.string.travel_item_price, travel.getPrecio()));

        Glide.with(holder.itemView.getContext())
                .load(travel.getImagenUrl())
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onTravelClick(id, travel));
    }

    @Override
    public int getItemCount() {
        return travels.size();
    }

    static class TravelViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView route;
        TextView dates;
        TextView price;

        TravelViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.travelItemImage);
            title = itemView.findViewById(R.id.travelItemTitle);
            route = itemView.findViewById(R.id.travelItemRoute);
            dates = itemView.findViewById(R.id.travelItemDates);
            price = itemView.findViewById(R.id.travelItemPrice);
        }
    }
}
