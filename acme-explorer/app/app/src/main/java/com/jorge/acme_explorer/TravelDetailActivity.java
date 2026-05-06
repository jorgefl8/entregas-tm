package com.jorge.acme_explorer;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jorge.acme_explorer.entity.Travel;
import com.jorge.acme_explorer.service.FirebaseDatabaseService;
import com.jorge.acme_explorer.util.UtilFecha;

public class TravelDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TRAVEL_ID = "extra_travel_id";

    private ImageView travelDetailImage;
    private TextView travelDetailTitle;
    private TextView travelDetailRoute;
    private TextView travelDetailDates;
    private TextView travelDetailPrice;
    private TextView travelDetailDescription;

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

        ImageButton backButton = findViewById(R.id.travelDetailBack);
        backButton.setOnClickListener(v -> finish());

        FirebaseDatabaseService.getInstance().getTravelRef(travelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Travel travel = snapshot.getValue(Travel.class);
                        if (travel == null) {
                            finish();
                            return;
                        }
                        bind(travel);
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
}
