package seedu.address.logic.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.parser.exceptions.ParseException;

/** Parses one CSV row, including quoted values and escaped quotes. */
public final class CsvParser {
    private CsvParser() {}

    /**
     * Parses a CSV row into its individual cell values.
     *
     * @param line CSV row to parse
     * @return parsed cell values, or {@code null} if {@code line} is null
     * @throws ParseException if the row contains an unclosed quoted value
     */
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

    /**
     * Reads and parses one CSV record. A record may span multiple physical lines when a quoted
     * value contains a line break.
     *
     * @param reader source of CSV lines
     * @return parsed values, or {@code null} at end of file
     * @throws IOException if the source cannot be read
     * @throws ParseException if the record contains an unclosed quoted value
     */
    public static List<String> parseRecord(BufferedReader reader) throws IOException, ParseException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        StringBuilder record = new StringBuilder(line);
        boolean quoted = isQuotedAtEnd(line, false);
        while (quoted) {
            record.append('\n');
            String continuation = reader.readLine();
            if (continuation == null) {
                throw new ParseException("Unclosed quote in CSV row.");
            }
            record.append(continuation);
            quoted = isQuotedAtEnd(continuation, quoted);
        }
        return parseLine(record.toString());
    }

    private static boolean isQuotedAtEnd(String line, boolean quoted) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    i++;
                } else {
                    quoted = !quoted;
                }
            }
        }
        return quoted;
    }
}
