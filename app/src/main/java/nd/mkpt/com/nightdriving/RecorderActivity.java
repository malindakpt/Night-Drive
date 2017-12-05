package nd.mkpt.com.nightdriving;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by MalindaK on 12/5/2017.
 */

public class RecorderActivity extends AppCompatActivity  implements RecognitionListener {

        private TextView returnedText;
        private ToggleButton toggleButton;
        private Button button1;
        private ProgressBar progressBar;
        private SpeechRecognizer speech = null;
        private Intent recognizerIntent;
        private String LOG_TAG = "Mkpt rec ";
        Handler handler = null;
        Runnable callBack = null;
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_record);
            returnedText = (TextView) findViewById(R.id.textView1);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);



            startListening();
            startTimeout();
        }

        private void stopTimeout(){
            if (handler != null && callBack != null) {
                handler.removeCallbacks(callBack);
            }
        }

        private void startTimeout(){

            if (handler != null && callBack != null) {
                handler.removeCallbacks(callBack);
            }
            handler = new Handler();
            callBack = new Runnable() {
                @Override
                public void run() {
                    Log.e(LOG_TAG, "Timeout occured ");
                    finishActivity(new ArrayList<String>());
                }
            };
            handler.postDelayed(callBack, 10000);
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
            stopListening();
        }


        private void stopListening(){
            if (speech != null)
            {
                speech.destroy();
                Log.i(LOG_TAG, "Stop Listening");
            }
        }

    private void startListening(){
        stopListening();
        speech=SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
    }




        @Override
        public void onBeginningOfSpeech()
        { // TODO Auto-generated method stub
            Log.i(LOG_TAG, "onBeginningOfSpeech");
            progressBar.setMax(10);
        }
        @Override
        public void onBufferReceived(byte[] arg0)
        {
            // TODO Auto-generated method stub
            Log.i(LOG_TAG, "onBufferReceived: " + arg0);
//            Toast.makeText(getApplicationContext(), "onBufferReceived",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onEndOfSpeech()
        { // TODO Auto-generated method stub
            Log.i(LOG_TAG, "onEndOfSpeech");
        }
        @Override
        public void onError(int errorCode)
        {
            String errorMessage = getErrorText(errorCode);
            Log.e(LOG_TAG, "FAILED " + errorMessage);
            Toast.makeText(getApplicationContext(), "FAILED",Toast.LENGTH_SHORT).show();
            startListening();
        }
        @Override
        public void onEvent(int arg0, Bundle arg1)
        {
            // TODO Auto-generated method stub
            Log.i(LOG_TAG, "onEvent");
            Toast.makeText(getApplicationContext(), "onEvent",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPartialResults(Bundle arg0)
        {
            // TODO Auto-generated method stub
            Log.i(LOG_TAG, "onPartialResults");
            Toast.makeText(getApplicationContext(), "onPartialResults",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onReadyForSpeech(Bundle arg0)
        {
            // TODO Auto-generated method stub
            Log.i(LOG_TAG, "onReadyForSpeech");
//            Toast.makeText(getApplicationContext(), "onReadyForSpeech",Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onResults(Bundle arg0)
        {
            Log.i(LOG_TAG, "onResults");
            ArrayList<String> matches = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            stopTimeout();
            finishActivity(matches);
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
            progressBar.setProgress((int) rmsdB);
        }

        private void finishActivity( ArrayList<String> matches){
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RecognizerIntent.EXTRA_RESULTS, matches);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
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
}
