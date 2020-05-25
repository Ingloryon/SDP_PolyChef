package ch.epfl.polychef.utils;

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
    
}