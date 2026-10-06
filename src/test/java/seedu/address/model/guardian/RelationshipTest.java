package seedu.address.model.guardian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RelationshipTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Relationship(null));
    }

    @Test
    public void constructor_blank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Relationship(""));
        assertThrows(IllegalArgumentException.class, () -> new Relationship("   "));
        assertThrows(IllegalArgumentException.class, () -> new Relationship("\t"));
    }

    @Test
    public void constructor_validRelationship_storesValue() {
        Relationship relationship = new Relationship("Legal guardian");

        assertEquals("Legal guardian", relationship.value);
        assertEquals("Legal guardian", relationship.toString());
    }

    @Test
    public void isValidRelationship() {
        assertFalse(Relationship.isValidRelationship(""));
        assertFalse(Relationship.isValidRelationship("   "));
        assertFalse(Relationship.isValidRelationship("\t\n"));

        assertTrue(Relationship.isValidRelationship("Mother"));
        assertTrue(Relationship.isValidRelationship("Grandparent"));
        assertTrue(Relationship.isValidRelationship("Legal guardian"));
    }

    @Test
    public void equals() {
        Relationship mother = new Relationship("Mother");

        assertEquals(mother, mother);
        assertEquals(mother, new Relationship("Mother"));

        assertNotEquals(mother, new Relationship("Father"));
        assertNotEquals(mother, new Relationship("mother"));
        assertNotEquals(mother, null);
        assertNotEquals(mother, "Mother");
    }

    @Test
    public void hashCode_equalRelationships_sameHashCode() {
        Relationship first = new Relationship("Mother");
        Relationship second = new Relationship("Mother");

        assertEquals(first.hashCode(), second.hashCode());
    }
}
