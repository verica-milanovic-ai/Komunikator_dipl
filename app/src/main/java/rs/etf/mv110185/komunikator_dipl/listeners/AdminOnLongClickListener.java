package rs.etf.mv110185.komunikator_dipl.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.R;
import rs.etf.mv110185.komunikator_dipl.admin.OptionController;
import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 26.09.2015..
 */
public class AdminOnLongClickListener implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(final View v) {
        if (CommunicatorController.IS_ADMIN == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CommunicatorController.mainActivityContext);
            final CharSequence[] items = {CommunicatorController.mainActivityContext.getString(R.string.set_image),
                    CommunicatorController.mainActivityContext.getString(R.string.set_text), CommunicatorController.mainActivityContext.getString(R.string.set_sound)};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (CommunicatorController.helper == null)
                        CommunicatorController.helper = new DBHelper(CommunicatorController.mainActivityContext);
                    OptionController c = new OptionController((OptionModel) v.getTag(), CommunicatorController.mainActivityContext);
                    if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.set_image))) {
                        c.selectImage(CommunicatorController.mainActivityContext);
                    } else if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.set_text)))
                        c.askForOptionName();
                    else if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.set_sound)))
                        c.selectVoice(CommunicatorController.mainActivityContext);
                }
            });
            builder.show();
        }
        return true;
    }
}
