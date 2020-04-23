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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.pages.EntryPageTest;

import static org.mockito.Mockito.when;

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


    @Mock
    TextToSpeech textToSpeech;


    @Before
    public void initIntent() {
        Intents.init();
        intentsTestRule.launchActivity(new Intent());
    }

    @Before
    public void initMockFirebaseDatabase() {
        MockitoAnnotations.initMocks(this);
        when(textToSpeech.getEngines()).thenReturn( new ArrayList<>() );
    }

    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
        Intents.release();
    }

    @Test
    public void voiceSythThrowsExcWhenNoGameEngines() {
        //TODO: complex since onInit needs its own declaration, hard to mock
       // assertThrows(UnsupportedOperationException.class, () ->  );

    }

    @Test
    public void voiceSythInitializesCorrectlyAndAcceptSimpleWord(){
        VoiceSynthesizer vs = new VoiceSynthesizer(intentsTestRule.getActivity());
        vs.speak("Test");
        vs.onStop();
    }
}

