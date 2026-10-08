package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalGuardians.GUARDIAN_DETAILS;
import static seedu.address.testutil.TypicalGuardians.MARY;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class GuardianIntegrationTest {
    @TempDir
    public Path folder;

    @Test
    public void addEditAndReload_preservesGuardian() throws Exception {
        JsonAddressBookStorage addressStorage = new JsonAddressBookStorage(folder.resolve("students.json"));
        StorageManager storage = new StorageManager(addressStorage,
                new JsonUserPrefsStorage(folder.resolve("preferences.json")));
        ModelManager model = new ModelManager();
        LogicManager logic = new LogicManager(model, storage);

        var result = logic.execute("addstudent n/Alice Tan p/87654321 e/alice@example.com a/456 Student Road"
                + GUARDIAN_DETAILS);
        assertTrue(result.getFeedbackToUser().contains("Guardian: Mary Tan"));
        assertEquals(MARY, addressStorage.readAddressBook().orElseThrow().getPersonList().getFirst()
                .getGuardian().orElseThrow());

        logic.execute("edit 1 p/88888888");
        Person restored = addressStorage.readAddressBook().orElseThrow().getPersonList().getFirst();
        assertEquals("88888888", restored.getPhone().value);
        assertEquals(MARY, restored.getGuardian().orElseThrow());
        assertEquals(model.getAddressBook().getPersonList().getFirst(), restored);
    }

    @Test
    public void guardianChanges_dataEqualityButNotStudentIdentity() {
        Person legacy = new PersonBuilder().build();
        Person withGuardian = new PersonBuilder(legacy).withGuardian(MARY).build();
        assertNotEquals(legacy, withGuardian);
        assertTrue(legacy.isSamePerson(withGuardian));
    }
}
