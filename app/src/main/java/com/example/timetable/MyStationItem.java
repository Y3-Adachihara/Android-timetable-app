package com.example.timetable;
//My駅間選択の画面で表示するリストビューの一項目を定義するクラス……（完了せず）
public class MyStationItem {
    private int departureId;
    private String departureName;
    private int destinationId;
    private String destinationName;
    private boolean isFavorite; // 追加：この駅間がお気に入りとして選択されているか

    public MyStationItem(int departureId, String departureName, int destinationId, String destinationName, boolean isFavorite) {
        this.departureId = departureId;
        this.departureName = departureName;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.isFavorite = isFavorite;
    }

    // Getterメソッド
    public int getDepartureId() { return departureId; }
    public String getDepartureName() { return departureName; }
    public int getDestinationId() { return destinationId; }
    public String getDestinationName() { return destinationName; }
    public boolean isFavorite() { return isFavorite; } // 追加：isFavoriteのgetter

    // Setterメソッド (チェックボックスの状態変更に対応するため)
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}