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

public class DeletePaymentCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private Person addPaymentsToFirstPerson() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person studentWithPayments = new PersonBuilder(student)
                .withPayments(PAYMENT_OCTOBER_PARTIAL, PAYMENT_NOVEMBER).build();
        model.setPerson(student, studentWithPayments);
        return studentWithPayments;
    }

    @Test
    public void execute_validIndexes_success() {
        Person student = addPaymentsToFirstPerson();
        DeletePaymentCommand command = new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);

        Person updatedStudent = new PersonBuilder(student).withPayments(PAYMENT_OCTOBER_PARTIAL).build();
        String expectedMessage = String.format(DeletePaymentCommand.MESSAGE_SUCCESS, student.getName(),
                PAYMENT_NOVEMBER);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, updatedStudent);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPaymentIndex_throwsCommandException() {
        addPaymentsToFirstPerson();
        DeletePaymentCommand command = new DeletePaymentCommand(INDEX_FIRST_PERSON, Index.fromOneBased(3));

        assertCommandFailure(command, model, DeletePaymentCommand.MESSAGE_INVALID_PAYMENT_INDEX);
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeletePaymentCommand command = new DeletePaymentCommand(outOfBoundIndex, INDEX_FIRST_PERSON);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeletePaymentCommand command = new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON)));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertFalse(command.equals(new DeletePaymentCommand(INDEX_SECOND_PERSON, INDEX_SECOND_PERSON)));
        assertFalse(command.equals(new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON)));
    }

    @Test
    public void toStringMethod() {
        DeletePaymentCommand command = new DeletePaymentCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        String expected = DeletePaymentCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", paymentIndex=" + INDEX_SECOND_PERSON + "}";
        assertEquals(expected, command.toString());
    }
}
