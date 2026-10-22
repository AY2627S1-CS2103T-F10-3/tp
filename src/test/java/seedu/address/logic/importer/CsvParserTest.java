package seedu.address.logic.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
