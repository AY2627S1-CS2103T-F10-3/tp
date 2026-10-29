package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_nullPerson_throwsIllegalValueException() {
        JsonSerializableAddressBook addressBook =
                new JsonSerializableAddressBook(Collections.singletonList(null), null);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_PERSON,
                addressBook::toModelType);
    }

    @Test
    public void toModelType_nullLesson_throwsIllegalValueException() {
        JsonSerializableAddressBook addressBook =
                new JsonSerializableAddressBook(null, Collections.singletonList(null));
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_LESSON,
                addressBook::toModelType);
    }


    @Test
    public void toModelType_duplicateLessonUid_throwsIllegalValueException() {
        JsonAdaptedLesson monday = new JsonAdaptedLesson("shared-uid", "2026", "MATH", "MONDAY",
                "TEN", "ELEVEN", 30, null);
        JsonAdaptedLesson tuesday = new JsonAdaptedLesson("shared-uid", "2026", "MATH", "TUESDAY",
                "TEN", "ELEVEN", 30, null);
        JsonSerializableAddressBook book = new JsonSerializableAddressBook(List.of(), List.of(monday, tuesday));

        assertThrows(IllegalValueException.class, book::toModelType);
    }

    @Test
    public void toModelType_blankLessonUid_throwsIllegalValueException() {
        JsonAdaptedLesson lesson = new JsonAdaptedLesson(" ", "2026", "MATH", "MONDAY", "TEN", "ELEVEN", 30, null);
        JsonSerializableAddressBook book = new JsonSerializableAddressBook(List.of(), List.of(lesson));

        assertThrows(IllegalValueException.class, book::toModelType);
    }

}
