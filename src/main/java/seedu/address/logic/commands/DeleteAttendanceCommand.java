package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ATTENDANCE_INDEX;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.person.Person;

/**
 * Deletes one attendance record of a student identified using its displayed index.
 */
public class DeleteAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "deleteattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes an attendance record for the student at the index in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_ATTENDANCE_INDEX + "ATTENDANCE_INDEX (as shown by " + ViewAttendanceCommand.COMMAND_WORD + ")\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_ATTENDANCE_INDEX + "2";

    public static final String MESSAGE_SUCCESS = "Deleted attendance for %1$s: %2$s";
    public static final String MESSAGE_INVALID_ATTENDANCE_INDEX = "The attendance index provided is invalid.";

    private final Index index;
    private final Index attendanceIndex;

    /**
     * Creates a DeleteAttendanceCommand to delete the attendance at {@code attendanceIndex}
     * of the student at {@code index}.
     */
    public DeleteAttendanceCommand(Index index, Index attendanceIndex) {
        requireNonNull(index);
        requireNonNull(attendanceIndex);
        this.index = index;
        this.attendanceIndex = attendanceIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(index.getZeroBased());
        if (attendanceIndex.getZeroBased() >= student.getAttendance().size()) {
            throw new CommandException(MESSAGE_INVALID_ATTENDANCE_INDEX);
        }

        List<Attendance> updatedAttendance = new ArrayList<>(student.getAttendance());
        Attendance deletedAttendance = updatedAttendance.remove(attendanceIndex.getZeroBased());
        model.setPerson(student, student.withAttendance(updatedAttendance));

        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), deletedAttendance));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof DeleteAttendanceCommand otherCommand
                && index.equals(otherCommand.index) && attendanceIndex.equals(otherCommand.attendanceIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("attendanceIndex", attendanceIndex)
                .toString();
    }
}
