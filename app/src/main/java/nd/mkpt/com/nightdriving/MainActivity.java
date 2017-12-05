package nd.mkpt.com.nightdriving;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView mTextMessage;
    public MainActivity mainActivity;

    private TextToSpeech tts;
    private TextView textView;
    private Button btnStart, btnStop;
    private ProgressBar progressBar;
    private boolean isAnswered = false;
    Handler handler = null;
    Runnable callBack = null;
    boolean isNeedToListen = false;

    int volume_level;

    private int qID = 0;
    private static final int REQUEST_CODE = 1234;
    private HashMap<String, String> params = new HashMap<String, String>();

    private String TAG = "Night Driving : ";
    private boolean isRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Started");
        QuestioinManager.loadQues();

        mainActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        this.qID = getIntent().getIntExtra("qID",0);

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");

        tts = new TextToSpeech(this, this);
        tts.setSpeechRate(0.65F);
        tts.setPitch(-5);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                isRun = true;
                askQues();
                progressBar.setIndeterminate(true);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                isRun = false;
                progressBar.setIndeterminate(false);
            }
        });

//        Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            btnStart.setEnabled(false);
        }


    }

    private void askQues(){
        isNeedToListen = true;
        speak(QuestioinManager.getQuestion());
    }


    public void startVoiceRecognitionActivity()
    {
//        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Give your answer ?");
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 15000);
//        startActivityForResult(intent, REQUEST_CODE);
//        Log.i(TAG, "Listner opened");

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        volume_level= am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0,0);


        Intent intent = new Intent(this, RecorderActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivityForResult(intent,REQUEST_CODE);
        Log.i(TAG, "Listner opened");
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
//        Log.i(TAG, "AAAAAAAA---------------------------- ");

        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level, 0);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {

            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            Log.i(TAG, "Got a answer : " +matches);
            if(matches.size()>0) {
                textView.setText(matches.get(0));
                isAnswered = true;
                isNeedToListen = false;
                speak(QuestioinManager.getAnswer());
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                          Log.i(TAG, "------- NEW QUESTION --------");
                          isNeedToListen = true;
                          speak(QuestioinManager.getQuestion());
                     }
                }, 5000);
            }else{
                textView.setText("ERRPR CODE");
                Toast.makeText(getApplicationContext(), "Timeout Please wakeup!",Toast.LENGTH_SHORT).show();
            }





//            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                    matches));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void speak(String str){

        if (handler != null && callBack != null) {
            handler.removeCallbacks(callBack);
        }

        if(isRun) {
            Log.i(TAG, "speak: " + str);

            isAnswered = false;
            tts.speak(str, TextToSpeech.QUEUE_FLUSH, params);

        }

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.e("TTS", "This Language is not supported");

                }

                @Override
                public void onDone(String utteranceId) {
                    if(isNeedToListen && isRun) {
                        mainActivity.startVoiceRecognitionActivity();
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    Log.e("TTS", "This Language is not supported");
                }
            });

            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnStart.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}


