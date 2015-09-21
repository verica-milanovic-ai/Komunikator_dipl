package rs.etf.mv110185.komunikator_dipl.user;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 06.08.2015..
 */
public class OptionController {
    protected OptionModel model;
    //   protected OptionView view;
    protected Context context;


    public OptionController(OptionModel model, Context context) {
        this.model = model;
        this.context = context;
        //   initControllerFromModel();
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

    /*  public OptionView getView() {
          return view;
      }

      public void setView(OptionView view) {
          this.view = view;
      }
  */
    // methods for changing model, doesn't have any effect because user can't change model!
    public void changeText(String newText) {
    }

    // called in MainActivity to open Image Resource Select Dialog
    public void selectImage(final AppCompatActivity mainActivity) {
    }

    // called in onActivityResult -> if (resultCode == RESULT_OK) if (requestCode == REQUEST_CAMERA)
    public void handleImageFromCamera(Intent data) {
    }

    // called in onActivityResult -> if (resultCode == RESULT_OK) if (requestCode == SELECT_FILE)
    public void handleImageFromGallery(Intent data) {
    }

    public void selectVoice(final AppCompatActivity mainActivity) {
    }

    // AudioRecorder did all job :D
    public void handleVoiceFromRecorder(Intent data) {
    }

    public void handleSelectedVoice(Intent data) {
    }
}
