package rs.etf.mv110185.komunikator_dipl.listeners;

import android.view.View;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 26.09.2015..
 */
public class DeleteOnClickListener implements View.OnClickListener {
    public void onClick(View v) {
        if (CommunicatorController.helper == null)
            CommunicatorController.helper = new DBHelper(CommunicatorController.mainActivityContext);
        OptionModel mod = (OptionModel) v.getTag();
        CommunicatorController.helper.deleteOption(mod);
        CommunicatorController.changeOptions();
    }
}
