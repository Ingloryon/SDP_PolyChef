
package ch.epfl.polychef.utils;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Class representing the voice synthesizer that will read the recipes' instructions.
 */
public class VoiceSynthesizer {
    private TextToSpeech textToSpeech;
    private Activity activity;

    /**
     * Constructs the Voice Synthesizer in a given activity.
     * @param activity the activity where the VoiceSynthesizer takes place
     */
    public VoiceSynthesizer(Activity activity) {
        this.activity=activity;

        initializeTextToSpeech();
    }

    /**
     * Speaks out the message with a synthetic voice.
     * @param message the text to speak out
     */
    public void speak(String message){
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH,null,null);
    }

    /**
     * Shutdown the textToSpeech object when the activity is on pause.
     */
    public void onStop(){
        textToSpeech.shutdown();
    }

    /**
     * initialize the object TextToSpeech.
     */
    private void initializeTextToSpeech() {
        textToSpeech= new TextToSpeech(activity, status -> {
            if(textToSpeech.getEngines().size()==0){
                throw new UnsupportedOperationException("There is no voice recognition engine.");
            }else{
                textToSpeech.setLanguage(Locale.UK);
            }
        });
    }
}