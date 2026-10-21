package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.testutil.PersonBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_lessonsStoredSeparatelyAndLinkedById_success() throws Exception {
        Path filePath = testFolder.resolve("LessonsAddressBook.json");
        AddressBook original = new AddressBook();
        original.addPerson(new PersonBuilder(ALICE).withLessons(VALID_LESSON_MATH).build());

        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        storage.saveAddressBook(original, filePath);
        String json = Files.readString(filePath);

        assertTrue(json.contains("\"lessons\""));
        assertTrue(json.contains("\"lessonIds\""));
        assertFalse(json.contains("\"lessons\":[{\"year\""));

        ReadOnlyAddressBook loaded = storage.readAddressBook(filePath).get();
        assertEquals(1, loaded.getLessonList().size());
        assertEquals(Set.of(VALID_LESSON_MATH), loaded.getPersonList().get(0).getLessons());
    }

    @Test
    public void readAddressBook_duplicateLessonDefinitions_keepsOneDefinition() throws Exception {
        Path filePath = testFolder.resolve("DuplicateLessonsAddressBook.json");
        Files.writeString(filePath, "{\"persons\":[],\"lessons\":["
                + "{\"year\":\"2026\",\"subject\":\"MATH\",\"day\":\"MONDAY\","
                + "\"startTime\":\"TEN\",\"endTime\":\"ELEVEN\",\"cost\":30},"
                + "{\"year\":\"2026\",\"subject\":\"MATH\",\"day\":\"MONDAY\","
                + "\"startTime\":\"TEN\",\"endTime\":\"ELEVEN\",\"cost\":30}]}");

        ReadOnlyAddressBook loaded = new JsonAddressBookStorage(filePath).readAddressBook(filePath).get();
        assertEquals(1, loaded.getLessonList().size());
    }

    @Test
    public void readAndSaveAddressBook_legacyPersonLessons_areMigrated() throws Exception {
        Path filePath = testFolder.resolve("LegacyLessonsAddressBook.json");
        Files.writeString(filePath, "{\"persons\":[{\"name\":\"Alice Pauline\","
                + "\"phone\":\"94351253\",\"email\":\"alice@example.com\","
                + "\"address\":\"123, Jurong West Ave 6, #08-111\",\"tags\":[],"
                + "\"lessons\":[{\"year\":\"2026\",\"subject\":\"MATH\","
                + "\"day\":\"MONDAY\",\"startTime\":\"TEN\","
                + "\"endTime\":\"ELEVEN\",\"cost\":30}]}]}");

        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        ReadOnlyAddressBook loaded = storage.readAddressBook(filePath).get();
        assertEquals(Set.of(VALID_LESSON_MATH), loaded.getPersonList().get(0).getLessons());

        storage.saveAddressBook(loaded, filePath);
        ReadOnlyAddressBook reloaded = storage.readAddressBook(filePath).get();
        assertEquals(Set.of(VALID_LESSON_MATH), reloaded.getPersonList().get(0).getLessons());
        assertEquals(1, reloaded.getLessonList().size());
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
