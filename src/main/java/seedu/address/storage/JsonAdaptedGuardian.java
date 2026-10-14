package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly representation of a complete guardian.
 */
class JsonAdaptedGuardian {
    private final String name;
    private final String phone;
    private final String email;
    private final String relationship;
    private final String address;

    /**
     * Constructs an adapted guardian from JSON fields.
     */
    @JsonCreator
    public JsonAdaptedGuardian(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("relationship") String relationship,
            @JsonProperty("address") String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.relationship = relationship;
        this.address = address;
    }

    /**
     * Converts a guardian into a representation suitable for JSON storage.
     */
    public JsonAdaptedGuardian(Guardian source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        relationship = source.getRelationship().value;
        address = source.getAddress().value;
    }

    /**
     * Returns a validated guardian from the stored fields.
     *
     * @throws IllegalValueException if a field is missing or invalid.
     */
    public Guardian toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException("Guardian's name field is missing!");
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException("Guardian name: " + Name.MESSAGE_CONSTRAINTS);
        }
        if (phone == null) {
            throw new IllegalValueException("Guardian's phone field is missing!");
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException("Guardian phone: " + Phone.MESSAGE_CONSTRAINTS);
        }
        if (email == null) {
            throw new IllegalValueException("Guardian's email field is missing!");
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException("Guardian email: " + Email.MESSAGE_CONSTRAINTS);
        }
        if (relationship == null) {
            throw new IllegalValueException("Guardian's relationship field is missing!");
        }
        if (!Relationship.isValidRelationship(relationship)) {
            throw new IllegalValueException("Guardian relationship: " + Relationship.MESSAGE_CONSTRAINTS);
        }
        if (address == null) {
            throw new IllegalValueException("Guardian's address field is missing!");
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException("Guardian address: " + Address.MESSAGE_CONSTRAINTS);
        }
        return new Guardian(new Name(name), new Phone(phone), new Email(email),
                new Relationship(relationship), new Address(address));
    }
}
