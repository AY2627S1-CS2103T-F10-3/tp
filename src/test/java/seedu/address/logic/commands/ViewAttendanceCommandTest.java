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

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class ViewAttendanceCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_withAttendance_listsNumberedAttendance() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student)
                .withAttendance(ATTENDANCE_PRESENT, ATTENDANCE_ABSENT).build());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        String expectedMessage = String.format(ViewAttendanceCommand.MESSAGE_HEADER, student.getName())
                + "\n1. " + ATTENDANCE_PRESENT + "\n2. " + ATTENDANCE_ABSENT;
        assertCommandSuccess(new ViewAttendanceCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_backdatedAttendance_sortsDatesAndPreservesSameDateOrder() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Attendance sameDate = new Attendance("2026-ENGLISH-MONDAY-TEN-ELEVEN",
                ATTENDANCE_PRESENT.getDate(), ATTENDANCE_PRESENT.getStatus());
        model.setPerson(student, new PersonBuilder(student)
                .withAttendance(ATTENDANCE_ABSENT, ATTENDANCE_PRESENT, sameDate).build());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        String expectedMessage = String.format(ViewAttendanceCommand.MESSAGE_HEADER, student.getName())
                + "\n1. " + ATTENDANCE_PRESENT + "\n2. " + sameDate + "\n3. " + ATTENDANCE_ABSENT;
        assertCommandSuccess(new ViewAttendanceCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);
        assertEquals(List.of(ATTENDANCE_ABSENT, ATTENDANCE_PRESENT, sameDate),
                model.getFilteredPersonList().getFirst().getAttendance());
    }

    @Test
    public void execute_noAttendance_showsEmptyMessage() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        String expectedMessage = String.format(ViewAttendanceCommand.MESSAGE_HEADER, student.getName())
                + "\n" + ViewAttendanceCommand.MESSAGE_NO_ATTENDANCE;
        assertCommandSuccess(new ViewAttendanceCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new ViewAttendanceCommand(outOfBoundIndex), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewAttendanceCommand command = new ViewAttendanceCommand(INDEX_FIRST_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewAttendanceCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertFalse(command.equals(new ViewAttendanceCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        String expected = ViewAttendanceCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, new ViewAttendanceCommand(INDEX_FIRST_PERSON).toString());
    }
}
