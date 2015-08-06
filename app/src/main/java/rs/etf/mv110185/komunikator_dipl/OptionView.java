package rs.etf.mv110185.komunikator_dipl;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.graphics.Color.BLACK;

/**
 * Created by wekab on 06.08.2015..
 */
public class OptionView extends LinearLayout {

    private ImageButton image;
    private TextView text;

    public OptionView(Context context) {
        super(context);
    }

    public OptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


     void initOptionView(Bitmap img, String text_val) {
        image = new ImageButton(this.getContext());
        image.setImageBitmap(img);
        text = new TextView(this.getContext());
        text.setText(text_val);
        image.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(60, 60);
        params1.setMargins(10, 5, 10, 0);
        image.setLayoutParams(params1);
        text.setSingleLine(false);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        text.setTextColor(BLACK);

        this.addView(image);
        this.addView(text);
    }


}
