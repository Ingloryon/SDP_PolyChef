package ch.epfl.polychef.utils;

import org.junit.Assert;

import java.util.List;

import ch.epfl.polychef.CallNotifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertFalse;

/**
 * Class for testing asynchronous call to the {@code CallNotifier}.
 *
 * @param <T> the type of the {@code CallNotifier}
 */
public class CallNotifierChecker<T> implements CallNotifier<T> {

    private final List<T> expected;
    private final boolean shouldBeSuccessful;

    private int numberOfCall = 0;

    /**
     * Constructor for checking that the {@code List<T>} expected values were notified or that {@code onFailure} was called.
     *
     * @param expected           the list of expected value to be called through {@code notify}
     * @param shouldBeSuccessful whether the call should be successful ({@code notify}) or not ({@code onFailure})
     */
    public CallNotifierChecker(List<T> expected, boolean shouldBeSuccessful) {
        this.expected = expected;
        this.shouldBeSuccessful = shouldBeSuccessful;
    }

    @Override
    public void notify(T data) {
        assertThat(data, isIn(expected));
        expected.remove(data);
        Assert.assertTrue(shouldBeSuccessful);
        ++numberOfCall;
    }

    @Override
    public void onFailure() {
        assertFalse(shouldBeSuccessful);
        ++numberOfCall;
    }

    /**
     * Assert if one of {@code notify} or {@code onFailure} was called.
     *
     * @param nTimes the number of times the function must have been called
     */
    public void assertWasCalled(int times) {
        assertThat(numberOfCall, equalTo(times));
    }
}
