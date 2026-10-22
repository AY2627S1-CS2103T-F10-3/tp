package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Lesson;

/**
 * Jackson-friendly attendance record. Lesson IDs are retained even after unenrolment.
 */
class JsonAdaptedAttendance {

    public static final String MESSAGE_MISSING_FIELD = "Attendance's %s field is missing!";
    public static final String MESSAGE_INVALID_DATE = "Attendance date must be a valid date in yyyy-MM-dd form.";
    public static final String MESSAGE_INVALID_SESSION_DATE =
            "Attendance date must match the lesson's year and weekday.";

    private final String lessonId;
    private final String date;
    private final String status;

    /**
     * Creates an adapted attendance record from saved fields.
     */
    @JsonCreator
    public JsonAdaptedAttendance(@JsonProperty("lessonId") String lessonId, @JsonProperty("date") String date,
            @JsonProperty("status") String status) {
        this.lessonId = lessonId;
        this.date = date;
        this.status = status;
    }

    /**
     * Converts a model attendance record to its storage representation.
     */
    public JsonAdaptedAttendance(Attendance source) {
        lessonId = source.getLessonId();
        date = source.getDate().toString();
        status = source.getStatus().name();
    }

    /** Returns the saved lesson identifier. */
    public String getLessonId() {
        return lessonId;
    }

    /**
     * Validates saved fields and reconstructs the attendance record.
     */
    public Attendance toModelType() throws IllegalValueException {
        return toModelType(null);
    }

    /**
     * Validates saved fields and reconstructs an attendance record, checking its date against {@code lesson}
     * when the lesson definition is available. A null lesson is allowed for historical records whose lesson
     * definition is no longer available.
     */
    public Attendance toModelType(Lesson lesson) throws IllegalValueException {
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
        if (lesson != null && !lesson.isValidSessionDate(modelDate)) {
            throw new IllegalValueException(MESSAGE_INVALID_SESSION_DATE);
        }
        return new Attendance(lessonId, modelDate, modelStatus);
    }
}
