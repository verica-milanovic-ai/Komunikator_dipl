package rs.etf.mv110185.komunikator_dipl.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.R;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;

public class NewOption extends AppCompatActivity {

    static OptionController controller;
    static NewOption instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_option);
        setListeners();
        controller = new OptionController(new OptionModel(), this);
        instance = this;
    }

    private void setListeners() {
        final EditText name = (EditText) findViewById(R.id.new_option_name);
        final CheckBox isFinal = (CheckBox) findViewById(R.id.new_option_is_final);
        final EditText finalText = (EditText) findViewById(R.id.new_option_final_text);
        Button selImage = (Button) findViewById(R.id.new_option_ch_image);
        //Button selVoice = (Button) findViewById(R.id.new_option_ch_voice);
        Button ok = (Button) findViewById(R.id.new_option_save);
        Button cancel = (Button) findViewById(R.id.new_option_cancel);

        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    name.clearFocus();
                }
                return false;
            }
        });


        selImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.selectImage(instance);
            }
        });
       /* selVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.selectVoice(instance);
            }
        });
        */
        isFinal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    finalText.setEnabled(true);
                    finalText.requestFocus();
                } else
                    finalText.setEnabled(false);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      String name_str = name.getText().toString();
                                      String finalText_str = null;
                                      if (finalText.getText() != null)
                                          finalText_str = finalText.getText().toString();
                                      boolean isFinal_bl = isFinal.isChecked();
                                      if (name_str == null || name_str.isEmpty()) {
                                          name.requestFocus();
                                          Toast.makeText(instance, "Ime opcije je obavezno!", Toast.LENGTH_SHORT).show();
                                          return;
                                      } else {
                                          controller.model.setText(name_str);
                                      }
                                      if (isFinal_bl) {
                                          if (finalText == null || finalText_str.isEmpty()) {
                                              finalText.requestFocus();
                                              Toast.makeText(instance, "Unesite krajnji tekst!", Toast.LENGTH_SHORT).show();
                                              return;
                                          } else {
                                              controller.model.setFinal_text(finalText_str);
                                          }
                                      }
                                      controller.model.setIs_final(isFinal_bl ? 1 : 0);
                                      Intent intent = getIntent();
                                      intent.putExtra("model", controller.model);
                                      setResult(RESULT_OK, intent);
                                      finish();
                                  }
                              }
        );

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_new_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CommunicatorController.REQUEST_CAMERA:
                    controller.handleImageFromCamera(data);
                    break;
             /*   case CommunicatorController.REQUEST_AUDIO_RECORDER:
                    controller.handleVoiceFromRecorder(data);
                    break;
                case CommunicatorController.SELECT_VOICE_FILE:
                    controller.handleSelectedVoice(data);
                    break;
                    */
                case CommunicatorController.SELECT_FILE:
                    controller.handleImageFromGallery(data);
                    break;

            }
        }
    }
}
