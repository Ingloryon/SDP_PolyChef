package ch.epfl.polychef.images;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.epfl.polychef.R;
import ch.epfl.polychef.image.ImageHandler;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.EntryPage;


public class ImageHandlerTest {

    @Rule
    public IntentsTestRule<EntryPage> intentsTestRule = new IntentsTestRule<>(EntryPage.class);

    ImageView imageView;

    @Mock
    UploadTask task;

    ImageHandler realImageHandler;
    ImageHandler fakeImageHandler;

    @Before
    public void setImagesAndMock() {
        MockitoAnnotations.initMocks(this);
        when(task.addOnSuccessListener(any())).thenReturn(task);
        realImageHandler = new ImageHandler(intentsTestRule.getActivity());
        fakeImageHandler = new FakeImageHandler(intentsTestRule.getActivity());
    }

    @Test
    public void initWithNullFails() {
        assertThrows(IllegalArgumentException.class, () -> new ImageHandler(null));
    }

    @Test
    public void getIntentDoesNotReturnNull() {
        assertNotNull(realImageHandler.getCameraIntent());
        assertNotNull(realImageHandler.getGalleryIntent());
    }

    @Test
    public void tryToUploadNullReturnNull() {
        assertThrows(IllegalArgumentException.class, () -> realImageHandler.prepareImageAndUpload(null, "image_name", null, null));
        assertThrows(IllegalArgumentException.class, () -> realImageHandler.uploadFromUri(null, "image_name", null, null));
    }

    @Test
    public void canUploadFromImageView() {
        imageView = new ImageView(intentsTestRule.getActivity());
        imageView.setImageResource(R.drawable.meatballs);
        fakeImageHandler.prepareImageAndUpload(imageView, "image_name", null, null);
    }

    @Test
    public void canUploadFromUri() {
        Uri fakeUri = Uri.parse("android.resource://ch.epfl.polychef/" + R.drawable.frenchtoast);
        fakeImageHandler.uploadFromUri(fakeUri, "image_name", null, null);
    }

    @Test
    public void handleActivityReturnNullIfNotGoodCode() {
        assertNull(realImageHandler.handleActivityResult(ImageHandler.REQUEST_IMAGE_CAPTURE, RESULT_CANCELED, null));
        assertNull(realImageHandler.handleActivityResult(ImageHandler.REQUEST_IMAGE_FROM_GALLERY, RESULT_CANCELED, null));
        assertNull(realImageHandler.handleActivityResult(ImageHandler.REQUEST_IMAGE_FROM_GALLERY, RESULT_OK, null));
        assertNull(realImageHandler.handleActivityResult(18, RESULT_OK, null));
        assertNull(realImageHandler.handleActivityResult(ImageHandler.REQUEST_IMAGE_CAPTURE, RESULT_OK, null));

    }

    @Test
    public void handleActivityReturnGoodThing() {
        Uri fakeUri = Uri.parse("test");
        Intent fakeIntent = new Intent().setData(fakeUri);
        assertThat(realImageHandler.handleActivityResult(ImageHandler.REQUEST_IMAGE_FROM_GALLERY, RESULT_OK, fakeIntent), equalTo(fakeUri));
    }

    @Test
    public void canGetImageStorage() {
        assertNotNull(realImageHandler.getImageStorage());
    }

    private class FakeImageStorage extends ImageStorage {
        @Override
        public UploadTask upload(byte[] image, String imageName, String user, String recipeUId) {
            return task;
        }
    }

    private class FakeImageHandler extends ImageHandler {
        public FakeImageHandler(Context context) {
            super(context);
        }

        @Override
        public ImageStorage getImageStorage() {
            return new FakeImageStorage();
        }
    }
}
