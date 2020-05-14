package ch.epfl.polychef.gamification;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.function.Function;

import ch.epfl.polychef.users.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AchievementTest {
    private ArrayList<String> picturesPaths = new ArrayList<>();
    private ArrayList<String> picturesLabels = new ArrayList<>();
    private ArrayList<Integer> levelSteps = new ArrayList<>();
    private Function<User, Integer> getUserNbRecipes = u -> u.getRecipes().size();

    @Before
    public void initArguments(){
        picturesPaths.add("pict0");
        picturesPaths.add("pict1");
        picturesPaths.add("pict2");
        picturesPaths.add("pict3");

        picturesLabels.add("label0");
        picturesLabels.add("label1");
        picturesLabels.add("label2");
        picturesLabels.add("label3");

        levelSteps.add(1);
        levelSteps.add(5);
        levelSteps.add(10);
    }


    @Test
    public void achievementRejectsInvalidInputs(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Achievement("", 3, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Achievement("achievement", 8, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Achievement("achievement", -1, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Achievement("achievement", 4, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes));
    }

    @Test
    public void achievementGetsCorrectLevel(){
        Achievement achi = createValidAchievement();
        User user = fakeUser();

        assertEquals(achi.getLevel(user), 0);

        user.addRecipe("fakeRecipeId");
        assertEquals(achi.getLevel(user), 1);

        for(int i = 0 ; i < 5 ; ++i){
            user.addRecipe("fakeRecipeId");
        }
        assertEquals(achi.getLevel(user), 2);

        for(int i = 0 ; i < 5 ; ++i){
            user.addRecipe("fakeRecipeId");
        }
        assertEquals(achi.getLevel(user), 3);
    }

    @Test
    public void gettersRejectsInvalidInputs(){
        Achievement achi = createValidAchievement();
        //for the images
        Assertions.assertThrows(IllegalArgumentException.class, () -> achi.getLevelImage(3+1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> achi.getLevelImage(-1));
        //for the labels
        Assertions.assertThrows(IllegalArgumentException.class, () -> achi.getLevelLabel(3+1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> achi.getLevelLabel(-1));
    }

    @Test
    public void gettersReturnCorrectArguments(){
        Achievement achi = createValidAchievement();
        assertEquals(achi.getName(), "cuistot");
        assertEquals(achi.getLevelImage(2), "pict2");
        assertEquals(achi.getLevelLabel(2), "label2");
    }


    private Achievement createValidAchievement() {
        return new Achievement("cuistot", 3, picturesPaths, picturesLabels, levelSteps, getUserNbRecipes);
    }

    private User fakeUser() {
        return new User("test@mail.com", "test");
    }
}
