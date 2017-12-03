package nd.mkpt.com.nightdriving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity implements RecognitionListener, TextToSpeech.OnInitListener {
    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private TextToSpeech tts;
    private static final int REQUEST_CODE = 1234;
    private Button btnStart;
    private HashMap<String, String> params = new HashMap<String, String>();
    public MainActivity mainActivity;

    private void startListen(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        speech.startListening(recognizerIntent);
    }

    private void stopListen(){
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        speech.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_main);
        returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1); progressBar.setVisibility(View.INVISIBLE); speech=SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en"); recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName()); recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH); recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            { // TODO Auto-generated method stub
                if (isChecked)
                {
                   startListen();
                }
                else
                {
                   stopListen();
                }
            }
        });

        tts = new TextToSpeech(this, this);
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
//
        // button on click event
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speak("Hi");
            }

        });
    }

    public void speak(String str){
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, params);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if (speech != null)
        {
            speech.destroy();
//            Log.i(LOG_TAG, "destroy");
        }
    }
    @Override
    public void onBeginningOfSpeech()
    { // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }
    @Override
    public void onBufferReceived(byte[] arg0)
    {
        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onBufferReceived: " + arg0);
    }



    @Override
    public void onEndOfSpeech()
    { // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }
    @Override
    public void onError(int errorCode)
    {
        // TODO Auto-generated method stub
        String errorMessage = getErrorText(errorCode);
//        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
    }
    @Override
    public void onEvent(int arg0, Bundle arg1)
    {
        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onEvent");
    }
    @Override
    public void onPartialResults(Bundle arg0)
    {
        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onPartialResults");
    }
    @Override
    public void onReadyForSpeech(Bundle arg0)
    {
        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onReadyForSpeech")
        ; }
    @Override
    public void onResults(Bundle arg0)
    {
        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) text += result + "\n";
        returnedText.setText(text);
    }
    @Override
    public void onRmsChanged(float rmsdB)
    {
        // TODO Auto-generated method stub
//        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }
    public static String getErrorText(int errorCode)
    {
        String message; switch (errorCode)
    {
        case SpeechRecognizer.ERROR_AUDIO:
            message = "Audio recording error";
            break;
        case SpeechRecognizer.ERROR_CLIENT:
            message = "Client side error";
            break;
        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            message = "Insufficient permissions";
            break;
        case SpeechRecognizer.ERROR_NETWORK:
            message = "Network error";
            break;
        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            message = "Network timeout";
            break;
        case SpeechRecognizer.ERROR_NO_MATCH:
            message = "No match";
            break;
        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            message = "RecognitionService busy";
            break;
        case SpeechRecognizer.ERROR_SERVER:
            message = "error from server";
            break;
        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
            message = "No speech input";
            break;
        default:
            message = "Didn't understand, please try again.";
            break;
    }
        return message;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    // Log.d("MainActivity", "TTS finished");
//                    mainActivity.startVoiceRecognitionActivity();
                    startListen();
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

//    public void startVoiceRecognitionActivity()
//    {
//
//        speech.startListening(recognizerIntent);
////        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
////        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
////                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
////        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
////        startActivityForResult(intent, REQUEST_CODE);
//    }
}
