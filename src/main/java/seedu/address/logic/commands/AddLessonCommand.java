package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;

/** Adds a lesson definition independently of any student. */
public class AddLessonCommand extends Command {
    public static final String COMMAND_WORD = "addlesson";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a lesson. Parameters: "
            + "y/YEAR s/SUBJECT d/DAY st/START_TIME et/END_TIME c/COST\n"
            + "where the start and end time are in 24h format from TEN to TWENTY \n"
            + "Example: " + COMMAND_WORD + " y/2026 s/MATH d/MONDAY st/TEN et/ELEVEN c/30";
    public static final String MESSAGE_SUCCESS = "New lesson added: %1$s";
    public static final String MESSAGE_DUPLICATE_LESSON = "This lesson already exists.";
    private final Lesson toAdd;

    public AddLessonCommand(Lesson lesson) {
        toAdd = requireNonNull(lesson);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasLesson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_LESSON);
        }
        model.addLesson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AddLessonCommand command && toAdd.equals(command.toAdd);
    }
}
