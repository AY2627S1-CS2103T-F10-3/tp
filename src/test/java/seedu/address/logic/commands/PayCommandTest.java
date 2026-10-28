package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPayments.NOVEMBER_2026;
import static seedu.address.testutil.TypicalPayments.OCTOBER_2026;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;
import static seedu.address.testutil.TypicalPayments.PAYMENT_OCTOBER_PARTIAL;
import static seedu.address.testutil.TypicalPayments.PAYMENT_OCTOBER_REST;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PayCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person studentWithLesson = new PersonBuilder(student).withLessons(VALID_LESSON_MATH)
                .withPayments(PAYMENT_OCTOBER_PARTIAL).build();
        model.setPerson(student, studentWithLesson);

        PayCommand payCommand = new PayCommand(INDEX_FIRST_PERSON, PAYMENT_OCTOBER_REST);
        Person paidStudent = new PersonBuilder(studentWithLesson)
                .withPayments(PAYMENT_OCTOBER_PARTIAL, PAYMENT_OCTOBER_REST).build();
        String expectedMessage = String.format(PayCommand.MESSAGE_SUCCESS, paidStudent.getName(),
                PAYMENT_OCTOBER_REST, OCTOBER_2026, 30, 30, 0);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(studentWithLesson, paidStudent);

        assertCommandSuccess(payCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        Person original = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person student = new PersonBuilder(original).withLessons(VALID_LESSON_MATH).build();
        model.setPerson(original, student);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Payment payment = new Payment(new Amount("25"), NOVEMBER_2026, LocalDate.of(2026, 11, 2));
        PayCommand payCommand = new PayCommand(INDEX_FIRST_PERSON, payment);
        Person paidStudent = new PersonBuilder(student).withPayments(payment).build();
        String expectedMessage = String.format(PayCommand.MESSAGE_SUCCESS, paidStudent.getName(),
                payment, NOVEMBER_2026, 30, 25, 5);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);
        expectedModel.setPerson(student, paidStudent);

        assertCommandSuccess(payCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noLessons_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person studentWithoutLessons = new PersonBuilder(student).withLessons().build();
        model.setPerson(student, studentWithoutLessons);

        PayCommand payCommand = new PayCommand(INDEX_FIRST_PERSON, PAYMENT_NOVEMBER);
        String expectedMessage = String.format(PayCommand.MESSAGE_NO_FEES_DUE, student.getName(), 2026,
                NOVEMBER_2026);
        assertCommandFailure(payCommand, model, expectedMessage);
    }

    @Test
    public void execute_monthOutsideLessonYear_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student).withLessons(VALID_LESSON_MATH).build());

        YearMonth september2025 = YearMonth.of(2025, 9);
        Payment payment = new Payment(new Amount("1"), september2025, LocalDate.of(2026, 10, 27));
        String expectedMessage = String.format(PayCommand.MESSAGE_NO_FEES_DUE, student.getName(), 2025,
                september2025);
        assertCommandFailure(new PayCommand(INDEX_FIRST_PERSON, payment), model, expectedMessage);
    }

    @Test
    public void execute_amountExceedsFeesDue_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student).withLessons(VALID_LESSON_MATH).build());

        Payment payment = new Payment(new Amount("70"), OCTOBER_2026, LocalDate.of(2026, 10, 24));
        String expectedMessage = String.format(PayCommand.MESSAGE_OVERPAYMENT, payment.getAmount(), 30,
                OCTOBER_2026, 30, 0);
        assertCommandFailure(new PayCommand(INDEX_FIRST_PERSON, payment), model, expectedMessage);
    }

    @Test
    public void execute_amountExceedsOutstanding_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student).withLessons(VALID_LESSON_MATH)
                .withPayments(PAYMENT_OCTOBER_PARTIAL).build());

        Payment payment = new Payment(new Amount("21"), OCTOBER_2026, LocalDate.of(2026, 10, 24));
        String expectedMessage = String.format(PayCommand.MESSAGE_OVERPAYMENT, payment.getAmount(), 20,
                OCTOBER_2026, 30, 10);
        assertCommandFailure(new PayCommand(INDEX_FIRST_PERSON, payment), model, expectedMessage);
    }

    @Test
    public void execute_monthAlreadyPaid_throwsCommandException() {
        Person student = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        model.setPerson(student, new PersonBuilder(student).withLessons(VALID_LESSON_MATH)
                .withPayments(PAYMENT_OCTOBER_PARTIAL, PAYMENT_OCTOBER_REST).build());

        String expectedMessage = String.format(PayCommand.MESSAGE_OVERPAYMENT,
                PAYMENT_OCTOBER_PARTIAL.getAmount(), 0, OCTOBER_2026, 30, 30);
        assertCommandFailure(new PayCommand(INDEX_FIRST_PERSON, PAYMENT_OCTOBER_PARTIAL), model, expectedMessage);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        PayCommand payCommand = new PayCommand(outOfBoundIndex, PAYMENT_NOVEMBER);

        assertCommandFailure(payCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        PayCommand payCommand = new PayCommand(INDEX_FIRST_PERSON, PAYMENT_NOVEMBER);

        assertTrue(payCommand.equals(payCommand));
        assertTrue(payCommand.equals(new PayCommand(INDEX_FIRST_PERSON, PAYMENT_NOVEMBER)));
        assertFalse(payCommand.equals(null));
        assertFalse(payCommand.equals(new ClearCommand()));
        assertFalse(payCommand.equals(new PayCommand(INDEX_SECOND_PERSON, PAYMENT_NOVEMBER)));
        assertFalse(payCommand.equals(new PayCommand(INDEX_FIRST_PERSON, PAYMENT_OCTOBER_REST)));
    }

    @Test
    public void toStringMethod() {
        PayCommand payCommand = new PayCommand(INDEX_FIRST_PERSON, PAYMENT_NOVEMBER);
        String expected = PayCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", payment=" + PAYMENT_NOVEMBER + "}";
        assertEquals(expected, payCommand.toString());
    }
}
