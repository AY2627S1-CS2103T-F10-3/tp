package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void execute_addReplaceAndClear_remarkAndOtherDetailsAreCorrect() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        parser.parseCommand("remark 1 r/Likes swimming").execute(model);
        assertEquals(new PersonBuilder(ALICE).withRemark("Likes swimming").build(),
                model.getFilteredPersonList().get(0));
        parser.parseCommand("remark 1 r/Prefers email").execute(model);
        assertEquals("Prefers email", model.getFilteredPersonList().get(0).getRemark().value);
        parser.parseCommand("remark 1 r/").execute(model);
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
        parser.parseCommand("remark 1 r/Temporary").execute(model);
        parser.parseCommand("remark 1").execute(model);
        assertEquals(ALICE, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.updateFilteredPersonList(person -> person.equals(BOB));
        parser.parseCommand("remark 1 r/Call tomorrow").execute(model);
        assertEquals(List.of(ALICE, new PersonBuilder(BOB).withRemark("Call tomorrow").build()),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidIndex_doesNotChangeModel() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        Command command = parser.parseCommand("remark 2 r/Out of range");
        assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
    }

    @Test
    public void parse_invalidArguments_rejected() {
        for (String input : List.of("remark", "remark 0 r/note", "remark -1 r/note",
                "remark one r/note", "remark 1 r/first r/second", "remark 1 unexpected")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
        }
    }

    @Test
    public void execute_editOtherField_preservesRemark() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder(ALICE).withRemark("Keep this note").build());
        parser.parseCommand("edit 1 p/98765432").execute(model);
        assertEquals(new PersonBuilder(ALICE).withRemark("Keep this note").withPhone("98765432").build(),
                model.getFilteredPersonList().get(0));
    }

    @Test
    public void equality_remarkAffectsValueButNotIdentity() throws Exception {
        Person withRemark = new PersonBuilder(ALICE).withRemark("A note").build();
        assertNotEquals(ALICE, withRemark);
        assertTrue(ALICE.isSamePerson(withRemark));
        assertEquals(parser.parseCommand("remark 1 r/A note"), parser.parseCommand("remark 1 r/A note"));
        assertNotEquals(parser.parseCommand("remark 1 r/A note"), parser.parseCommand("remark 2 r/A note"));
        assertNotEquals(parser.parseCommand("remark 1 r/A note"), parser.parseCommand("remark 1 r/Another"));
    }
}
