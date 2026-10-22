package seedu.address.model.attendance;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable attendance record for one student, lesson and date.
 * Stores the lesson ID so history survives changes to the student's enrolment.
 */
public class Attendance {

    public static final String MESSAGE_LESSON_CONSTRAINTS = "Lesson ID must not be blank.";
    public static final String MESSAGE_STATUS_CONSTRAINTS = "Attendance status must be present or absent.";

    private final String lessonId;
    private final LocalDate date;
    private final AttendanceStatus status;

    /**
     * Creates an attendance record with the given lesson ID, session date and status.
     */
    public Attendance(String lessonId, LocalDate date, AttendanceStatus status) {
        requireAllNonNull(lessonId, date, status);
        checkArgument(!lessonId.isBlank(), MESSAGE_LESSON_CONSTRAINTS);
        this.lessonId = lessonId;
        this.date = date;
        this.status = status;
    }

    public String getLessonId() {
        return lessonId;
    }

    public LocalDate getDate() {
        return date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    /**
     * Returns whether another record refers to the same lesson session, regardless of status.
     */
    public boolean isSameSession(Attendance other) {
        return other != null && lessonId.equals(other.lessonId) && date.equals(other.date);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Attendance otherAttendance
                && isSameSession(otherAttendance) && status == otherAttendance.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId, date, status);
    }

    @Override
    public String toString() {
        return String.format("%s on %s: %s", lessonId, date, status);
    }
}
