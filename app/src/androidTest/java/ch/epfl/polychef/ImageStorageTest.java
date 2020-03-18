package ch.epfl.polychef;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ch.epfl.polychef.image.ImageStorage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageStorageTest {

    @Mock
    FirebaseStorage firebaseStorage;

    @Mock
    StorageReference storageReference;

    @Mock
    StorageReference imageReferenceSuccess;

    @Mock
    StorageReference imageReferenceFail;

    @Mock
    public Task<byte []> successTask;

    @Mock
    public Task<byte []> FailTask;

    @Before
    public void initMockFirebaseStorage() {
        MockitoAnnotations.initMocks(this);
        successTask = Tasks.forResult(new byte[] {1, 2, 3});
        FailTask = Tasks.forException(new Exception());
        when(firebaseStorage.getReference(any(String.class))).thenReturn(storageReference);
        when(firebaseStorage.getReference()).thenReturn(storageReference);
        when(storageReference.putBytes(any(byte[].class))).thenReturn(mock(UploadTask.class));
        when(storageReference.child("images/work.png")).thenReturn(imageReferenceSuccess);
        when(storageReference.child("images/fail.png")).thenReturn(imageReferenceFail);
        when(imageReferenceSuccess.getBytes(anyLong())).thenReturn(successTask);
        when(imageReferenceFail.getBytes(anyLong())).thenReturn(FailTask);
    }

    @Test
    public void nullOrEmptyImageThrowsError() {
        ImageStorage imageStorage = new ImageStorageMock();
        assertThrows(IllegalArgumentException.class, () -> imageStorage.upload(null));
        assertThrows(IllegalArgumentException.class, () -> imageStorage.upload(new byte[] {}));
        assertThrows(IllegalArgumentException.class, () -> imageStorage.getImage(null, new FakeCallHandler(null, true)));
        assertThrows(IllegalArgumentException.class, () -> imageStorage.getImage("test.png", null));
    }

    @Test
    public void canGetStorage() {
        assertNotNull(new ImageStorage().getStorage());
    }

    @Test
    public void canUploadImages() {
        ImageStorage imageStorage = new ImageStorageMock();
        UploadTask ut = imageStorage.upload(new byte[] {1});
        assertThrows(NullPointerException.class, () -> ut.getResult());
    }

    @Test
    public synchronized void canDownloadImage() throws InterruptedException {
        ImageStorage imageStorage = new ImageStorageMock();
        FakeCallHandler f1 = new FakeCallHandler(new byte[] {1, 2, 3}, true);
        imageStorage.getImage("work.png", f1);
        FakeCallHandler f2 = new FakeCallHandler(null, false);
        imageStorage.getImage("fail.png", f2);
        wait(1000);
        assertTrue(f1.wasCalled);
        assertTrue(f2.wasCalled);
    }

    private class ImageStorageMock extends ImageStorage {
        @Override
        public FirebaseStorage getStorage() {
            return firebaseStorage;
        }
    }

    private class FakeCallHandler implements CallHandler {

        private final byte[] expected;
        private final boolean shouldBeSuccessful;

        private boolean wasCalled = false;

        public FakeCallHandler(byte[] expected, boolean shouldBeSuccessful) {
            this.expected = expected;
            this.shouldBeSuccessful = shouldBeSuccessful;
        }

        @Override
        public void onSuccess(byte[] bytes) {
            assertThat(bytes, equalTo(expected));
            assertTrue(shouldBeSuccessful);
            wasCalled = true;
        }

        @Override
        public void onFailure() {
            assertFalse(shouldBeSuccessful);
            wasCalled = true;
        }
    }
}
