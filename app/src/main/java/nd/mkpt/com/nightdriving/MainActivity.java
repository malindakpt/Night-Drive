package nd.mkpt.com.nightdriving;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView mTextMessage;
    public MainActivity mainActivity;

    private TextToSpeech tts;
    private Button btnStart;
    private EditText txtText;


    private static final int REQUEST_CODE = 1234;
    private ListView wordsList;

    Timer timer = new Timer();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainActivity = this;
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setContentView(R.layout.voice_recog);
        setContentView(R.layout.activity);

        // Combined tasks


        //

//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//
//        tts = new TextToSpeech(this, this);
//
//        btnSpeak = (Button) findViewById(R.id.btnSpeak);
//
//        txtText = (EditText) findViewById(R.id.txtText);


        tts = new TextToSpeech(this, this);
        // button on click event
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut();
            }

        });


        // STT
//        Button speakButton = (Button) findViewById(R.id.speakButton);
//
//        wordsList = (ListView) findViewById(R.id.list);
//
//        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            btnStart.setEnabled(false);
//            speakButton.setText("Recognizer not present");
        }
    }

    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    /**
     * Fire an intent to start the voice recognition activity.
     */
    public void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
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
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            tts.speak("Thanks "+matches.get(0), TextToSpeech.QUEUE_FLUSH, null);
//            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
//                    matches));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void speakOut() {

        String text = " Hi Malinda ";

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);


        TimerTask updateBall = new UpdateBallTask(this);
        timer.schedule(updateBall,3000);

//        Toast.makeText(this.getApplication(),"----------",Toast.LENGTH_SHORT).show();
//
//        final Application app = this.getApplication();
//
//        System.out.println("-------------------");
//        Log.e("TTS", "-------------------");

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Toast.makeText(app.getApplicationContext(),"ddddddddd",Toast.LENGTH_SHORT).show();
//            }
//
//        }, 0, 1000);
       // startVoiceRecognitionActivity();
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
    UtteranceProgressListener utteranceProgressListener=new UtteranceProgressListener() {

        @Override
        public void onStart(String utteranceId) {
            System.out.println("sdsd");
//            Log.d(TAG, "onStart ( utteranceId :"+utteranceId+" ) ");
        }

        @Override
        public void onError(String utteranceId) {
            System.out.println("sdsd");
        }

        @Override
        public void onDone(String utteranceId) {
            System.out.println("sdsd");
        }
    };
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    // Log.d("MainActivity", "TTS finished");
                    mainActivity.startVoiceRecognitionActivity();
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });


            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnStart.setEnabled(true);
//                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}

class UpdateBallTask extends TimerTask {
//    Ball myBall;
    private MainActivity app;
    public UpdateBallTask(MainActivity app){
        this.app=app;
    }

    public void run() {
        //calculate the new position of myBall
//        app.startVoiceRecognitionActivity();
    }
}

