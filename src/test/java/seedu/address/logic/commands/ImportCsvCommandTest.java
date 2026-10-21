package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ModelManager;

public class ImportCsvCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_duplicateStudents_skipsAndReportsAllDuplicates() throws Exception {
        Path lessons = temporaryDirectory.resolve("importlessons.csv");
        Path students = temporaryDirectory.resolve("importstudents.csv");
        Files.writeString(lessons, "id,year,subject,day,start_time,end_time,cost\n"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN,2026,ENGLISH,MONDAY,TEN,ELEVEN,30\n");
        String header = "name,phone,email,address,lessons\n";
        String alice = "Alice Tan,91234567,alice@example.com,1 Main Street,"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN\n";
        String bob = "Bob Lim,92345678,bob@example.com,2 Main Street,"
                + "2026-ENGLISH-MONDAY-TEN-ELEVEN\n";
        Files.writeString(students, header + alice + bob + alice);

        ModelManager model = new ModelManager();
        CommandResult result = new ImportCsvCommand(students, lessons).execute(model);

        assertEquals(2, model.getAddressBook().getPersonList().size());
        assertTrue(result.getFeedbackToUser().contains("Imported 2 student(s)"));
        assertTrue(result.getFeedbackToUser().contains("Alice Tan"));
    }
}
