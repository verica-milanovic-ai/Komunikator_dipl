package rs.etf.mv110185.komunikator_dipl.admin;

/**
 * Created by Verica Milanovic on 17.09.2015..
 */
/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;

import rs.etf.mv110185.komunikator_dipl.CommunicatorController;
import rs.etf.mv110185.komunikator_dipl.R;
import rs.etf.mv110185.komunikator_dipl.db.OptionModel;


public class AudioRecorder extends AppCompatActivity {
    private static final String LOG_TAG = "AudioRecorder";
    private String mFileName = null;
    private int id;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;

    private String optionName = "";
    private OptionModel model;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        // returns sound_src path to the MainActivity
        getIntent().putExtra("voice_src", mFileName);
        mRecorder = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_recorder);

        optionName = getIntent().getStringExtra("option_name");
        String id_str = optionName.substring(optionName.indexOf('_') + 1);
        id = Integer.parseInt(id_str);
        model = CommunicatorController.helper.getOption(id);
        mFileName = getFilesDir().getAbsolutePath();
        mFileName += "/" + optionName + ".3gp";

        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(((Button) findViewById(R.id.record_btn)));
        mPlayButton = new PlayButton((Button) findViewById(R.id.play_btn));
        Button ok = (Button) findViewById(R.id.sound_save);
        Button cancel = (Button) findViewById(R.id.sound_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent().putExtra("voice_src", mFileName);
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", model);
                intent.putExtra("modelBundle", bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    class RecordButton {
        boolean mStartRecording = true;
        Button btn;

        View.OnClickListener clicker = new View.OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    btn.setText(getString(R.string.stop_recording));
                } else {
                    btn.setText(getString(R.string.record));
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Button bbtn) {
            btn = bbtn;
            btn.setText(getString(R.string.record));
            btn.setOnClickListener(clicker);
        }
    }

    class PlayButton {
        boolean mStartPlaying = true;
        Button btn;

        View.OnClickListener clicker = new View.OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    btn.setText(getString(R.string.stop_music));
                } else {
                    btn.setText(getString(R.string.play_sound));
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Button bbtn) {
            btn = bbtn;
            btn.setText(getString(R.string.play_sound));
            btn.setOnClickListener(clicker);
        }
    }
}