package ch.epfl.polychef.utils;


import android.app.Activity;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ch.epfl.polychef.CallNotifier;
import ch.epfl.polychef.pages.EntryPage;

@RunWith(AndroidJUnit4.class)
public class VoiceRecognizerTest {

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
    public void voiceRecognizerDoNothingOnError() {
        VoiceRecognizer vr=new VoiceRecognizer(mockCallNotifier);
        vr.onError(null);
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
