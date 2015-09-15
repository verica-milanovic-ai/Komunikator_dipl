package rs.etf.mv110185.komunikator_dipl.user;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Verica Milanovic  on 06.08.2015..
 */
public class FinalOptionView extends OptionView {
    // text size on View
    public static final float FINAL_TEXT_SIZE = 12;

    public FinalOptionView(Context context) {
        super(context);
    }

    public FinalOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initOptionView(String text_val) {
        getText().setText(text_val);
        getText().setTextSize(FINAL_TEXT_SIZE);
    }
}
