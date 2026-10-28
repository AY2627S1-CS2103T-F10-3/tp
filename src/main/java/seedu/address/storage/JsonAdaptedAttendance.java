package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;

/**
 * Jackson-friendly attendance record. Lesson IDs are retained even after unenrolment.
 */
class JsonAdaptedAttendance {

    public static final String MESSAGE_MISSING_FIELD = "Attendance's %s field is missing!";
    public static final String MESSAGE_INVALID_DATE = "Attendance date must be a valid date in yyyy-MM-dd form.";

    private final String lessonId;
    private final String lessonUid;
    private final String date;
    private final String status;

    /**
     * Creates an adapted attendance record from saved fields.
     */
    @JsonCreator
    public JsonAdaptedAttendance(@JsonProperty("lessonId") String lessonId,
            @JsonProperty("lessonUid") String lessonUid, @JsonProperty("date") String date,
            @JsonProperty("status") String status) {
        this.lessonId = lessonId;
        this.lessonUid = lessonUid;
        this.date = date;
        this.status = status;
    }

    /**
     * Creates an adapted attendance record without a stable lesson ID, as saved by older versions.
     */
    public JsonAdaptedAttendance(String lessonId, String date, String status) {
        this(lessonId, null, date, status);
    }

    /**
     * Converts a model attendance record to its storage representation.
     */
    public JsonAdaptedAttendance(Attendance source) {
        lessonId = source.getLessonId();
        lessonUid = source.getLessonUid().orElse(null);
        date = source.getDate().toString();
        status = source.getStatus().name();
    }

    /**
     * Validates saved fields and reconstructs the attendance record.
     * Historical dates need not match the current lesson schedule, which may have been edited.
     * Session year and weekday are validated when recording new attendance instead.
     */
    public Attendance toModelType() throws IllegalValueException {
        if (lessonId == null || date == null || status == null) {
            String field = lessonId == null ? "lessonId" : date == null ? "date" : "status";
            throw new IllegalValueException(String.format(MESSAGE_MISSING_FIELD, field));
        }
        if (lessonId.isBlank()) {
            throw new IllegalValueException(Attendance.MESSAGE_LESSON_CONSTRAINTS);
        }
        LocalDate modelDate;
        try {
            modelDate = LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(MESSAGE_INVALID_DATE, exception);
        }
        AttendanceStatus modelStatus;
        try {
            modelStatus = AttendanceStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(Attendance.MESSAGE_STATUS_CONSTRAINTS, exception);
        }
        return new Attendance(lessonId, lessonUid, modelDate, modelStatus);
    }
}
