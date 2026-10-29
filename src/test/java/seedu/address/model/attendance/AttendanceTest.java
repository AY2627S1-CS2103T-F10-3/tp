package seedu.address.model.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_ABSENT;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class AttendanceTest {

    @Test
    public void constructor_invalidFields_throwsException() {
        LocalDate date = ATTENDANCE_PRESENT.getDate();
        assertThrows(NullPointerException.class, () -> new Attendance(null, date, AttendanceStatus.PRESENT));
        assertThrows(NullPointerException.class, () -> new Attendance("lesson", null, AttendanceStatus.PRESENT));
        assertThrows(NullPointerException.class, () -> new Attendance("lesson", date, null));
        assertThrows(IllegalArgumentException.class, () -> new Attendance(" ", date, AttendanceStatus.PRESENT));
    }

    @Test
    public void isSameSession_ignoresStatus() {
        Attendance absent = new Attendance(ATTENDANCE_PRESENT.getLessonId(), ATTENDANCE_PRESENT.getDate(),
                AttendanceStatus.ABSENT);
        assertTrue(ATTENDANCE_PRESENT.isSameSession(absent));
        assertNotEquals(ATTENDANCE_PRESENT, absent);
        assertFalse(ATTENDANCE_PRESENT.isSameSession(ATTENDANCE_ABSENT));
        assertFalse(ATTENDANCE_PRESENT.isSameSession(new Attendance("other", ATTENDANCE_PRESENT.getDate(),
                AttendanceStatus.PRESENT)));
        assertFalse(ATTENDANCE_PRESENT.isSameSession(null));
    }

    @Test
    public void equals_sameValues_sameHashCode() {
        Attendance copy = new Attendance(ATTENDANCE_PRESENT.getLessonId(),
                ATTENDANCE_PRESENT.getLessonUid().orElseThrow(), ATTENDANCE_PRESENT.getDate(),
                AttendanceStatus.PRESENT);
        assertEquals(ATTENDANCE_PRESENT, copy);
        assertNotEquals(ATTENDANCE_PRESENT, ATTENDANCE_PRESENT.withLessonUid(null));
        assertEquals(ATTENDANCE_PRESENT.hashCode(), copy.hashCode());
        assertNotEquals(ATTENDANCE_PRESENT, null);
        assertNotEquals(ATTENDANCE_PRESENT, "attendance");
        assertEquals("2026-MATH-MONDAY-TEN-ELEVEN on 2026-10-19: PRESENT", copy.toString());
    }

    @Test
    public void isSameSession_linkedRecords_comparedByStableLessonId() {
        LocalDate date = ATTENDANCE_PRESENT.getDate();
        Attendance oldId = new Attendance("2026-MATH-MONDAY-TEN-ELEVEN", "lesson-1", date, AttendanceStatus.PRESENT);
        Attendance newId = new Attendance("2026-MATH-TUESDAY-TEN-ELEVEN", "lesson-1", date, AttendanceStatus.ABSENT);
        Attendance otherLesson = new Attendance("2026-MATH-MONDAY-TEN-ELEVEN", "lesson-2", date,
                AttendanceStatus.PRESENT);
        assertTrue(oldId.isSameSession(newId));
        assertFalse(oldId.isSameSession(otherLesson));
    }
}
