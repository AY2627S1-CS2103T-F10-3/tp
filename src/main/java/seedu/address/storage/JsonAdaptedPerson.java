package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonBook;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";
    public static final String UNKNOWN_LESSON_ID_MESSAGE = "Person references an unknown lesson ID: %s";
    public static final String NULL_ENTRY_MESSAGE_FORMAT = "%s records must not be null.";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final JsonAdaptedGuardian guardian;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<String> lessonIds = new ArrayList<>();
    private final List<JsonAdaptedLesson> legacyLessons = new ArrayList<>();
    private final List<JsonAdaptedPayment> payments = new ArrayList<>();
    private final List<JsonAdaptedAttendance> attendance = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, tags, null, null, null, null);
    }

    /**
     * Accepts records created before attendance was introduced.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags,
            List<String> lessonIds, List<JsonAdaptedLesson> lessons, JsonAdaptedGuardian guardian,
            List<JsonAdaptedPayment> payments) {
        this(name, phone, email, address, tags, lessonIds, lessons, guardian, payments, null);
    }

    /**
     * Reads guardian, payment and attendance information when present, while accepting legacy records.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("lessonIds") List<String> lessonIds,
            @JsonProperty("lessons") List<JsonAdaptedLesson> lessons,
            @JsonProperty("guardian") JsonAdaptedGuardian guardian,
            @JsonProperty("payments") List<JsonAdaptedPayment> payments,
            @JsonProperty("attendance") List<JsonAdaptedAttendance> attendance) {
        this.guardian = guardian;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (lessonIds != null) {
            this.lessonIds.addAll(lessonIds);
        }
        if (lessons != null) {
            this.legacyLessons.addAll(lessons);
        }
        if (payments != null) {
            this.payments.addAll(payments);
        }
        if (attendance != null) {
            this.attendance.addAll(attendance);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        guardian = source.getGuardian().map(JsonAdaptedGuardian::new).orElse(null);
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        lessonIds.addAll(source.getLessons().stream().map(Lesson::getId).collect(Collectors.toList()));
        payments.addAll(source.getPayments().stream()
                .map(JsonAdaptedPayment::new)
                .collect(Collectors.toList()));
        attendance.addAll(source.getAttendance().stream()
                .map(JsonAdaptedAttendance::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        return toModelType(new LessonBook());
    }

    public Person toModelType(LessonBook lessonBook) throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException(String.format(NULL_ENTRY_MESSAGE_FORMAT, "Tags"));
            }
            personTags.add(tag.toModelType());
        }

        final List<Lesson> personLessons = new ArrayList<>();
        if (lessonBook != null && !lessonIds.isEmpty()) {
            for (String lessonId : lessonIds) {
                Lesson lesson = lessonBook.getLesson(lessonId);
                if (lesson == null) {
                    throw new IllegalValueException(String.format(UNKNOWN_LESSON_ID_MESSAGE, lessonId));
                }
                personLessons.add(lesson);
            }
        } else if (lessonBook != null) {
            // v1.3 stored complete lesson definitions inside each person. Import them
            // into the shared lesson book so the next save can normalize the format.
            for (JsonAdaptedLesson legacyLesson : legacyLessons) {
                if (legacyLesson == null) {
                    throw new IllegalValueException(String.format(NULL_ENTRY_MESSAGE_FORMAT, "Lessons"));
                }
                Lesson lesson = legacyLesson.toModelType();
                lessonBook.addLesson(lesson);
                personLessons.add(lessonBook.getLesson(lesson.getId()));
            }
        }

        final List<Payment> personPayments = new ArrayList<>();
        for (JsonAdaptedPayment payment : payments) {
            if (payment == null) {
                throw new IllegalValueException(String.format(NULL_ENTRY_MESSAGE_FORMAT, "Payment"));
            }
            personPayments.add(payment.toModelType());
        }

        final List<Attendance> personAttendance = new ArrayList<>();
        for (JsonAdaptedAttendance record : attendance) {
            if (record == null) {
                throw new IllegalValueException(String.format(NULL_ENTRY_MESSAGE_FORMAT, "Attendance"));
            }
            Lesson lesson = lessonBook == null ? null : lessonBook.getLesson(record.getLessonId());
            Attendance modelAttendance = record.toModelType(lesson);
            if (personAttendance.stream().anyMatch(modelAttendance::isSameSession)) {
                throw new IllegalValueException(Person.MESSAGE_DUPLICATE_ATTENDANCE);
            }
            personAttendance.add(modelAttendance);
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final Set<Tag> modelTags = new HashSet<>(personTags);
        final Set<Lesson> modelLessons = new HashSet<>(personLessons);
        return new Person(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelLessons,
                guardian == null ? Optional.empty() : Optional.of(guardian.toModelType()), personPayments,
                personAttendance);
    }

}
