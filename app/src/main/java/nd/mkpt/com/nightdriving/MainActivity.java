package nd.mkpt.com.nightdriving;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView mTextMessage;
    public MainActivity mainActivity;

    private TextToSpeech tts;
    private Button btnStart;

    private boolean isAnswered = false;
    Handler handler = null;
    Runnable callBack = null;
    boolean isNeedToListen = false;

    private int qID = 0;
    private static final int REQUEST_CODE = 1234;
    private HashMap<String, String> params = new HashMap<String, String>();

    private String TAG = "Night Driving : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Started");
        mainActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        this.qID = getIntent().getIntExtra("qID",0);

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");

        tts = new TextToSpeech(this, this);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                askQues();
            }

        });

//        // Disable button if no recognition service is present
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
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Give your answer ?");
        startActivityForResult(intent, REQUEST_CODE);
        Log.i(TAG, "Listner opened");
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {



            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            Log.i(TAG, "Got a answer : " +matches);

            isAnswered = true;
            isNeedToListen = false;
            speak("Ok...  good    ");


            final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "------- NEW QUESTION --------");
                            isNeedToListen = true;
                            speak(QuestioinManager.getQuestion());

                        }
                    }, 5000);

//            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                    matches));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void speak(String str){

        Log.i(TAG, "speak: "+str);

        isAnswered = false;
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, params);

        if(handler != null && callBack!= null){
            handler.removeCallbacks(callBack);
        }

        handler = new Handler();
        callBack = new Runnable() {
            @Override
            public void run() {
                if(!isAnswered) {
                    isNeedToListen = true;
                    speak("I did not get you");

//                    Log.i(TAG, "speak: "+"---I did not get you");

                }else{
                    Log.i(TAG, "Ques answered");
                }
            }
        };
        handler.postDelayed(callBack, 10000);


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
                    if(isNeedToListen) {
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


