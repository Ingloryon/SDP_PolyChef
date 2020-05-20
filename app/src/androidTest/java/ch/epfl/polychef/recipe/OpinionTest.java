package ch.epfl.polychef.recipe;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class OpinionTest {

    @Test
    public void canCreateSimpleOpinion() {
        Opinion withoutComment = new Opinion(2);
        Opinion withComment = new Opinion(4, "This is a comment");
        assertThat(withoutComment.getRate(), is(2));
        assertThat(withoutComment.getComment(), isEmptyOrNullString());
        assertThat(withComment.getRate(), is(4));
        assertThat(withComment.getComment(), is("This is a comment"));
    }

    @Test
    public void equalOpinionsAreEqual() {
        Opinion withoutComment = new Opinion(2);
        assertEquals(withoutComment, withoutComment);
        Opinion otherWithoutComment = new Opinion(2);
        assertEquals(withoutComment, otherWithoutComment);
        Opinion withComment = new Opinion(4, "This is a comment");
        assertEquals(withComment, withComment);
        Opinion otherWithComment = new Opinion(4, "This is a comment");
        assertEquals(withComment, otherWithComment);
        assertNotEquals(withComment, withoutComment);
        Opinion otherDifferentWithComment = new Opinion(4, "This is an other comment");
        assertNotEquals(withComment, otherDifferentWithComment);
    }
}
