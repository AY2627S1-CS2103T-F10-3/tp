package seedu.address.model.guardian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class GuardianTest {

    private static final Name NAME = new Name("Alice Tan");
    private static final Phone PHONE = new Phone("91234567");
    private static final Email EMAIL = new Email("alice@example.com");
    private static final Relationship RELATIONSHIP = new Relationship("Mother");
    private static final Address ADDRESS = new Address("123 Example Street");

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Guardian(null, PHONE, EMAIL, RELATIONSHIP, ADDRESS));
        assertThrows(NullPointerException.class, () -> new Guardian(NAME, null, EMAIL, RELATIONSHIP, ADDRESS));
        assertThrows(NullPointerException.class, () -> new Guardian(NAME, PHONE, null, RELATIONSHIP, ADDRESS));
        assertThrows(NullPointerException.class, () -> new Guardian(NAME, PHONE, EMAIL, null, ADDRESS));
        assertThrows(NullPointerException.class, () -> new Guardian(NAME, PHONE, EMAIL, RELATIONSHIP, null));
    }

    @Test
    public void constructor_validFields_storesDetails() {
        Guardian guardian = new Guardian(NAME, PHONE, EMAIL, RELATIONSHIP, ADDRESS);

        assertEquals(NAME, guardian.getName());
        assertEquals(PHONE, guardian.getPhone());
        assertEquals(EMAIL, guardian.getEmail());
        assertEquals(RELATIONSHIP, guardian.getRelationship());
        assertEquals(ADDRESS, guardian.getAddress());
    }

    @Test
    public void equals() {
        Guardian guardian = new Guardian(NAME, PHONE, EMAIL, RELATIONSHIP, ADDRESS);
        Guardian copy = new Guardian(new Name("Alice Tan"), new Phone("91234567"),
                new Email("alice@example.com"), new Relationship("Mother"), new Address("123 Example Street"));

        assertEquals(guardian, guardian);
        assertEquals(guardian, copy);
        assertEquals(copy, guardian);
        assertNotEquals(guardian, null);
        assertNotEquals(guardian, "Alice Tan");

        assertNotEquals(guardian, new Guardian(new Name("Bob Tan"), PHONE, EMAIL, RELATIONSHIP, ADDRESS));
        assertNotEquals(guardian, new Guardian(NAME, new Phone("98765432"), EMAIL, RELATIONSHIP, ADDRESS));
        assertNotEquals(guardian, new Guardian(NAME, PHONE, new Email("bob@example.com"), RELATIONSHIP, ADDRESS));
        assertNotEquals(guardian, new Guardian(NAME, PHONE, EMAIL, new Relationship("Aunt"), ADDRESS));
        assertNotEquals(guardian, new Guardian(NAME, PHONE, EMAIL, RELATIONSHIP, new Address("456 Example Road")));
    }

    @Test
    public void hashCode_equalGuardians_sameHashCode() {
        Guardian first = new Guardian(NAME, PHONE, EMAIL, RELATIONSHIP, ADDRESS);
        Guardian second = new Guardian(new Name("Alice Tan"), new Phone("91234567"),
                new Email("alice@example.com"), new Relationship("Mother"), new Address("123 Example Street"));

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void toStringMethod() {
        Guardian guardian = new Guardian(NAME, PHONE, EMAIL, RELATIONSHIP, ADDRESS);
        String expected = Guardian.class.getCanonicalName() + "{name=Alice Tan, phone=91234567, "
                + "email=alice@example.com, relationship=Mother, address=123 Example Street}";

        assertEquals(expected, guardian.toString());
    }
}
