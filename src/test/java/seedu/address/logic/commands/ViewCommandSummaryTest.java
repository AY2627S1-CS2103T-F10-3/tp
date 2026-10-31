package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_ENGLISH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalGuardians.MARY;
import static seedu.address.testutil.TypicalPayments.PAYMENT_OCTOBER_PARTIAL;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the optional summary views of {@link ViewCommand}.
 */
public class ViewCommandSummaryTest {

    @Test
    public void execute_summary_countsAllSavedContactsAndPreservesFilter() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().withName("Alice").withEmail("shared@example.com")
                .withAddress("Shared address").withPhone("12345678").withTags("friends", "team")
                .withLessons(VALID_LESSON_MATH).build());
        book.addPerson(new PersonBuilder().withName("Bob").withEmail("shared@example.com")
                .withAddress("Shared address").withPhone("12345678").withTags("friends")
                .withLessons(VALID_LESSON_MATH).build());
        Model model = new ModelManager(book, new UserPrefs());
        model.updateFilteredPersonList(person -> person.getName().toString().equals("Alice"));

        assertSummary(model, "email", "shared@example.com : 2");
        assertSummary(model, "address", "Shared address : 2");
        assertSummary(model, "phone", "12345678 : 2");
        assertSummary(model, "name", "Alice : 1\nBob : 1");
        assertSummary(model, "tag", "friends : 2\nteam : 1");
        assertSummary(model, "lesson", formatLesson(VALID_LESSON_MATH) + ": 2");
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(book, model.getAddressBook());
    }

    @Test
    public void execute_emptyAddressBook_showsNoValues() {
        Model model = new ModelManager();
        assertSummary(model, "email", "No values found.");
    }

    @Test
    public void execute_lessonSummary_countsDistinctLessonsAndSkipsStudentsWithoutLessons() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().withName("Alice")
                .withLessons(VALID_LESSON_MATH, VALID_LESSON_ENGLISH).build());
        book.addPerson(new PersonBuilder().withName("Bob").withLessons(VALID_LESSON_MATH).build());
        book.addPerson(new PersonBuilder().withName("Charlie").build());
        Model model = new ModelManager(book, new UserPrefs());

        assertSummary(model, "lesson", formatLesson(VALID_LESSON_ENGLISH) + ": 1\n"
                + formatLesson(VALID_LESSON_MATH) + ": 2");
    }

    @Test
    public void execute_lessonSummary_noLessons() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Charlie").build());

        assertSummary(model, "lesson", "No values found.");
    }

    @Test
    public void execute_newFieldSummaries_countsGuardiansPaymentsAndAttendance() {
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().withName("Bob").withGuardian(MARY)
                .withPayments(PAYMENT_OCTOBER_PARTIAL, PAYMENT_OCTOBER_PARTIAL)
                .withAttendance(ATTENDANCE_PRESENT).build());
        book.addPerson(new PersonBuilder().withGuardian(MARY).withPayments(PAYMENT_OCTOBER_PARTIAL)
                .withAttendance(ATTENDANCE_PRESENT).build());
        Model model = new ModelManager(book, new UserPrefs());

        assertSummary(model, "guardian", "Name: Mary Tan | Phone: 91234567 | Email: mary@example.com | "
                + "Relationship: Mother | Address: 123 Example Road : 2");
        assertSummary(model, "payment", PAYMENT_OCTOBER_PARTIAL + " : 2");
        assertSummary(model, "attendance", ATTENDANCE_PRESENT + " : 2");
    }

    @Test
    public void execute_noSelector_listsAllContacts() {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().build());
        model.updateFilteredPersonList(person -> false);
        assertEquals(new CommandResult(ViewCommand.MESSAGE_SUCCESS), new ViewCommand("").execute(model));
        assertEquals(1, model.getFilteredPersonList().size());
    }

    private void assertSummary(Model model, String field, String rows) {
        assertEquals(new CommandResult("Distinct " + field + " values (frequency):\n" + rows),
                new ViewCommand(field).execute(model));
    }

    private String formatLesson(seedu.address.model.lesson.Lesson lesson) {
        return lesson.getId() + " (cost: " + lesson.getCost() + ")";
    }

}
