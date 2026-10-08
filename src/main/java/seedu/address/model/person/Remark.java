package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/** Represents an optional remark attached to a person. */
public class Remark {

    /** Empty remarks are allowed to support removing a person's remark. */
    public final String value;

    /** Creates a remark with the given text. */
    public Remark(String value) {
        requireNonNull(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Remark otherRemark && value.equals(otherRemark.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
