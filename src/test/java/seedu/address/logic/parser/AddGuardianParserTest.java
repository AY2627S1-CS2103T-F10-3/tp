package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.TypicalGuardians.GUARDIAN_DETAILS;
import static seedu.address.testutil.TypicalGuardians.MARY;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class AddGuardianParserTest {
    private static final String STUDENT = " n/Alice Tan p/87654321 e/alice@example.com a/456 Student Road";
    private static final List<String> FIELDS = List.of(" gn/Mary Tan", " gp/91234567", " ge/mary@example.com",
            " gr/Mother", " ga/123 Example Road");
    private final AddStudentCommandParser parser = new AddStudentCommandParser();

    @Test
    public void parse_missingGuardianField_failure() {
        String message = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddStudentCommand.MESSAGE_USAGE);
        assertParseFailure(parser, STUDENT, message);
        for (String field : FIELDS) {
            assertParseFailure(parser, STUDENT + GUARDIAN_DETAILS.replace(field, ""), message);
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
    public void parse_invalidInput_doesNotAddStudent() {
        ModelManager model = new ModelManager();
        assertThrows(ParseException.class, () -> parser.parse(STUDENT).execute(model));
        assertEquals(0, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void parseRelationship_trimsWhitespace() throws Exception {
        assertEquals(new Relationship("Legal guardian"), ParserUtil.parseRelationship("  Legal guardian  "));
    }
}
