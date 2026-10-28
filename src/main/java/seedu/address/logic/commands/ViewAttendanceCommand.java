package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.person.Person;

/**
 * Lists the recorded attendance of a student identified using its displayed index.
 */
public class ViewAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "viewattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists the attendance of the student identified by the index number used in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_HEADER = "Attendance for %1$s (earliest date first):";
    public static final String MESSAGE_NO_ATTENDANCE = "No attendance recorded.";

    private final Index index;

    /**
     * Creates a ViewAttendanceCommand to list the attendance of the student at {@code index}.
     */
    public ViewAttendanceCommand(Index index) {
        this.index = requireNonNull(index);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(index.getZeroBased());
        StringBuilder feedback = new StringBuilder(String.format(MESSAGE_HEADER, student.getName()));
        List<Attendance> attendance = student.getAttendanceInDateOrder();
        if (attendance.isEmpty()) {
            feedback.append("\n").append(MESSAGE_NO_ATTENDANCE);
        }
        for (int i = 0; i < attendance.size(); i++) {
            feedback.append("\n").append(i + 1).append(". ").append(attendance.get(i));
        }
        return new CommandResult(feedback.toString());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ViewAttendanceCommand otherCommand
                && index.equals(otherCommand.index);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("index", index).toString();
    }
}
