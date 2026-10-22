package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Year;

import org.junit.jupiter.api.Test;

public class LessonTest {
    @Test
    public void isValidCost() {
        assertTrue(Lesson.isValidCost(0));
        assertTrue(Lesson.isValidCost(30));
        assertFalse(Lesson.isValidCost(-1));
    }

    @Test
    public void getters_returnLessonDetails() {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30);

        assertEquals(Year.of(2026), lesson.getYear());
        assertEquals(Subject.MATH, lesson.getSubject());
        assertEquals(Day.MONDAY, lesson.getDay());
        assertEquals(Time.TEN, lesson.getStartTime());
        assertEquals(Time.ELEVEN, lesson.getEndTime());
        assertEquals(30, lesson.getCost());
    }

    @Test
    public void isValidSessionDate_matchesYearAndWeekday() {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30);

        assertTrue(lesson.isValidSessionDate(LocalDate.of(2026, 10, 19)));
        assertFalse(lesson.isValidSessionDate(LocalDate.of(2026, 10, 20)));
        assertFalse(lesson.isValidSessionDate(LocalDate.of(2027, 10, 18)));
    }

    @Test
    public void toString_returnsInputFormat() {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30);

        assertEquals("2026-MATH-MONDAY-TEN-ELEVEN-30", lesson.toString());
    }

}
