package rs.etf.mv110185.komunikator_dipl.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import rs.etf.mv110185.komunikator_dipl.R;


/**
 * Created by Verica Milanovic on 15.09.2015..
 */
public class OptionView extends rs.etf.mv110185.komunikator_dipl.user.OptionView {

    private ImageButton delete;

    public OptionView(Context context) {
        super(context);
    }

    public OptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initOptionView(Bitmap img, String text_val) {
        super.initOptionView(img, text_val);
        delete = new ImageButton(getContext());
        delete.setImageResource(R.drawable.izbrisi);
        delete.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
        // TODO : check position!!!
        this.addView(delete);
    }

}
