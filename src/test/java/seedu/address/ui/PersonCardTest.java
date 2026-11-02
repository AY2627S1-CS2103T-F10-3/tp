package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Year;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

public class PersonCardTest {
    private static final YearMonth CURRENT_MONTH = YearMonth.of(2026, 10);

    @Test
    public void formatLesson_pendingRevisionShowsCurrentRevisionId() {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY,
                Time.TEN, Time.ELEVEN, 30).withRevision(new LessonRevision(Year.of(2026), Subject.MATH,
                        Day.TUESDAY, Time.TEN, Time.ELEVEN, 40, YearMonth.of(2026, 11)), CURRENT_MONTH);

        assertEquals("2026-MATH-MONDAY-TEN-ELEVEN",
                PersonCard.formatLesson(lesson, CURRENT_MONTH));
    }
}
