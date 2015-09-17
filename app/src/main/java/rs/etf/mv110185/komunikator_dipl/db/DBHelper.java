package rs.etf.mv110185.komunikator_dipl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Verica Milanovic  on 8/2/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String FILE_NAME = "populateDB.txt";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KomunikatorDB.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOL_TYPE = " INTEGER";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.CommunicatorOption.TABLE_NAME + " (" +
                    DBContract.CommunicatorOption.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_VOICE_SRC + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_FINAL_TEXT + TEXT_TYPE + " DEFAULT NULL" + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_IS_FINAL + BOOL_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_IS_SUB_OPTION + BOOL_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_PARENT + INT_TYPE + " DEFAULT 0 REFERENCES option(id) ON DELETE SET DEFAULT )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.CommunicatorOption.TABLE_NAME;

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        populateDB(db);
    }

    private void populateDB(SQLiteDatabase db) {
        // must be in separate thread! it's long lasting operation!
        new PopulateDBTask().execute(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addOption(OptionModel option) {
        // 1. get database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 2. set values
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC, option.getImage_src());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_VOICE_SRC, option.getVoice_src());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_IS_SUB_OPTION, option.getIs_sub_option());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_IS_FINAL, option.getIs_final());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_PARENT, option.getParent());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_FINAL_TEXT, option.getFinal_text());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_TEXT, option.getText());

        // 3. insert
        db.insert(DBContract.CommunicatorOption.TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }


    // C R U D operations => (create "add", read "get", update, delete) option

    public OptionModel getOption(int id) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                        DBContract.CommunicatorOption.COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build option object
        OptionModel option = new OptionModel();
        option.setId(Integer.parseInt(cursor.getString(0)));
        option.setImage_src(cursor.getString(1));
        option.setVoice_src(cursor.getString(2));
        option.setIs_sub_option(Integer.parseInt(cursor.getString(3)));
        option.setIs_final(Integer.parseInt(cursor.getString(4)));
        option.setParent(Integer.parseInt(cursor.getString(5)));
        option.setFinal_text(cursor.getString(6));
        option.setText(cursor.getString(7));

        // 5. return option
        return option;
    }

    // Get All Options
    public List<OptionModel> getAllOptions() {
        List<OptionModel> opts = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + DBContract.CommunicatorOption.TABLE_NAME;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build option and add it to list
        OptionModel option = null;
        if (cursor.moveToFirst()) {
            do {
                option = new OptionModel();
                option.setId(Integer.parseInt(cursor.getString(0)));
                option.setImage_src(cursor.getString(1));
                option.setVoice_src(cursor.getString(2));
                option.setIs_sub_option(Integer.parseInt(cursor.getString(3)));
                option.setIs_final(Integer.parseInt(cursor.getString(4)));
                option.setParent(Integer.parseInt(cursor.getString(5)));
                option.setFinal_text(cursor.getString(6));
                option.setText(cursor.getString(7));

                // Add option to opts
                opts.add(option);
            } while (cursor.moveToNext());
        }

        // return opts
        return opts;
    }

    // Get All SubOptions from parent
    public List<OptionModel> getAllOptions(OptionModel model) {
        List<OptionModel> opts = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + DBContract.CommunicatorOption.TABLE_NAME + " WHERE "
                + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '" + model.getId() + "'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build option and add it to list
        OptionModel option = null;
        if (cursor.moveToFirst()) {
            do {
                option = new OptionModel();
                option.setId(Integer.parseInt(cursor.getString(0)));
                option.setImage_src(cursor.getString(1));
                option.setVoice_src(cursor.getString(2));
                option.setIs_sub_option(Integer.parseInt(cursor.getString(3)));
                option.setIs_final(Integer.parseInt(cursor.getString(4)));
                option.setParent(Integer.parseInt(cursor.getString(5)));
                option.setFinal_text(cursor.getString(6));
                option.setText(cursor.getString(7));

                // Add option to opts
                opts.add(option);
            } while (cursor.moveToNext());
        }

        // return opts
        return opts;
    }

    // Updating single option
    public int updateOption(OptionModel option) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC, option.getImage_src());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_VOICE_SRC, option.getVoice_src());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_IS_SUB_OPTION, option.getIs_sub_option());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_IS_FINAL, option.getIs_final());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_PARENT, option.getParent());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_FINAL_TEXT, option.getFinal_text());
        values.put(DBContract.CommunicatorOption.COLUMN_NAME_TEXT, option.getText());

        // 3. updating row
        int i = db.update(DBContract.CommunicatorOption.TABLE_NAME, //table
                values, // column/value
                DBContract.CommunicatorOption.COLUMN_NAME_ID + " = ?", // selections
                new String[]{String.valueOf(option.getId())}); //selection args

        // 4. close
        db.close();
        return i;
    }

    // Deleting single option
    public void deleteOption(OptionModel option) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(DBContract.CommunicatorOption.TABLE_NAME,
                DBContract.CommunicatorOption.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(option.getId())});

        // 3. close
        db.close();
    }

    private class PopulateDBTask extends AsyncTask<SQLiteDatabase, Void, Void> {
        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */

        @Override
        protected Void doInBackground(SQLiteDatabase... params) {

            File f = new File(context.getFilesDir(), FILE_NAME);
            SQLiteDatabase db = params[0];
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    db.execSQL(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(Void result) {

        }
    }

}
