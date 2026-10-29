package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.storage.JsonAdaptedPerson.NULL_ENTRY_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAttendance.MATH_LESSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;
import static seedu.address.testutil.TypicalPayments.PAYMENT_OCTOBER_PARTIAL;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonBook;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Time;
import seedu.address.model.payment.Amount;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_withPayments_keepsPaymentsInOrder() throws Exception {
        Person bensonWithPayments = new PersonBuilder(BENSON)
                .withPayments(PAYMENT_NOVEMBER, PAYMENT_OCTOBER_PARTIAL).build();
        assertEquals(bensonWithPayments, new JsonAdaptedPerson(bensonWithPayments).toModelType());
    }

    @Test
    public void toModelType_invalidPayment_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, null, null, null, List.of(new JsonAdaptedPayment("0", "2026-10", "2026-10-01")));
        assertThrows(IllegalValueException.class, Amount.MESSAGE_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_nullPayment_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, null, null, null, Collections.singletonList(null));
        assertThrows(IllegalValueException.class, String.format(NULL_ENTRY_MESSAGE_FORMAT, "Payment"),
                person::toModelType);
    }

    @Test
    public void toModelType_nullTag_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                Collections.singletonList(null));
        assertThrows(IllegalValueException.class, String.format(NULL_ENTRY_MESSAGE_FORMAT, "Tags"),
                person::toModelType);
    }

    @Test
    public void toModelType_nullLegacyLesson_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, null, Collections.singletonList(null), null, null);
        assertThrows(IllegalValueException.class, String.format(NULL_ENTRY_MESSAGE_FORMAT, "Lessons"),
                person::toModelType);
    }

    @Test
    public void toModelType_lessonIdsWithNullLegacyLesson_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, List.of(MATH_LESSON.getId()), Collections.singletonList(null), null, null);
        LessonBook lessonBook = new LessonBook();
        lessonBook.addLesson(MATH_LESSON);

        assertThrows(IllegalValueException.class, String.format(NULL_ENTRY_MESSAGE_FORMAT, "Lessons"), () ->
                person.toModelType(lessonBook));
    }

    @Test
    public void toModelType_nullAttendance_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, null, null, null, null, Collections.singletonList(null));
        assertThrows(IllegalValueException.class, String.format(NULL_ENTRY_MESSAGE_FORMAT, "Attendance"),
                person::toModelType);
    }

    @Test
    public void toModelType_attendanceOutsideCurrentSchedule_preservesHistoricalDate() throws Exception {
        JsonAdaptedAttendance historicalAttendance =
                new JsonAdaptedAttendance(MATH_LESSON.getId(), "2026-10-20", "PRESENT");
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, List.of(MATH_LESSON.getId()), null, null, null, List.of(historicalAttendance));
        LessonBook lessonBook = new LessonBook();
        lessonBook.addLesson(MATH_LESSON);

        // Attendance saved without a stable lesson ID is linked to the lesson on load.
        assertEquals(List.of(historicalAttendance.toModelType().withLessonUid(MATH_LESSON.getUid())),
                person.toModelType(lessonBook).getAttendance());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }


    @Test
    public void toModelType_mismatchedAttendanceLessonIdAndUid_rejectsRecord() {
        JsonAdaptedAttendance record = new JsonAdaptedAttendance("2026-SCIENCE-TUESDAY-TEN-ELEVEN",
                MATH_LESSON.getUid(), "2026-10-19", "PRESENT");
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, List.of(MATH_LESSON.getId()), null, null, null, List.of(record));
        LessonBook catalog = new LessonBook();
        catalog.addLesson(MATH_LESSON);

        assertThrows(IllegalValueException.class, () -> person.toModelType(catalog));
    }

    @Test
    public void toModelType_deletedLessonUid_preservesHistoricalAttendance() throws Exception {
        JsonAdaptedAttendance record = new JsonAdaptedAttendance(MATH_LESSON.getId(), "deleted-lesson",
                "2026-10-19", "PRESENT");
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, null, null, null, null, List.of(record));

        assertEquals(List.of(record.toModelType()), person.toModelType().getAttendance());
    }

    @Test
    public void toModelType_legacyAttendanceFromPreviousEdit_preservesUnresolvableOldId() throws Exception {
        JsonAdaptedAttendance record = new JsonAdaptedAttendance("2026-MATH-TUESDAY-TEN-ELEVEN",
                "2026-10-20", "PRESENT");
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, List.of(MATH_LESSON.getId()), null, null, null, List.of(record));
        LessonBook catalog = new LessonBook();
        catalog.addLesson(MATH_LESSON);

        assertEquals(List.of(record.toModelType()), person.toModelType(catalog).getAttendance());
    }

    @Test
    public void toModelType_ambiguousLegacyAttendance_doesNotGuessIdentity() throws Exception {
        Lesson moved = MATH_LESSON.withRevision(new LessonRevision(MATH_LESSON.getYear(), MATH_LESSON.getSubject(),
                Day.TUESDAY, Time.TEN, Time.ELEVEN, 40, YearMonth.of(2026, 11)), YearMonth.of(2026, 10));
        Lesson reusedSlot = new Lesson(MATH_LESSON.getYear(), MATH_LESSON.getSubject(), MATH_LESSON.getDay(),
                MATH_LESSON.getStartTime(), MATH_LESSON.getEndTime(), 30);
        LessonBook catalog = new LessonBook();
        catalog.addLesson(moved);
        catalog.addLesson(reusedSlot);
        JsonAdaptedAttendance record = new JsonAdaptedAttendance(MATH_LESSON.getId(), "2026-10-19", "PRESENT");
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, List.of(moved.getId()), null, null, null, List.of(record));

        assertEquals(List.of(record.toModelType()), person.toModelType(catalog).getAttendance());
    }

}
