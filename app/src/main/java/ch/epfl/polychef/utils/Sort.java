package ch.epfl.polychef.utils;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.recipe.Rating;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.users.User;

public class Sort {
    private static String actualQuery;

    public static void sortBySimilarity(List<Miniatures> list, String query){
        actualQuery = query;
        Collections.sort(list,similarityComparator);
    }

    public static void sortByRate(List<Miniatures> list){
        Collections.sort(list,rateComparator);
    }

    public static void sortByIngredientSimilarity(List<Miniatures> list, String query){
        actualQuery = query;
        Collections.sort(list,similarityIngredientComparator);
    }

    private static Comparator<Miniatures> similarityComparator = (o1, o2) -> {
        String s1 = getName(o1);
        String s2 = getName(o2);
        if(Similarity.similarity(s1,actualQuery)>Similarity.similarity(s2,actualQuery)){
            return -1;
        }else if(Similarity.similarity(s1,actualQuery)==Similarity.similarity(s2,actualQuery)){
            return 0;
        }else {
            return 1;
        }
    };

    private static Comparator<Miniatures> similarityIngredientComparator = (o1, o2) -> {
        List<Double> sim1 = Lists.transform(((Recipe)o1).getIngredients(), x-> Similarity.similarity(x.getName(),actualQuery));
        List<Double> sim2 = Lists.transform(((Recipe)o2).getIngredients(), x-> Similarity.similarity(x.getName(),actualQuery));
        Double max1 = Collections.max(sim1);
        Double max2 = Collections.max(sim2);
        if(max1>max2){
            return -1;
        }else if(max1==max2){
            return 0;
        }else {
            return 1;
        }
    };

    private static Comparator<Miniatures> rateComparator = (o1, o2) -> {
        Rating r1 = o1.getRating();
        Rating r2 = o2.getRating();
        if(r1.ratingAverage()>r2.ratingAverage()){
            return -1;
        }else if(r1.ratingAverage()==r2.ratingAverage()){
            return 0;
        }else {
            return 1;
        }
    };

    private static String getName(Miniatures miniature){
        if(miniature.getClass().equals(Recipe.class)){
            return ((Recipe)miniature).getName();
        }else{
            return ((User)miniature).getUsername();
        }
    }
}
