package rs.etf.mv110185.komunikator_dipl;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import rs.etf.mv110185.komunikator_dipl.admin.NewOption;
import rs.etf.mv110185.komunikator_dipl.admin.OptionController;
import rs.etf.mv110185.komunikator_dipl.admin.dialog.ChangePassDialog;
import rs.etf.mv110185.komunikator_dipl.db.DBContract;
import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.FlagModel;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 17.09.2015..
 */
public class CommunicatorController implements AdapterView.OnItemClickListener, View.OnClickListener, Serializable {

    public static final int REQUEST_CAMERA = 1;
    public static final int SELECT_FILE = 2;
    public static final int SELECT_VOICE_FILE = 5;
    public static final int SELECT_PROFILE_IMAGE = 4;
    public static final int REQUEST_AUDIO_RECORDER = 3;
    public static final int REQUEST_NEW_OPTION = 6;

    public static int IS_ADMIN = 0;
    public static AppCompatActivity mainActivityContext;
    public static DBHelper helper = null;
    public static String password;
    private static List<OptionModel> user_options;
    private static List<OptionModel> userPathList;
    private static OptionModel currentOption = null;
    private static OptionModel newOption = null;
    private static MediaPlayer mPlayer;
    private static CommunicatorController controller = null;
    private static Menu menu;

    private CommunicatorController(AppCompatActivity context) {
        mainActivityContext = context;
        //newOption = new ArrayList<>();
        if (helper == null)
            helper = new DBHelper(mainActivityContext);
        IS_ADMIN = 0;

    }

    public static OptionModel getCurrentOption() {
        return currentOption;
    }

    public static void setCurrentOption(OptionModel currentOption) {
        CommunicatorController.currentOption = currentOption;
    }

    public static CommunicatorController getCommunicatorController(AppCompatActivity context) {
        if (controller == null) {
            controller = new CommunicatorController(context);
            addFirstOptions();
            setListeners();
        }
        return controller;
    }

    public static DBHelper getHelper() {
        if (helper == null) helper = new DBHelper(mainActivityContext);
        return helper;
    }

