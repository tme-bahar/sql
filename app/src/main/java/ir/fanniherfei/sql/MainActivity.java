package ir.fanniherfei.sql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FeedReaderDbHelper dbHelper ;
    String sortColumn = FeedEntry._ID;
    String sortArrangement = "ASC";
    String selection = null;
    String[] selectionArgs = null;
    int sorted =0;
    boolean filterOn = false;
    List ID = new ArrayList<>();
    SQLiteDatabase mydatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydatabase = openOrCreateDatabase("database.db",MODE_PRIVATE,null);
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
        //heads
        TextView IdShow = findViewById(R.id.idHead);
        TextView titleShow = findViewById(R.id.titleHead);
        TextView subtitleShow = findViewById(R.id.subtitleHead);
        TextView checkedShow = findViewById(R.id.checkedHead);
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
        EditText thirdColumn = findViewById(R.id.editTextThirdCreate);
        if(tableName != null){
            newTable.setChecked(false);
            name.setText(tableName);
            firstCulumn.setText(sharedPref.getString("firstColumn",""));
            secondCulumn.setText(sharedPref.getString("secondColumn",""));
            thirdColumn.setText(sharedPref.getString("thirdColumn",""));
        }
        //create or new
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView titleInsert = findViewById(R.id.textViewTitleInsert);
                TextView subtitleInsert = findViewById(R.id.textViewSubtitleInsert);
                TextView checkedInsert = findViewById(R.id.textViewCheckedInsert);
                FeedEntry.TABLE_NAME = name.getText().toString();
                FeedEntry.COLUMN_NAME_TITLE = firstCulumn.getText().toString();
                FeedEntry.COLUMN_NAME_SUBTITLE = secondCulumn.getText().toString();
                FeedEntry.COLUMN_NAME_CHECK = thirdColumn.getText().toString();
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
                editor.putString("thirdColumn",FeedEntry.COLUMN_NAME_CHECK);
                editor.apply();
                CC.setVisibility(View.GONE);
                IC.setVisibility(View.VISIBLE);
                cv.setVisibility(View.VISIBLE);
                titleInsert.setText(FeedEntry.COLUMN_NAME_TITLE);
                subtitleInsert.setText(FeedEntry.COLUMN_NAME_SUBTITLE);
                checkedInsert.setText(FeedEntry.COLUMN_NAME_CHECK);
                titleShow.setText(FeedEntry.COLUMN_NAME_TITLE);
                subtitleShow.setText(FeedEntry.COLUMN_NAME_SUBTITLE);
                checkedShow.setText(FeedEntry.COLUMN_NAME_CHECK);
                show();
            }
        });


        //insert
        EditText title = findViewById(R.id.editTextTitleInsert);
        EditText subtitle = findViewById(R.id.editTextSubtitleInsert);
        EditText checked = findViewById(R.id.editTextCheckedInsert);
        EditText ID = findViewById(R.id.editTextIdInsert);
        Button insert = findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ID.getText().toString().isEmpty())
                insert(dbHelper,title.getText().toString(),subtitle.getText().toString(),checked.getText().toString());
                else{
                    if(title.getText().length() != 0)
                        update(title.getText().toString(),FeedEntry.COLUMN_NAME_TITLE,ID.getText().toString());
                    if(subtitle.getText().length() != 0)
                        update(subtitle.getText().toString(),FeedEntry.COLUMN_NAME_SUBTITLE,ID.getText().toString());
                    if(checked.getText().length() != 0)
                        update(checked.getText().toString(),FeedEntry.COLUMN_NAME_CHECK,ID.getText().toString());
                }
                title.setText("");
                subtitle.setText("");
                checked.setText("");
                show();
            }
        });

        //update
        ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                insert.setText("insert");
            else
                insert.setText("update");
            }
        });

    //---------------------------------------------------------------------------------------------


        //show
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        //filter
        EditText filterValue = findViewById(R.id.editTextTitleFilter);
        Switch columnFilter = findViewById(R.id.switch2);
        FloatingActionButton Filter = findViewById(R.id.floatingActionButton);
        CardView filterCard = findViewById(R.id.filterCard);
        Button filterButton = findViewById(R.id.filter);
        filterCard.setVisibility(View.GONE);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(filterValue.getText().toString().isEmpty()){
                        return;}
                    selectionArgs = new String[1];
                    selectionArgs[0] = filterValue.getText().toString();
                    switchFilterColumn(columnFilter);
            }
        });
        Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterCard.setVisibility(filterOn ? View.GONE : View.VISIBLE);
                filterOn = !filterOn;
                selection = null;
                selectionArgs  = null;
                show();
            }
        });


        //sorting
        IdShow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                sortColumn = FeedEntry._ID;
                sortArrangement = (sorted != 0) ? "DESC": sortArrangement;
                sorted = 0;
                if("ASC".equals(sortArrangement)){
                    sortArrangement = "DESC";
                    IdShow.setBackgroundColor(Color.parseColor("#ff0000"));
                    titleShow.setBackgroundResource(R.color.teal_200);
                    subtitleShow.setBackgroundResource(R.color.teal_200);
                }else{
                    sortArrangement = "ASC";
                    IdShow.setBackgroundColor(Color.parseColor("#00ff00"));
                    titleShow.setBackgroundResource(R.color.teal_200);
                    subtitleShow.setBackgroundResource(R.color.teal_200);
                }
                show();
            }
        });
        titleShow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                sortColumn = FeedEntry.COLUMN_NAME_TITLE;
                sortArrangement = (sorted != 1) ? "DESC": sortArrangement;
                sorted = 1;
                if("ASC".equals(sortArrangement)){
                    sortArrangement = "DESC";
                    titleShow.setBackgroundColor(Color.parseColor("#ff0000"));
                    IdShow.setBackgroundResource(R.color.teal_200);
                    subtitleShow.setBackgroundResource(R.color.teal_200);
                }else{
                    sortArrangement = "ASC";
                    titleShow.setBackgroundColor(Color.parseColor("#00ff00"));
                    IdShow.setBackgroundResource(R.color.teal_200);
                    subtitleShow.setBackgroundResource(R.color.teal_200);
                }
                show();
            }
        });
        subtitleShow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                sortColumn = FeedEntry.COLUMN_NAME_SUBTITLE;
                sortArrangement = (sorted != 2) ? "DESC": sortArrangement;
                sorted = 2;
                if("ASC".equals(sortArrangement)){
                    sortArrangement = "DESC";
                    subtitleShow.setBackgroundColor(Color.parseColor("#ff0000"));
                    IdShow.setBackgroundResource(R.color.teal_200);
                    titleShow.setBackgroundResource(R.color.teal_200);
                }else{
                    sortArrangement = "ASC";
                    subtitleShow.setBackgroundColor(Color.parseColor("#00ff00"));
                    IdShow.setBackgroundResource(R.color.teal_200);
                    titleShow.setBackgroundResource(R.color.teal_200);
                }
                show();
            }
        });



    }
    void switchFilterColumn(Switch columnFilter){

        if(!columnFilter.isChecked())
            selection = FeedEntry.COLUMN_NAME_TITLE + " = ?";
        else
            selection = FeedEntry.COLUMN_NAME_SUBTITLE + " = ?";
        show();
    }
    int delete(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id)};
        // Issue SQL statement.
        return  db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
    }
    void update(String newvalue,String culumn,String ID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(culumn, newvalue);

        // Which row to update, based on the title
        String selection = FeedEntry._ID + " LIKE ?";
        String[] selectionArgs = { ID };

        int count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }
    void insert(FeedReaderDbHelper dbHelper,String title,String subtitle,String check){
        //insert
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_CHECK, check);
        values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        // Insert the new row, returning the primary key value of the new row
        db.insert(FeedEntry.TABLE_NAME, null, values);

        //notif
        Toast.makeText(getApplicationContext(),"Insert done", Toast.LENGTH_LONG).show();
    }


    void show(){
        SQLiteDatabase dbp = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_CHECK,
                FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =sortColumn + " " + sortArrangement;
        Cursor cursor = dbp.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        Cursor cursor = dbp.rawQuery("SELECT * FROM "+FeedEntry.TABLE_NAME+" WHERE "+selection+" ORDER BY "+sortOrder,selectionArgs);
        ID = new ArrayList<>();
        List TITLE = new ArrayList<>();
        List ISACTIVE = new ArrayList<>();
        List SUBTITLE = new ArrayList<>();
        while(cursor.moveToNext()) {
            String Id = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry._ID));
            ID.add(Id);
            String Title = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE));
            TITLE.add(Title);

            String ISACT = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_CHECK));
            ISACTIVE.add(ISACT);

            String SubTitle = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_SUBTITLE));
            SUBTITLE.add(SubTitle);
        }
        cursor.close();
        RecyclerView Table = findViewById(R.id.Table);
        Table.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(MainActivity.this,ID,TITLE,SUBTITLE,ISACTIVE);
        Table.setLayoutManager(new LinearLayoutManager(this));
        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("are you sure to delete this row ?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),
                                        delete(ID.get(position).toString())+" deleted!" , Toast.LENGTH_LONG).show();
                                show();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        Table.setAdapter(adapter);
    }
    void upgrade(){
        SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                        FeedEntry.COLUMN_NAME_CHECK + " BIT," +
                        FeedEntry.COLUMN_NAME_SUBTITLE + " TEXT)";
        SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
    public  static class FeedEntry implements BaseColumns {
        public static  String TABLE_NAME = "entry";
        public static  String COLUMN_NAME_TITLE = "title";
        public static  String COLUMN_NAME_CHECK = "isactive";
        public static  String COLUMN_NAME_SUBTITLE = "subtitle";
    }


     static  String SQL_CREATE_ENTRIES =
            "CREATE  TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedEntry.COLUMN_NAME_CHECK + " BIT," +
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
    public class columnInfo{
        public String name;
        public String type;
        public columnInfo(String name,String type){
            this.name = name;
            this.type = type;
        }
    }
}