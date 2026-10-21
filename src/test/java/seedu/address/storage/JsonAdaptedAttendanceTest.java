package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalAttendance.MATH_LESSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedAttendanceTest {

    @Test
    public void toModelType_validRecord_roundTrips() throws Exception {
        assertEquals(ATTENDANCE_PRESENT, new JsonAdaptedAttendance(ATTENDANCE_PRESENT).toModelType());
        assertEquals(ATTENDANCE_PRESENT,
                new JsonAdaptedAttendance(MATH_LESSON.getId(), "2026-10-19", "present").toModelType());
    }

    @Test
    public void toModelType_invalidFields_throwsIllegalValueException() {
        for (String lesson : new String[]{null, "", " "}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedAttendance(lesson, "2026-10-19", "PRESENT").toModelType());
        }
        for (String date : new String[]{null, "", "2026-02-29", "2026-11-31", "19-10-2026"}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedAttendance(MATH_LESSON.getId(), date, "PRESENT").toModelType());
        }
        for (String status : new String[]{null, "", "LATE"}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedAttendance(MATH_LESSON.getId(), "2026-10-19", status).toModelType());
        }
    }
}
