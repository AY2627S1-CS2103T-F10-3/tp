package seedu.address.model.attendance.exceptions;

/**
 * Signals that changing a lesson ID would merge two attendance records for the same student and date.
 */
public class AttendanceConflictException extends RuntimeException {
    /** Creates an exception describing how to resolve the conflicting history. */
    public AttendanceConflictException() {
        super("Editing this lesson would create duplicate attendance records for the same date. "
                + "Use viewattendance and deleteattendance to resolve the conflicting records first.");
    }
}
