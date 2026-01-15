package com.example.timetable;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity {

    public ListView lvTimeTable;
    private TextView departureStationName;
    private TextView destinationStationName;
    private ListAdapter adapter;
    private List<ListItem> listItems;   //自分で定義したリスト型（<>の中のやつ。普通に文字列型のリストならString（文字列型）が入る）の配列を宣言しておく
    private SimpleDateFormat dateFormat;
    private String currentDate;
    public Calendar currentTime;
    private DatabaseHelper _helper;
    private int departureId;    //再度表示する際に、直前まで検索していた出発駅を参照するためにここに定義している
    private int wantToGoId; //これも↑と同じ。再度表示する際に、直前まで検索していた目的地となる駅を参照するためにここに定義している
    private int isWeekday;
    private int intHour;
    private int intMinute;

    private ActivityResultLauncher<Intent> stationSearchLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        //現在の年月日と曜日を取得して表示
        Calendar calendar = Calendar.getInstance();
        this.dateFormat = new SimpleDateFormat("yyyy年MM月dd日(E)", Locale.JAPAN);
        this.currentDate = dateFormat.format(calendar.getTime());
        TextView dateTextView = findViewById(R.id.yyyyMMdd);
        dateTextView.setText(currentDate);


        _helper = new DatabaseHelper(MainActivity.this);


        currentTime = Calendar.getInstance();
        lvTimeTable = findViewById(R.id.lvTimeTable);
        listItems = new ArrayList<>();

        //リストビューに対するアダプターを設定
        adapter = new ListAdapter(this, listItems);
        lvTimeTable.setAdapter(adapter);

        //アプリ起動時にはMy駅間登録されていないから、初期設定的な感じで表示する駅間
        departureId = 16;   //東静岡駅
        wantToGoId = 27;    //掛川駅
        isWeekday = 1;  //平日

        //二つのTextViewをUI部品と紐づけ
        departureStationName = findViewById(R.id.startStation);
        destinationStationName = findViewById(R.id.destinationStation);

        stationSearchLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            departureId = data.getIntExtra("departure_id", departureId);
                            wantToGoId = data.getIntExtra("destination_id", wantToGoId);

                            loadTimeTableData();
                        }
                    }
                }
        );

        loadMostRecentMyStation();

        lvTimeTable.setOnItemClickListener(new ListItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTimeTableData();
    }

    @Override
    protected void onDestroy() {
        if (_helper != null) {
            _helper.close();
        }
        super.onDestroy();
    }

    private void loadMostRecentMyStation() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = _helper.getReadableDatabase(); // DatabaseHelperが初期化済みであることを前提

            // MyStationsTableから最も新しい登録日時を持つ駅間を1件取得
            // ORDER BY registration_timestamp DESC で降順に並べ、LIMIT 1 で1件のみ取得
            String sql = "SELECT departure_id, destination_id FROM MyStationsTable ORDER BY registration_timestamp DESC LIMIT 1";
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                // 最も新しいMy駅間が見つかった場合、そのIDをセット
                int depIdIndex = cursor.getColumnIndex("departure_id");
                int destIdIndex = cursor.getColumnIndex("destination_id");

                if (depIdIndex != -1 && destIdIndex != -1) { // カラムが存在するか確認
                    departureId = cursor.getInt(depIdIndex);
                    wantToGoId = cursor.getInt(destIdIndex);
                }

            } else {
                //デフォルトの駅間は東静岡駅→掛川
                Toast.makeText(this, getResources().getString(R.string.app_no_myStations), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("MainActivity", getResources().getString(R.string.app_myStations_load_error_log) + e.getMessage(), e);
            Toast.makeText(this, getResources().getString(R.string.app_myStations_load_error_toast), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

    private void loadTimeTableData() {
        TimeTableReload reload = new TimeTableReload();
        String[] stationNames = getResources().getStringArray(R.array.station_names);

        //現在時刻を取得
        currentTime = Calendar.getInstance();
        intHour = currentTime.get(Calendar.HOUR_OF_DAY);
        intMinute = currentTime.get(Calendar.MINUTE);

        //○○駅→〇〇駅の○○に検索した駅名を入れるための処理。

        departureStationName.setText(stationNames[departureId] + getResources().getString(R.string.app_station));
        destinationStationName.setText(stationNames[wantToGoId] + getResources().getString(R.string.app_station));

        List<ListItem> tmpTimeTableList = reload.timeTableReload(this, departureId, wantToGoId, isWeekday, intHour, intMinute);

        listItems.clear();
        listItems.addAll(tmpTimeTableList);
        adapter.notifyDataSetChanged();
    }

    public void btnReload(View view) {
        loadTimeTableData();
    }

    public void onAddMyStationsClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddMyStationActivity.class);
        startActivity(intent);
    }

    public void onSearchButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, StationSearchActivity.class);
        // startActivity(intent);   これは試したけど、駅検索ボタンを押したらクラッシュしてしまう
        stationSearchLauncher.launch(intent);
    }
    public void onMyStationsSelect(View view) {
        Intent intent = new Intent(MainActivity.this, MyStationSelectActivity.class);
        startActivity(intent);
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //クリックされたListItemオブジェクトを取得
            ListItem clickedListItem =(ListItem)parent.getItemAtPosition(position);

            long _id = clickedListItem.getId();
            String currentUserMemo = clickedListItem.getUserMemo();

            Intent intent = new Intent(MainActivity.this, UserMemoEditActivity.class);
            intent.putExtra("_id", _id);
            intent.putExtra("currentUserMemo" , currentUserMemo);
            startActivity(intent);
        }
    }
}