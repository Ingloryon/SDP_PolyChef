package ch.epfl.polychef.image;

import ch.epfl.polychef.CallHandler;
import ch.epfl.polychef.Preconditions;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

/**
 * Uploader and downloader of image from the Firebase Storage.
 */
public class ImageStorage {

    private static final long TEN_MEGABYTE = 10 * 1024 * 1024;

    /**
     * Upload an image to Firebase.
     * @param image the image to upload
     * @return the {@code UploadTask} for handling Firebase response
     */
    public UploadTask upload(byte[] image){
        Preconditions.checkArgument(image != null, "image to upload cannot be null");
        Preconditions.checkArgument(image.length != 0, "image to upload cannot be empty");
        String path = "images/" + UUID.randomUUID() + ".png";
        StorageReference storageRef = getStorage().getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("User", "TODO:USERNAME")
                .setCustomMetadata("Recipe", "TODO:RECIPE_UID")
                .build();

        return storageRef.putBytes(image, metadata);
    }

    /**
     * Get an image from Firebase with {@code imageName} name.
     * @param imageName the name of the image
     * @param caller the CallHandler to call on success or failure
     */
    public void getImage(String imageName, final CallHandler caller) {
        Preconditions.checkArgument(imageName != null, "image name to download cannot be null");
        Preconditions.checkArgument(caller != null, "CallHandler cannot be null");
        StorageReference storageRef = getStorage().getReference();
        StorageReference imgRef = storageRef.child("images/"+imageName);
        imgRef.getBytes(TEN_MEGABYTE).addOnSuccessListener(bytes -> caller.onSuccess(bytes))
                .addOnFailureListener(exception -> caller.onFailure());
    }

    /**
     * Get the current instance of the {@code FirebaseStorage}.
     * @return the current instance of the {@code FirebaseStorage}
     */
    public FirebaseStorage getStorage() {
        return FirebaseStorage.getInstance();
    }
}
