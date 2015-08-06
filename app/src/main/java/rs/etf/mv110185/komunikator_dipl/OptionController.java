package rs.etf.mv110185.komunikator_dipl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by wekab on 06.08.2015..
 */
public class OptionController {
    private OptionModel model;
    private OptionView view;
    private Context context;

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

    // called when user select Option
    public List<OptionModel> optionSelected() {
        List<OptionModel> ret = null;
        if (model.getIs_final() == 0) {
            DBHelper dbHelper = new DBHelper(context);

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
