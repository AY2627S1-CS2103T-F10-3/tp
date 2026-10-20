package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;

import org.junit.jupiter.api.Test;

public class LessonBookTest {
    @Test
    public void addLesson_duplicateLesson_keepsOneDefinition() {
        LessonBook lessonBook = new LessonBook();

        assertTrue(lessonBook.addLesson(VALID_LESSON_MATH));
        assertFalse(lessonBook.addLesson(new Lesson(VALID_LESSON_MATH.getYear(), VALID_LESSON_MATH.getSubject(),
                VALID_LESSON_MATH.getDay(), VALID_LESSON_MATH.getStartTime(), VALID_LESSON_MATH.getEndTime(),
                VALID_LESSON_MATH.getCost())));
        assertEquals(1, lessonBook.getLessons().size());
    }

    @Test
    public void hasLesson_unknownLesson_returnsFalse() {
        LessonBook lessonBook = new LessonBook();

        assertFalse(lessonBook.hasLesson(VALID_LESSON_MATH));
    }
}
