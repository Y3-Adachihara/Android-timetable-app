package com.example.timetable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TimeTableReload {
    private static final String sqlSelect = "SELECT _id, * FROM trainTimeTable WHERE departure_id = ? AND is_weekday = ? AND is_downbound = ? AND ((hour > ?) OR (hour = ? AND minute >= ?)) ORDER BY hour, minute ASC";
    public List<ListItem> timeTableReload(Context context,int departureStation, int wantToGoStation, int isWeekday,int currentHour, int currentMinute) {

        //currentHourとcurrentMinuteから現在時刻を取得。

        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<ListItem> timeTableListView = new ArrayList<>();
        try {
            //乗り換え有り無しの変数
            int isDownbound;
            //出発駅と降りたい駅は、時刻表csvからじゃなくてこのメソッドの引数で入っているから、カーソルでデータ取得する前から判断できる
            if (departureStation < wantToGoStation) {
                isDownbound = 1;
            } else {
                isDownbound = 0;
            }

            db = helper.getReadableDatabase();
            String[] selectItems = {String.valueOf(departureStation), String.valueOf(isWeekday), String.valueOf(isDownbound), String.valueOf(currentHour), String.valueOf(currentHour), String.valueOf(currentMinute)};
            cursor = db.rawQuery(sqlSelect, selectItems);

            //カーソルにある各要素のインデックスを取得する。これらがリスト一項目に表示させる各要素があるインデックスとなる

            int index = cursor.getColumnIndex("_id");

            int hourIndex = cursor.getColumnIndex("hour");
            int minuteIndex = cursor.getColumnIndex("minute");
            //↓これは表示には使わないが、乗り換えありの判断に必要になるインデックス
            int destinationIdIndex = cursor.getColumnIndex("destination_id");
            int destinationNameIndex = cursor.getColumnIndex("destination_name");
            int departurePlatformNumberIndex = cursor.getColumnIndex("departure_platform_number");
            int userMemoIndex = cursor.getColumnIndex("user_memo");

            while (cursor.moveToNext()) {
                Long idIndex = cursor.getLong(index);   //時刻表テーブルの識別ID

                int hour = cursor.getInt(hourIndex);    //出発時
                int minute = cursor.getInt(minuteIndex);    //出発分

                int destinationId = cursor.getInt(destinationIdIndex);  //終着駅ID
                String  destinationName = cursor.getString(destinationNameIndex);   //終着駅名
                int departurePlatformNumber = cursor.getInt(departurePlatformNumberIndex);  //出発駅ホーム番線
                String userMemo = cursor.getString(userMemoIndex);  //ユーザメモ

                int needTransfer = 0;   //デフォルトは乗り換えなし（下のif-elseに引っかからなかった場合、needTransferに代入されずにエラーとなるため。）
                if (isDownbound == 1 && destinationId < wantToGoStation) {    //1なら下り
                        needTransfer = 1;   //乗り換えあり

                } else if (isDownbound == 0 && wantToGoStation < destinationId) {  //0なら上り
                        needTransfer = 1;   //乗り換えあり
                }

                String  remainingTime = "";  //未来の出発分-現在の分が15分いないなら、残り○○分と表示させる
                if (hour == currentHour && minute - currentMinute <= 30) {
                    int differenceTime = minute - currentMinute;
                    //あとで文章をstring.xmlから取得するようにする。
                    remainingTime = context.getResources().getString(R.string.app_ato) + String.valueOf(differenceTime) + context.getResources().getString(R.string.app_minute);
                }

                timeTableListView.add(new ListItem(idIndex, hour, minute, destinationName, departurePlatformNumber,needTransfer, userMemo, remainingTime));

            }
            return timeTableListView;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}