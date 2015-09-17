package rs.etf.mv110185.komunikator_dipl.admin;

import android.app.AlertDialog;
import android.content.Context;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rs.etf.mv110185.komunikator_dipl.ComunicatorController;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

/**
 * Created by Verica Milanovic on 15.09.2015..
 */
public class OptionController extends rs.etf.mv110185.komunikator_dipl.user.OptionController {

    public OptionController(OptionModel model, rs.etf.mv110185.komunikator_dipl.user.OptionView view, Context context) {
        super(model, view, context);
    }

    public OptionController(OptionModel model, Context context) {
        super(model, context);
    }

    // methods for changing model
    public void changeText(String newText) {
        model.setText(newText);
    }

    // called in MainActivity to open Image Resource Select Dialog
    public void selectImage(final AppCompatActivity mainActivity) {
        final CharSequence[] items = {"Фотографиши", "Изабери из галерије", "Поништи"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Додај фотографију");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Фотографиши")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mainActivity.startActivityForResult(intent, ComunicatorController.REQUEST_CAMERA);
                } else if (items[item].equals("Изабери из галерије")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    mainActivity.startActivityForResult(
                            Intent.createChooser(intent, "Изабери фотографију"),
                            ComunicatorController.SELECT_FILE);
                } else if (items[item].equals("Поништи")) {
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
        view.setImage(pict);
    }

    // called in onActivityResult -> if (resultCode == RESULT_OK) if (requestCode == SELECT_FILE)
    public void handleImageFromGallery(Intent data, final AppCompatActivity mainActivity) {
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
        // TODO : steluj da bude lepo prikazano :D !!!
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        pict = BitmapFactory.decodeFile(selectedImagePath, options);
        model.setImage_src(selectedImagePath);
        view.setImage(pict);

    }


    public void selectVoice(final AppCompatActivity mainActivity) {
        final CharSequence[] items = {"Сними звук", "Изабери постојећи", "Поништи"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Додај звук");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Сними звук")) {
                    Intent intent = new Intent(mainActivity, AudioRecorder.class);
                    intent.putExtra("option_name", "option_" + model.getId());
                    mainActivity.startActivityForResult(intent, ComunicatorController.REQUEST_AUDIO_RECORDER);
                } else if (items[item].equals("Изабери постојећи")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("audio/*");
                    mainActivity.startActivityForResult(
                            Intent.createChooser(intent, "Изабери постојећи"),
                            ComunicatorController.SELECT_FILE);
                } else if (items[item].equals("Поништи")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // TODO: DO THIS METHODS!!!
    public void handleVoiceFromRecorder() {
    }

    public void handleSelectedVoice() {
    }
}
