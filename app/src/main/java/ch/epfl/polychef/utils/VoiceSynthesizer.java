package ch.epfl.polychef.utils;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class VoiceSynthesizer {
    private TextToSpeech textToSpeech;
    private Activity activity;

    public VoiceSynthesizer(Activity activity) {
        this.activity=activity;
        this.textToSpeech = new TextToSpeech(activity, getInitListerner(this.textToSpeech));
    }

    /**
     * Speaks out the message with a synthetic voice.
     * @param message the text to speak out
     */
    public void speak(String message){
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH,null,null);
    }

    /**
     * gets the TextToSpeechListener redefintion
     * this format is used so it allows to mock the textToSpeech attribute (add constructor that takes it as attribute)
     */
    private TextToSpeech.OnInitListener getInitListerner(TextToSpeech txtToSpeech) {

        return new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(txtToSpeech.getEngines().size()==0){
                    throw new UnsupportedOperationException("There is no voice recognition engine.");
                }else{
                    txtToSpeech.setLanguage(Locale.UK);
                }
            }
        };
    }

    /**
     * Shutdown the textToSpeech object when the activity is on pause.
     */
    public void onStop(){
        textToSpeech.shutdown();
    }
}