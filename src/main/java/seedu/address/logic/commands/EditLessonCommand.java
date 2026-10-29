package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.YearMonth;
import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Time;
import seedu.address.model.lesson.exceptions.LessonException;


/**
 * Edits selected fields of an existing lesson by adding a new revision that takes effect from a billing month.
 * Earlier revisions are kept, so attendance and fees before that month are unaffected.
 */
public class EditLessonCommand extends Command {
    public static final String COMMAND_WORD = "editlesson";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits supplied lesson fields from a billing month "
            + "onwards (default: next month). Parameters: CURRENT_ID "
            + "[d/DAY] [st/START_TIME] [et/END_TIME] [c/COST] [from/YYYY-MM]";
    public static final String MESSAGE_SUCCESS = "Lesson updated: %1$s (effective from %2$s)";
    public static final String MESSAGE_NOT_FOUND = "Lesson with CURRENT_ID '%s' was not found. "
            + "CURRENT_ID must contain these fields in order: YEAR-SUBJECT-DAY-START_TIME-END_TIME "
            + "(year, subject, day, start time and end time; no cost), "
            + "for example, 2026-MATH-MONDAY-TEN-ELEVEN. "
            + "Command format: editlesson CURRENT_ID [d/DAY] [st/START_TIME] "
            + "[et/END_TIME] [c/COST] [from/YYYY-MM] (at least one lesson field required).";
    public static final String MESSAGE_IMMUTABLE_FIELDS = "Year and subject cannot be changed.";
    public static final String MESSAGE_DUPLICATE = "A lesson with the edited ID already exists.";
    public static final String MESSAGE_INVALID_TIME_RANGE = "Start time must be before end time.";
    private final String currentId;
    private final Day day;
    private final Time startTime;
    private final Time endTime;
    private final Integer cost;
    private final YearMonth effectiveFrom;
    private final Clock clock;

    /**
     * Creates an {@code EditLessonCommand} that takes effect from next month.
     *
     * @see #EditLessonCommand(String, Day, Time, Time, Integer, YearMonth)
     */
    public EditLessonCommand(String currentId, Day day, Time startTime,
            Time endTime, Integer cost) {
        this(currentId, day, startTime, endTime, cost, null);
    }

    /**
     * Creates an {@code EditLessonCommand} with the specified lesson identifier and updated fields.
     *
     * @param currentId identifier of the lesson to edit
     * @param day updated day, or {@code null} to retain the existing value
     * @param startTime updated start time, or {@code null} to retain the existing value
     * @param endTime updated end time, or {@code null} to retain the existing value
     * @param cost updated cost, or {@code null} to retain the existing value
     * @param effectiveFrom first billing month the change applies to, or {@code null} for next month
     */
    public EditLessonCommand(String currentId, Day day, Time startTime,
            Time endTime, Integer cost, YearMonth effectiveFrom) {
        this(currentId, day, startTime, endTime, cost, effectiveFrom, Clock.systemDefaultZone());
    }

    /**
     * Creates an {@code EditLessonCommand} that uses {@code clock} to work out the default billing month.
     */
    EditLessonCommand(String currentId, Day day, Time startTime,
            Time endTime, Integer cost, YearMonth effectiveFrom, Clock clock) {
        this.currentId = requireNonNull(currentId);
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.effectiveFrom = effectiveFrom;
        this.clock = requireNonNull(clock);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Lesson currentLesson = model.getAddressBook().getLessonList().stream()
                .filter(lesson -> lesson.getId().equals(currentId)).findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_NOT_FOUND, currentId)));
        Time editedStartTime = startTime == null ? currentLesson.getStartTime() : startTime;
        Time editedEndTime = endTime == null ? currentLesson.getEndTime() : endTime;
        if (!Lesson.isValidTimeRange(editedStartTime, editedEndTime)) {
            throw new CommandException(MESSAGE_INVALID_TIME_RANGE);
        }
        YearMonth month = resolveEffectiveMonth(currentLesson);
        Lesson editedLesson;
        try {
            editedLesson = currentLesson.withRevision(new LessonRevision(
                    currentLesson.getYear(),
                    currentLesson.getSubject(),
                    day == null ? currentLesson.getDay() : day,
                    editedStartTime,
                    editedEndTime,
                    cost == null ? currentLesson.getCost() : cost,
                    month), YearMonth.now(clock));
        } catch (LessonException e) {
            throw new CommandException(e.getMessage(), e);
        }
        if (!model.editLesson(currentId, editedLesson)) {
            throw new CommandException(MESSAGE_DUPLICATE);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedLesson, month));
    }

    /**
     * Returns the explicit billing month if given; otherwise next month, or the month of the lesson's latest
     * pending change if that is later (so that a pending change is corrected rather than rejected).
     */
    private YearMonth resolveEffectiveMonth(Lesson lesson) {
        if (effectiveFrom != null) {
            return effectiveFrom;
        }
        YearMonth nextMonth = YearMonth.now(clock).plusMonths(1);
        YearMonth latest = lesson.getLatestRevision().getEffectiveFrom();
        return latest.isAfter(nextMonth) ? latest : nextMonth;
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
                && Objects.equals(cost, otherCommand.cost)
                && Objects.equals(effectiveFrom, otherCommand.effectiveFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentId, day, startTime, endTime, cost, effectiveFrom);
    }

}
