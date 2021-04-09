package ir.fanniherfei.sql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FeedReaderDbHelper dbHelper ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //share pref
        SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
        //editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //Create Card
        CardView CC = findViewById(R.id.createCard);
        //Insert Card
        CardView IC = findViewById(R.id.insertCard);
        IC.setVisibility(View.GONE);
        //select Scrool
        ScrollView cv = findViewById(R.id.tableScrol);
        cv.setVisibility(View.GONE);
        //show button
        Button show = findViewById(R.id.show);
        show.setVisibility(View.GONE);
        //create Button
        Button create = findViewById(R.id.create);
        create.setText("Create");
        //Switch
        Switch newTable = findViewById(R.id.switch1);
        newTable.setChecked(true);
        newTable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                create.setText(isChecked ? "Create" : "Open");
            }
        });
        String tableName = sharedPref.getString("tableName",null);
        EditText name = findViewById(R.id.editTextNameCreate);
        EditText firstCulumn = findViewById(R.id.editTextTitleCreate);
        EditText secondCulumn = findViewById(R.id.editTextSubtitleCreate);
        if(tableName != null){
            newTable.setChecked(false);
            name.setText(tableName);
            firstCulumn.setText(sharedPref.getString("firstColumn",""));
            secondCulumn.setText(sharedPref.getString("secondColumn",""));
        }
        //create or new
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView titleInsert = findViewById(R.id.textViewTitleInsert);
                TextView subtitleInsert = findViewById(R.id.textViewSubtitleInsert);
                TextView titleShow = findViewById(R.id.titleHead);
                TextView subtitleShow = findViewById(R.id.subtitleHead);
                FeedEntry.TABLE_NAME = name.getText().toString();
                FeedEntry.COLUMN_NAME_TITLE = firstCulumn.getText().toString();
                FeedEntry.COLUMN_NAME_SUBTITLE = secondCulumn.getText().toString();
                if(!FeedEntry.TABLE_NAME.matches("[a-zA-Z ]+")||!FeedEntry.COLUMN_NAME_TITLE.matches("[a-zA-Z ]+")||!FeedEntry.COLUMN_NAME_SUBTITLE.matches("[a-zA-Z ]+"))
                {
                    Toast.makeText(getApplicationContext(),"unvalid!",Toast.LENGTH_LONG).show();
                    return;
                }
                upgrade();
                dbHelper= new FeedReaderDbHelper(MainActivity.this);
                editor.putString("tableName",FeedEntry.TABLE_NAME);
                editor.putString("firstColumn",FeedEntry.COLUMN_NAME_TITLE);
                editor.putString("secondColumn",FeedEntry.COLUMN_NAME_SUBTITLE);
                editor.apply();
                CC.setVisibility(View.GONE);
                IC.setVisibility(View.VISIBLE);
                cv.setVisibility(View.VISIBLE);
                show.setVisibility(View.VISIBLE);
                titleInsert.setText(FeedEntry.COLUMN_NAME_TITLE);
                subtitleInsert.setText(FeedEntry.COLUMN_NAME_SUBTITLE);
                titleShow.setText(FeedEntry.COLUMN_NAME_TITLE);
                subtitleShow.setText(FeedEntry.COLUMN_NAME_SUBTITLE);
                show(dbHelper,FeedEntry._ID + " ASC");
            }
        });


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
                show(dbHelper,FeedEntry._ID + " ASC");
            }
        });


    //---------------------------------------------------------------------------------------------


        //show
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(dbHelper,FeedEntry.COLUMN_NAME_TITLE + " DESC");
            }
        });

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


    void show(FeedReaderDbHelper dbHelper,String sort){
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
        String sortOrder =sort;
        Cursor cursor = dbp.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List ID = new ArrayList<>();
        List TITLE = new ArrayList<>();
        List SUBTITLE = new ArrayList<>();
        while(cursor.moveToNext()) {
            String Id = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry._ID));
            ID.add(Id);

            String Title = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            TITLE.add(Title);

            String SubTitle = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            SUBTITLE.add(SubTitle);
        }
        cursor.close();
        RecyclerView Table = findViewById(R.id.Table);
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(MainActivity.this,ID,TITLE,SUBTITLE);
        Table.setLayoutManager(new LinearLayoutManager(this));
        Table.setAdapter(adapter);
    }
    void upgrade(){
        SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                        FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
        SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    }

    public static class FeedEntry implements BaseColumns {
        public static  String TABLE_NAME = "entry";
        public static  String COLUMN_NAME_TITLE = "title";
        public static  String COLUMN_NAME_SUBTITLE = "subtitle";
    }


     static  String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";

     static  String SQL_DELETE_ENTRIES =
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
            Toast.makeText(getApplicationContext(),SQL_CREATE_ENTRIES,Toast.LENGTH_LONG).show();
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