package rs.etf.mv110185.komunikator_dipl;
// ANDROID's package

import android.content.Intent;
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
        if (savedInstanceState == null) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            controller = CommunicatorController.getCommunicatorController(this);
            // CommunicatorController.fetchPasswordAndPPicture();
        } else {
            controller = (CommunicatorController) savedInstanceState.getSerializable("controller");
            controller.resume();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("controller", controller);
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        CommunicatorController.setMenu(menu);
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
                    CommunicatorController.changePass(getFragmentManager());
                    return true;
                case R.id.action_exit_admin:
                    CommunicatorController.exitAdminMode();
                    return true;
                default:
                    //somebody else must process this action
                    return super.onContextItemSelected(item);
            }
        } else {
            CommunicatorController.askForPass();
            //I've finished with processing this click action
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            CommunicatorController.onActivityOKResult(requestCode, data);
        }

    }
}
