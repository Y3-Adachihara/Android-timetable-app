//CSVファイルの内容を取得するクラス

package com.example.timetable;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CSVReader {
    //csvを読み込むためのメソッド
    public static String[] readCSV(Context context, String fillename) { //ここのfilenameはassetsフォルダに格納されているcsvファイルのファイル名
        AssetManager assetManager = context.getAssets();
        String [] data = null;  //csvファイルを文字列（一行が一文字列）として格納するための配列
        try {
            InputStream inputStream = assetManager.open(fillename); //csvファイルの中身を、0と1のバイトデータとして受け取る
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);   //バイトデータとして受け取ったやつを、文字として扱えるようにする
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  //変換した文字データを一行ずつ読み込むためのやつ
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            //csvファイルの行を一行ずつ読み込み続けるループ。行が無くなるまで繰り返すために、!=nullとしている。
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line); //csvファイルを一行読み込み
                stringBuilder.append("\n"); //末尾に改行文字を入れる
            }
            //ループを抜けた直後は、（一行目）\n（二行目）\n…みたいになっている。これを↓
            data = stringBuilder.toString().split("\n");
            //↑の行で文字列型にし、\nで分けることで、（n行目）\nが文字列型配列の一要素となって順次格納されていく

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
