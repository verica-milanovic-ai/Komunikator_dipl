package rs.etf.mv110185.komunikator_dipl.admin;

import android.content.Context;

import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 15.09.2015..
 */
public class OptionController extends rs.etf.mv110185.komunikator_dipl.user.OptionController {

    public OptionController(OptionModel model, rs.etf.mv110185.komunikator_dipl.user.OptionView view, Context context) {
        super(model, view, context);
    }

    public OptionController(OptionModel model, Context context) {
        super(model, context);
    }

    // methods for changing model
    public void changeImage(String imageSrc) {
        model.setImage_src(imageSrc);
    }

    public void changeText(String newText) {
        model.setText(newText);
    }

}
