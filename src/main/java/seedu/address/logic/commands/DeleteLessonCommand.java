package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/** Removes a lesson definition and unenrols it from all students. */
public class DeleteLessonCommand extends Command {
    public static final String COMMAND_WORD = "deletelesson";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes a lesson by its ID. Parameters: LESSON_ID\n"
            + "Example: " + COMMAND_WORD + " 2026-MATH-MONDAY-TEN-ELEVEN";
    public static final String MESSAGE_SUCCESS = "Lesson removed: %1$s";
    public static final String MESSAGE_LESSON_NOT_FOUND = "The lesson does not exist.";

    private final String lessonId;

    public DeleteLessonCommand(String lessonId) {
        this.lessonId = requireNonNull(lessonId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.deleteLesson(lessonId)) {
            throw new CommandException(MESSAGE_LESSON_NOT_FOUND);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, lessonId));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof DeleteLessonCommand command && lessonId.equals(command.lessonId);
    }
}
