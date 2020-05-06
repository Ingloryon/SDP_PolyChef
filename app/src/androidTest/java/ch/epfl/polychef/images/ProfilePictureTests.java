package ch.epfl.polychef.images;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import ch.epfl.polychef.image.ProfilePicture;

@RunWith(AndroidJUnit4.class)
public class ProfilePictureTests {

    @Test
    public void profilePictureRejectsEmptyFileName(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ProfilePicture("A label", ""));
    }

    @Test
    public void getterAndSettersReturnCorresctInfos(){
        ProfilePicture profilePict = new ProfilePicture("The label !", "man");
        profilePict.setPictureLabel("The label is better");
        profilePict.setPicturePath("boy");
        Assertions.assertEquals(profilePict.getPictureLabel(), "The label is better");
        Assertions.assertEquals(profilePict.getPicturePath(), "boy");
    }



}
