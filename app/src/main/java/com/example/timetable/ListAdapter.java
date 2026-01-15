package com.example.timetable;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

public class ListAdapter extends BaseAdapter{
    private Context context;
    private List<ListItem> infoListItem;
    private LayoutInflater layoutInflater;

    public ListAdapter(Context context, List<ListItem> listItem) {
        this.context = context;
        this.infoListItem = listItem;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount(){
        return infoListItem.size();
    }
    @Override
    public Object getItem(int position) {
        return infoListItem.get(position);
    }
    @Override
    public long getItemId(int position) {
        return infoListItem.get(position).getId();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.list_item_custom,parent,false);
            //↑では、リストビューに表示する一項目（一箱）が生成されている
            //layoutInflater    :xmlレイアウトファイルをViewオブジェクトに変換して生成するやつ。もし再利用可能なビューが無かった場合、新しくビューを作成する必要がある。
            //ビュー   :ここではリストビューの項目に配置する内容とでも解釈する（○○時○○分とかの項目）

            holder = new ViewHolder();//新たに生成された一項目の各要素に対する参照を保持するためのViewHolderオブジェクトを生成する

            //各構成部品を↑で宣言したViewHolderオブジェクトに保持させる
            holder.itemNumberTextView = convertView.findViewById(R.id.itemNumberTextView);
            holder.hourTextView = convertView.findViewById(R.id.hour);
            holder.minuteTextView = convertView.findViewById(R.id.minitue);
            holder.destinationTextView = convertView.findViewById(R.id.destination);
            holder.platformNumberTextView = convertView.findViewById(R.id.platformNumber);
            holder.isTransferTextView = convertView.findViewById(R.id.isTransfer);
            holder.remainingTimeTextView = convertView.findViewById(R.id.remainingTime);    //残り時間の部品も保持させた

            holder.userMemoTextView = convertView.findViewById(R.id.userMemo);

            convertView.setTag(holder); //保持させるべき情報を入れたところで、convertViewにタグ付け

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem listItem = infoListItem.get(position);



        //ListItemから取り出した各情報を、ViewHolderオブジェクトの各TextViewにセットする
        holder.itemNumberTextView.setText(String.valueOf(position+1));
        holder.hourTextView.setText(String.valueOf(listItem.getHour()));

        if (listItem.getMinute() / 10 == 0) {
            //setTextの中で文字列連結するのはあんましやらんほうがいいらしい？
            holder.minuteTextView.setText("0" + String.valueOf(listItem.getMinute()));
        } else {
            holder.minuteTextView.setText(String.valueOf(listItem.getMinute()));
        }

        holder.destinationTextView.setText(listItem.getDestination());
        holder.platformNumberTextView.setText(String.valueOf(listItem.getPlatform()));


        //乗り換えが必要なら”あり”を表示する
        //0か1か(乗り換えが必要かどうか)の判断は、
        if(listItem.isHasTransfer() == 1) {
            holder.isTransferTextView.setVisibility(View.VISIBLE);
            holder.isTransferTextView.setText(R.string.app_isTransfer);
        } else {
            holder.isTransferTextView.setVisibility(View.GONE);
        }

        //発車まで15分以内なら残り時間を表示
        holder.remainingTimeTextView.setText(listItem.getRemainingTime());


        //ユーザメモは書かれていたら表示する
        if (listItem.getUserMemo() != null && !listItem.getUserMemo().isEmpty()) {
            holder.userMemoTextView.setVisibility(View.VISIBLE);
            holder.userMemoTextView.setText(listItem.getUserMemo());
        } else {
            holder.userMemoTextView.setVisibility(View.GONE);   //GONE  :ビューを非表示にするのみならず、余分な余白も取らない便利なやつ
        }

        return convertView;
    }
    static class ViewHolder {
        TextView itemNumberTextView;
        TextView hourTextView;
        TextView minuteTextView;
        TextView destinationTextView;
        TextView platformNumberTextView;
        TextView isTransferTextView;
        TextView remainingTimeTextView;
        TextView userMemoTextView;
    }
}


