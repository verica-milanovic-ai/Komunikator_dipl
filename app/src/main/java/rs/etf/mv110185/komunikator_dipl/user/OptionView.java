package rs.etf.mv110185.komunikator_dipl.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import rs.etf.mv110185.komunikator_dipl.R;

import static android.graphics.Color.BLACK;

/**
 * Created by Verica Milanovic  on 06.08.2015..
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

    public ImageButton getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image.setImageBitmap(image);
        this.image.setScaleType(ImageView.ScaleType.CENTER);
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public void initOptionView(Bitmap img, String text_val) {
        image = new ImageButton(this.getContext());
        image.setId(R.id.imageButton);
        image.setImageBitmap(img);
        text = new TextView(this.getContext());
        text.setText(text_val);
        text.setId(R.id.textView);
        image.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(60, 60);
        params1.setMargins(10, 5, 10, 0);
        image.setLayoutParams(params1);
        text.setSingleLine(false);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        text.setTextColor(BLACK);
        text.setLayoutParams(params2);
        this.addView(image);
        this.addView(text);
    }

    public void configureImageButton(ImageButton image, Bitmap img) {
        image.setImageBitmap(img);
        image.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(60, 60);
        params1.setMargins(10, 5, 10, 0);
        image.setLayoutParams(params1);
    }


}
