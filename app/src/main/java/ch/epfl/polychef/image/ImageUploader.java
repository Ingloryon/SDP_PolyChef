package ch.epfl.polychef.image;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ImageUploader {

    public final static String UPLOAD_FAILED = "UPLOAD_FAILED";

    private FirebaseStorage storage;

    public ImageUploader(){
        storage = FirebaseStorage.getInstance();
    }

    public String upload(byte[] image){
        String path = "images/" + UUID.randomUUID() + ".png";
        StorageReference storageRef = storage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("User", "TODO:USERNAME")
                .setCustomMetadata("Recipe", "TODO:RECIPE_UID")
                .build();

        UploadTask uploadTask = storageRef.putBytes(image, metadata);


        return UPLOAD_FAILED;
    }
}
