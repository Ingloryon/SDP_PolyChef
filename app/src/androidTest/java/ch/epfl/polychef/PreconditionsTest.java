package ch.epfl.polychef;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreconditionsTest {

    @Test
    public void checkArgumentOnlyRejectsFalseBooleans(){
        Preconditions.checkArgument(true, "The conditions is true");
        Preconditions.checkArgument(true);
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false));
        assertThrows(IllegalArgumentException.class, () -> Preconditions.checkArgument(false, "The conditions fails"));
    }

    @Test
    public void checkIndexOnlyRejectsOutOfBounds(){
        Preconditions.checkIndex(2, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> Preconditions.checkIndex(14, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> Preconditions.checkIndex(-5, 50));
    }
}