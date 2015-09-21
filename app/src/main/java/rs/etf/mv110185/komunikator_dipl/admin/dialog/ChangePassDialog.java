package rs.etf.mv110185.komunikator_dipl.admin.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import rs.etf.mv110185.komunikator_dipl.R;


public class ChangePassDialog extends DialogFragment {

    private int OK = 0;

    // USE THIS METHOD TO CALL DTHIS DIALOG!!!
    static ChangePassDialog newInstance(String pass) {
        ChangePassDialog f = new ChangePassDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("pass", pass);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.change_pass_dialog, null);

        String pass = getArguments().getString("pass");
        EditText old = (EditText) dialog_layout.findViewById(R.id.old_pass);
        EditText pnew = (EditText) dialog_layout.findViewById(R.id.new_pass);
        EditText pconf = (EditText) dialog_layout.findViewById(R.id.pass_confirm);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.change_pass_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.ok_dialog_ch_pass, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (old.getText().equals(pass)) {
                            if (pnew.getText().equals(pconf.getText())) {
                                // OK! CHANGE PASS WRITE IT INTO DATABASE IN ASYNC TASK!
                                OK = 1;
                                getActivity().getIntent().putExtra("retVal", OK);
                                getActivity().getIntent().putExtra("newPass", pnew.getText());
                                ChangePassDialog.this.getDialog().dismiss();
                            } else {
                                // NOK! CONF AND NEW MUST BE THE SAME!
                                pconf.requestFocus();
                                pconf.selectAll();
                                CharSequence text = getText(R.string.doesnt_match_conf_new);
                                int duration = Toast.LENGTH_SHORT;
                                OK = 0;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        } else {
                            // NOK! WRONG PASS!
                            old.requestFocus();
                            old.selectAll();
                            CharSequence text = getText(R.string.wrong_pass);
                            int duration = Toast.LENGTH_SHORT;
                            OK = 0;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_dialog_ch_pass, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // this means that admin doesn't want to save new password/or to change it.
                        OK = 0;
                        getActivity().getIntent().putExtra("retVal", OK);
                        ChangePassDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
