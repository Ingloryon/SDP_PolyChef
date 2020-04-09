package ch.epfl.polychef.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

import ch.epfl.polychef.R;

public class VoiceRecognitionActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        FloatingActionButton floatingActionButton=(FloatingActionButton) findViewById(R.id.voiceRecognitionFloatingActionButton);
        floatingActionButton.setOnClickListener((view)->{
            startListening();
        });

        initializeTextToSpeech();
        initializeSpeechRecognizer();
    }

    private void startListening(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,new Long(1000000));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,new Long(1000000));
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,new Long(1000000));
        speechRecognizer.startListening(intent);
    }

    /**
     * initialize the object SpeechRecognizer
     */
    private void initializeSpeechRecognizer() {
        if(SpeechRecognizer.isRecognitionAvailable(this)){
            speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onResults(Bundle bundle) {
                    TextView foundWordsLabel=(TextView) findViewById(R.id.foundWords);
                    foundWordsLabel.setText(foundWordsLabel.getText()+"{|||}");

                    startListening();

                    List<String> results=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(results.get(0));
                }

                @Override
                public void onReadyForSpeech(Bundle params) {}
                @Override
                public void onBeginningOfSpeech() {
                    TextView foundWordsLabel=(TextView) findViewById(R.id.foundWords);
                    foundWordsLabel.setText(foundWordsLabel.getText()+"{>>>}");
                }
                @Override
                public void onRmsChanged(float rmsdB) {}
                @Override
                public void onBufferReceived(byte[] buffer) {}
                @Override
                public void onEndOfSpeech() {
                    TextView foundWordsLabel=(TextView) findViewById(R.id.foundWords);
                    foundWordsLabel.setText(foundWordsLabel.getText()+"{<<<}");
                }
                @Override
                public void onError(int error) {
                    TextView foundWordsLabel=(TextView) findViewById(R.id.foundWords);
                    foundWordsLabel.setText(foundWordsLabel.getText()+"{E"+error+"}");
                }
                @Override
                public void onPartialResults(Bundle partialResults) {
                    List<String> results=partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    TextView foundWordsLabel=(TextView) findViewById(R.id.foundWords);
                    //foundWordsLabel.setText(results.get(0));
                    foundWordsLabel.setText(foundWordsLabel.getText()+",-,");
                }

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
        }else{
            Toast.makeText(VoiceRecognitionActivity.this,"An Error Occurred",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * initialize the object TextToSpeech
     */
    private void initializeTextToSpeech() {
        textToSpeech= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    Toast.makeText(VoiceRecognitionActivity.this ,"There is no voice recognition engine.",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    textToSpeech.setLanguage(Locale.UK);
                    speak("Ready to start.");
                }
            }
        });
    }

    /**
     * trigger different action depending on the command received
     * @param command the text received
     */
    private void processResult(String command){
        command=command.toLowerCase();

        if(command.contains("next")){
            speak("You commanded to read the next instruction");
        }else if(command.contains("repeat")){
            speak("You commanded to repeat the instruction");
        }else{
            speak("Please repeat, I heard that you were saying "+command);
        }
    }

    /**
     * Speaks out the message with a synthetic voice
     * @param message the text to speak out
     */
    private void speak(String message){
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH,null,null);
    }

    /**
     *  Shutdown the textToSpeech object when the activity is on pause
     */
    @Override
    protected void onPause(){
        super.onPause();
        Toast.makeText(VoiceRecognitionActivity.this ,"ShutDown VoiceRecognition",Toast.LENGTH_SHORT).show();
        textToSpeech.shutdown();
    }
}
