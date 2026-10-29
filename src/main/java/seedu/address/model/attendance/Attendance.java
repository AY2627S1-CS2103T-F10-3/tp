package seedu.address.model.attendance;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable attendance record for one student, lesson and date.
 * Stores the ID of the lesson revision in effect when the session was held, so the original timing is kept even
 * after the lesson is edited, and the lesson's stable internal ID, so the record stays linked to the lesson.
 */
public class Attendance {

    public static final String MESSAGE_LESSON_CONSTRAINTS = "Lesson ID must not be blank.";
    public static final String MESSAGE_STATUS_CONSTRAINTS = "Attendance status must be present or absent.";

    private final String lessonId;
    private final String lessonUid;
    private final LocalDate date;
    private final AttendanceStatus status;

    /**
     * Creates an attendance record that is not yet linked to a lesson's stable ID.
     */
    public Attendance(String lessonId, LocalDate date, AttendanceStatus status) {
        this(lessonId, null, date, status);
    }

    /**
     * Creates an attendance record with the given lesson revision ID, stable lesson ID (nullable), session date
     * and status.
     */
    public Attendance(String lessonId, String lessonUid, LocalDate date, AttendanceStatus status) {
        requireAllNonNull(lessonId, date, status);
        checkArgument(!lessonId.isBlank(), MESSAGE_LESSON_CONSTRAINTS);
        this.lessonId = lessonId;
        this.lessonUid = lessonUid == null || lessonUid.isBlank() ? null : lessonUid;
        this.date = date;
        this.status = status;
    }

    /** Returns the lesson ID as it was when the session was held. */
    public String getLessonId() {
        return lessonId;
    }

    /** Returns the stable internal ID of the lesson, if this record is linked to one. */
    public Optional<String> getLessonUid() {
        return Optional.ofNullable(lessonUid);
    }

    /** Returns a copy of this record linked to the lesson with stable ID {@code uid}. */
    public Attendance withLessonUid(String uid) {
        return new Attendance(lessonId, uid, date, status);
    }

    public LocalDate getDate() {
        return date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    /**
     * Returns true if both records are for the same lesson and date. Records linked to stable lesson IDs are
     * compared by those IDs, so a session cannot be recorded twice under the old and new lesson IDs.
     */
    public boolean isSameSession(Attendance other) {
        if (other == null || !date.equals(other.date)) {
            return false;
        }
        if (lessonUid != null && other.lessonUid != null) {
            return lessonUid.equals(other.lessonUid);
        }
        return lessonId.equals(other.lessonId);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof Attendance otherAttendance
                && lessonId.equals(otherAttendance.lessonId) && Objects.equals(lessonUid, otherAttendance.lessonUid)
                && date.equals(otherAttendance.date) && status == otherAttendance.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonId, lessonUid, date, status);
    }

    @Override
    public String toString() {
        return String.format("%s on %s: %s", lessonId, date, status);
    }
}
