package ch.epfl.polychef.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ch.epfl.polychef.utils.Preconditions;

import static android.app.Activity.RESULT_OK;

/**
 * A handler for getting image both through the camera and the gallery and upload them to firebase.
 */
public class ImageHandler {

    public static final int REQUEST_IMAGE_FROM_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    private Uri fileUri;
    private final Context context;

    public ImageHandler(Context context) {
        Preconditions.checkArgument(context != null, "Context should not be null");
        this.context = context;
    }

    /**
     * Get the intent to choose an image from the gallery.
     *
     * @return the gallery intent
     */
    public Intent getGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return Intent.createChooser(intent, "Select Picture");
    }

    /**
     * Get the intent to take a picture using the camera.
     *
     * @return the camera intent
     */
    public Intent getCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File outputFile = File.createTempFile("IMG_", ".jpg",
                    context.getCacheDir());
            fileUri = FileProvider.getUriForFile(context, "ch.epfl.polychef.provider",
                    outputFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                return takePictureIntent;
            }
        } catch (IOException e) {
            Log.d("IMAGE-UPLOAD", "Error");
        }
        return null;
    }

    /**
     * Prepare and upload the {@code imageView} to Firebase.
     *
     * @param imageView the image to upload
     */
    public void prepareImageAndUpload(ImageView imageView, String imageName, String user, String recipeUId) {
        Preconditions.checkArgument(imageView != null, "imageView should not be null");
        Drawable drawable = imageView.getDrawable();

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        uploadFromBitMap(bitmapDrawable.getBitmap(), imageName, user, recipeUId);
    }

    /**
     * Upload the {@code image} to Firebase.
     *
     * @param image the image to upload
     */
    public void uploadFromUri(Uri image, String imageName, String user, String recipeUId) {
        Preconditions.checkArgument(image != null, "image uri should not be null");
        try {
            uploadFromBitMap(MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                    image), imageName, user, recipeUId);
        } catch (IOException e) {
            Log.d("IMAGE-UPLOAD", "Error");
        }
    }

    private void uploadFromBitMap(Bitmap bitmap, String imageName, String user, String recipeUId) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        ImageStorage uploader = getImageStorage();
        uploader.upload(imageInByte, imageName, user, recipeUId).addOnSuccessListener(taskSnapshot -> Log.d("IMAGE-UPLOAD",
                "Success")).addOnFailureListener(e -> Log.d("IMAGE-UPLOAD",
                "Error"));
    }

    /**
     * Handle result, should be called by activity's {@code onActivityResult} method.
     *
     * @param requestCode {@code onActivityResult} method's requestCode
     * @param resultCode  {@code onActivityResult} method's resultCode
     * @param data        {@code onActivityResult} method's data
     * @return the image's Uri or null if it failed to get it
     */
    public Uri handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return null;
        }
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                return fileUri;
            case REQUEST_IMAGE_FROM_GALLERY:
                return data != null ? data.getData() : null;
            default:
                return null;
        }
    }

    public ImageStorage getImageStorage() {
        return new ImageStorage();
    }
}
