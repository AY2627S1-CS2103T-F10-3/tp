package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;
import static seedu.address.testutil.TypicalPayments.PAYMENT_OCTOBER_PARTIAL;
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

public class ViewPaymentsCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_withPayments_listsNumberedPayments() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student)
                .withPayments(PAYMENT_OCTOBER_PARTIAL, PAYMENT_NOVEMBER).build());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        String expectedMessage = String.format(ViewPaymentsCommand.MESSAGE_HEADER, student.getName())
                + "\n1. " + PAYMENT_OCTOBER_PARTIAL + "\n2. " + PAYMENT_NOVEMBER;
        assertCommandSuccess(new ViewPaymentsCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noPayments_showsEmptyMessage() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        String expectedMessage = String.format(ViewPaymentsCommand.MESSAGE_HEADER, student.getName())
                + "\n" + ViewPaymentsCommand.MESSAGE_NO_PAYMENTS;
        assertCommandSuccess(new ViewPaymentsCommand(INDEX_FIRST_PERSON), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new ViewPaymentsCommand(outOfBoundIndex), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewPaymentsCommand command = new ViewPaymentsCommand(INDEX_FIRST_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ViewPaymentsCommand(INDEX_FIRST_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertFalse(command.equals(new ViewPaymentsCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        String expected = ViewPaymentsCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, new ViewPaymentsCommand(INDEX_FIRST_PERSON).toString());
    }
}
