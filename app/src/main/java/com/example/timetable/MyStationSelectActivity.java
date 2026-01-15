package com.example.timetable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MyStationSelectActivity extends AppCompatActivity {
    public ListView lvTimeTable;
    private MyStationSelectAdapter adapter;
    private List<MyStationItem> listItems;
    private DatabaseHelper _helper;
    private String sqlMyStationSelect = "SELECT * FROM trainTimeTable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_station_select);

        /*
        _helper = new DatabaseHelper(MyStationSelectActivity.this);

        lvTimeTable = findViewById(R.id.lv_my_stations);
        listItems = new ArrayList<>();
        adapter = new MyStationSelectAdapter(this,listItems);

        SQLiteDatabase db = null;
        Cursor cursor = null;


         */


    }


    /*
    private void onCancelButtonClick(View view) {
        finish();
    }

     */
}