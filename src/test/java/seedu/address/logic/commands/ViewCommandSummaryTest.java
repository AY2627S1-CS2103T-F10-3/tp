package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                .withAddress("Shared address").withPhone("12345678").withTags("friends", "team").build());
        book.addPerson(new PersonBuilder().withName("Bob").withEmail("shared@example.com")
                .withAddress("Shared address").withPhone("12345678").withTags("friends").build());
        Model model = new ModelManager(book, new UserPrefs());
        model.updateFilteredPersonList(person -> person.getName().toString().equals("Alice"));

        assertSummary(model, "email", "shared@example.com : 2");
        assertSummary(model, "address", "Shared address : 2");
        assertSummary(model, "phone", "12345678 : 2");
        assertSummary(model, "name", "Alice : 1\nBob : 1");
        assertSummary(model, "tag", "friends : 2\nteam : 1");
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(book, model.getAddressBook());
    }

    @Test
    public void execute_emptyAddressBook_showsNoValues() {
        Model model = new ModelManager();
        assertSummary(model, "email", "No values found.");
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

}
