package ch.epfl.polychef.utils;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class VoiceSynthesizer {
    private TextToSpeech textToSpeech;
    private Activity activity;

    public VoiceSynthesizer(Activity activity) {
        this.activity=activity;

        initializeTextToSpeech();
    }

    /**
     * initialize the object TextToSpeech
     */
    private void initializeTextToSpeech() {
        textToSpeech= new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(textToSpeech.getEngines().size()==0){
                    throw new UnsupportedOperationException("There is no voice recognition engine.");
                }else{
                    textToSpeech.setLanguage(Locale.UK);
                    speak("Ready to start.");
                }
            }
        });
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
    protected void onPause(){
        textToSpeech.shutdown();
    }
}