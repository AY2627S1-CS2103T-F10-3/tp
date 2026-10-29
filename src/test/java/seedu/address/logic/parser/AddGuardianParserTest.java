package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalGuardians.GUARDIAN_DETAILS;
import static seedu.address.testutil.TypicalGuardians.MARY;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class AddGuardianParserTest {
    private static final String STUDENT = " n/Alice Tan p/87654321 e/alice@example.com a/456 Student Road";
    private static final List<String> FIELDS = List.of(" gn/Mary Tan", " gp/91234567", " ge/mary@example.com",
            " gr/Mother", " ga/123 Example Road");
    private final AddStudentCommandParser parser = new AddStudentCommandParser();

    @Test
    public void parse_withoutGuardian_success() {
        assertParseSuccess(parser, STUDENT, new AddStudentCommand(new PersonBuilder()
                .withName("Alice Tan").withPhone("87654321").withEmail("alice@example.com")
                .withAddress("456 Student Road").build()));
    }

    @Test
    public void parse_partialGuardianFields_failure() {
        for (String field : FIELDS) {
            assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS.replace(field, ""),
                    AddStudentCommand.MESSAGE_INCOMPLETE_GUARDIAN_DETAILS);
        }
    }

    @Test
    public void parse_blankGuardianField_failure() {
        List<String> messages = List.of(Name.MESSAGE_CONSTRAINTS, Phone.MESSAGE_CONSTRAINTS,
                Email.MESSAGE_CONSTRAINTS, Relationship.MESSAGE_CONSTRAINTS, Address.MESSAGE_CONSTRAINTS);
        for (int i = 0; i < FIELDS.size(); i++) {
            String field = FIELDS.get(i);
            String prefix = field.substring(0, 4);
            assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS.replace(field, prefix + "   "), messages.get(i));
        }
    }

    @Test
    public void parse_repeatedGuardianField_failure() {
        for (String field : FIELDS) {
            Prefix prefix = new Prefix(field.substring(1, 4));
            assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS + field,
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    @Test
    public void parse_invalidGuardianContact_failure() {
        assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS.replace("Mary Tan", "Mary!"), Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS.replace("91234567", "phone"), Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS.replace("mary@example.com", "invalid"),
                Email.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_reorderedFields_keepsStudentAndGuardianSeparate() throws Exception {
        ModelManager model = new ModelManager();
        parser.parse(GUARDIAN_DETAILS + STUDENT + " t/friend").execute(model);
        var student = model.getAddressBook().getPersonList().getFirst();
        assertEquals(new Name("Alice Tan"), student.getName());
        assertEquals(new Phone("87654321"), student.getPhone());
        assertEquals(new Address("456 Student Road"), student.getAddress());
        assertEquals(MARY, student.getGuardian().orElseThrow());
    }

    @Test
    public void parse_withoutGuardian_addsStudent() throws Exception {
        ModelManager model = new ModelManager();
        parser.parse(STUDENT).execute(model);
        assertEquals(1, model.getAddressBook().getPersonList().size());
        assertFalse(model.getAddressBook().getPersonList().getFirst().getGuardian().isPresent());
    }

    @Test
    public void parseRelationship_trimsWhitespace() throws Exception {
        assertEquals(new Relationship("Legal guardian"), ParserUtil.parseRelationship("  Legal guardian  "));
    }
}
