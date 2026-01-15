package com.example.timetable;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;



public class AddMyStationActivity extends AppCompatActivity {
    private int departurePosition = 0;
    private int destinationPosition = 0;
    private DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_my_station);

        Spinner departureSpinner = findViewById(R.id.departure_station_spinner);
        Spinner destinationSpinner = findViewById(R.id.arrival_station_spinner);

        helper = new DatabaseHelper(AddMyStationActivity.this);
        String [] tmpArray = getResources().getStringArray(R.array.station_names);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
          this,
          android.R.layout.simple_spinner_item,
          tmpArray
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
            Toast.makeText(AddMyStationActivity.this, "出発駅と到着駅に同じ駅は指定できません。", Toast.LENGTH_LONG).show();
            return;
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String[] stationNames = getResources().getStringArray(R.array.station_names);
        try {

            db = helper.getWritableDatabase();
            String isAddedSql = "SELECT * FROM MyStationsTable WHERE departure_id = ? AND destination_id = ?";
            String[] placeAry = {String.valueOf(departurePosition), String.valueOf(destinationPosition)};


            cursor = db.rawQuery(isAddedSql, placeAry);
            if (cursor.moveToFirst()) {
                Toast.makeText(AddMyStationActivity.this, String.format(getString(R.string.my_stations_alert)), Toast.LENGTH_LONG).show();
            } else {
                String addMyStnSql = "INSERT INTO MyStationsTable (departure_id, departure_name, destination_id, destination_name) VALUES(?, ?, ?, ?)";
                SQLiteStatement stmt = db.compileStatement(addMyStnSql);

                stmt.bindLong(1, departurePosition);
                stmt.bindString(2, stationNames[departurePosition]);
                stmt.bindLong(3, destinationPosition);
                stmt.bindString(4, stationNames[destinationPosition]);

                Long isInserted = stmt.executeInsert(); //executeInsert()は、挿入を実行するだけでなく、戻り値として挿入されたかどうか（されてたら）

                if (isInserted != -1) {
                    Toast.makeText(AddMyStationActivity.this, stationNames[departurePosition] + String.format(getString(R.string.app_arrow)) + stationNames[destinationPosition] + String.format(getString(R.string.my_station_added_message)), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddMyStationActivity.this, String.format(getString(R.string.my_station_failure_message)), Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception e) {
            Toast.makeText(AddMyStationActivity.this, String.format(getString(R.string.my_station_database_error)), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public void onCancelButtonClick(View view) {
        finish();
    }
}