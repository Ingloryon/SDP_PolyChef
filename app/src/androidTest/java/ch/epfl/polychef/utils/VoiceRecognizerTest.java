package ch.epfl.polychef.utils;


import android.app.Activity;

import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.polychef.CallNotifier;

public class VoiceRecognizerTest {

    CallNotifier<String> mockCallNotifier= Mockito.mock(CallNotifier.class);

    Activity mockActivity= Mockito.mock(Activity.class);

    @Test
    public void VoiceRecognizerStartAndStopWithoutErrorTest(){
        VoiceRecognizer vr=new VoiceRecognizer(mockCallNotifier);
        vr.start(mockActivity);
        vr.onStop();
    }



}
