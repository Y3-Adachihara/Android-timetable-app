package com.example.timetable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "trainTimeTable.db";
    private static final int DATABASE_VERSION = 8;
    /*主キーを変えたときに1→2にした（2025/07/13） My駅間テーブルの方も主キー変えて2→3にした(2025/07/14) My駅間テーブルの最後にいらないカンマがあったため、それを削除した3→4(2025-07-15)
    My駅間テーブルの_idに型が指定されていなかったため、INTEGERを追加した 4→5(2025-07-15) My駅間テーブルに、レコード追加時刻の属性を追加した 5→6(2025-07-17) 最後の駅の追加 6→7(2025-07-18)
    滑り込みで全駅制覇7→8(2025-07-18)*/
    private final Context context;  //CSVReaderのコンストラクタは第一引数にcontextが必要。MainActivity.javaでCSV取得するんならthisで済むけども、ここでは新しく作らないといけない。
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //時刻表テーブルの作成
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE trainTimeTable(");
        sb.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append("departure_id INTEGER NOT NULL,");
        sb.append("departure_name TEXT NOT NULL,");
        sb.append("departure_platform_number INTEGER NOT NULL,");
        sb.append("is_weekday INTEGER NOT NULL,");
        sb.append("is_holiday INTEGER NOT NULL,");
        sb.append("is_upbound INTEGER NOT NULL,");
        sb.append("is_downbound INTEGER NOT NULL,");
        sb.append("hour INTEGER NOT NULL,");
        sb.append("minute INTEGER NOT NULL,");
        sb.append("destination_id INTEGER NOT NULL,");
        sb.append("destination_name TEXT NOT NULL,");
        sb.append("user_memo TEXT");
        sb.append(");");
        String sql = sb.toString();
        db.execSQL(sql);

        //My駅間登録用のテーブルの作成
        StringBuilder MyStations = new StringBuilder();
        MyStations.append("CREATE TABLE MyStationsTable(");
        MyStations.append("_id INTEGER PRIMARY KEY AUTOINCREMENT,");
        MyStations.append("departure_id INTEGER NOT NULL,");
        MyStations.append("departure_name TEXT NOT NULL,");
        MyStations.append("destination_id INTEGER NOT NULL,");
        MyStations.append("destination_name TEXT NOT NULL,");
        MyStations.append("registration_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP");
        MyStations.append(");");
        String myStationSql = MyStations.toString();
        db.execSQL(myStationSql);


        //CSVを読み込むためのクラス（CSVReader.javaで作ったやつ）で定義したメソッドを呼び出す。
        //戻り値は文字列型の配列にしているため、obtainedCSVも文字列型の配列とする
        String[] obtainedCSV = CSVReader.readCSV(context,"TimeTableCSV.csv");   //出発駅ID,出発駅名,出発ホーム番号,平日,休日,上り,下り,時,分,行先の駅ID,行先の駅名

        //ここからCSVのデータをデータベースのテーブルに挿入する処理ども
        if (obtainedCSV != null) {
            db.beginTransaction();  //大量の挿入処理なので、トランザクションで高速化する


            //INSERTは列名を先に作っておいたテーブルと一致させておかないと、クラッシュしてホーム画面に戻る。他の人が画面遷移先でSQL文作るときは注意！！（これのせいで一生クラッシュしてた）
            String sqlInsert = "INSERT INTO trainTimeTable (departure_id,departure_name,departure_platform_number,is_weekday,is_holiday,is_upbound,is_downbound,hour,minute,destination_id,destination_name) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            //↑のINSERT文は、下のfor文でcsvの行数分実行される。
            //東静岡駅の平休上下だけで266行もある。これを全駅揃えると、1000行など余裕で超える。このように、同じSQL文を何度も使う場合は、先にINSERT文をコンパイルしておくことで実行速度を速くすることができる
            SQLiteStatement stmt = db.compileStatement(sqlInsert);



            try {

                //一行ずつ、項目を挿入していく
                for (String tmpStr : obtainedCSV) {
                    String[] commmaArray = tmpStr.split(",");

                    if(commmaArray.length == 11) {
                        try {
                            stmt.bindLong(1,Integer.parseInt(commmaArray[0]));
                            stmt.bindString(2,commmaArray[1]);
                            stmt.bindLong(3,Integer.parseInt(commmaArray[2]));
                            stmt.bindLong(4,Integer.parseInt(commmaArray[3]));
                            stmt.bindLong(5,Integer.parseInt(commmaArray[4]));
                            stmt.bindLong(6,Integer.parseInt(commmaArray[5]));
                            stmt.bindLong(7,Integer.parseInt(commmaArray[6]));
                            stmt.bindLong(8,Integer.parseInt(commmaArray[7]));
                            stmt.bindLong(9,Integer.parseInt(commmaArray[8]));
                            stmt.bindLong(10,Integer.parseInt(commmaArray[9]));
                            stmt.bindString(11,commmaArray[10]);

                            stmt.executeInsert();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            android.util.Log.e("DatabaseHelper", "Failed to parse number in CSV line: " + tmpStr);
                        }
                    } else {
                        android.util.Log.w("DatabaseHelper", "Skipping malformed CSV line: " + tmpStr);
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }



    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS trainTimeTable");
        db.execSQL("DROP TABLE IF EXISTS MyStationsTable"); // MyStationsTableも再作成
        onCreate(db);
    }
}
