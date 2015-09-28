package rs.etf.mv110185.komunikator_dipl.listeners;

import android.view.View;
import android.widget.ImageView;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 26.09.2015..
 */
public class UserOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        ImageView im = (ImageView) v;
        OptionModel model = (OptionModel) im.getTag();
        CommunicatorController.setCurrentOption(model);
        if (model.getVoice_src() != null)
            CommunicatorController.startPlaying(model.getVoice_src(), model);
        CommunicatorController.changeOptions();

    }
}
