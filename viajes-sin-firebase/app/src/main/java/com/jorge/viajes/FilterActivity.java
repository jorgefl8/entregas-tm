package com.jorge.viajes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.jorge.viajes.entity.Filtro;
import com.jorge.viajes.util.UtilFecha;

import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    private Calendar calFechaIni = null;
    private Calendar calFechaFin = null;
    private Button btnDateStart, btnDateEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        btnDateStart = findViewById(R.id.btnDateStart);
        btnDateEnd = findViewById(R.id.btnDateEnd);
        EditText editPriceMin = findViewById(R.id.editPriceMin);
        EditText editPriceMax = findViewById(R.id.editPriceMax);
        Button btnSave = findViewById(R.id.btnSaveFilter);

        btnDateStart.setText(getString(R.string.filter_date_start));
        btnDateEnd.setText(getString(R.string.filter_date_end));

        btnDateStart.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new DatePickerDialog(this, (picker, year, month, day) -> {
                calFechaIni = Calendar.getInstance();
                calFechaIni.set(year, month, day, 0, 0, 0);
                btnDateStart.setText(UtilFecha.formateaFecha(calFechaIni));
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnDateEnd.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            new DatePickerDialog(this, (picker, year, month, day) -> {
                calFechaFin = Calendar.getInstance();
                calFechaFin.set(year, month, day, 23, 59, 59);
                btnDateEnd.setText(UtilFecha.formateaFecha(calFechaFin));
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            Filtro filtro = new Filtro();

            String minStr = editPriceMin.getText().toString().trim();
            String maxStr = editPriceMax.getText().toString().trim();
            if (!minStr.isEmpty()) filtro.setPrecioMin(Integer.parseInt(minStr));
            if (!maxStr.isEmpty()) filtro.setPrecioMax(Integer.parseInt(maxStr));
            if (calFechaIni != null) filtro.setFechaIni(UtilFecha.calendarALong(calFechaIni));
            if (calFechaFin != null) filtro.setFechaFin(UtilFecha.calendarALong(calFechaFin));

            Intent intent = new Intent();
            intent.putExtra("FILTRO", filtro);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
