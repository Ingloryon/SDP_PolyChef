package ch.epfl.polychef;

import org.junit.Test;

public class PreconditionsTest {

    @Test
    public void checkArgumentRejectsFalseBooleans(){
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false));
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false, "The conditions fails"));
    }

    @Test
    public void checkIndexRejectsOutofBounds(){
        assertThrows(IndexOutOfBoundsException.class, () -> Preconditions.checkIndex(14, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> Preconditions.checkIndex(-5, 50));
    }
}