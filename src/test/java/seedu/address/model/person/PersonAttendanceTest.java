package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_ABSENT;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.testutil.PersonBuilder;

public class PersonAttendanceTest {

    @Test
    public void attendance_defensivelyCopiedAndUnmodifiable() {
        List<Attendance> records = new ArrayList<>(List.of(ATTENDANCE_PRESENT));
        Person original = new PersonBuilder().build();
        Person updated = original.withAttendance(records);
        records.clear();
        assertEquals(List.of(ATTENDANCE_PRESENT), updated.getAttendance());
        assertTrue(original.getAttendance().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> updated.getAttendance().clear());
        assertNotEquals(original, updated);
        assertTrue(original.isSamePerson(updated));
    }

    @Test
    public void withAttendance_duplicateSessionOrNull_rejected() {
        Person person = new PersonBuilder().build();
        Attendance conflicting = new Attendance(ATTENDANCE_PRESENT.getLessonId(), ATTENDANCE_PRESENT.getDate(),
                AttendanceStatus.ABSENT);
        assertThrows(IllegalArgumentException.class, () ->
                person.withAttendance(List.of(ATTENDANCE_PRESENT, conflicting)));
        assertThrows(IllegalArgumentException.class, () ->
                person.withAttendance(List.of(ATTENDANCE_PRESENT, ATTENDANCE_PRESENT)));
        assertThrows(NullPointerException.class, () -> person.withAttendance(null));
        List<Attendance> nullRecord = new ArrayList<>();
        nullRecord.add(null);
        assertThrows(NullPointerException.class, () -> person.withAttendance(nullRecord));
    }

    @Test
    public void copies_preserveBothHistories() {
        Person person = new PersonBuilder().withPayments(PAYMENT_NOVEMBER)
                .withAttendance(ATTENDANCE_PRESENT, ATTENDANCE_ABSENT).build();
        assertEquals(List.of(PAYMENT_NOVEMBER), person.withAttendance(List.of()).getPayments());
        assertEquals(person.getAttendance(), person.withPayments(List.of()).getAttendance());
        assertEquals(person, new PersonBuilder(person).build());
        assertEquals(person.hashCode(), new PersonBuilder(person).build().hashCode());
    }
}
