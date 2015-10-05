package rs.etf.mv110185.komunikator_dipl.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Verica Milanovic  on 8/2/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String FILE_NAME = "populateDB.txt";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KomunikatorDB.db";
    public static final String NO_FLAG = "NO";
    public static final String YES_FLAG = "YES";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOL_TYPE = " INTEGER";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.CommunicatorOption.TABLE_NAME + " (" +
                    DBContract.CommunicatorOption._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_VOICE_SRC + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_FINAL_TEXT + TEXT_TYPE + " DEFAULT NULL" + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_IS_FINAL + BOOL_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_IS_SUB_OPTION + BOOL_TYPE + COMMA_SEP +
                    DBContract.CommunicatorOption.COLUMN_NAME_PARENT + INT_TYPE + " DEFAULT 0 REFERENCES option(" +
                    DBContract.CommunicatorOption.COLUMN_NAME_PARENT + ") ON DELETE SET DEFAULT )";
    private static final String SQL_CREATE_FLAGS =
            "CREATE TABLE " + DBContract.CommunicatorFlag.TABLE_NAME + " (" +
                    DBContract.CommunicatorFlag._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DBContract.CommunicatorFlag.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    DBContract.CommunicatorFlag.COLUMN_NAME_VALUE + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.CommunicatorOption.TABLE_NAME;
    private static final String SQL_DELETE_FLAGS =
            "DROP TABLE IF EXISTS " + DBContract.CommunicatorFlag.TABLE_NAME;
    public static boolean populated = false;
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    public static OptionModel fillOption(Cursor cursor) {
        OptionModel option = new OptionModel();
        option.setId(cursor.getInt(cursor.getColumnIndex(DBContract.CommunicatorOption._ID)));
        option.setImage_src(cursor.getString(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC)));
        option.setVoice_src(cursor.getString(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_VOICE_SRC)));
        option.setIs_sub_option(cursor.getInt(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IS_SUB_OPTION)));
        option.setIs_final(cursor.getInt(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IS_FINAL)));
        option.setParent(cursor.getInt(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_PARENT)));
        option.setFinal_text(cursor.getString(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_FINAL_TEXT)));
        option.setText(cursor.getString(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_TEXT)));
        return option;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_FLAGS);
        populateDB(db);
    }

    private void populateDB(SQLiteDatabase db) {
        // must be in separate thread! it's long lasting operation!
        fillDB(db);
        //new PopulateDBTask().execute(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_FLAGS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * *******************************************************************************************
     * **********************************  FLAG  *************************************************
     * *******************************************************************************************
     */


    public void addFlag(FlagModel flag) {
        // 1. get database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 2. set values
        values.put(DBContract.CommunicatorFlag.COLUMN_NAME_NAME, flag.getName());
        values.put(DBContract.CommunicatorFlag.COLUMN_NAME_VALUE, flag.getValue());

        // 3. insert
        db.insert(DBContract.CommunicatorFlag.TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public FlagModel getFlag(int id) {
        FlagModel ret = new FlagModel();
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(DBContract.CommunicatorFlag.TABLE_NAME, // a. table
                        DBContract.CommunicatorFlag.COLUMNS, // b. column names
                        " " + DBContract.CommunicatorFlag._ID + " = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor.moveToFirst()) {

            // 4. build option object
            ret.setId(Integer.parseInt(cursor.getString(0)));
            ret.setName(cursor.getString(1));
            ret.setValue(cursor.getString(2));
        }
        // 5. return option
        db.close();
        return ret;
    }

    public FlagModel getFlag(String name) {
        FlagModel ret = new FlagModel();
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(DBContract.CommunicatorFlag.TABLE_NAME, // a. table
                        DBContract.CommunicatorFlag.COLUMNS, // b. column names
                        " name = ?", // c. selections
                        new String[]{String.valueOf(name)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor.moveToFirst()) {


            // 4. build option object
            ret.setId(Integer.parseInt(cursor.getString(0)));
            ret.setName(cursor.getString(1));
            ret.setValue(cursor.getString(2));
        }
        db.close();
        // 5. return option
        return ret;
    }

    public int updateFlag(FlagModel flag) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(DBContract.CommunicatorFlag.COLUMN_NAME_NAME, flag.getName());
        values.put(DBContract.CommunicatorFlag.COLUMN_NAME_VALUE, flag.getValue());

        // 3. updating row
        int i = db.update(DBContract.CommunicatorFlag.TABLE_NAME, //table
                values, // column/value
                DBContract.CommunicatorFlag.COLUMN_NAME_NAME + " LIKE ?", // selections
                new String[]{flag.getName()}); //selection args

        // 4. close
        db.close();
        return i;
    }

    // Deleting single option
    public void deleteFlag(FlagModel flag) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(DBContract.CommunicatorFlag.TABLE_NAME,
                DBContract.CommunicatorFlag._ID + " = ?",
                new String[]{String.valueOf(flag.getId())});

        // 3. close
        db.close();
    }

    /**
     * *******************************************************************************************
     * **********************************   OPTION  **********************************************
     * *******************************************************************************************
     */
    public void addOption(OptionModel option, SQLiteDatabase db) {
        // 1. get database
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
        // db.close();
    }


    // C R U D operations => (create "add", read "get", update, delete) option

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

    public int getLastOptionID(SQLiteDatabase db) {
        // 1. get reference to readable DB
        //SQLiteDatabase db = this.getReadableDatabase();
        final String MY_QUERY = "SELECT MAX(" + DBContract.CommunicatorOption._ID + ") AS _id FROM option";

        // 2. build query
        Cursor cursor =
                db.rawQuery(MY_QUERY, null); // h. limit

        // 3. if we got results get the first one
        int max_id = -1;
        if (cursor != null) {
            cursor.moveToFirst();
            max_id = cursor.getInt(cursor.getColumnIndex("_id"));
        }
        // 5. return option
        return max_id;
    }

    public OptionModel getOption(String text, SQLiteDatabase db) {
        // 1. get reference to readable DB
        //SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                        DBContract.CommunicatorOption.COLUMNS, // b. column names
                        " " + DBContract.CommunicatorOption.COLUMN_NAME_TEXT + " LIKE '" + text + "'", // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        OptionModel option = null;
        if (cursor != null) {
            cursor.moveToFirst();

            // 4. build option object
            option = fillOption(cursor);
        }
        // 5. return option
        return option;
    }

    public OptionModel getOption(int id) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                        DBContract.CommunicatorOption.COLUMNS, // b. column names
                        " " + DBContract.CommunicatorOption._ID + " = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        OptionModel option = null;
        if (cursor != null) {
            cursor.moveToFirst();

            // 4. build option object
            option = fillOption(cursor);
        }
        db.close();
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
                option = fillOption(cursor);

                // Add option to opts
                opts.add(option);
            } while (cursor.moveToNext());
        }
        db.close();
        // return opts
        return opts;
    }

    // Get All SubOptions from parent
    public Cursor getAllOptions_cursor(OptionModel model, SQLiteDatabase db) {
        // 1. build the query
        Cursor cursor;
        //SQLiteDatabase db = this.getWritableDatabase();
        if (model != null) {
            cursor = db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                    DBContract.CommunicatorOption.COLUMNS, // b. column names
                    " " + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '" + String.valueOf(model.getId()) + "'", // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        } else {
            cursor = db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                    DBContract.CommunicatorOption.COLUMNS, // b. column names
                    " " + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '0'", // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        }

        // 2. get reference to writable DB
        //db.close();
        return cursor;
    }

    public Cursor getAllOptions_cursor(OptionModel model) {
        // 1. build the query
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        if (model != null) {
            cursor = db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                    DBContract.CommunicatorOption.COLUMNS, // b. column names
                    " " + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '"
                            + String.valueOf(model.getId()) + "'", // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        } else {
            cursor = db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                    DBContract.CommunicatorOption.COLUMNS, // b. column names
                    " " + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '0'", // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        }
        // 2. return cursor
        return cursor;
    }

    public List<OptionModel> getAllOptions(OptionModel model) {
        List<OptionModel> opts = new LinkedList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = getAllOptions_cursor(model, db);

        // 3. go over each row, build option and add it to list
        OptionModel option = null;
        if (cursor.moveToFirst()) {
            do {
                option = fillOption(cursor);

                // Add option to opts
                opts.add(option);
            } while (cursor.moveToNext());
        }

        // return opts
        db.close();
        return opts;
    }

    // Updating single option
    public int updateOption(OptionModel option, SQLiteDatabase db) {


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
                DBContract.CommunicatorOption._ID + " = ?", // selections
                new String[]{String.valueOf(option.getId())}); //selection args

        // 4. close
        // db.close();
        return i;
    }

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
                DBContract.CommunicatorOption._ID + " = ?", // selections
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
                DBContract.CommunicatorOption._ID + " = ?",
                new String[]{String.valueOf(option.getId())});
        Cursor cursor = db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                DBContract.CommunicatorOption.COLUMNS, // b. column names
                " " + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '" + String.valueOf(option.getId()) + "'", // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        // delete all children and their children, and their children... :D
        while (cursor.moveToNext()) {
            deleteOption(fillOption(cursor), db);
        }
        // 3. close
        db.close();
    }

    public void deleteOption(OptionModel option, SQLiteDatabase db) {

        // 1. get reference to writable DB
        // SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(DBContract.CommunicatorOption.TABLE_NAME,
                DBContract.CommunicatorOption._ID + " = ?",
                new String[]{String.valueOf(option.getId())});
        Cursor cursor = db.query(DBContract.CommunicatorOption.TABLE_NAME, // a. table
                DBContract.CommunicatorOption.COLUMNS, // b. column names
                " " + DBContract.CommunicatorOption.COLUMN_NAME_PARENT + " = '"
                        + String.valueOf(option.getId())
                        + "'", // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        // delete all children and their children, and their children...
        while (cursor.moveToNext()) {
            deleteOption(fillOption(cursor), db);
        }
    }


    private void fillDB(SQLiteDatabase db) {
        fillFromAssets(db);
        logOptionTable(db);
        populated = true;


    }

    private void logOptionTable(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT * FROM " + DBContract.CommunicatorOption.TABLE_NAME, null);
        if (c != null) {
            while (c.moveToNext()) {
                OptionModel op = fillOption(c);
                Log.e("DATABASE - option", op.toString());
            }
        }
    }

    private void fillFromAssets(SQLiteDatabase db) {

        AssetManager manager = context.getAssets();
        try {
            String[] files_n_folders = manager.list("options_images");
            for (int i = 0; i < files_n_folders.length; i++) {
                String name = files_n_folders[i].substring(2);
                OptionModel model = new OptionModel();
                model.setText(name);
                model.setParent(0);
                // CREATE OPTION IN DATABASE!!!
                addOption(model, db);
                // u osnovnom folderu su sve folderi!!!!
                fillFromFolder("options_images" + File.separator + files_n_folders[i], manager, getLastOptionID(db), db);
                //logOptionTable(db);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // manager.close();
        }


    }

    private void fillFromFolder(String files_n_folder, AssetManager manager, int parent_id, SQLiteDatabase db) throws IOException {
        String folder_name = files_n_folder;
        StringTokenizer tokenizer = new StringTokenizer(folder_name, File.separator);
        String[] content = context.getAssets().list(folder_name);
        for (int i = 0; i < content.length; i++) {
            //folders begins with number
            if (content[i].charAt(0) >= '0' && content[i].charAt(0) <= '9') {
                String path = folder_name + "/" + content[i];
                String name = content[i].substring(content[i].indexOf('$') + 1);
                OptionModel model = new OptionModel();
                model.setText(name);
                model.setParent(parent_id);
                // CREATE OPTION IN DATABASE!!!
                addOption(model, db);

                fillFromFolder(path, manager, getLastOptionID(db), db);

            } else {    //files don't begin with number
                String option_name = content[i];
                String path = folder_name + File.separator + option_name;
                OptionModel tmp = new OptionModel();

                //////////// COPYING!!!
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = manager.open(path);
                    File outFile = new File(context.getFilesDir(), option_name);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                    path = outFile.getAbsolutePath();
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + option_name, e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
                /////////// FINISHED!

                tmp.setImage_src(path);
                tmp.setText(option_name.substring(0, option_name.length() - 4));
                tmp.setFinal_text(option_name.substring(0, option_name.length() - 4));
                boolean isFinal = false;
                String folder_path = folder_name;
                String name = option_name.substring(0, option_name.length() - 4);
                isFinal = !folder_path.endsWith(name);
                if (isFinal) {
                    tmp.setParent(parent_id);
                    tmp.setIs_final(isFinal ? 1 : 0);
                    addOption(tmp, db);
                } else {
                    OptionModel real = getOption(name, db);
                    tmp.setId(real.getId());
                    tmp.setParent(real.getParent());
                    tmp.setIs_final(content.length == 1 ? 1 : 0);
                    updateOption(tmp, db);
                }

            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
