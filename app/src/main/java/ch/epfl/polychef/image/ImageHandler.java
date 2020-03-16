package ch.epfl.polychef.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ch.epfl.polychef.R;

import static android.app.Activity.RESULT_OK;

public class ImageHandler {

    public static final int REQUEST_IMAGE_FROM_GALLERY = 1;
    public static final  int REQUEST_IMAGE_CAPTURE = 2;

    private Uri fileUri;

    public Intent getGalleryIntent(Context context) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return Intent.createChooser(intent, "Select Picture");
    }

    public Intent getCameraIntent(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File outputFile = File.createTempFile("IMG_", ".jpg", context.getCacheDir());
            fileUri = FileProvider.getUriForFile(context, "ch.epfl.polychef.provider", outputFile);
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

    public void prepareImageAndUpload(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        ImageUploader uploader = new ImageUploader();
        if(uploader.upload(imageInByte) == ImageUploader.UPLOAD_FAILED){
            Log.d("IMAGE-UPLOAD", ImageUploader.UPLOAD_FAILED);
        } else {
            Log.d("IMAGE-UPLOAD", "Success");
        }
    }

    public Uri handleActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    return fileUri;
                case REQUEST_IMAGE_FROM_GALLERY:
                    return data.getData();
            }
        }
        return null;
    }
}
