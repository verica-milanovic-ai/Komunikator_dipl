package rs.etf.mv110185.komunikator_dipl.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.List;

import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 06.08.2015..
 */
public class OptionController {
    protected OptionModel model;
    protected OptionView view;
    protected Context context;

    public OptionController(OptionModel model, OptionView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
    }

    public OptionController(OptionModel model, Context context) {
        this.model = model;
        this.context = context;
        initControllerFromModel();
    }

    private void initControllerFromModel() {
        view = new OptionView(context);
        File image = new File(context.getFilesDir(), model.getImage_src());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);

        view.initOptionView(bitmap, model.getText());
        view.setId(model.getId());
    }

    // called when user select Option => it wont be in main thread!!!
    // must check if it's not null; possible null value means that selected option is final option
    public List<OptionModel> optionSelected() {
        List<OptionModel> ret = null;
        if (model.getIs_final() == 0) {
            DBHelper dbHelper = new DBHelper(context);
            // returns all child options from selected option
            ret = dbHelper.getAllOptions(model);
        }
        return ret;
    }

    public OptionModel getModel() {
        return model;
    }

    public void setModel(OptionModel model) {
        this.model = model;
    }

    public OptionView getView() {
        return view;
    }

    public void setView(OptionView view) {
        this.view = view;
    }
}
