package rs.etf.mv110185.komunikator_dipl;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import rs.etf.mv110185.komunikator_dipl.db.DBContract;
import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.listeners.AdminOnLongClickListener;
import rs.etf.mv110185.komunikator_dipl.listeners.UserOnClickListener;

/**
 * Created by Verica Milanovic on 26.09.2015..
 */
public class MyOptionBinder implements SimpleCursorAdapter.ViewBinder {

    @Override
    public boolean setViewValue(View view, Cursor cursor,
                                int columnIndex) {
        // IMAGE BUTTON - Picture
        if (view.getId() == R.id.imageButton) {
            String path = cursor.getString(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC));
            ImageButton ib = (ImageButton) view;
            ib.setImageDrawable(Drawable.createFromPath(path));
            ib.setTag(DBHelper.fillOption(cursor));
            if (CommunicatorController.IS_ADMIN == 1)
                ib.setOnLongClickListener(new AdminOnLongClickListener());
            ib.setOnClickListener(new UserOnClickListener());
            ib.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            if (CommunicatorController.IS_ADMIN == 1) {
                //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 70);
                //params.setMargins(0, 10, 0, 10);
                //ib.setLayoutParams(params);
                ib.setTag(DBHelper.fillOption(cursor));
                ib.setOnClickListener(new UserOnClickListener());
                if (CommunicatorController.IS_ADMIN == 1)
                    ib.setOnLongClickListener(new AdminOnLongClickListener());

            } else {
                // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 70);
                // params.setMargins(0, 10, 0, 10);
                // ib.setLayoutParams(params);
            }
            return true;
            // TEXT VIEW
        } else if (view.getId() == R.id.textView) {
            TextView tv = (TextView) view;
            tv.setText(cursor.getString(cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_TEXT)));
            return true;
            // IMAGE BUTTON DELETE
        }
        return false;
    }

}
