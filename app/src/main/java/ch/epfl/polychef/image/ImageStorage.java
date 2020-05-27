package ch.epfl.polychef.image;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.utils.Preconditions;

/**
 * Uploader and downloader of image from the Firebase Storage.
 */
public class ImageStorage {

    private static final long TEN_MEGABYTE = 10 * 1024 * 1024;

    private static ImageStorage INSTANCE = new ImageStorage();

    public static ImageStorage getInstance() {
        return INSTANCE;
    }

    private Map<String, byte[]> storedImages;

    private ImageStorage(){
        storedImages = new HashMap<>();
    }

    /**
     * Upload an image to Firebase.
     * @param image the image to upload
     * @return the {@code UploadTask} for handling Firebase response
     */
    public UploadTask upload(byte[] image, String imageName, String user, String recipeUId) {
        Preconditions.checkArgument(image != null, "image to upload cannot be null");
        Preconditions.checkArgument(image.length != 0, "image to upload cannot be empty");
        Preconditions.checkArgument(imageName != null, "image path to upload cannot be null");

        String path = "images/" + imageName + ".jpg";
        StorageReference storageRef = getStorage().getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("User", user == null ? "no_user" : user)
                .setCustomMetadata("Recipe", recipeUId == null ? "no_recipe" : recipeUId)
                .build();

        UploadTask task = storageRef.putBytes(image, metadata);

        task.addOnSuccessListener(taskSnapshot -> storedImages.put(imageName, image));

        return task;
    }

    /**
     * Get an image from Firebase with {@code imageName} name.
     * @param imageName the name of the image
     * @param caller the CallHandler to call on success or failure
     */
    public void getImage(String imageName, final CallHandler<byte []> caller) {
        Preconditions.checkArgument(imageName != null, "image name to download cannot be null");
        Preconditions.checkArgument(caller != null, "CallHandler cannot be null");

        if(storedImages.containsKey(imageName)){
            caller.onSuccess(storedImages.get(imageName));
        } else {
            StorageReference storageRef = getStorage().getReference();
            StorageReference imgRef = storageRef.child("images/" + imageName + ".jpg");
            imgRef.getBytes(TEN_MEGABYTE)
                    .addOnFailureListener(exception -> caller.onFailure())
                    .addOnSuccessListener(bytes -> {
                        storedImages.put(imageName, bytes);
                        caller.onSuccess(bytes);
                    });

        }
    }

    /**
     * Get the current instance of the {@code FirebaseStorage}.
     * @return the current instance of the {@code FirebaseStorage}
     */
    public FirebaseStorage getStorage() {
        return FirebaseStorage.getInstance();
    }
}
