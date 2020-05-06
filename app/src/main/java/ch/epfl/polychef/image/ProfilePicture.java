package ch.epfl.polychef.image;

/**
 * Class that represents a possible profile Picture.
 */
public class ProfilePicture {

    private String pictureName;
    // Image name (Without extension)
    private String picturePath;


    /**
     *
     * @param pictureName
     * @param picturePath
     */
    public ProfilePicture(String pictureName, String picturePath) {
        this.pictureName = pictureName;
        this.picturePath = picturePath;
    }

    /**
     *
     * @return
     */
    public String getPictureName() {
        return pictureName;
    }

    /**
     *
     * @param pictureName
     */
    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    /**
     *
     * @return
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     *
     * @param picturePath
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    @Override
    public String toString()  {
        return this.pictureName;
    }
}