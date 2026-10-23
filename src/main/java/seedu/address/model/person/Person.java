package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.payment.Payment;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    public static final String MESSAGE_DUPLICATE_ATTENDANCE =
            "Attendance is already recorded for this lesson and date. "
            + "Use viewattendance and deleteattendance to remove the existing record before recording a correction.";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Optional<Guardian> guardian;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Lesson> lessons = new HashSet<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<Attendance> attendance = new ArrayList<>();

    /**
     * Creates a legacy person without guardian information. Other fields must be non-null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, HashSet.newHashSet(0), Optional.empty());
    }

    /**
     * Creates a person with guardian information; an empty guardian represents a legacy record.
     */
    public Person(Name name, Phone phone, Email email, Address address,
                Set<Tag> tags, Set<Lesson> lessons, Optional<Guardian> guardian) {
        this(name, phone, email, address, tags, lessons, guardian, List.of());
    }

    /**
     * Creates a person with guardian information and recorded payments, kept in the given order.
     */
    public Person(Name name, Phone phone, Email email, Address address,
                Set<Tag> tags, Set<Lesson> lessons, Optional<Guardian> guardian, List<Payment> payments) {
        this(name, phone, email, address, tags, lessons, guardian, payments, List.of());
    }

    /**
     * Creates a person with payment and attendance history, kept in the given order.
     * Each lesson and date may have at most one attendance record.
     */
    public Person(Name name, Phone phone, Email email, Address address,
                Set<Tag> tags, Set<Lesson> lessons, Optional<Guardian> guardian, List<Payment> payments,
                List<Attendance> attendance) {
        requireAllNonNull(name, phone, email, address, tags, lessons, guardian, payments, attendance);
        for (Attendance record : attendance) {
            requireNonNull(record);
            checkArgument(this.attendance.stream().noneMatch(record::isSameSession), MESSAGE_DUPLICATE_ATTENDANCE);
            this.attendance.add(record);
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.lessons.addAll(lessons);
        this.guardian = guardian;
        this.payments.addAll(payments);
    }

    public Optional<Guardian> getGuardian() {
        return guardian;
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

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable lesson set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Lesson> getLessons() {
        return Collections.unmodifiableSet(lessons);
    }

    /**
     * Returns an immutable payment list, in the order the payments were recorded.
     */
    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    /**
     * Returns a copy of this person with {@code payments} replacing the recorded payments.
     */
    public Person withPayments(List<Payment> payments) {
        return new Person(name, phone, email, address, tags, lessons, guardian, payments, attendance);
    }

    /**
     * Returns an immutable attendance list in recording order.
     */
    public List<Attendance> getAttendance() {
        return Collections.unmodifiableList(attendance);
    }

    /**
     * Returns a copy of this person with the given attendance history, retaining payments.
     */
    public Person withAttendance(List<Attendance> attendance) {
        return new Person(name, phone, email, address, tags, lessons, guardian, payments, attendance);
    }

    /**
     * Returns the fees due for {@code month}: the monthly cost of each lesson held in that month's year.
     */
    public long getFeesDue(YearMonth month) {
        requireNonNull(month);
        return lessons.stream()
                .filter(lesson -> lesson.getYear().getValue() == month.getYear())
                .mapToLong(Lesson::getCost)
                .sum();
    }

    /**
     * Returns the total amount paid for {@code month}.
     */
    public long getAmountPaid(YearMonth month) {
        requireNonNull(month);
        return payments.stream()
                .filter(payment -> payment.getMonth().equals(month))
                .mapToLong(payment -> payment.getAmount().value)
                .sum();
    }

    /**
     * Returns the fees still owed for {@code month}, or zero if the month is fully paid.
     */
    public long getOutstandingFees(YearMonth month) {
        return Math.max(0, getFeesDue(month) - getAmountPaid(month));
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && lessons.equals(otherPerson.lessons)
                && guardian.equals(otherPerson.guardian)
                && payments.equals(otherPerson.payments)
                && attendance.equals(otherPerson.attendance);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, lessons, guardian, payments, attendance);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags);
        guardian.ifPresent(value -> builder.add("guardian", value));
        return builder.toString();
    }

}
