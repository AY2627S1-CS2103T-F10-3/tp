package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;

public class AddLessonCommandTest {
    @Test
    public void execute_validLesson_addsLesson() throws Exception {
        ModelManager model = new ModelManager();
        CommandResult result = new AddLessonCommand(VALID_LESSON_MATH).execute(model);

        assertEquals(String.format(AddLessonCommand.MESSAGE_SUCCESS, VALID_LESSON_MATH), result.getFeedbackToUser());
        assertEquals(1, model.getAddressBook().getLessonList().size());
        assertEquals(VALID_LESSON_MATH, model.getAddressBook().getLessonList().get(0));
    }

    @Test
    public void execute_duplicateLesson_throwsCommandException() throws Exception {
        ModelManager model = new ModelManager();
        new AddLessonCommand(VALID_LESSON_MATH).execute(model);

        assertThrows(CommandException.class, AddLessonCommand.MESSAGE_DUPLICATE_LESSON,
                () -> new AddLessonCommand(VALID_LESSON_MATH).execute(model));
    }

    @Test
    public void constructor_nullLesson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddLessonCommand(null));
    }
}
