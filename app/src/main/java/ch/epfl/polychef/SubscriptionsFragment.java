package ch.epfl.polychef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionsFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public SubscriptionsFragment() { }

    static final int REQUEST_IMAGE_FROM_GALLERY = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    private Uri fileUri;

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscriptions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = getView().findViewById(R.id.imageUploaded);

        getView().findViewById(R.id.chooseImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add a picture");

                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals("Take Photo")) {
                        startTakePictureIntent();
                    } else if (options[item].equals("Choose from Gallery")) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_FROM_GALLERY);
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        getView().findViewById(R.id.uploadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.uploadButton).setVisibility(View.GONE);
                getView().findViewById(R.id.chooseImageButton).setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);

                ImageView imageView = getView().findViewById(R.id.imageUploaded);
                Drawable drawable = imageView.getDrawable();

                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable .getBitmap();
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
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageURI(fileUri);

            imageView.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.chooseImageButton).setVisibility(View.GONE);
        }
        if (data != null && requestCode == REQUEST_IMAGE_FROM_GALLERY) {
            imageView = getView().findViewById(R.id.imageUploaded);
            Uri selectedImage = data.getData();
            imageView.setImageURI(selectedImage);

            imageView.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.chooseImageButton).setVisibility(View.GONE);
        }
    }

    private void startTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File outputFile = File.createTempFile("IMG_", ".jpg", getActivity().getCacheDir());
            fileUri = FileProvider.getUriForFile(getActivity(), "ch.epfl.polychef.provider", outputFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (IOException e) {
            Log.d("IMAGE-UPLOAD", "Error");
        }
    }
}
