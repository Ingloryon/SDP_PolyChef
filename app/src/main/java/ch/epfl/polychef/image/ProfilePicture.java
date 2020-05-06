package ch.epfl.polychef.image;

import androidx.annotation.NonNull;

import ch.epfl.polychef.utils.Preconditions;

/**
 * Class that represents a possible profile Picture.
 */
public class ProfilePicture {

    private String pictureLabel;
    // Image name (Without extension)
    private String picturePath;


    /**
     * Constructor of a new profile picture.
     * @param pictureLabel the label of the picture
     * @param picturePath the name of the drawable image, must be non-empty
     */
    public ProfilePicture(@NonNull String pictureLabel, @NonNull String picturePath) {
        Preconditions.checkArgument(!picturePath.isEmpty(), "The image name must be non empty");
        this.pictureLabel = pictureLabel;
        this.picturePath = picturePath;
    }

    /**
     * Returns the label of the picture.
     * @return the name of the label
     */
    public String getPictureLabel() {
        return pictureLabel;
    }

    /**
     * Sets the label of the picture.
     * @param pictureLabel the new label to set
     */
    public void setPictureLabel(String pictureLabel) {
        this.pictureLabel = pictureLabel;
    }

    /**
     * Returns the picture name in drawable.
     * @return the image name
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * Sets the name of the profile picture
     * @param picturePath the new name of the picture png
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    @Override
    public String toString()  {
        return "The picture has name " + this.picturePath + "in drawable. Its label is" + this.pictureLabel;
    }
}