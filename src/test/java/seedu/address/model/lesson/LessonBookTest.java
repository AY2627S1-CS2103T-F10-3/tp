package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.exceptions.LessonException;

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

    @Test
    public void addLesson_reusedSlot_keepsNewIdentityWithoutRemapping() {
        Lesson moved = VALID_LESSON_MATH.withRevision(new LessonRevision(VALID_LESSON_MATH.getYear(),
                VALID_LESSON_MATH.getSubject(), Day.TUESDAY, Time.TEN, Time.ELEVEN, 40,
                YearMonth.of(2026, 11)), YearMonth.of(2026, 10));
        Lesson newLesson = new Lesson(VALID_LESSON_MATH.getYear(), VALID_LESSON_MATH.getSubject(), Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30);
        LessonBook catalog = new LessonBook();
        catalog.addLesson(moved);

        assertTrue(catalog.addLesson(newLesson));
        assertNotEquals(moved.getUid(), newLesson.getUid());
        assertSame(newLesson, catalog.getLessonByCurrentId(newLesson.getId()));
    }

    @Test
    public void addLesson_duplicateUid_rejectsInsteadOfRemapping() {
        LessonBook catalog = new LessonBook();
        catalog.addLesson(VALID_LESSON_MATH);
        Lesson differentSlot = new Lesson(VALID_LESSON_MATH.getYear(), VALID_LESSON_MATH.getSubject(), Day.TUESDAY,
                Time.TEN, Time.ELEVEN, 30).withUid(VALID_LESSON_MATH.getUid());

        assertThrows(LessonException.class, () -> catalog.addLesson(differentSlot));
        assertEquals(1, catalog.getLessons().size());
        assertEquals(VALID_LESSON_MATH.getUid(), differentSlot.getUid());
    }

}