    private static void setListeners() {
        ImageView profPict = (ImageView) mainActivityContext.findViewById(R.id.profilePicture);
        profPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicatorController.returnToBeginning();
            }
        });
        profPict.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (IS_ADMIN == 1)
                    CommunicatorController.changeProfilePicture();
                return true;
            }
        });
    }

    private static void returnToBeginning() {
        currentOption = null;
        userPathList = new ArrayList<>();
        changeOptions();
    }

    private static void changeProfilePicture() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mainActivityContext.startActivityForResult(
                Intent.createChooser(intent, mainActivityContext.getString(R.string.choose_image)),
                CommunicatorController.SELECT_PROFILE_IMAGE);
    }

    public static void handleProfileImage(final Intent data) {
        ImageView profP = (ImageView) mainActivityContext.findViewById(R.id.profilePicture);

        AsyncTask<Void, Void, Bitmap> a = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};

                CursorLoader cursorLoader = new CursorLoader(
                        mainActivityContext,
                        selectedImageUri, projection, null, null, null);

                Cursor cursor = cursorLoader.loadInBackground();

                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap pict;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                // TODO : steluj da bude lepo prikazano :D !!!
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                pict = BitmapFactory.decodeFile(selectedImagePath, options);
                if (helper == null)
                    helper = new DBHelper(mainActivityContext);
                helper.updateFlag(new FlagModel("profile_picture", selectedImagePath));
                return pict;
            }
        }.execute();

        Bitmap pict = null;
        try {
            pict = a.get();
            profP.setImageBitmap(pict);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private static void addFirstOptions() {
        // empty at the beginning
        userPathList = new ArrayList<>();
        currentOption = null;

        GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);
        // TODO: DON'T KNOW IF THIS IS POSSIBLE!
        if (option_gv.getAdapter() == null) {
            if (helper == null)
                helper = new DBHelper(mainActivityContext);
            Cursor cursor = helper.getAllOptions_cursor(null);
            if (!cursor.moveToFirst())
                return;
            String[] fieldList = DBContract.CommunicatorOption.COLUMNS;
            int[] viewIdList = {R.id.imageButton, R.id.textView, R.id.del_option};
            SimpleCursorAdapter la = new SimpleCursorAdapter(mainActivityContext,
                    R.layout.admin_option_item, cursor, fieldList, viewIdList,
                    0);
            la.setViewBinder(new MyOptionBinder());
            option_gv.setAdapter(la);
        } else
            changeOptions();
    }

    private static void setPasswordForCommunicator() {
        // create dialog! :D
        final Dialog newPass = new Dialog(mainActivityContext);
        // Set GUI of login screen
        newPass.setContentView(R.layout.create_pass_dialog);

        // Init button of login GUI
        final EditText pass = (EditText) newPass.findViewById(R.id.etxtPassword);
        final EditText conf_pass = (EditText) newPass.findViewById(R.id.etxtPassword_conf);
        Button btnLogin = (Button) newPass.findViewById(R.id.btnLogin);
        Button btnCancel = (Button) newPass.findViewById(R.id.btnCancel);

        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conf_pass.getText().equals(pass.getText().toString())) {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.pass_saved), Toast.LENGTH_LONG).show();
                    password = pass.getText().toString();
                    CommunicatorController.saveNewPass(pass.getText().toString());
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

    public static void changePass(FragmentManager fragmentManager) {
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

    private static void saveNewPass(final String newPass) {
        // SAVE PASS TO DATABASE
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (helper == null)
                    helper = new DBHelper(mainActivityContext);
                helper.addFlag(new FlagModel("password", newPass));
                password = newPass;
                return null;
            }
        }.execute();
    }

    public static void exitAdminMode() {
        CommunicatorController.changeToUserView();
    }

    public static void askForPass() {
        // create dialog! :D
        final Dialog login = new Dialog(mainActivityContext);
        // Set GUI of login screen
        login.setContentView(R.layout.login_dialog);

        // Init button of login GUI
        final EditText pass = (EditText) login.findViewById(R.id.etxtPassword);
        Button btnLogin = (Button) login.findViewById(R.id.btnLogin);
        Button btnCancel = (Button) login.findViewById(R.id.btnCancel);

        // Attached listener for login GUI button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = pass.getText().toString();
                if (password == null || password.equals(p)) {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.welcome_to_admin), Toast.LENGTH_LONG).show();
                    CommunicatorController.changeToAdminView();
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

    private static void changeToUserView() {
        if (IS_ADMIN == 1) {
            IS_ADMIN = 0;
            currentOption = null;
            userPathList = new ArrayList<>();
            menu.clear();
            mainActivityContext.getMenuInflater().inflate(R.menu.menu_main, menu);
            updateOptionsMenu();
            CommunicatorController.changeOptions();
        }
    }

    private static void changeToAdminView() {
        if (IS_ADMIN == 0) {
            IS_ADMIN = 1;
            currentOption = null;
            userPathList = new ArrayList<>();
            menu.clear();
            mainActivityContext.getMenuInflater().inflate(R.menu.menu_admin, menu);
            updateOptionsMenu();
            changeOptions();
        }
    }


    //////////////////////////////////// PLAYER!!!  ////////////////////////////////////

    public static void startPlaying(String mFileName, final int position) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                CommunicatorController.stopPlaying();
                CommunicatorController.changeOptions(position);
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


    public static void startPlaying(String mFileName, final OptionModel model) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                CommunicatorController.stopPlaying();
                currentOption = model;
                CommunicatorController.changeOptions();
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

    private static void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public static void changeOptions() {
        if (!userPathList.contains(currentOption))
            userPathList.add(currentOption);

        final GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);
        final LinearLayout ll = (LinearLayout) mainActivityContext.findViewById(R.id.userPathList);

        try {
            new AsyncTask<View.OnClickListener, Void, Void>() {
                @Override
                protected Void doInBackground(final View.OnClickListener... arg0) {
                    mainActivityContext.runOnUiThread(new Runnable() {
                        public void run() {
                            ll.removeAllViewsInLayout();
                            for (OptionModel opt : userPathList) {
                                if (opt != null) {
                                    File image = new File(opt.getImage_src());
                                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
                                    ImageView iv = new ImageView(mainActivityContext);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
                                    params.setMargins(0, 5, 5, 5);
                                    iv.setLayoutParams(params);
                                    iv.setImageBitmap(bitmap);
                                    iv.setOnClickListener(arg0[0]);
                                    //TO RECOGNIZE ImageView IN LISTENER :D
                                    iv.setTag(opt);
                                    ll.addView(iv);
                                }
                            }

                            if (helper == null) helper = new DBHelper(mainActivityContext);
                            CommunicatorController.fillOptions(helper.getAllOptions(currentOption));
                            Cursor cursor = helper.getAllOptions_cursor(currentOption);
                            String[] fieldList = DBContract.CommunicatorOption.COLUMNS;
                            int[] viewIdList = {R.id.imageButton, R.id.textView, R.id.del_option};

                            int isFinal_currOpt = currentOption == null ? 0 : currentOption.getIs_final();
                            if (isFinal_currOpt == 0) {
                                SimpleCursorAdapter la = new SimpleCursorAdapter(mainActivityContext,
                                        R.layout.admin_option_item, cursor, fieldList, viewIdList,
                                        0);
                                option_gv.setVisibility(View.VISIBLE);
                                TextView tv_final = (TextView) mainActivityContext.findViewById(R.id.finalText);
                                tv_final.setVisibility(View.GONE);

                                la.setViewBinder(new MyOptionBinder());
                                option_gv.setAdapter(la);
                                Button btn = (Button) mainActivityContext.findViewById(R.id.add_new_opt_btn);
                                if (IS_ADMIN == 1) {
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                           /* newOption.add(new OptionController(new OptionModel(), mainActivityContext));
                                            int index = newOption.size() - 1;
                                            newOption.get(index).askForOptionName();
                                            if (currentOption != null)
                                                newOption.get(index).getModel().setParent(currentOption.getId());
                                            else
                                                newOption.get(index).getModel().setParent(0);
                                           */
                                            //TODO: start activity for Result :D
                                            Intent newOption_intent = new Intent(mainActivityContext, NewOption.class);
                                            mainActivityContext.startActivityForResult(newOption_intent, REQUEST_NEW_OPTION);
                                        }
                                    });
                                    btn.setVisibility(View.VISIBLE);

                                } else {
                                    btn.setVisibility(View.GONE);
                                }
                            } else {
                                option_gv.setVisibility(View.GONE);
                                Button btn = (Button) mainActivityContext.findViewById(R.id.add_new_opt_btn);
                                btn.setVisibility(View.GONE);

                                TextView tv_final = (TextView) mainActivityContext.findViewById(R.id.finalText);
                                if (currentOption != null)
                                    tv_final.setText(currentOption.getFinal_text());
                                tv_final.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    return null;
                }
            }.execute(controller).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private static void changeOptions(int position) {
        currentOption = user_options.get(position);
        changeOptions();
    }

    private static void fillOptions(List<OptionModel> allOptions) {
        user_options = new ArrayList<>();
        for (int i = 0; i < allOptions.size(); i++) {
            user_options.add(allOptions.get(i));
        }
    }

    public static void onActivityOKResult(int requestCode, Intent data) {
        Bundle bundle;
        OptionModel mod;
        if (requestCode != SELECT_PROFILE_IMAGE && requestCode != REQUEST_NEW_OPTION) {
            bundle = data.getBundleExtra("modelBundle");
            mod = (OptionModel) bundle.getSerializable("model");
            OptionController tmp = new OptionController(mod, mainActivityContext);
            switch (requestCode) {
                case REQUEST_CAMERA:
                    tmp.handleImageFromCamera(data);
                    helper.updateOption(tmp.getModel());
                    break;
                case REQUEST_AUDIO_RECORDER:
                    tmp.handleVoiceFromRecorder(data);
                    helper.updateOption(tmp.getModel());
                    break;
                case SELECT_VOICE_FILE:
                    tmp.handleSelectedVoice(data);
                    helper.updateOption(tmp.getModel());
                    break;
                case SELECT_FILE:
                    tmp.handleImageFromGallery(data);
                    helper.updateOption(tmp.getModel());
                    break;
                case REQUEST_NEW_OPTION:
                    handleNewOption(data);
                    break;
                default:
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_NEW_OPTION:
                    handleNewOption(data);
                    break;
                case SELECT_PROFILE_IMAGE:
                    handleProfileImage(data);
                    break;
                default:
                    break;
            }
        }
    }

    private static void handleNewOption(Intent data) {
        newOption = (OptionModel) data.getSerializableExtra("model");
        newOption.setParent(currentOption == null ? 0 : currentOption.getId());
        helper.addOption(newOption);
        newOption = null;
    }

    public static void setMenu(Menu menu) {
        CommunicatorController.menu = menu;
        updateOptionsMenu();
    }

    private static void updateOptionsMenu() {
        if (menu != null) {
            mainActivityContext.onPrepareOptionsMenu(menu);
        }
    }

    public static void fetchPassword() {
        password = helper.getFlag("password").getValue();
    }

    // TODO: IMPLEMENT!!!
    public void showStats() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentOption.getVoice_src() != null) {
            startPlaying(currentOption.getVoice_src(), position);
        } else {
            changeOptions(position);
        }
    }

    // MUST RETURN TO SELECTED LEVEL
    @Override
    public void onClick(View v) {
        //  odsecem deo liste koji je posle currOption-a
        //  ponovo popunim gridView
        ImageView iv = (ImageView) v;
        OptionModel mod = (OptionModel) iv.getTag();
        int index = userPathList.indexOf(mod);
        userPathList = userPathList.subList(0, index + 1);
        currentOption = mod;
        if (mod != null && mod.getVoice_src() != null)
            startPlaying(mod.getVoice_src(), mod);
        changeOptions();
    }

    public void resume() {
        changeOptions();
    }
}
