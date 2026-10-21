package seedu.address.logic.importer;

import java.util.List;
import java.util.Locale;

import seedu.address.logic.parser.exceptions.ParseException;

/** Provides validated, named access to a parsed CSV row. */
public class CsvRow {
    private final List<String> headers;
    private final List<String> values;

    public CsvRow(List<String> headers, List<String> values) {
        this.headers = headers;
        this.values = values;
    }

    public String get(String field) throws ParseException {
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).trim().toLowerCase(Locale.ROOT).equals(field)) {
                return i < values.size() ? values.get(i).trim() : "";
            }
        }
        throw new ParseException("Missing required column: " + field);
    }

    public boolean hasField(String field) {
        return headers.stream().anyMatch(header -> header.trim().toLowerCase(Locale.ROOT).equals(field));
    }
}
