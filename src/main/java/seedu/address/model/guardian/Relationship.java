package seedu.address.model.guardian;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a guardian's relationship to a student.
 * Guarantees: immutable; the value is not blank.
 */
public class Relationship {

    public static final String MESSAGE_CONSTRAINTS =
            "Relationship should not be blank";

    public final String value;

    /**
     * Constructs a {@code Relationship}.
     *
     * @param relationship A non-blank relationship.
     */
    public Relationship(String relationship) {
        requireNonNull(relationship);
        checkArgument(isValidRelationship(relationship), MESSAGE_CONSTRAINTS);
        value = relationship;
    }

    /**
     * Returns true if the given string is a valid relationship.
     */
    public static boolean isValidRelationship(String test) {
        return !test.isBlank();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Relationship otherRelationship)) {
            return false;
        }

        return value.equals(otherRelationship.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
