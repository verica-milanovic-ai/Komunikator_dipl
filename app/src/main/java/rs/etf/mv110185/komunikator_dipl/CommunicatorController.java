package rs.etf.mv110185.komunikator_dipl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import java.util.concurrent.ExecutionException;

import rs.etf.mv110185.komunikator_dipl.admin.OptionController;
import rs.etf.mv110185.komunikator_dipl.admin.dialog.ChangePassDialog;
import rs.etf.mv110185.komunikator_dipl.db.DBContract;
import rs.etf.mv110185.komunikator_dipl.db.DBHelper;
import rs.etf.mv110185.komunikator_dipl.db.FlagModel;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 17.09.2015..
 */
public class CommunicatorController implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final int REQUEST_CAMERA = 1;
    public static final int SELECT_FILE = 2;
    public static final int SELECT_VOICE_FILE = 5;
    public static final int SELECT_PROFILE_IMAGE = 4;
    public static final int REQUEST_AUDIO_RECORDER = 3;

    public static int IS_ADMIN = 0;
    private String password;

    private List<OptionModel> user_options;
    private List<OptionModel> userPathList;
    private OptionModel currentOption = null;
    private List<OptionController> newOption = null;
    private AppCompatActivity mainActivityContext;
    private MediaPlayer mPlayer;

    public CommunicatorController(AppCompatActivity context) {
        mainActivityContext = context;
        newOption = new ArrayList<>();
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
        setListeners();
    }

    private void setListeners() {
        GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);
        option_gv.setOnItemClickListener(this);

        ImageView profPict = (ImageView) mainActivityContext.findViewById(R.id.profilePicture);
        profPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_ADMIN == 0) {
                    addFirstOptions();
                } else {
                    changeProfilePicture();
                }
            }
        });

        ImageButton adminButton = (ImageButton) mainActivityContext.findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_ADMIN == 0) {
                    askForPass();
                } else {

                }
            }
        });
    }

    private void changeProfilePicture() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mainActivityContext.startActivityForResult(
                Intent.createChooser(intent, mainActivityContext.getString(R.string.choose_image)),
                CommunicatorController.SELECT_PROFILE_IMAGE);
    }

    public void handleProfileImage(final Intent data) {
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

                DBHelper helper = new DBHelper(mainActivityContext);
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

    private void addFirstOptions() {
        // empty at the beginning
        userPathList = new ArrayList<>();
        currentOption = null;

        GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);
        // TODO: DON'T KNOW IF THIS IS POSSIBLE!
        if (option_gv.getAdapter() == null) {
            DBHelper helper = new DBHelper(mainActivityContext);
            Cursor cursor = helper.getAllOptions_cursor(null);
            String[] fieldList = DBContract.CommunicatorOption.COLUMNS;
            int[] viewIdList = {R.id.imageButton, R.id.textView};
            SimpleCursorAdapter la = new SimpleCursorAdapter(mainActivityContext,
                    R.layout.admin_option_item, cursor, fieldList, viewIdList,
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
        }

    }

    private void setPasswordForCommunicator() {
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

    private void saveNewPass(final String newPass) {
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

    public void exitAdminMode() {
        changeToUserView();
    }


    public void askForPass() {
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
                if (password.equals(pass.getText())) {
                    Toast.makeText(mainActivityContext,
                            mainActivityContext.getString(R.string.welcome_to_admin), Toast.LENGTH_LONG).show();
                    changeToAdminView();
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


    private void changeToUserView() {
        if (IS_ADMIN == 1) {
            IS_ADMIN = 0;
            currentOption = null;
            userPathList = new ArrayList<>();
            changeOptions();
        }
    }

    private void changeToAdminView() {
        if (IS_ADMIN == 0) {
            IS_ADMIN = 1;
            currentOption = null;
            userPathList = new ArrayList<>();
            changeOptions();
        }
    }


    //////////////////////////////////// PLAYER!!!  ////////////////////////////////////

    private void startPlaying(String mFileName, final int position) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                changeOptions(position);
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
        if (currentOption.getVoice_src() != null) {
            startPlaying(currentOption.getVoice_src(), position);
        } else {
            changeOptions(position);
        }
    }

    private void changeOptions() {
        userPathList.add(currentOption);

        final GridView option_gv = (GridView) mainActivityContext.findViewById(R.id.main_gridView);
        final LinearLayout ll = (LinearLayout) mainActivityContext.findViewById(R.id.userPathList);

        new AsyncTask<View.OnClickListener, Void, Void>() {
            @Override
            protected Void doInBackground(final View.OnClickListener... arg0) {
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
                            //TO RECOGNIZE ImageView IN LISTENER :D
                            iv.setTag(opt);
                            ll.addView(iv);
                        }

                        DBHelper helper = new DBHelper(mainActivityContext);
                        fillOptions(helper.getAllOptions(currentOption));
                        Cursor cursor = helper.getAllOptions_cursor(currentOption);
                        String[] fieldList = DBContract.CommunicatorOption.COLUMNS;
                        int[] viewIdList = {R.id.imageButton, R.id.textView, R.id.del_option};


                        if (currentOption.getIs_final() == 0) {
                            SimpleCursorAdapter la = new SimpleCursorAdapter(mainActivityContext,
                                    R.layout.admin_option_item, cursor, fieldList, viewIdList,
                                    0);

                            option_gv.setVisibility(View.VISIBLE);
                            TextView tv_final = (TextView) mainActivityContext.findViewById(R.id.finalText);
                            tv_final.setVisibility(View.GONE);

                            la.setViewBinder(new ViewBinder() {
                                @Override
                                public boolean setViewValue(final View view, Cursor cursor,
                                                            int columnIndex) {
                                    if (columnIndex == cursor
                                            .getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_IMAGE_SRC)) {
                                        File image = new File(mainActivityContext.getFilesDir(), cursor.getString(columnIndex));
                                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 70, 60, true);
                                        final ImageButton ib = (ImageButton) view;
                                        ib.setImageBitmap(bitmap);
                                        if (IS_ADMIN == 1) {
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 60);
                                            params.setMargins(0, 0, 0, 10);
                                            ib.setLayoutParams(params);
                                            ib.setTag(cursor.getInt(cursor.getColumnIndex("id")));
                                            ib.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    if (IS_ADMIN == 1) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityContext);
                                                        final CharSequence[] items = {mainActivityContext.getString(R.string.set_image),
                                                                mainActivityContext.getString(R.string.set_text), mainActivityContext.getString(R.string.set_sound)};
                                                        builder.setItems(items, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                DBHelper helper = new DBHelper(mainActivityContext);
                                                                OptionController c = new OptionController(helper.getOption((int) ib.getTag()), mainActivityContext);
                                                                if (items[which].equals(mainActivityContext.getString(R.string.set_image))) {
                                                                    c.selectImage(mainActivityContext);
                                                                } else if (items[which].equals(mainActivityContext.getString(R.string.set_text)))
                                                                    c.askForOptionName();
                                                                else if (items[which].equals(mainActivityContext.getString(R.string.set_sound)))
                                                                    c.selectVoice(mainActivityContext);
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                    return true;
                                                }
                                            });
                                        } else {
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, 60);
                                            params.setMargins(0, 10, 0, 10);
                                            ib.setLayoutParams(params);
                                        }
                                        return true;
                                    } else if (columnIndex == cursor.getColumnIndex(DBContract.CommunicatorOption.COLUMN_NAME_TEXT)) {
                                        TextView tv = (TextView) view;
                                        tv.setText(cursor.getString(columnIndex));
                                        return true;
                                    }
                                    if (view.getId() == R.id.del_option && IS_ADMIN == 1) {
                                        view.setVisibility(View.VISIBLE);
                                        view.setTag(cursor.getInt(cursor.getColumnIndex("id")));
                                        view.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DBHelper helper = new DBHelper(mainActivityContext);
                                                OptionModel mod = new OptionModel();
                                                mod.setId((int) view.getTag());
                                                helper.deleteOption(mod);
                                                changeOptions();
                                            }
                                        });
                                    } else if (view.getId() == R.id.del_option)
                                        view.setVisibility(View.GONE);
                                    return false;
                                }
                            });
                            option_gv.setAdapter(la);
                            if (IS_ADMIN == 1) {
                                LinearLayout ll = (LinearLayout) mainActivityContext.getLayoutInflater().inflate(R.layout.admin_new_option_item, null);
                                option_gv.addView(ll, option_gv.getChildCount());
                                ll.findViewById(R.id.add_new_opt_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        newOption.add(new OptionController(new OptionModel(), mainActivityContext));
                                        int index = newOption.size() - 1;
                                        newOption.get(index).askForOptionName();
                                        newOption.get(index).getModel().setParent(currentOption.getId());
                                    }
                                });
                            }
                        } else {
                            option_gv.setVisibility(View.GONE);
                            TextView tv_final = (TextView) mainActivityContext.findViewById(R.id.finalText);
                            tv_final.setText(currentOption.getFinal_text());
                            tv_final.setVisibility(View.VISIBLE);
                        }
                    }
                });
                return null;
            }
        }.execute(this);
    }


    private void changeOptions(int position) {
        currentOption = user_options.get(position);
        changeOptions();
    }

    private void fillOptions(List<OptionModel> allOptions) {
        user_options = new ArrayList<>();
        for (int i = 0; i < allOptions.size(); i++) {
            user_options.add(allOptions.get(i));
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
        changeOptions();
    }

    public void saveChanges() {
        boolean may_save = true;
        for (OptionController cont : newOption)
            if (!cont.isCanBeSaved()) {
                may_save = false;
                Toast.makeText(mainActivityContext, mainActivityContext.getString(R.string.cant_be_saved), Toast.LENGTH_LONG);
                return;
            }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DBHelper helper = new DBHelper(mainActivityContext);
                for (OptionController opt : newOption) {
                    helper.addOption(opt.getModel());
                }
                newOption = new ArrayList<OptionController>();
                return null;
            }
        }.execute();
        changeOptions();
    }


    public void onActivityOKResult(int requestCode, Intent data) {
        Bundle bundle;
        OptionModel mod;
        if (requestCode != SELECT_PROFILE_IMAGE) {
            bundle = data.getBundleExtra("modelBundle");
            mod = (OptionModel) bundle.getSerializable("model");
            OptionController tmp = new OptionController(mod, mainActivityContext);
            switch (requestCode) {
                case REQUEST_CAMERA:
                    tmp.handleImageFromCamera(data);
                    break;
                case REQUEST_AUDIO_RECORDER:
                    tmp.handleVoiceFromRecorder(data);
                    break;
                case SELECT_VOICE_FILE:
                    tmp.handleSelectedVoice(data);
                    break;
                case SELECT_FILE:
                    tmp.handleImageFromGallery(data);
                    break;
                default:
                    break;
            }
        } else {
            handleProfileImage(data);
        }
    }
}
