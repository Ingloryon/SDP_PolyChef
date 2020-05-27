package ch.epfl.polychef.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import ch.epfl.polychef.CallNotifier;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * A class representing the interface a voice recognition system.
 */
@SuppressWarnings("WeakerAccess")
public class VoiceRecognizer implements RecognitionListener {
    private static final String KWS_SEARCH = "wakeup";//keyword to start recognition
    private static final String MENU_SEARCH = "menu";//name of the recognition set of words
    private static final String KEYPHRASE = "poly chef"; // Keyword we are looking for to activate recognition

    //Recognition object
    private SpeechRecognizer recognizer;
    private CallNotifier<String> callNotifier;

    /**
     * Constructor of VoiceRecognizer.
     * @param callNotifier the call notifier to associate it with
     */
    public VoiceRecognizer(CallNotifier<String> callNotifier){
        this.callNotifier=callNotifier;
    }

    /**
     * Starts the voice recognizer.
     * @param activity the activity where it starts
     */
    @SuppressLint("StaticFieldLeak")
    public void start(Activity activity){
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {

            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(activity);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result == null) {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    /**
     * Shuts down the voice recognizer.
     */
    public void onStop() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * Gets the speech recognizer.
     * @return the speech recognizer
     */
    public SpeechRecognizer getRecognizer(){
        return recognizer;
    }

    /**
     * Sets a new recognizer for the voice recognizer.
     * @param recognizer the new recognizer
     */
    protected void setRecognizer(SpeechRecognizer recognizer){
        this.recognizer=recognizer;
    }

    /**
     * Switches the search depending on the keywords.
     * @param searchName the search name given by user
     */
    protected void switchSearch(String searchName) {
        recognizer.stop();

        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(KWS_SEARCH);
        }else {
            recognizer.startListening(searchName, 10000);
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();

            if (text.equals(KEYPHRASE)) {
                switchSearch(MENU_SEARCH);
            }
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null && !hypothesis.getHypstr().equals(KEYPHRASE)) {
            callNotifier.notify(hypothesis.getHypstr());
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        // there is nothing to do on the beginning of speech
    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH)) {
            switchSearch(KWS_SEARCH);
        }
    }

    @Override
    public void onError(Exception error) {
        onStop();
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();
        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create your custom grammar-based search
        File menuGrammar = new File(assetsDir, "mymenu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
    }
}
