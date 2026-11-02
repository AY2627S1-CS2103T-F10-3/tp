package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    @Test
    public void backUpDataFile_existingFile_copiesToBakFile() throws IOException {
        Path dataFile = testFolder.resolve("addressbook.json");
        Files.writeString(dataFile, "{ invalid");
        Files.writeString(testFolder.resolve("addressbook.json.bak"), "older backup");

        Optional<Path> backup = MainApp.backUpDataFile(dataFile);

        assertEquals(Optional.of(testFolder.resolve("addressbook.json.bak")), backup);
        assertEquals("{ invalid", Files.readString(backup.get()));
        assertEquals("{ invalid", Files.readString(dataFile));
    }

    @Test
    public void backUpDataFile_missingFile_returnsEmpty() {
        assertTrue(MainApp.backUpDataFile(testFolder.resolve("missing.json")).isEmpty());
    }
}
