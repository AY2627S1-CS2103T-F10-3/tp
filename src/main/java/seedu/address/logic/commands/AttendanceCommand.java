package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.person.Person;

/**
 * Records a student's attendance at a scheduled lesson session.
 */
public class AttendanceCommand extends Command {

    public static final String COMMAND_WORD = "attendance";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records attendance for the student identified by the index in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_LESSON + "LESSON_ID " + PREFIX_DATE + "DATE " + PREFIX_STATUS + "STATUS\n"
            + "DATE must be yyyy-MM-dd. STATUS must be present or absent.\n"
            + "Example: " + COMMAND_WORD
            + " 1 l/2026-MATH-MONDAY-TEN-ELEVEN d/2026-10-19 s/present";
    public static final String MESSAGE_SUCCESS = "Recorded attendance for %1$s: %2$s";
    public static final String MESSAGE_NOT_ENROLLED = "The student is not enrolled in the specified lesson. "
            + "Use the lesson ID without its cost.";
    public static final String MESSAGE_INVALID_SESSION_DATE = "The date must match the year and weekday of the "
            + "lesson schedule in effect on that date.";

    private final Index index;
    private final Attendance attendance;

    /**
     * Creates a command to record attendance for the student at the displayed index.
     */
    public AttendanceCommand(Index index, Attendance attendance) {
        this.index = requireNonNull(index);
        this.attendance = requireNonNull(attendance);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(index.getZeroBased());
        String lessonId = attendance.getLessonId();
        Lesson lesson = student.getLessons().stream()
                .filter(candidate -> candidate.getId().equals(lessonId)).findFirst()
                .or(() -> student.getLessons().stream()
                        .filter(candidate -> candidate.hasRevisionId(lessonId)).findFirst())
                .orElseThrow(() -> new CommandException(MESSAGE_NOT_ENROLLED));
        LessonRevision revision = lesson.getRevisionOn(attendance.getDate());
        if (!revision.isValidSessionDate(attendance.getDate())) {
            throw new CommandException(MESSAGE_INVALID_SESSION_DATE);
        }
        Attendance recorded = new Attendance(revision.getId(), lesson.getUid(), attendance.getDate(),
                attendance.getStatus());
        if (student.getAttendance().stream().anyMatch(recorded::isSameSession)) {
            throw new CommandException(Person.MESSAGE_DUPLICATE_ATTENDANCE);
        }

        List<Attendance> updatedAttendance = new ArrayList<>(student.getAttendance());
        updatedAttendance.add(recorded);
        model.setPerson(student, student.withAttendance(updatedAttendance));
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), recorded));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AttendanceCommand otherCommand
                && index.equals(otherCommand.index) && attendance.equals(otherCommand.attendance);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).add("attendance", attendance).toString();
    }
}
