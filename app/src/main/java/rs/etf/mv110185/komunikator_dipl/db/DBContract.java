package rs.etf.mv110185.komunikator_dipl.db;

import android.provider.BaseColumns;

/**
 * Created by Verica Milanovic  on 8/2/2015.
 */
public final class DBContract {
    public DBContract() {
    }

    public static abstract class CommunicatorOption implements BaseColumns {
        public static final String TABLE_NAME = "option";
        // public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_IMAGE_SRC = "image_src";
        public static final String COLUMN_NAME_VOICE_SRC = "voice_src";
        public static final String COLUMN_NAME_IS_SUB_OPTION = "is_sub_option";
        public static final String COLUMN_NAME_IS_FINAL = "is_final";
        public static final String COLUMN_NAME_PARENT = "parent";
        public static final String COLUMN_NAME_FINAL_TEXT = "final_text";
        public static final String COLUMN_NAME_TEXT = "name_text";

        public static final String[] COLUMNS = {_ID, COLUMN_NAME_IMAGE_SRC, COLUMN_NAME_VOICE_SRC,
                COLUMN_NAME_IS_SUB_OPTION, COLUMN_NAME_IS_FINAL, COLUMN_NAME_PARENT,
                COLUMN_NAME_FINAL_TEXT, COLUMN_NAME_TEXT};

    }

    public static abstract class CommunicatorFlag implements BaseColumns {
        public static final String TABLE_NAME = "flag";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_VALUE = "value";
        // public static final String COLUMN_NAME_ID = "_id";
        public static final String[] COLUMNS = {_ID, COLUMN_NAME_NAME, COLUMN_NAME_VALUE};
    }
}
