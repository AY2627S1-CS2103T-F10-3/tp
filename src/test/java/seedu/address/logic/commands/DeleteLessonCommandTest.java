package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;

public class DeleteLessonCommandTest {
    @Test
    public void execute_existingLesson_deletesLesson() throws Exception {
        ModelManager model = new ModelManager();
        new AddLessonCommand(VALID_LESSON_MATH).execute(model);

        CommandResult result = new DeleteLessonCommand(VALID_LESSON_MATH.getId()).execute(model);

        assertEquals(String.format(DeleteLessonCommand.MESSAGE_SUCCESS, VALID_LESSON_MATH.getId()),
                result.getFeedbackToUser());
        assertEquals(0, model.getAddressBook().getLessonList().size());
    }

    @Test
    public void execute_missingLesson_throwsCommandException() {
        ModelManager model = new ModelManager();

        assertThrows(CommandException.class, DeleteLessonCommand.MESSAGE_LESSON_NOT_FOUND, () ->
                new DeleteLessonCommand(VALID_LESSON_MATH.getId()).execute(model));
    }

    @Test
    public void execute_nullLessonId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteLessonCommand(null));
    }
}
