package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_ABSENT;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class DeleteAttendanceCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private Person addAttendanceToFirstPerson() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person studentWithAttendance = new PersonBuilder(student)
                .withAttendance(ATTENDANCE_PRESENT, ATTENDANCE_ABSENT).build();
        model.setPerson(student, studentWithAttendance);
        return studentWithAttendance;
    }

    @Test
    public void execute_validIndexes_success() {
        Person student = addAttendanceToFirstPerson();
        DeleteAttendanceCommand command = new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);

        Person updatedStudent = new PersonBuilder(student).withAttendance(ATTENDANCE_PRESENT).build();
        String expectedMessage = String.format(DeleteAttendanceCommand.MESSAGE_SUCCESS, student.getName(),
                ATTENDANCE_ABSENT);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, updatedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidAttendanceIndex_throwsCommandException() {
        addAttendanceToFirstPerson();
        DeleteAttendanceCommand command = new DeleteAttendanceCommand(INDEX_FIRST_PERSON, Index.fromOneBased(3));

        assertCommandFailure(command, model, DeleteAttendanceCommand.MESSAGE_INVALID_ATTENDANCE_INDEX);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteAttendanceCommand command = new DeleteAttendanceCommand(outOfBoundIndex, INDEX_FIRST_PERSON);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteAttendanceCommand command = new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertFalse(command.equals(new DeleteAttendanceCommand(INDEX_SECOND_PERSON, INDEX_SECOND_PERSON)));
        assertFalse(command.equals(new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON)));
    }

    @Test
    public void toStringMethod() {
        DeleteAttendanceCommand command = new DeleteAttendanceCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        String expected = DeleteAttendanceCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", attendanceIndex=" + INDEX_SECOND_PERSON + "}";
        assertEquals(expected, command.toString());
    }
}
