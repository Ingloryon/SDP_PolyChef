package ch.epfl.polychef.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EitherTest {

    @Test
    public void leftRightThrowsErrorIfNull() {
        assertThrows(IllegalArgumentException.class, () -> Either.left(null));
        assertThrows(IllegalArgumentException.class, () -> Either.right(null));
    }

    @Test
    public void testEqualityLeft() {
        Either<String, Integer> e1 = Either.left("test");
        Either<String, Integer> e2 = Either.left("test");
        Either<String, Integer> e3 = Either.left("test_not_the_same");
        assertTrue(e1.equals(e2));
        assertTrue(!e1.equals(e3));
        assertTrue(!e2.equals(e3));
    }

    @Test
    public void testEqualityRight() {
        Either<String, Integer> e1 = Either.right(1);
        Either<String, Integer> e2 = Either.right(1);
        Either<String, Integer> e3 = Either.right(2);
        assertTrue(e1.equals(e2));
        assertTrue(!e1.equals(e3));
        assertTrue(!e2.equals(e3));
    }

    @Test
    public void testEqualityNone() {
        Either<String, Integer> either = new Either<>();
        assertTrue( either.isNone() );
        Either<String, Integer> e1 = Either.none();
        Either<String, Integer> e2 = Either.none();
        assertTrue(e1.equals(e2));
    }

    @Test
    public void notEqualityForDifferentType() {
        Either<String, Integer> e1 = Either.left("test");
        Either<Integer, Integer> e2 = Either.left(1);
        assertTrue(!e1.equals(e2));
        Either<String, Integer> e3 = Either.right(1);
        assertTrue(!e1.equals(e3));
        Either<String, Integer> e4 = Either.none();
        assertTrue(!e1.equals(e4));
        assertTrue(!e2.equals(e3));
        assertTrue(!e2.equals(e4));
        assertTrue(!e3.equals(e4));
    }

    @Test
    public void toStringProduceRightString() {
        Either<String, Integer> e1 = Either.left("test");
        assertThat(e1.toString(), equalTo("Either: left value: test"));
        Either<Integer, Integer> e2 = Either.left(1);
        assertThat(e2.toString(), equalTo("Either: left value: 1"));
        Either<String, Integer> e3 = Either.right(1);
        assertThat(e3.toString(), equalTo("Either: right value: 1"));
        Either<String, Integer> e4 = Either.none();
        assertThat(e4.toString(), equalTo("Either: none"));
    }
}
