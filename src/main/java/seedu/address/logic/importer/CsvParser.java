package seedu.address.logic.importer;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;

/** Parses one CSV row, including quoted values and escaped quotes. */
public final class CsvParser {
    private CsvParser() {}

    public static List<String> parseLine(String line) throws ParseException {
        if (line == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cell.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                result.add(cell.toString());
                cell.setLength(0);
            } else {
                cell.append(character);
            }
        }
        if (quoted) {
            throw new ParseException("Unclosed quote in CSV row.");
        }
        result.add(cell.toString());
        return result;
    }
}
