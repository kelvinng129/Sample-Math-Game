package com.mobile.game;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        dbHelper = new DatabaseHelper(this);
        ListView listView = findViewById(R.id.listview2);

        // Retrieve game logs from the SQLite database
        List<RecordBean> rankList = getGameLogsFromDatabase();

        // Create an instance of RecordAdapter with the retrieved list of records
        RecordAdapter rankAdapter = new RecordAdapter(RecordActivity.this, rankList);
        listView.setAdapter(rankAdapter);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Method to retrieve game logs from the SQLite database
    private List<RecordBean> getGameLogsFromDatabase() {
        List<RecordBean> rankList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllGameLogs();

        if (cursor.moveToFirst()) {
            do {
                String playDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PLAY_DATE));
                String playTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PLAY_TIME));
                int duration = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DURATION));
                int correctCount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CORRECT_COUNT));

                RecordBean record = new RecordBean();
                record.setDate(playDate);
                record.setTime(duration);
                record.setCorrect(correctCount);

                rankList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return rankList;
    }
}