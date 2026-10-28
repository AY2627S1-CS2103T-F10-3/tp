package seedu.address.logic.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class CsvParserTest {
    @Test
    public void parseLine_quotedFields_success() throws ParseException {
        assertEquals(List.of("Alice", "12, Main Street", "She said \"hello\""),
                CsvParser.parseLine("Alice,\"12, Main Street\",\"She said \"\"hello\"\"\""));
    }

    @Test
    public void parseLine_unclosedQuote_throwsParseException() {
        assertThrows(ParseException.class, () -> CsvParser.parseLine("Alice,\"12, Main Street"));
    }

    @Test
    public void parseRecord_multilineQuotedField_preservesLineBreak() throws Exception {
        String csv = "Alice,\"1 Road\nSingapore\",email@example.com\n";
        BufferedReader reader = new BufferedReader(new StringReader(csv));
        assertEquals(List.of("Alice", "1 Road\nSingapore", "email@example.com"), CsvParser.parseRecord(reader));
    }
}
