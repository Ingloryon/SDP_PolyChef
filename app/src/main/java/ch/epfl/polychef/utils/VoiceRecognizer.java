package ch.epfl.polychef.utils;

import java.io.File;
import java.io.IOException;

import ch.epfl.polychef.CallNotifier;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class VoiceRecognizer implements RecognitionListener {

    private static final String KWS_SEARCH = "wakeup";//keyword to start recognition
    private static final String MENU_SEARCH = "menu";//name of the recognition set of words
    // Keyword we are looking for to activate recognition
    private static final String KEYPHRASE = "poly chef";

    //Recognition object
    private SpeechRecognizer recognizer;

    private CallNotifier<String> callNotifier;

    public VoiceRecognizer(CallNotifier<String> callNotifier){
        this.callNotifier=callNotifier;
    }

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
                Log.d("vr","onPostExecute");
                if (result != null) {
                    System.out.println(result.getMessage());
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
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

    public void onStop() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();

        if (text.equals(KEYPHRASE)) {
            switchSearch(MENU_SEARCH);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        Log.d("vr","onResult found : "+hypothesis.getHypstr());

        if (hypothesis != null && !hypothesis.getHypstr().equals(KEYPHRASE)) {
            callNotifier.notify(hypothesis.getHypstr());
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH)) {
            switchSearch(KWS_SEARCH);
        }
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(KWS_SEARCH);
        }else {
            recognizer.startListening(searchName, 10000);
        }
    }

    @Override
    public void onError(Exception error) {
        System.out.println(error.getMessage());
    }

    @Override
    public void onTimeout() {
        Log.d("vr","onTimeout");
        switchSearch(KWS_SEARCH);
    }
}
