package rs.etf.mv110185.komunikator_dipl.admin;

import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.R;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 15.09.2015..
 */
public class OptionController {

    OptionModel model;
    AppCompatActivity context;
    boolean canBeSaved = false;

    public OptionController(OptionModel model, AppCompatActivity context) {
        this.model = model;
        this.context = context;
        canBeSaved = false;
    }

    // methods for changing model


    public OptionModel getModel() {
        return model;
    }

    public void setModel(OptionModel model) {
        this.model = model;
    }

    public boolean isCanBeSaved() {
        canBeSaved = model.getImage_src() != null &&
                ((model.getIs_final() == 1 && model.getFinal_text() != null)
                        || (model.getIs_final() == 0 && model.getText() != null));
        return canBeSaved;
    }

    public void setCanBeSaved(boolean canBeSaved) {
        this.canBeSaved = canBeSaved;
    }

    public void changeText(String newText) {
        model.setText(newText);
    }

    public void askForOptionName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        // Get the layout inflater
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.option_name_dialog, null);
        final EditText et = (EditText) v.findViewById(R.id.option_name);
        CheckBox cb = (CheckBox) v.findViewById(R.id.is_final_option);
        final EditText final_text = (EditText) v.findViewById(R.id.option_final_text);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final_text.setVisibility(View.VISIBLE);
                    model.setIs_final(1);
                } else {
                    final_text.setVisibility(View.GONE);
                    model.setIs_final(0);
                }
            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String txt = et.getText().toString();
                        model.setText(txt);
                        if (model.getIs_final() == 1) {
                            String fnl = final_text.getText().toString();
                            if (!fnl.isEmpty())
                                model.setFinal_text(fnl);
                            else
                                model.setFinal_text(txt);
                        }
                        CommunicatorController.helper.updateOption(model);
                        CommunicatorController.changeOptions();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();

    }

    // called in MainActivity to open Image Resource Select Dialog

    public void selectImage(final AppCompatActivity mainActivity) {
        final CharSequence[] items = {context.getString(R.string.take_photo), context.getString(R.string.choose_from_gallery), context.getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(context.getString(R.string.take_photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    /*Bundle bundle = new Bundle();
                    bundle.putSerializable("model", model);
                    intent.putExtra("modelBundle", bundle);
                    */
                    CommunicatorController.newOption = model;
                    mainActivity.startActivityForResult(intent, CommunicatorController.REQUEST_CAMERA);
                } else if (items[item].equals(context.getString(R.string.choose_from_gallery))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    CommunicatorController.newOption = model;
                    /*Bundle bundle = new Bundle();
                    bundle.putSerializable("model", model);
                    intent.putExtra("modelBundle", bundle);
                    */
                    intent.setType("image/*");
                    mainActivity.startActivityForResult(
                            Intent.createChooser(intent, context.getString(R.string.choose_image)),
                            CommunicatorController.SELECT_FILE);
                } else if (items[item].equals(context.getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // called in onActivityResult -> if (resultCode == RESULT_OK) if (requestCode == REQUEST_CAMERA)
    public void handleImageFromCamera(Intent data) {
        Bitmap pict = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        pict.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            Log.e("OptionController_admin", "createNewFile() failed!");
        } catch (IOException e) {
            Log.e("OptionController_admin", "output stream not ok!");
        }
        model.setImage_src(destination.getAbsolutePath());
        //view.setImage(pict);
    }

    // called in onActivityResult -> if (resultCode == RESULT_OK) if (requestCode == SELECT_FILE)
    public void handleImageFromGallery(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};

        CursorLoader cursorLoader = new CursorLoader(
                context,
                selectedImageUri, projection, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(column_index);
        Bitmap pict;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 100;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        pict = BitmapFactory.decodeFile(selectedImagePath, options);
        // Now Create File
        File outputFile = new File(CommunicatorController.path_gallery, System.currentTimeMillis() + ".jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            pict.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        model.setImage_src(outputFile.getAbsolutePath());
        //view.setImage(pict);

    }

    public void selectVoice(final AppCompatActivity mainActivity) {
        final CharSequence[] items = {context.getString(R.string.record_audio), context.getString(R.string.cancel)
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.add_sound));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(context.getString(R.string.record_audio))) {
                    Intent intent = new Intent(mainActivity, AudioRecorder.class);
                    intent.putExtra("option_name", "option_" + model.getId());
                   /* Bundle bundle = new Bundle();
                    bundle.putSerializable("model", model);
                    intent.putExtra("modelBundle", bundle);
                    */
                    CommunicatorController.newOption = model;
                    mainActivity.startActivityForResult(intent, CommunicatorController.REQUEST_AUDIO_RECORDER);
                } else if (items[item].equals(context.getString(R.string.choose_existing))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("audio/*");
                    /*Bundle bundle = new Bundle();
                    bundle.putSerializable("model", model);
                    intent.putExtra("modelBundle", bundle);
                    */
                    CommunicatorController.newOption = model;
                    mainActivity.startActivityForResult(
                            Intent.createChooser(intent, context.getString(R.string.choose_existing)),
                            CommunicatorController.SELECT_VOICE_FILE);
                } else if (items[item].equals(context.getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // AudioRecorder did all job :D
    public void handleVoiceFromRecorder(Intent data) {
        model.setVoice_src(data.getStringExtra("voice_src"));
    }

    public void handleSelectedVoice(Intent data) {
        Uri selectedVoiceUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};

        CursorLoader cursorLoader = new CursorLoader(
                context,
                selectedVoiceUri, projection, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedVoicePath = cursor.getString(column_index);
        model.setVoice_src(selectedVoicePath);
    }
}
