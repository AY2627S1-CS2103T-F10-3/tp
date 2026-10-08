package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalGuardians.MARY;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class JsonAdaptedGuardianTest {
    @Test
    public void toModelType_validGuardian_returnsGuardian() throws Exception {
        assertEquals(MARY, new JsonAdaptedGuardian(MARY).toModelType());
    }

    @Test
    public void toModelType_missingOrInvalidField_throwsIllegalValueException() {
        String[] values = {"Mary Tan", "91234567", "mary@example.com", "Mother", "123 Example Road"};
        for (int i = 0; i < values.length; i++) {
            String[] missing = values.clone();
            missing[i] = null;
            assertThrows(IllegalValueException.class, () -> adapted(missing).toModelType());
            String[] blank = values.clone();
            blank[i] = "";
            assertThrows(IllegalValueException.class, () -> adapted(blank).toModelType());
        }
        assertThrows(IllegalValueException.class, () ->
                new JsonAdaptedGuardian("Mary!", "91234567", "mary@example.com", "Mother", "Road").toModelType());
        assertThrows(IllegalValueException.class, () ->
                new JsonAdaptedGuardian("Mary", "abc", "mary@example.com", "Mother", "Road").toModelType());
        assertThrows(IllegalValueException.class, () ->
                new JsonAdaptedGuardian("Mary", "91234567", "invalid", "Mother", "Road").toModelType());
    }

    private JsonAdaptedGuardian adapted(String[] fields) {
        return new JsonAdaptedGuardian(fields[0], fields[1], fields[2], fields[3], fields[4]);
    }
}
