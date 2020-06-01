package ch.epfl.polychef;

import ch.epfl.polychef.recipe.Rating;

/**
 * The general Miniature interface, a Miniature represents a clickable visual that links to an extended page.
 */
public interface Miniatures {

    /**
     * Whether the miniature represents a {@link ch.epfl.polychef.users.User}.
     * @return Whether the miniature represents a User
     */
    boolean isUser();

    /**
     * Whether the miniature represents a {@link ch.epfl.polychef.recipe.Recipe}.
     * @return Whether the miniature represents a Recipe
     */
    boolean isRecipe();

    /**
     * Gets the key of the object described by the miniature.
     * @return the key of the object described
     */
    String getKey();

    /**
     * Sets the key of the object described by the miniature.
     * @param key the new key for the object described
     */
    void setKey(String key);

    /**
     * Gets the name of the object the miniature describes.
     * @return the name the miniature describes
     */
    String getName();

    /**
     * Gets the rating of the object described by the miniature.
     * @return the rating of the object described
     */
    Rating getRating();
}
