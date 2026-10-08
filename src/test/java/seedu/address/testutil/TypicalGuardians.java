package seedu.address.testutil;

import seedu.address.model.guardian.Guardian;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Guardian data shared by parser, command, and storage tests.
 */
public class TypicalGuardians {
    public static final Guardian MARY = new Guardian(new Name("Mary Tan"), new Phone("91234567"),
            new Email("mary@example.com"), new Relationship("Mother"), new Address("123 Example Road"));
    public static final String GUARDIAN_DETAILS = " gn/Mary Tan gp/91234567 ge/mary@example.com"
            + " gr/Mother ga/123 Example Road";
}
