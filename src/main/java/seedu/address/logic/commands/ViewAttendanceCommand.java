package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

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
    public static final String MESSAGE_SCHEDULE_CHANGED = "   Schedule changed to %1$s";

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
            describeLaterChange(model, attendance.get(i)).ifPresent(change ->
                    feedback.append("\n").append(String.format(MESSAGE_SCHEDULE_CHANGED, change)));
        }
        return new CommandResult(feedback.toString());
    }

    /**
     * Returns how the lesson of {@code record} is held now, if it has changed since the session was recorded.
     * Changes to the weekly slot are shown as eg. {@code TUESDAY TEN-ELEVEN}; other changes show the full lesson ID.
     */
    private static Optional<String> describeLaterChange(Model model, Attendance record) {
        Optional<Lesson> lesson = record.getLessonUid().flatMap(uid -> model.getAddressBook().getLessonList()
                .stream().filter(candidate -> candidate.getUid().equals(uid)).findFirst());
        if (lesson.isEmpty() || lesson.get().getId().equals(record.getLessonId())) {
            return Optional.empty();
        }
        LessonRevision original = lesson.get().getRevisionOn(record.getDate());
        LessonRevision latest = lesson.get().getLatestRevision();
        boolean onlySlotChanged = original.getYear().equals(latest.getYear())
                && original.getSubject() == latest.getSubject();
        return Optional.of(onlySlotChanged ? latest.getScheduleDescription() : latest.getId());
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
