package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import java.time.Year;
import java.util.Objects;

/** Edits all fields of an existing lesson definition. */
public class EditLessonCommand extends Command {
    public static final String COMMAND_WORD = "editlesson";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits supplied lesson fields. Parameters: CURRENT_ID "
            + "[y/YEAR] [s/SUBJECT] [d/DAY] [st/START_TIME] [et/END_TIME] [c/COST]";
    public static final String MESSAGE_SUCCESS = "Lesson updated: %1$s";
    public static final String MESSAGE_NOT_FOUND = "Lesson with CURRENT_ID '%s' was not found. "
            + "CURRENT_ID must contain these fields in order: YEAR-SUBJECT-DAY-START_TIME-END_TIME "
            + "(year, subject, day, start time and end time; no cost), "
            + "for example, 2026-MATH-MONDAY-TEN-ELEVEN. "
            + "Command format: editlesson CURRENT_ID [y/YEAR] [s/SUBJECT] [d/DAY] [st/START_TIME] "
            + "[et/END_TIME] [c/COST] (at least one field required).";
    public static final String MESSAGE_DUPLICATE = "A lesson with the edited ID already exists.";
    private final String currentId;
    private final Year year;
    private final Subject subject;
    private final Day day;
    private final Time startTime;
    private final Time endTime;
    private final Integer cost;

    public EditLessonCommand(String currentId, Year year, Subject subject, Day day, Time startTime,
            Time endTime, Integer cost) {
        this.currentId = requireNonNull(currentId);
        this.year = year;
        this.subject = subject;
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
        Lesson editedLesson = new Lesson(year == null ? currentLesson.getYear() : year,
                subject == null ? currentLesson.getSubject() : subject,
                day == null ? currentLesson.getDay() : day,
                startTime == null ? currentLesson.getStartTime() : startTime,
                endTime == null ? currentLesson.getEndTime() : endTime,
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
                && Objects.equals(year, otherCommand.year)
                && Objects.equals(subject, otherCommand.subject)
                && Objects.equals(day, otherCommand.day)
                && Objects.equals(startTime, otherCommand.startTime)
                && Objects.equals(endTime, otherCommand.endTime)
                && Objects.equals(cost, otherCommand.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentId, year, subject, day, startTime, endTime, cost);
    }

}
