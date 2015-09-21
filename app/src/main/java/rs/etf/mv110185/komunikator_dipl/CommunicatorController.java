package rs.etf.mv110185.komunikator_dipl;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rs.etf.mv110185.komunikator_dipl.admin.dialog.ChangePassDialog;
import rs.etf.mv110185.komunikator_dipl.db.DBContract;
import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.FlagModel;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;
import rs.etf.mv110185.komunikator_dipl.user.OptionController;

/**
 * Created by Verica Milanovic on 17.09.2015..
 */
public class CommunicatorController implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final int REQUEST_CAMERA = 1;
    public static final int SELECT_FILE = 2;
    public static final int REQUEST_AUDIO_RECORDER = 3;

    public static int IS_ADMIN = 0;
    private String password;

    private List<OptionController> options;
    private List<OptionModel> userPathList;
    private OptionController newOption, currentOption = null;
    private AppCompatActivity mainActivityContext;
    private MediaPlayer mPlayer;

    public CommunicatorController(AppCompatActivity context) {
        mainActivityContext = context;
        newOption = new OptionController(new OptionModel(), context);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DBHelper helper = new DBHelper(mainActivityContext);
                FlagModel flag = helper.getFlag("password");
                password = flag == null ? null : flag.getValue();
                if (password == null) {
                    setPasswordForCommunicator();
                }
                return null;
            }
        }.execute();

        addFirstOptions();
    }

    private void addFirstOptions() {
        // empty at the beginning
        userPathList = new ArrayList<>();

        GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);

        DBHelper helper = new DBHelper(mainActivityContext);
        Cursor cursor = helper.getAllOptions_cursor(null);
        String[] fieldList = DBContract.CommunicatorOption.COLUMNS;
        int[] viewIdList = {R.id.imageButton, R.id.textView};
        SimpleCursorAdapter la = new SimpleCursorAdapter(mainActivityContext,
                R.layout.user_option_item, cursor, fieldList, viewIdList,
                0);

        la.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                if (columnIndex == cursor
                        .getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC)) {
                    File image = new File(mainActivityContext.getFilesDir(), cursor.getString(columnIndex));
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
                    ImageButton ib = (ImageButton) view;
                    ib.setImageBitmap(bitmap);
                    return true;
                } else if (columnIndex == cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_TEXT)) {
                    TextView tv = (TextView) view;
                    tv.setText(cursor.getString(columnIndex));
                    return true;
                }
                return false;
            }
        });
        option_gv.setAdapter(la);
        option_gv.setOnItemClickListener(this);
    }

    private void setPasswordForCommunicator() {
        // create dialog! :D
        final Dialog newPass = new Dialog(mainActivityContext);
        // Set GUI of login screen
        newPass.setContentView(R.layout.create_pass_dialog);

        // Init button of login GUI
        EditText pass = (EditText) newPass.findViewById(R.id.etxtPassword);
        EditText conf_pass = (EditText) newPass.findViewById(R.id.etxtPassword_conf);
        Button btnLogin = (Button) newPass.findViewById(R.id.btnLogin);
        Button btnCancel = (Button) newPass.findViewById(R.id.btnCancel);

        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conf_pass.getText().equals(pass.getText())) {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.pass_saved), Toast.LENGTH_LONG).show();
                    password = pass.getText().toString();
                    saveNewPass(pass.getText().toString());
                    newPass.dismiss();
                } else {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.doesnt_match_conf_new), Toast.LENGTH_LONG).show();
                    conf_pass.requestFocus();
                    conf_pass.selectAll();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPass.cancel();
            }
        });
        // Make dialog box visible.
        newPass.show();
    }


    // TODO: IMPLEMENT!!!
    public void showStats() {
    }

    public void changePass(FragmentManager fragmentManager) {
        //build dialog  - ask for current password
        // ask for new password and for conformation
        // save password to database
        ChangePassDialog passDialog = new ChangePassDialog();
        passDialog.show(fragmentManager, "TAG");
        Intent intent = mainActivityContext.getIntent();
        int retVal = 0;
        retVal = intent.getIntExtra("retVal", retVal);
        String newPass = "";
        newPass = intent.getStringExtra("newPass");
        if (retVal == 1) {
            saveNewPass(newPass);
            password = newPass;
        }
    }

    private void saveNewPass(String newPass) {
        // SAVE PASS TO DATABASE
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DBHelper helper = new DBHelper(mainActivityContext);
                helper.addFlag(new FlagModel("password", newPass));
                password = newPass;
                return null;
            }
        }.execute();
    }

    // TODO: IMPLEMENT!!!
    public void saveChanges() {

    }

    public void exitAdminMode() {
        IS_ADMIN = 0;
    }

    public void askForPass() {
        // create dialog! :D
        final Dialog login = new Dialog(mainActivityContext);
        // Set GUI of login screen
        login.setContentView(R.layout.login_dialog);

        // Init button of login GUI
        EditText pass = (EditText) login.findViewById(R.id.etxtPassword);
        Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
        Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals(pass.getText())) {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.welcome_to_admin), Toast.LENGTH_LONG).show();
                    IS_ADMIN = 1;
                    login.dismiss();
                } else {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.wrong_pass_admin), Toast.LENGTH_LONG).show();
                    pass.requestFocus();
                    pass.selectAll();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IS_ADMIN = 0;
                login.cancel();
            }
        });

        // Make dialog box visible.
        login.show();
    }


    //////////////////////////////////// PLAYER!!!  ////////////////////////////////////

    private void startPlaying(String mFileName, int position) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                changeOptions(currentOption, position);
            }
        });
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("CommunicatorController", "prepare() failed - playing voice when pressed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentOption.getModel().getVoice_src() != null) {
            startPlaying(currentOption.getModel().getVoice_src(), position);
        } else {
            changeOptions(currentOption, position);
        }
    }

    private void changeOptions(OptionController option, int position) {
        currentOption = options.get(position);
        userPathList.add(currentOption.getModel());

        GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);
        LinearLayout ll = (LinearLayout) mainActivityContext.findViewById(R.id.userPathList);

        new AsyncTask<View.OnClickListener, Void, Void>() {
            @Override
            protected Void doInBackground(View.OnClickListener... arg0) {
                mainActivityContext.runOnUiThread(new Runnable() {
                    public void run() {
                        ll.removeAllViewsInLayout();
                        for (OptionModel opt : userPathList) {
                            File image = new File(mainActivityContext.getFilesDir(), opt.getImage_src());
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                            ImageView iv = new ImageView(mainActivityContext);
                            iv.setImageBitmap(bitmap);
                            iv.setOnClickListener(arg0[0]);
                            ll.addView(iv);
                        }

                        DBHelper helper = new DBHelper(mainActivityContext);
                        fillOptions(helper.getAllOptions(currentOption.getModel()));
                        Cursor cursor = helper.getAllOptions_cursor(currentOption.getModel());
                        String[] fieldList = DBContract.CommunicatorOption.COLUMNS;
                        int[] viewIdList = {R.id.imageButton, R.id.textView};


                        if (currentOption.getModel().getIs_final() == 0) {
                            SimpleCursorAdapter la = new SimpleCursorAdapter(mainActivityContext,
                                    R.layout.user_option_item, cursor, fieldList, viewIdList,
                                    0);

                            la.setViewBinder(new ViewBinder() {
                                @Override
                                public boolean setViewValue(View view, Cursor cursor,
                                                            int columnIndex) {
                                    if (columnIndex == cursor
                                            .getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC)) {
                                        File image = new File(mainActivityContext.getFilesDir(), cursor.getString(columnIndex));
                                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true);
                                        ImageButton ib = (ImageButton) view;
                                        ib.setImageBitmap(bitmap);
                                        return true;
                                    } else if (columnIndex == cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_TEXT)) {
                                        TextView tv = (TextView) view;
                                        tv.setText(cursor.getString(columnIndex));
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            option_gv.setAdapter(la);
                        } else {
                            option_gv.setVisibility(View.INVISIBLE);
                            TextView tv_final = (TextView) mainActivityContext.findViewById(R.id.finalText);
                            tv_final.setText(currentOption.getModel().getFinal_text());
                            tv_final.setVisibility(View.VISIBLE);
                        }
                    }
                });
                return null;
            }
        }.execute(this);
    }

    private void fillOptions(List<OptionModel> allOptions) {
        options = new ArrayList<>();
        for (int i = 0; i < allOptions.size(); i++) {
            options.add(new OptionController(allOptions.get(i), mainActivityContext));
        }
    }

    // MUST RETURN TO SELECTED LEVEL
    @Override
    public void onClick(View v) {
        // TODO: odsecem deo liste koji je posle currOption-a
        // TODO: ponovo popunim gridView
    }
}
