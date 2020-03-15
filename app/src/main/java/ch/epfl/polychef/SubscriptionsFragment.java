package ch.epfl.polychef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionsFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public SubscriptionsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscriptions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getView().findViewById(R.id.chooseImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            }
        });

        getView().findViewById(R.id.uploadButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.uploadButton).setVisibility(View.GONE);
                getView().findViewById(R.id.chooseImageButton).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.imageUploaded).setVisibility(View.GONE);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            Uri selectedImage = data.getData();
            ImageView imageView = getView().findViewById(R.id.imageUploaded);
            imageView.setImageURI(selectedImage);

            getView().findViewById(R.id.imageUploaded).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.chooseImageButton).setVisibility(View.GONE);
        }
    }
}
