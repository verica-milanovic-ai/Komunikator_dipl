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
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_IMAGE_SRC = "image_src";
        public static final String COLUMN_NAME_IS_SUB_OPTION = "is_sub_option";
        public static final String COLUMN_NAME_IS_FINAL = "is_final";
        public static final String COLUMN_NAME_PARENT = "parent";
        public static final String COLUMN_NAME_FINAL_TEXT = "final_text";
        public static final String COLUMN_NAME_TEXT = "text";

        public static final String[] COLUMNS = {COLUMN_NAME_ID, COLUMN_NAME_IMAGE_SRC,
                COLUMN_NAME_IS_SUB_OPTION, COLUMN_NAME_IS_FINAL, COLUMN_NAME_PARENT,
                COLUMN_NAME_FINAL_TEXT, COLUMN_NAME_TEXT};
    }
}
