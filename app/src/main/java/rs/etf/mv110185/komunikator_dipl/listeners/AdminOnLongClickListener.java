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
                    CommunicatorController.mainActivityContext.getString(R.string.set_text), CommunicatorController.mainActivityContext.getString(R.string.set_sound), "izbrisi opciju"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (CommunicatorController.helper == null)
                        CommunicatorController.helper = new DBHelper(CommunicatorController.mainActivityContext);
                    final OptionController c = new OptionController((OptionModel) v.getTag(), CommunicatorController.mainActivityContext);
                    if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.set_image))) {
                        c.selectImage(CommunicatorController.mainActivityContext);
                    } else if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.set_text)))
                        c.askForOptionName();
                    else if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.set_sound)))
                        c.selectVoice(CommunicatorController.mainActivityContext);
                    else if (items[which].equals(CommunicatorController.mainActivityContext.getString(R.string.delete_option))) {
                        //ask if sure?
                        AlertDialog.Builder builder = new AlertDialog.Builder(CommunicatorController.mainActivityContext);

                        builder.setTitle(CommunicatorController.mainActivityContext.getString(R.string.option_delete));
                        builder.setMessage(CommunicatorController.mainActivityContext.getString(R.string.are_you_sure_to_del_opt));

                        builder.setPositiveButton(CommunicatorController.mainActivityContext.getString(R.string.yes), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing but close the dialog
                                OptionModel mod = c.getModel();
                                CommunicatorController.helper.deleteOption(mod);
                                CommunicatorController.changeOptions();
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton(CommunicatorController.mainActivityContext.getString(R.string.no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                }
            });
            builder.show();
        }
        return true;
    }
}
