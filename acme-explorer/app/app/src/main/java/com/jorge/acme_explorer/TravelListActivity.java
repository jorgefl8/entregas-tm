package com.jorge.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jorge.acme_explorer.entity.Travel;
import com.jorge.acme_explorer.service.FirebaseDatabaseService;

import java.util.ArrayList;
import java.util.List;

public class TravelListActivity extends AppCompatActivity {

    private FirebaseDatabaseService dbService;
    private TravelListAdapter adapter;
    private ValueEventListener travelsListener;

    private TextView travelListCount;
    private TextView travelListEmpty;
    private ProgressBar travelListProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_list);

        dbService = FirebaseDatabaseService.getInstance();

        travelListCount = findViewById(R.id.travelListCount);
        travelListEmpty = findViewById(R.id.travelListEmpty);
        travelListProgress = findViewById(R.id.travelListProgress);
        RecyclerView recycler = findViewById(R.id.travelListRecycler);
        ImageButton profileButton = findViewById(R.id.travelListProfileButton);

        adapter = new TravelListAdapter((id, travel) -> {
            Intent intent = new Intent(this, TravelDetailActivity.class);
            intent.putExtra(TravelDetailActivity.EXTRA_TRAVEL_ID, id);
            startActivity(intent);
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        travelListProgress.setVisibility(View.VISIBLE);
        travelsListener = dbService.getTravelsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                travelListProgress.setVisibility(View.GONE);
                List<String> ids = new ArrayList<>();
                List<Travel> items = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Travel travel = child.getValue(Travel.class);
                    if (travel == null) continue;
                    ids.add(child.getKey());
                    items.add(travel);
                }
                adapter.setData(ids, items);
                travelListCount.setText(getString(R.string.travel_list_count, items.size()));
                travelListEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                travelListProgress.setVisibility(View.GONE);
                travelListEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (travelsListener != null) {
            dbService.getTravelsRef().removeEventListener(travelsListener);
        }
    }
}
