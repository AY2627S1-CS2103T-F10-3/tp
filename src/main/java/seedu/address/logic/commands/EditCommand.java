package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_RELATIONSHIP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_GUARDIAN_NAME + "GUARDIAN_NAME] "
            + "[" + PREFIX_GUARDIAN_PHONE + "GUARDIAN_PHONE] "
            + "[" + PREFIX_GUARDIAN_EMAIL + "GUARDIAN_EMAIL] "
            + "[" + PREFIX_GUARDIAN_RELATIONSHIP + "GUARDIAN_RELATIONSHIP] "
            + "[" + PREFIX_GUARDIAN_ADDRESS + "GUARDIAN_ADDRESS]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com "
            + PREFIX_GUARDIAN_PHONE + "98765432";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_INCOMPLETE_GUARDIAN = "This student has no guardian. "
            + "To add one, provide all of: "
            + PREFIX_GUARDIAN_NAME + " " + PREFIX_GUARDIAN_PHONE + " " + PREFIX_GUARDIAN_EMAIL + " "
            + PREFIX_GUARDIAN_RELATIONSHIP + " " + PREFIX_GUARDIAN_ADDRESS;

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     *
     * @throws CommandException if guardian fields are edited for a person without a guardian,
     *     but not all guardian fields are provided.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor)
            throws CommandException {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        Optional<Guardian> updatedGuardian = createEditedGuardian(personToEdit.getGuardian(), editPersonDescriptor);

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags, updatedGuardian);
    }

    /**
     * Returns the guardian of a person after applying the guardian fields in {@code editPersonDescriptor}.
     * If the person has no guardian, a new guardian is created only when all guardian fields are provided.
     *
     * @throws CommandException if the person has no guardian and only some guardian fields are provided.
     */
    private static Optional<Guardian> createEditedGuardian(Optional<Guardian> guardianToEdit,
            EditPersonDescriptor editPersonDescriptor) throws CommandException {
        if (!editPersonDescriptor.isAnyGuardianFieldEdited()) {
            return guardianToEdit;
        }

        if (guardianToEdit.isEmpty()) {
            if (!editPersonDescriptor.isEveryGuardianFieldEdited()) {
                throw new CommandException(MESSAGE_INCOMPLETE_GUARDIAN);
            }
            return Optional.of(new Guardian(editPersonDescriptor.getGuardianName().get(),
                    editPersonDescriptor.getGuardianPhone().get(),
                    editPersonDescriptor.getGuardianEmail().get(),
                    editPersonDescriptor.getGuardianRelationship().get(),
                    editPersonDescriptor.getGuardianAddress().get()));
        }

        Guardian guardian = guardianToEdit.get();
        return Optional.of(new Guardian(
                editPersonDescriptor.getGuardianName().orElse(guardian.getName()),
                editPersonDescriptor.getGuardianPhone().orElse(guardian.getPhone()),
                editPersonDescriptor.getGuardianEmail().orElse(guardian.getEmail()),
                editPersonDescriptor.getGuardianRelationship().orElse(guardian.getRelationship()),
                editPersonDescriptor.getGuardianAddress().orElse(guardian.getAddress())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Name guardianName;
        private Phone guardianPhone;
        private Email guardianEmail;
        private Relationship guardianRelationship;
        private Address guardianAddress;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setGuardianName(toCopy.guardianName);
            setGuardianPhone(toCopy.guardianPhone);
            setGuardianEmail(toCopy.guardianEmail);
            setGuardianRelationship(toCopy.guardianRelationship);
            setGuardianAddress(toCopy.guardianAddress);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags) || isAnyGuardianFieldEdited();
        }

        /**
         * Returns true if at least one guardian field is edited.
         */
        public boolean isAnyGuardianFieldEdited() {
            return CollectionUtil.isAnyNonNull(guardianName, guardianPhone, guardianEmail, guardianRelationship,
                    guardianAddress);
        }

        /**
         * Returns true if every guardian field is edited.
         */
        public boolean isEveryGuardianFieldEdited() {
            return guardianName != null && guardianPhone != null && guardianEmail != null
                    && guardianRelationship != null && guardianAddress != null;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public void setGuardianName(Name guardianName) {
            this.guardianName = guardianName;
        }

        public Optional<Name> getGuardianName() {
            return Optional.ofNullable(guardianName);
        }

        public void setGuardianPhone(Phone guardianPhone) {
            this.guardianPhone = guardianPhone;
        }

        public Optional<Phone> getGuardianPhone() {
            return Optional.ofNullable(guardianPhone);
        }

        public void setGuardianEmail(Email guardianEmail) {
            this.guardianEmail = guardianEmail;
        }

        public Optional<Email> getGuardianEmail() {
            return Optional.ofNullable(guardianEmail);
        }

        public void setGuardianRelationship(Relationship guardianRelationship) {
            this.guardianRelationship = guardianRelationship;
        }

        public Optional<Relationship> getGuardianRelationship() {
            return Optional.ofNullable(guardianRelationship);
        }

        public void setGuardianAddress(Address guardianAddress) {
            this.guardianAddress = guardianAddress;
        }

        public Optional<Address> getGuardianAddress() {
            return Optional.ofNullable(guardianAddress);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(guardianName, otherEditPersonDescriptor.guardianName)
                    && Objects.equals(guardianPhone, otherEditPersonDescriptor.guardianPhone)
                    && Objects.equals(guardianEmail, otherEditPersonDescriptor.guardianEmail)
                    && Objects.equals(guardianRelationship, otherEditPersonDescriptor.guardianRelationship)
                    && Objects.equals(guardianAddress, otherEditPersonDescriptor.guardianAddress);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("guardianName", guardianName)
                    .add("guardianPhone", guardianPhone)
                    .add("guardianEmail", guardianEmail)
                    .add("guardianRelationship", guardianRelationship)
                    .add("guardianAddress", guardianAddress)
                    .toString();
        }
    }
}
