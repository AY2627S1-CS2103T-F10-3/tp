package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_ABSENT;
import static seedu.address.testutil.TypicalAttendance.ATTENDANCE_PRESENT;
import static seedu.address.testutil.TypicalAttendance.MATH_LESSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AttendanceStorageTest {

    @TempDir
    public Path testFolder;

    @Test
    public void saveAndRead_preservesBothHistoriesAndLessons() throws Exception {
        Person student = new PersonBuilder().withLessons(MATH_LESSON).withPayments(PAYMENT_NOVEMBER)
                .withAttendance(ATTENDANCE_PRESENT, ATTENDANCE_ABSENT).build();
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(student);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("attendance.json"));
        storage.saveAddressBook(addressBook);
        assertEquals(addressBook, new AddressBook(storage.readAddressBook().orElseThrow()));

        // Removing the enrolment must not erase attendance history on restart.
        Person unenrolled = new PersonBuilder(student).withLessons().build();
        addressBook.setPerson(student, unenrolled);
        storage.saveAddressBook(addressBook);
        assertEquals(unenrolled, storage.readAddressBook().orElseThrow().getPersonList().getFirst());
    }

    @Test
    public void read_legacyFileWithoutAttendance_defaultsToEmpty() throws Exception {
        Path file = testFolder.resolve("legacy.json");
        Files.writeString(file, personJson(""));
        ReadOnlyAddressBook loaded = new JsonAddressBookStorage(file).readAddressBook().orElseThrow();
        assertTrue(loaded.getPersonList().getFirst().getAttendance().isEmpty());
    }

    @Test
    public void read_invalidOrDuplicateAttendance_reportsDataLoadingError() throws Exception {
        String record = "{\"lessonId\":\"2026-MATH-MONDAY-TEN-ELEVEN\","
                + "\"date\":\"2026-10-19\",\"status\":\"PRESENT\"}";
        Path file = testFolder.resolve("invalid.json");
        for (String records : List.of("null", "{}", record + "," + record,
                record + "," + record.replace("PRESENT", "ABSENT"), record.replace("PRESENT", "INVALID"))) {
            Files.writeString(file, personJson(",\"attendance\":[" + records + "]"));
            assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(file).readAddressBook());
        }
    }

    private String personJson(String extraField) {
        return "{\"persons\":[{\"name\":\"Amy Bee\",\"phone\":\"85355255\","
                + "\"email\":\"amy@example.com\",\"address\":\"123 Street\",\"tags\":[]" + extraField + "}]}";
    }
}
