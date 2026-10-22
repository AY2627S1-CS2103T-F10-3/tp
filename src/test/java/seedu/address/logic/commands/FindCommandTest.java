package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void execute_prefixedSearches_filtersStudentsWithoutChangingSavedData() throws Exception {
        Guardian guardian = new Guardian(new Name("Mary Tan"), new Phone("98765432"),
                new Email("mary@example.com"), new Relationship("Mother"), new Address("Main Street"));
        Person alice = new PersonBuilder().withName("Alice Tan").withPhone("91234567")
                .withGuardian(guardian).build();
        Person bob = new PersonBuilder().withName("Bob Tan").withPhone("82345678")
                .withGuardian(guardian).build();
        Person mary = new PersonBuilder().withName("Mary Lim").withPhone("98761234").build();
        AddressBook book = new AddressBook();
        book.setPersons(List.of(alice, bob, mary));
        Model searchModel = new ModelManager(book, new UserPrefs());
        AddressBook originalBook = new AddressBook(searchModel.getAddressBook());

        assertSearchResults(searchModel, "find n/LIC", List.of(alice));
        assertSearchResults(searchModel, "find p/9876", List.of(mary));
        assertSearchResults(searchModel, "find gn/ARY", List.of(alice, bob));
        assertSearchResults(searchModel, "find gp/9876", List.of(alice, bob));
        assertSearchResults(searchModel, "find gp/9876 n/lice Bob", List.of(alice, bob));
        assertSearchResults(searchModel, "find n/lice Bob p/9123 gn/ARY gp/9876", List.of(alice));
        assertSearchResults(searchModel, "find n/Mary gp/9876", List.of());
        assertSearchResults(searchModel, "find n/Alice gp/1111", List.of());
        assertSearchResults(searchModel, "find Alice Bob", List.of(alice, bob));
        assertSearchResults(searchModel, "find ge/MARY@EXAMPLE ga/main gr/MOTH", List.of(alice, bob));
        assertSearchResults(searchModel, "find ge/mary n/Bob", List.of(bob));
        assertSearchResults(searchModel, "find ga/missing", List.of());
        assertEquals(originalBook, searchModel.getAddressBook());
    }

    @Test
    public void execute_lessonSearches_returnsMatchingRosterWithoutChangingData() throws Exception {
        Lesson mondayMath = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30);
        Lesson tuesdayMath = new Lesson(Year.of(2026), Subject.MATH, Day.TUESDAY, Time.TEN, Time.ELEVEN, 40);
        Person alice = new PersonBuilder().withName("Alice Tan").withLessons(mondayMath, tuesdayMath).build();
        Person bob = new PersonBuilder().withName("Bob Tan").withLessons(tuesdayMath).build();
        Person carol = new PersonBuilder().withName("Carol Tan").withLessons().build();
        AddressBook book = new AddressBook();
        book.setPersons(List.of(alice, bob, carol));
        Model searchModel = new ModelManager(book, new UserPrefs());
        AddressBook originalBook = new AddressBook(searchModel.getAddressBook());

        assertSearchResults(searchModel, "find l/MaTh", List.of(alice, bob));
        assertSearchResults(searchModel, "find l/2026-math-monday-ten-eleven", List.of(alice));
        assertSearchResults(searchModel, "find l/2026-MATH-TUESDAY-TEN-ELEVEN", List.of(alice, bob));
        assertSearchResults(searchModel, "find l/science monday", List.of(alice));
        assertSearchResults(searchModel, "find l/math n/Bob", List.of(bob));
        assertSearchResults(searchModel, "find l/math n/Carol", List.of());
        assertSearchResults(searchModel, "find l/science", List.of());
        assertSearchResults(searchModel, "find l/2026-MATH-MONDAY-TEN-ELEVE", List.of());
        assertSearchResults(searchModel, "find l/026-MATH-MONDAY-TEN-ELEVEN", List.of());
        assertSearchResults(searchModel, "find l/2026-MATH-MONDAY-TEN-ELEVEN-30", List.of());
        assertEquals(originalBook, searchModel.getAddressBook());
    }

    @Test
    public void execute_unpaidSearch_filtersByBillingMonthAndOtherFields() throws Exception {
        Lesson lesson = new Lesson(Year.of(2026), Subject.MATH, Day.MONDAY, Time.TEN, Time.ELEVEN, 100);
        YearMonth month = YearMonth.of(2026, 10);
        LocalDate date = LocalDate.of(2026, 10, 1);
        Person unpaid = new PersonBuilder().withName("Alice Unpaid").withLessons(lesson).build();
        Person partial = new PersonBuilder().withName("Bob Partial").withLessons(lesson)
                .withPayments(new Payment(new Amount("30"), month, date)).build();
        Person paid = new PersonBuilder().withName("Carol Paid").withLessons(lesson)
                .withPayments(new Payment(new Amount("40"), month, date),
                        new Payment(new Amount("60"), month, date)).build();
        Person overpaid = new PersonBuilder().withName("Dan Overpaid").withLessons(lesson)
                .withPayments(new Payment(new Amount("120"), month, date)).build();
        Person otherMonth = new PersonBuilder().withName("Eve Other").withLessons(lesson)
                .withPayments(new Payment(new Amount("100"), month.minusMonths(1), date)).build();
        Person noFees = new PersonBuilder().withName("Frank No Fees").withLessons().withTags("unpaid").build();
        AddressBook book = new AddressBook();
        book.setPersons(List.of(unpaid, partial, paid, overpaid, otherMonth, noFees));
        Model searchModel = new ModelManager(book, new UserPrefs());
        AddressBook originalBook = new AddressBook(searchModel.getAddressBook());

        assertSearchResults(searchModel, "find unpaid/2026-10", List.of(unpaid, partial, otherMonth));
        assertSearchResults(searchModel, "find n/ALICE unpaid/2026-10", List.of(unpaid));
        assertSearchResults(searchModel, "find unpaid/2026-10 l/math n/bob", List.of(partial));
        assertSearchResults(searchModel, "find l/science unpaid/2026-10", List.of());
        assertSearchResults(searchModel, "find t/unpaid unpaid/2026-10", List.of());
        assertSearchResults(searchModel, "find unpaid/2027-10", List.of());
        assertSearchResults(searchModel, "find paid/2026-10", List.of(paid, overpaid));
        assertSearchResults(searchModel, "find paid/2026-09", List.of(otherMonth));
        assertSearchResults(searchModel, "find l/MATH paid/2026-10 n/CAROL", List.of(paid));
        assertSearchResults(searchModel, "find paid/2027-10", List.of());
        assertSearchResults(searchModel, "find unpaid/2026-10", List.of(unpaid, partial, otherMonth));
        for (String input : List.of("find unpaid/", "find unpaid/2026-13",
                "find unpaid/2026-10 unpaid/2026-11", "find paid/", "find paid/2026-13",
                "find paid/2026-10 paid/2026-11", "find paid/2026-10 unpaid/2026-10")) {
            assertThrows(ParseException.class, () -> new AddressBookParser().parseCommand(input).execute(searchModel));
            assertEquals(List.of(unpaid, partial, otherMonth), searchModel.getFilteredPersonList());
        }
        assertEquals(originalBook, searchModel.getAddressBook());
    }

    @Test
    public void execute_invalidSearch_preservesPreviousResultsAndSavedData() throws Exception {
        assertSearchResults(model, "find Kurz", List.of(CARL));
        AddressBook originalBook = new AddressBook(model.getAddressBook());
        AddressBookParser parser = new AddressBookParser();

        for (String input : List.of("find n/", "find n/Alice gp/", "find n/Alice n/Bob",
                "find n/Alice x/test", "find Alice gp/9876", "find l/", "find l/math l/science",
                "find e/", "find t/friend t/other")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input).execute(model), input);
            assertEquals(List.of(CARL), model.getFilteredPersonList(), input);
            assertEquals(originalBook, model.getAddressBook(), input);
        }
    }

    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }

    private void assertSearchResults(Model searchModel, String input, List<Person> expected) throws Exception {
        CommandResult result = new AddressBookParser().parseCommand(input).execute(searchModel);
        assertEquals(new CommandResult(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expected.size())), result, input);
        assertEquals(expected, searchModel.getFilteredPersonList(), input);
    }
}
