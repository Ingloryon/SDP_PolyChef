package ch.epfl.polychef.images;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.utils.CallHandlerChecker;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageStorageTest {

    @Mock
    FirebaseStorage firebaseStorage;

    @Mock
    StorageReference storageReference;

    ImageStorage imageStorageSpy;

    @Mock
    StorageReference imageReferenceSuccess;

    @Mock
    StorageReference imageReferenceFail;

    @Mock
    UploadTask uploadTask;

    @Mock
    public Task<byte []> successTask;

    @Mock
    public Task<byte []> failTask;

    @Before
    public void initMockFirebaseStorage() {
        MockitoAnnotations.initMocks(this);
        successTask = Tasks.forResult(new byte[] {1, 2, 3});
        failTask = Tasks.forException(new Exception());
        imageStorageSpy = Mockito.spy(ImageStorage.getInstance());
        when(imageStorageSpy.getStorage()).thenReturn(firebaseStorage);
        when(firebaseStorage.getReference(any(String.class))).thenReturn(storageReference);
        when(firebaseStorage.getReference()).thenReturn(storageReference);
        when(storageReference.putBytes(any(byte[].class), any(StorageMetadata.class))).thenReturn(uploadTask);
        when(storageReference.child("images/work.jpg")).thenReturn(imageReferenceSuccess);
        when(storageReference.child("images/fail.jpg")).thenReturn(imageReferenceFail);
        when(imageReferenceSuccess.getBytes(anyLong())).thenReturn(successTask);
        when(imageReferenceFail.getBytes(anyLong())).thenReturn(failTask);
    }

    @Test
    public void nullOrEmptyImageThrowsError() {
        assertThrows(IllegalArgumentException.class, () -> imageStorageSpy.upload(null,"image_name", null, null));
        assertThrows(IllegalArgumentException.class, () -> imageStorageSpy.upload(new byte[] {},"image_name", null, null));
        assertThrows(IllegalArgumentException.class, () -> imageStorageSpy.getImage(null, new CallHandlerChecker<byte []>(null, true)));
        assertThrows(IllegalArgumentException.class, () -> imageStorageSpy.getImage("test.png", null));
    }

    @Test
    public void canGetStorage() {
        assertNotNull(ImageStorage.getInstance().getStorage());
    }

    @Test
    public void canUploadImages() {
        UploadTask ut = imageStorageSpy.upload(new byte[] {1}, "image_name", null, null);
    }

    @Test
    public synchronized void canDownloadImage() throws InterruptedException {
        CallHandlerChecker<byte []> f1 = new CallHandlerChecker<>(new byte[] {1, 2, 3}, true);
        imageStorageSpy.getImage("work", f1);
        CallHandlerChecker<byte []> f2 = new CallHandlerChecker<>(null, false);
        imageStorageSpy.getImage("fail", f2);
        wait(1000);
        f1.assertWasCalled();
        f2.assertWasCalled();
    }
}
