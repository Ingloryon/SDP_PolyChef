package ch.epfl.polychef.image;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import ch.epfl.polychef.CallHandler;

public class ImageUploader {

    public final static String UPLOAD_FAILED = "UPLOAD_FAILED";

    private FirebaseStorage storage;
    final long TEN_MEGABYTE = 10 * 1024 * 1024;

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

    public void getImage(String imageName, final CallHandler c) {
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("images/"+imageName);
        islandRef.getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                c.onSuccess(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                c.onFailure();
            }
        });
    }
}
