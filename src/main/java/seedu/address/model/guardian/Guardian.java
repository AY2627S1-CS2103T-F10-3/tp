package seedu.address.model.guardian;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Represents a student's guardian.
 * Guarantees: all fields are non-null, validated, and immutable.
 */
public class Guardian {

    private final Name name;
    private final Phone phone;
    private final Email email;
    private final Relationship relationship;
    private final Address address;

    /**
     * Constructs a {@code Guardian} with the given details.
     * Every field must be present and non-null.
     */
    public Guardian(Name name, Phone phone, Email email,
            Relationship relationship, Address address) {
        requireAllNonNull(name, phone, email, relationship, address);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.relationship = relationship;
        this.address = address;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Address getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Guardian otherGuardian)) {
            return false;
        }

        return name.equals(otherGuardian.name)
                && phone.equals(otherGuardian.phone)
                && email.equals(otherGuardian.email)
                && relationship.equals(otherGuardian.relationship)
                && address.equals(otherGuardian.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, relationship, address);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("relationship", relationship)
                .add("address", address)
                .toString();
    }
}
