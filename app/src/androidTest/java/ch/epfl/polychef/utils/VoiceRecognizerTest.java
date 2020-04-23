package ch.epfl.polychef.utils;


import android.Manifest;
import android.app.Activity;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.pages.EntryPage;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class VoiceRecognizerTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);
    @Rule
    public IntentsTestRule<EntryPage> intentsTestRule = new IntentsTestRule<>(EntryPage.class);

    CallNotifier<String> mockCallNotifier= Mockito.mock(CallNotifier.class);

    Activity mockActivity= Mockito.mock(Activity.class);

    @Test
    public void voiceRecognizerStartAndStopWithoutErrorTest(){
        VoiceRecognizer vr=new VoiceRecognizer(mockCallNotifier);
        vr.start(intentsTestRule.getActivity());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vr.onStop();
    }

    @Test
    public void voiceRecognizerDoesNotInitializeCorrectlyWhenInvalidActivity(){
        VoiceRecognizer vr=new VoiceRecognizer(mockCallNotifier);
        vr.start(mockActivity);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(vr.getRecognizer()==null);
    }

    @Test
    public void voiceRecognizerSwitchSearchWhenSearchNameIsNotKeyWordRaiseNoError(){
        MockVoiceRecognizer vr=new MockVoiceRecognizer(mockCallNotifier);

        vr.setRecognizer(Mockito.mock(SpeechRecognizer.class));

        vr.switchSearch("menu");//which is a valid but is not the KeyWord
        vr.onStop();
    }

    @Test
    public void voiceRecognizerDoNothingOnError() {
        VoiceRecognizer vr=new VoiceRecognizer(mockCallNotifier);
        vr.onError(null);
    }

    @Test
    public void voiceRecognizerDoNothingOnBeginningOfSpeech(){
        VoiceRecognizer vr=new VoiceRecognizer(mockCallNotifier);
        vr.onBeginningOfSpeech();
    }

    @Test
    public void voiceRecognizerSwitchSearchOnTimeOut(){
        ArrayList<String> arl=new ArrayList<>();
        arl.add("wakeup");
        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(arl,true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);
        vr.activeCheckMode(true);
        vr.onTimeout();
        vr.onStop();
        callNotifierChecker.assertWasCalled(1);
    }

    @Test
    public void voiceRecognizerDoesNothingWhenHypothesisIsNull(){
        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(Collections.emptyList(),true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);
        vr.activeCheckMode(true);
        vr.onPartialResult(null);
        vr.onStop();
        callNotifierChecker.assertWasCalled(0);
    }

    @Test
    public void voiceRecognizerDoesNothingWhenHypothesisIsNotKeyWord(){
        Hypothesis mockHypothesis=Mockito.mock(Hypothesis.class);
        when(mockHypothesis.getHypstr()).thenReturn("notKeyPhrase");
        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(Collections.emptyList(),true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);
        vr.activeCheckMode(true);
        vr.onPartialResult(mockHypothesis);
        vr.onStop();
        callNotifierChecker.assertWasCalled(0);
    }

    @Test
    public void voiceRecognizerSwitchSearchWhenHypothesisIsKeyWord(){
        Hypothesis mockHypothesis=Mockito.mock(Hypothesis.class);
        when(mockHypothesis.getHypstr()).thenReturn("poly chef");//which is the key phrase
        ArrayList<String> arl=new ArrayList<>();
        arl.add("menu");
        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(arl,true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);
        vr.activeCheckMode(true);
        vr.onPartialResult(mockHypothesis);
        vr.onStop();
        callNotifierChecker.assertWasCalled(1);
    }

    @Test
    public void voiceRecognizerOnResultDoNothingWhenHypothesisIsNull() {
        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(Collections.emptyList(),true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);

        vr.onResult(null);
        vr.onStop();
        callNotifierChecker.assertWasCalled(0);
    }

    @Test
    public void voiceRecognizerOnResultNotifyWhenHypothesisIsKeyPhrase() {
        Hypothesis mockHypothesis=Mockito.mock(Hypothesis.class);
        when(mockHypothesis.getHypstr()).thenReturn("poly chef");//which is the key phrase

        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(Collections.emptyList(),true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);

        vr.onResult(mockHypothesis);
        vr.onStop();
        callNotifierChecker.assertWasCalled(0);
    }

    @Test
    public void voiceRecognizerOnResultNotifyWhenHypothesisIsNotKeyPhrase() {
        Hypothesis mockHypothesis=Mockito.mock(Hypothesis.class);
        when(mockHypothesis.getHypstr()).thenReturn("NotKeyPhrase");

        ArrayList<String> arl=new ArrayList<>();
        arl.add("NotKeyPhrase");

        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(arl,true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);

        vr.onResult(mockHypothesis);
        vr.onStop();
        callNotifierChecker.assertWasCalled(1);
    }

    @Test
    public void voiceRecognizerOnEndOfSpeechSwitchSearchWhenInMenuMode() {
        ArrayList<String> arl=new ArrayList<>();
        arl.add("wakeup");

        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(arl,true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);

        SpeechRecognizer recognizer=Mockito.mock(SpeechRecognizer.class);
        when(recognizer.getSearchName()).thenReturn("menu");

        vr.setRecognizer(recognizer);

        vr.activeCheckMode(true);
        vr.onEndOfSpeech();
        callNotifierChecker.assertWasCalled(1);
    }

    @Test
    public void voiceRecognizerOnEndOfSpeechDoNotSwitchSearchWhenNotInMenuMode(){
        CallNotifierChecker<String> callNotifierChecker=new CallNotifierChecker<>(Collections.emptyList(),true);
        MockVoiceRecognizer vr=new MockVoiceRecognizer(callNotifierChecker);

        SpeechRecognizer recognizer=Mockito.mock(SpeechRecognizer.class);
        when(recognizer.getSearchName()).thenReturn("wakeup");

        vr.setRecognizer(recognizer);

        vr.activeCheckMode(true);
        vr.onEndOfSpeech();
        callNotifierChecker.assertWasCalled(0);
    }

    class MockVoiceRecognizer extends VoiceRecognizer{

        private boolean doesCheck=false;
        private CallNotifier<String> callNotifier;

        public MockVoiceRecognizer(CallNotifier<String> callNotifier) {
            super(callNotifier);
            this.callNotifier=callNotifier;
        }

        public void activeCheckMode(boolean active){
            doesCheck=active;
        }

        @Override
        protected void switchSearch(String searchName){
            if(doesCheck){
                callNotifier.notify(searchName);
            }else{
                super.switchSearch(searchName);
            }
        }
    }
}
