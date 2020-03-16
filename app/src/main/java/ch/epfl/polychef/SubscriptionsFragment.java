package ch.epfl.polychef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import ch.epfl.polychef.image.ImageHandler;
import ch.epfl.polychef.image.ImageUploader;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionsFragment extends Fragment implements CallHandler {

    /**
     * Required empty public constructor.
     */
    public SubscriptionsFragment() {
    }

    private ImageView imageView;
    private ImageHandler imageHandler;

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
        imageHandler = new ImageHandler(getActivity());

        getView().findViewById(R.id.chooseImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add a picture");

                builder.setItems(options, (dialog, item) -> {
                    if (options[item].equals("Take Photo")) {
                        startActivityForResult(imageHandler.getCameraIntent(), ImageHandler.REQUEST_IMAGE_CAPTURE);
                    } else if (options[item].equals("Choose from Gallery")) {
                        startActivityForResult(imageHandler.getGalleryIntent(), ImageHandler.REQUEST_IMAGE_FROM_GALLERY);
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
                imageHandler.prepareImageAndUpload(imageView);
            }
        });
        final CallHandler c = this;
        getView().findViewById(R.id.downloadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageUploader().getImage("8eaae973-ff3a-418a-b7f4-1e622033bc37.png", c);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageReturned = imageHandler.handleActivityResult(requestCode, resultCode, data);

        imageView.setImageURI(imageReturned);

        imageView.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.chooseImageButton).setVisibility(View.GONE);

    }

    @Override
    public void onSuccess(byte[] bytes) {
        Toast.makeText(getActivity(), "Success getting the image from Firebase", Toast.LENGTH_LONG).show();
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bmp);
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(), "An error occurred while getting the image from Firebase", Toast.LENGTH_LONG).show();
    }
}
