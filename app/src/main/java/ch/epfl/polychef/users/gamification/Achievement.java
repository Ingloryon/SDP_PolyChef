package ch.epfl.polychef.users.gamification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.polychef.users.User;
import ch.epfl.polychef.utils.Preconditions;

/**
 *
 */
public final class Achievement {
    private static final int MAX_ACHIEVEMENT_STEPS = 5;
    private final List<Integer> steps = new ArrayList<>();
    private final List<String> picturesLabels = new ArrayList<>();
    private final List<String> picturesNames = new ArrayList<>();
    private final int nbOfSteps;


    public Achievement(String name, int nbOfSteps, List<String> picturesNames, List<String> picturesLabels, List<Integer> steps){
        Preconditions.checkArgument(0 < nbOfSteps && nbOfSteps <= MAX_ACHIEVEMENT_STEPS, "The number of steps must be strictly positive and less than the maximum number of steps (" + MAX_ACHIEVEMENT_STEPS + ")");
        Preconditions.checkArgument(picturesNames.size() == nbOfSteps && picturesLabels.size() == nbOfSteps && steps.size() == nbOfSteps);

        this.nbOfSteps = nbOfSteps;
        Collections.copy(this.steps, steps);
        Collections.copy(this.picturesLabels, picturesLabels);
        Collections.copy(this.picturesNames, picturesNames);
    }


/*
    public int getAchivementLevel(User user){
        for(int i = 0 ; i < steps.size() ; ++i){
            if()
        }

        return 0;
    }

    private void getNumberOfPostedRecipes(){
        userToDisplay.getRecipes().size()
    }
*/

}
