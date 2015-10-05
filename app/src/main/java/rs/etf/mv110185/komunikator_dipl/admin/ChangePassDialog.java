package rs.etf.mv110185.komunikator_dipl.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.R;


public class ChangePassDialog extends DialogFragment {

    // USE THIS METHOD TO CALL DTHIS DIALOG!!!
    public static ChangePassDialog newInstance(String pass) {
        ChangePassDialog f = new ChangePassDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("pass", pass);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog_layout = inflater.inflate(R.layout.change_pass_dialog, null);

        final String pass = getArguments().getString(context.getString(R.string.pass));
        final EditText old = (EditText) dialog_layout.findViewById(R.id.old_pass);
        final EditText pnew = (EditText) dialog_layout.findViewById(R.id.new_pass);
        final EditText pconf = (EditText) dialog_layout.findViewById(R.id.pass_confirm);
        final Button ok = (Button) dialog_layout.findViewById(R.id.ok_btn);
        final Button cancel = (Button) dialog_layout.findViewById(R.id.cancel_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_pass = old.getText().toString();
                if (old_pass.equals(pass) || pass == null) {
                    String newP = pnew.getText().toString();
                    String confP = pconf.getText().toString();
                    if (newP.equals(confP)) {
                        // OK! CHANGE PASS WRITE IT INTO DATABASE IN ASYNC TASK!
                        String newpass = pnew.getText().toString();
                        CommunicatorController.saveNewPass(newpass);
                        ChangePassDialog.this.getDialog().dismiss();
                    } else {
                        // NOK! CONF AND NEW MUST BE THE SAME!
                        pconf.requestFocus();
                        pconf.selectAll();
                        CharSequence text = getText(R.string.doesnt_match_conf_new);
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }
                } else {
                    // NOK! WRONG PASS!
                    old.requestFocus();
                    old.selectAll();
                    CharSequence text = getText(R.string.wrong_pass);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassDialog.this.getDialog().cancel();
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialog_layout);
        return builder.create();
    }

}
