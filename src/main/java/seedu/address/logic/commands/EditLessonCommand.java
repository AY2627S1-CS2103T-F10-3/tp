package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Time;


/** Represents a command that edits selected fields of an existing lesson definition. */
public class EditLessonCommand extends Command {
    public static final String COMMAND_WORD = "editlesson";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits supplied lesson fields. Parameters: CURRENT_ID "
            + "[d/DAY] [st/START_TIME] [et/END_TIME] [c/COST]";
    public static final String MESSAGE_SUCCESS = "Lesson updated: %1$s";
    public static final String MESSAGE_IMMUTABLE_FIELDS = "Year and subject cannot be changed.";
    public static final String MESSAGE_NOT_FOUND = "Lesson with CURRENT_ID '%s' was not found. "
            + "CURRENT_ID must contain these fields in order: YEAR-SUBJECT-DAY-START_TIME-END_TIME "
            + "(year, subject, day, start time and end time; no cost). "
            + "for example, 2026-MATH-MONDAY-TEN-ELEVEN. "
            + "Command format: editlesson CURRENT_ID [d/DAY] [st/START_TIME] [et/END_TIME] "
            + "[c/COST] (at least one field required).";
    public static final String MESSAGE_DUPLICATE = "A lesson with the edited ID already exists.";
    public static final String MESSAGE_INVALID_TIME_RANGE = "Start time must be before end time.";
    private final String currentId;
    private final Day day;
    private final Time startTime;
    private final Time endTime;
    private final Integer cost;

    /**
     * Creates an {@code EditLessonCommand} with the specified lesson identifier and updated fields.
     *
     * @param currentId identifier of the lesson to edit
     * @param day updated day, or {@code null} to retain the existing value
     * @param startTime updated start time, or {@code null} to retain the existing value
     * @param endTime updated end time, or {@code null} to retain the existing value
     * @param cost updated cost, or {@code null} to retain the existing value
     */
    public EditLessonCommand(String currentId, Day day, Time startTime,
            Time endTime, Integer cost) {
        this.currentId = requireNonNull(currentId);
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.getAddressBook().getLessonList().stream().noneMatch(lesson -> lesson.getId().equals(currentId))) {
            throw new CommandException(String.format(MESSAGE_NOT_FOUND, currentId));
        }
        Lesson currentLesson = model.getAddressBook().getLessonList().stream()
                .filter(lesson -> lesson.getId().equals(currentId)).findFirst().get();
        Time editedStartTime = startTime == null ? currentLesson.getStartTime() : startTime;
        Time editedEndTime = endTime == null ? currentLesson.getEndTime() : endTime;
        if (!Lesson.isValidTimeRange(editedStartTime, editedEndTime)) {
            throw new CommandException(MESSAGE_INVALID_TIME_RANGE);
        }
        Lesson editedLesson = new Lesson(currentLesson.getYear(),
                currentLesson.getSubject(),
                day == null ? currentLesson.getDay() : day,
                editedStartTime,
                editedEndTime,
                cost == null ? currentLesson.getCost() : cost);
        if (!model.editLesson(currentId, editedLesson)) {
            throw new CommandException(MESSAGE_DUPLICATE);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedLesson));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EditLessonCommand otherCommand)) {
            return false;
        }
        return currentId.equals(otherCommand.currentId)
                && Objects.equals(day, otherCommand.day)
                && Objects.equals(startTime, otherCommand.startTime)
                && Objects.equals(endTime, otherCommand.endTime)
                && Objects.equals(cost, otherCommand.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentId, day, startTime, endTime, cost);
    }

}
