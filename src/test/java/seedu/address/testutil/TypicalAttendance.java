package seedu.address.testutil;

import java.time.LocalDate;
import java.time.Year;

import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

/**
 * Lesson sessions and attendance records shared by attendance tests.
 */
public class TypicalAttendance {
    public static final Lesson MATH_LESSON = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY,
            Time.TEN, Time.ELEVEN, 120);
    public static final Attendance ATTENDANCE_PRESENT = new Attendance(MATH_LESSON.getId(), MATH_LESSON.getUid(),
            LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
    public static final Attendance ATTENDANCE_ABSENT = new Attendance(MATH_LESSON.getId(), MATH_LESSON.getUid(),
            LocalDate.of(2026, 10, 26), AttendanceStatus.ABSENT);

    private TypicalAttendance() {}
}
