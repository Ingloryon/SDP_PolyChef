package ch.epfl.polychef.utils;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import ch.epfl.polychef.Miniatures;
import ch.epfl.polychef.recipe.Rating;
import ch.epfl.polychef.recipe.Recipe;

/**
 * An utility class containing sorting methods.
 */
public class Sort {
    private static String actualQuery;

    /**
     * Private constructor to prevent instantiation.
     */
    private Sort(){
    }

    /**
     * Sort the given Miniature list by similarity with the given query.
     * @param list the list of recipe to sort
     * @param query the query that determines the sorting similarity
     */
    public static void sortBySimilarity(List<Miniatures> list, String query){
        actualQuery = query;
        Collections.sort(list,similarityComparator);
    }

    /**
     * Sorts the given recipes by their rate.
     * @param list the list of recipes to sort
     */
    public static void sortByRate(List<Miniatures> list){
        Collections.sort(list,rateComparator);
    }

    /**
     * Sort the given Miniature list by ingredient similarity with the given query.
     * @param list the list of recipes to sort
     * @param query the ingredient query that determines the sorting similarity
     */
    public static void sortByIngredientSimilarity(List<Miniatures> list, String query){
        actualQuery = query;
        Collections.sort(list,similarityIngredientComparator);
    }

    private static Comparator<Miniatures> similarityComparator = (o1, o2) -> {
        String s1 = o1.getName();
        String s2 = o2.getName();

        //noinspection UseCompareMethod (here we want to return exactly -1 or 1)
        return Similarity.similarity(s1,actualQuery)>Similarity.similarity(s2,actualQuery) ? -1 :
                Similarity.similarity(s1,actualQuery)==Similarity.similarity(s2,actualQuery) ? 0 : 1;
    };

    private static Comparator<Miniatures> similarityIngredientComparator = (o1, o2) -> {
        List<Double> sim1 = Lists.transform(((Recipe)o1).getIngredients(), x-> Similarity.similarity(Objects.requireNonNull(x).getName(),actualQuery));
        List<Double> sim2 = Lists.transform(((Recipe)o2).getIngredients(), x-> Similarity.similarity(Objects.requireNonNull(x).getName(),actualQuery));
        Double max1 = Collections.max(sim1);
        Double max2 = Collections.max(sim2);

        return max1>max2 ? -1 : max1.equals(max2) ? 0 : 1;
    };

    private static Comparator<Miniatures> rateComparator = (o1, o2) -> {
        Rating r1 = o1.getRating();
        Rating r2 = o2.getRating();

        //noinspection UseCompareMethod (here we want to return exactly -1 or 1)
        return r1.ratingAverage()>r2.ratingAverage() ? -1 : r1.ratingAverage()==r2.ratingAverage() ? 0 : 1;
    };
}
