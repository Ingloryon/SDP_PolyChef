package ch.epfl.polychef.utils;

import org.junit.Assert;

import ch.epfl.polychef.CallHandler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Class for testing asynchronous call to the {@code CallHandler}.
 *
 * @param <T> the type of data returned by the call
 */
public class CallHandlerChecker<T> implements CallHandler<T> {

    private final T expected;
    private final boolean shouldBeSuccessful;

    private boolean wasCalled = false;

    /**
     * Constructor for checking that the {@code expected} values were called {@code onSuccess} or that {@code onFailure} was called.
     *
     * @param expected           the expected value to be called through {@code onSuccess}
     * @param shouldBeSuccessful whether the call should be successful ({@code onSuccess}) or not ({@code onFailure})
     */
    public CallHandlerChecker(T expected, boolean shouldBeSuccessful) {
        this.expected = expected;
        this.shouldBeSuccessful = shouldBeSuccessful;
    }

    @Override
    public void onSuccess(T data) {
        assertThat(data, equalTo(expected));
        Assert.assertTrue(shouldBeSuccessful);
        wasCalled = true;
    }

    @Override
    public void onFailure() {
        assertFalse(shouldBeSuccessful);
        wasCalled = true;
    }

    /**
     * Assert if one of {@code onSuccess} or {@code onFailure} was called.
     */
    public void assertWasCalled() {
        assertTrue(wasCalled);
    }
}
