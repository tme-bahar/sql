package ir.fanniherfei.sql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //create table
        FeedReaderDbHelper dbHelper;
        dbHelper = new FeedReaderDbHelper(MainActivity.this);

        //insert
        EditText title = findViewById(R.id.editTextTitleInsert);
        EditText subtitle = findViewById(R.id.editTextSubtitleInsert);
        Button insert = findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(dbHelper,title.getText().toString(),subtitle.getText().toString());
                title.setText("");
                subtitle.setText("");
                show(dbHelper);
            }
        });


    //---------------------------------------------------------------------------------------------


        //show
        Button show = findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(dbHelper);
            }
        });

        //auto show
        show(dbHelper);
    }


    void insert(FeedReaderDbHelper dbHelper,String title,String subtitle){
        //insert
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        // Insert the new row, returning the primary key value of the new row
        db.insert(FeedEntry.TABLE_NAME, null, values);

        //notif
        Toast.makeText(getApplicationContext(),"Insert done", Toast.LENGTH_LONG).show();
    }


    void show(FeedReaderDbHelper dbHelper){
        SQLiteDatabase dbp = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry._ID + " ASC";
        Cursor cursor = dbp.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List itemIds = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        StringBuilder ID = new StringBuilder();
        StringBuilder TITLE = new StringBuilder();
        StringBuilder SUBTITLE = new StringBuilder();
        while(cursor.moveToNext()) {
            String Id = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry._ID));
            sb.append(Id);
            sb.append("---");
            ID.append(Id).append("\n\n").append("----").append("\n\n");

            String Title = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            sb.append(Title);
            sb.append("---");
            TITLE.append(Title).append("\n\n").append("----").append("\n\n");

            String SubTitle = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            sb.append(SubTitle);
            sb.append("\n");
            SUBTITLE.append(SubTitle).append("\n\n").append("----").append("\n\n");

            itemIds.add("itemId");
        }
        cursor.close();
        TextView Idtv = findViewById(R.id.idText);
        TextView Titletv = findViewById(R.id.titleText);
        TextView Subtv = findViewById(R.id.subtitleText);
        Idtv.setText(ID.toString());
        Titletv.setText(TITLE.toString());
        Subtv.setText(SUBTITLE.toString());
    }


    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}