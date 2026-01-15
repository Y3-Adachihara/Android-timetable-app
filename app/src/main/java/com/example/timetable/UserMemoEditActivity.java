package com.example.timetable;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserMemoEditActivity extends AppCompatActivity {
    private DatabaseHelper _helper;
    private long listRawId;
    private String listText;
    private EditText userMemo;  //入力された文字列を格納

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_memo_edit);

        Intent intent =getIntent();
        this.listRawId = intent.getLongExtra("_id", -1);
        this.listText = intent.getStringExtra("currentUserMemo");

        this.userMemo = findViewById(R.id.updateEdit);
        userMemo.setText(listText); //入力されていたユーザーメモがあればここでセットしている。

        _helper =new DatabaseHelper(UserMemoEditActivity.this);
    }

    @Override
    protected void onDestroy() {
        if (_helper != null) {
            _helper.close();
        }
        super.onDestroy();
    }

    public void onCommitButtonClick(View view) {    //userMemoを変更するやつ
        SQLiteDatabase db = null;
        try {
            String inputedStr = userMemo.getText().toString();
            db = _helper.getWritableDatabase();
            String updateSql = "UPDATE trainTimeTable SET user_memo = ? WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(updateSql);

            stmt.bindString(1, inputedStr);
            stmt.bindLong(2, listRawId);
            int isUpdated = stmt.executeUpdateDelete(); //executeUpdateDeleteは、戻り値として更新された行数が入る。

            if (isUpdated > 0) {    //一行分が更新されたなら、isUpdatedは1になっているはず
                Toast.makeText(UserMemoEditActivity.this, String.format(getString(R.string.user_memo_added_message)), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UserMemoEditActivity.this, String.format(getString(R.string.user_memo_failure_message)), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //_helperはonDestroyでcloseするから、ここでは書かなくていい
            if (db != null && db.isOpen()) {
                db.close();
            }
            finish();
        }
    }


    public void onDeleteButtonClick(View view) {
        SQLiteDatabase db = null;
        try {
            db = _helper.getWritableDatabase();
            String updateSql = "UPDATE trainTimeTable SET user_memo = ? WHERE _id = ?";
            SQLiteStatement stmt = db.compileStatement(updateSql);

            stmt.bindString(1, ""); //空文字に更新することで削除を実現
            stmt.bindLong(2, listRawId);
            int isUpdated = stmt.executeUpdateDelete(); //executeUpdateDeleteは、戻り値として更新された行数が入る。

            if (isUpdated > 0) {    //一行分が更新されたなら、isUpdatedは1になっているはず
                Toast.makeText(UserMemoEditActivity.this, String.format(getString(R.string.user_memo_deleted_message)), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UserMemoEditActivity.this, String.format(getString(R.string.user_memo_delete_failure_message)), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //_helperはonDestroyでcloseするから、ここでは書かなくていい
            if (db != null && db.isOpen()) {
                db.close();
            }
            finish();
        }
    }

    public void onCancelButtonClick(View view) {
        finish();
    }
}