package ch.epfl.polychef.utils;

import android.content.Intent;
import android.speech.tts.TextToSpeech;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class VoiceSynthesizerTest {

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new EntryPageTest.FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false,
            false);

    /*
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
    */

    @Mock
    TextToSpeech textToSpeech;


    @Before
    public void initIntent() {
        Intents.init();
        intentsTestRule.launchActivity(new Intent());
        MockitoAnnotations.initMocks(this);
        when(textToSpeech.getEngines()).thenReturn( new ArrayList<>() );
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    
    @Test
    public void voiceSythInitializesCorrectlyAndAcceptSimpleWord(){
        VoiceSynthesizer vs = new VoiceSynthesizer(intentsTestRule.getActivity());
        vs.speak("Test");
        vs.onStop();
    }


        /* //TODO: complicated test (hard to mock and get the exception in the right place
    @Test(expected = UnsupportedOperationException.class)
    public void voiceSythThrowsExcWhenNoGameEngines()  {

        exceptionRule.expect(UnsupportedOperationException.class);
        exceptionRule.expectMessage("There is no voice recognition engine.");

        VoiceSynthesizer vs = new VoiceSynthesizer(intentsTestRule.getActivity(), textToSpeech);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            //we don't mind if the sleep is interrupted
        }

        intentsTestRule.finishActivity();
        Intents.release();
    }*/

}


