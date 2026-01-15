package com.example.timetable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

// MyStationAdapter.java (骨組み)
public class MyStationSelectAdapter extends BaseAdapter {
    private Context context;
    private List<MyStationItem> myStationList;
    private LayoutInflater layoutInflater;

    public MyStationSelectAdapter(Context context, List<MyStationItem> myStationList) {
        this.context = context;
        this.myStationList = myStationList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return myStationList.size(); }
    @Override
    public Object getItem(int position) { return myStationList.get(position); }
    @Override
    public long getItemId(int position) { return position; } // 必要に応じてDBの_idなど
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ここでレイアウトをインフレートし、MyStationItemのデータを表示するロジックを記述
        // 例: departureName と destinationName を TextView にセット
        // ViewHolder パターンも適用
        return convertView;
    }

    static class ViewHolder {
        // 表示するViewの参照を保持
        TextView stationNamesTextView; // 例: "出発駅名 → 到着駅名" を表示するTextView
    }
}
