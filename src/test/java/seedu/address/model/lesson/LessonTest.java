package seedu.address.model.lesson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.exceptions.LessonException;

public class LessonTest {
    @Test
    public void isValidCost() {
        assertTrue(Lesson.isValidCost(0));
        assertTrue(Lesson.isValidCost(30));
        assertFalse(Lesson.isValidCost(-1));
    }

    @Test
    public void isValidTimeRange_requiresStartBeforeEnd() {
        assertTrue(Lesson.isValidTimeRange(Time.TEN, Time.ELEVEN));
        assertFalse(Lesson.isValidTimeRange(Time.ELEVEN, Time.TEN));
        assertFalse(Lesson.isValidTimeRange(Time.TEN, Time.TEN));
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

    @Test
    public void getCostFor_usesRevisionInEffectThatMonth() {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30)
                .withRevision(new LessonRevision(Year.of(2026), Subject.MATH, Day.TUESDAY, Time.TEN, Time.ELEVEN,
                        40, YearMonth.of(2026, 11)));

        assertEquals(0, lesson.getCostFor(YearMonth.of(2025, 12)));
        assertEquals(30, lesson.getCostFor(YearMonth.of(2026, 10)));
        assertEquals(40, lesson.getCostFor(YearMonth.of(2026, 11)));
        assertEquals(0, lesson.getCostFor(YearMonth.of(2027, 1)));
        assertEquals(Day.MONDAY, lesson.getRevisionOn(LocalDate.of(2026, 10, 31)).getDay());
        assertEquals(Day.TUESDAY, lesson.getRevisionOn(LocalDate.of(2026, 11, 1)).getDay());
        assertEquals("2026-MATH-TUESDAY-TEN-ELEVEN", lesson.getId());
        assertEquals("2026-MATH-MONDAY-TEN-ELEVEN", lesson.getUid());
        assertTrue(lesson.hasRevisionId("2026-MATH-MONDAY-TEN-ELEVEN"));
    }

    @Test
    public void withRevision_earlierThanLatest_throwsLessonException() {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30)
                .withRevision(new LessonRevision(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN,
                        40, YearMonth.of(2026, 11)));

        assertThrows(LessonException.class, () -> lesson.withRevision(new LessonRevision(Year.of(2026),
                Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 50, YearMonth.of(2026, 10))));
    }

    @Test
    public void constructor_duplicateEffectiveMonth_throwsLessonException() {
        LessonRevision revision = new LessonRevision(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN,
                Time.ELEVEN, 30, YearMonth.of(2026, 1));
        assertThrows(LessonException.class, () -> new Lesson("uid", List.of(revision, revision)));
        assertThrows(LessonException.class, () -> new Lesson("uid", List.of()));
    }
}
