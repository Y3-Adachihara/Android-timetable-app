package com.example.timetable;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class StationSearchActivity extends AppCompatActivity {
    private int departurePosition = 0;
    private int destinationPosition = 0;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_station_serch);

        Spinner departureSpinner = findViewById(R.id.departure_station_spinner);
        Spinner destinationSpinner = findViewById(R.id.arrival_station_spinner);

        helper = new DatabaseHelper(StationSearchActivity.this);
        String[] stationNames = getResources().getStringArray(R.array.station_names);


        //スピナーに対するアダプターを設定するやつら↓
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                stationNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departureSpinner.setAdapter(spinnerAdapter);
        destinationSpinner.setAdapter(spinnerAdapter);

        departureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                departurePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                destinationPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (helper != null) {
            helper.close();
        }
    }

    public void onConfirmButtonClick(View view) {
        if (destinationPosition == departurePosition) {
            Toast.makeText(StationSearchActivity.this, getResources().getString(R.string.station_search_same_stations_message), Toast.LENGTH_LONG).show();
            return;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("departure_id", departurePosition);
        returnIntent.putExtra("destination_id", destinationPosition);

        setResult(RESULT_OK, returnIntent);
        finish();

    }

    public void onCancelButtonClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}