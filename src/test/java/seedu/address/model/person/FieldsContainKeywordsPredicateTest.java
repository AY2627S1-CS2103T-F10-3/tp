package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.guardian.Guardian;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.person.FieldsContainKeywordsPredicate.Field;
import seedu.address.testutil.PersonBuilder;

public class FieldsContainKeywordsPredicateTest {
    private final Person student = new PersonBuilder().withName("Alice Bob").withPhone("91234567")
            .withGuardian(new Guardian(new Name("Mary Tan"), new Phone("98765432"),
                    new Email("mary@example.com"), new Relationship("Mother"), new Address("Main Street")))
            .build();

    @Test
    public void equals_sameCriteria_returnsTrue() {
        FieldsContainKeywordsPredicate predicate = new FieldsContainKeywordsPredicate(
                Map.of(Field.NAME, List.of("Alice", "Bob")));
        FieldsContainKeywordsPredicate equalPredicate = new FieldsContainKeywordsPredicate(
                Map.of(Field.NAME, List.of("Alice", "Bob")));

        assertEquals(predicate, predicate);
        assertEquals(predicate, equalPredicate);
        assertEquals(equalPredicate, predicate);
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("Alice Bob"));
        assertFalse(predicate.equals(new FieldsContainKeywordsPredicate(
                Map.of(Field.NAME, List.of("Mary")))));
        assertFalse(predicate.equals(new FieldsContainKeywordsPredicate(
                Map.of(Field.GUARDIAN_NAME, List.of("Alice", "Bob")))));
    }

    @Test
    public void hashCode_equalPredicates_workAsHashSetKeys() {
        FieldsContainKeywordsPredicate predicate = new FieldsContainKeywordsPredicate(
                Map.of(Field.NAME, List.of("Alice", "Bob"), Field.GUARDIAN_PHONE, List.of("9876")));
        FieldsContainKeywordsPredicate equalPredicate = new FieldsContainKeywordsPredicate(
                Map.of(Field.GUARDIAN_PHONE, List.of("9876"), Field.NAME, List.of("Alice", "Bob")));
        Set<FieldsContainKeywordsPredicate> predicates = new HashSet<>();
        predicates.add(predicate);

        assertTrue(predicates.contains(equalPredicate));
        assertFalse(predicates.add(equalPredicate));
        assertEquals(1, predicates.size());
        assertEquals(predicate.hashCode(), equalPredicate.hashCode());
        assertTrue(predicates.remove(equalPredicate));
        assertTrue(predicates.isEmpty());
    }

    @Test
    public void test_partialFields_matchOnlyRequestedField() {
        assertTrue(matches(Field.NAME, "LIC", student));
        assertTrue(matches(Field.PHONE, "9123", student));
        assertTrue(matches(Field.GUARDIAN_NAME, "ARY", student));
        assertTrue(matches(Field.GUARDIAN_PHONE, "9876", student));
        assertFalse(matches(Field.NAME, "Mary", student));
        assertFalse(matches(Field.PHONE, "9876", student));
        assertFalse(matches(Field.GUARDIAN_NAME, "Alice", student));
        assertFalse(matches(Field.GUARDIAN_PHONE, "9123", student));
    }

    @Test
    public void test_additionalFields_matchesOnlyRequestedFields() {
        Person person = new PersonBuilder(student).withEmail("alice@school.com").withAddress("East Avenue")
                .withTags("Unpaid", "Scholarship").build();
        assertTrue(matches(Field.EMAIL, "SCHOOL", person));
        assertTrue(matches(Field.ADDRESS, "aven", person));
        assertTrue(matches(Field.TAG, "PAID", person));
        assertTrue(matches(Field.GUARDIAN_EMAIL, "MARY@", person));
        assertTrue(matches(Field.GUARDIAN_ADDRESS, "MAIN", person));
        assertTrue(matches(Field.GUARDIAN_RELATIONSHIP, "MOTH", person));
        assertFalse(matches(Field.EMAIL, "mary", person));
        assertFalse(matches(Field.ADDRESS, "Main", person));
        assertFalse(matches(Field.GUARDIAN_EMAIL, "school", person));
        assertFalse(matches(Field.GUARDIAN_ADDRESS, "East", person));
        assertFalse(matches(Field.GUARDIAN_RELATIONSHIP, "father", person));
        assertFalse(matches(Field.TAG, "missing", person));
        Person withoutOptionalFields = new PersonBuilder().withTags().build();
        for (Field field : List.of(Field.TAG, Field.GUARDIAN_EMAIL, Field.GUARDIAN_ADDRESS,
                Field.GUARDIAN_RELATIONSHIP)) {
            assertFalse(matches(field, "a", withoutOptionalFields));
        }
        assertTrue(new FieldsContainKeywordsPredicate(Map.of(Field.EMAIL, List.of("missing", "school"),
                Field.TAG, List.of("scholar"))).test(person));
        assertFalse(new FieldsContainKeywordsPredicate(Map.of(Field.EMAIL, List.of("school"),
                Field.TAG, List.of("missing"))).test(person));
    }

    @Test
    public void test_missingGuardian_doesNotMatchGuardianFields() {
        Person legacyStudent = new PersonBuilder().build();
        assertFalse(matches(Field.GUARDIAN_NAME, "Mary", legacyStudent));
        assertFalse(matches(Field.GUARDIAN_PHONE, "9876", legacyStudent));
    }

    @Test
    public void test_multipleFields_requireAllFieldsButAnyKeywordWithinField() {
        assertTrue(new FieldsContainKeywordsPredicate(Map.of(Field.NAME, List.of("Carol", "lic"),
                Field.GUARDIAN_PHONE, List.of("9876"))).test(student));
        assertFalse(new FieldsContainKeywordsPredicate(Map.of(Field.NAME, List.of("lic"),
                Field.GUARDIAN_PHONE, List.of("1111"))).test(student));
    }

    private boolean matches(Field field, String keyword, Person person) {
        return new FieldsContainKeywordsPredicate(Map.of(field, List.of(keyword))).test(person);
    }
}
