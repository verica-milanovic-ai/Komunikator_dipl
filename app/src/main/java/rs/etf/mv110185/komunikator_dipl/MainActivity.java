package rs.etf.mv110185.komunikator_dipl;
// ANDROID's package

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    // Options To Display
    private CommunicatorController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: restore activity if it's needed
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new CommunicatorController(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (CommunicatorController.IS_ADMIN == 1) {
            switch (item.getItemId()) {
                case R.id.action_stats:
                    // TODO: New Activity => statistics !!!
                    controller.showStats();
                    return true;
                case R.id.action_change_pass:
                    // TODO: Dialog for changing pass!!!
                    controller.changePass(getFragmentManager());
                    return true;
                case R.id.action_save_changes:
                    // TODO: Save changes
                    controller.saveChanges();
                    return true;
                case R.id.action_exit_admin:
                    // TODO: Exit admin mode!!!
                    controller.exitAdminMode();
                    return true;
                default:
                    //somebody else must process this action
                    return super.onContextItemSelected(item);
            }
        } else {
            //TODO : Ask for password dialog!!!
            controller.askForPass();
            //I've finished with processing this click action
            return true;
        }
    }


}
