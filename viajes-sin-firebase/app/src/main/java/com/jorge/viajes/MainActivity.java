package com.jorge.viajes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] labels = {
                getString(R.string.menu_available),
                getString(R.string.menu_selected)
        };
        int[] icons = {
                android.R.drawable.ic_menu_mapmode,
                android.R.drawable.ic_menu_agenda
        };

        MenuAdapter adapter = new MenuAdapter(this, labels, icons);
        ListView menuList = findViewById(R.id.menuList);
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                startActivity(new Intent(this, TripsActivity.class));
            } else {
                startActivity(new Intent(this, SelectedTripsActivity.class));
            }
        });
    }
}
