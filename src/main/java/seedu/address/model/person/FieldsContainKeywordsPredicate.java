package seedu.address.model.person;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Matches every requested field, with any keyword sufficient within a field.
 */
public class FieldsContainKeywordsPredicate implements Predicate<Person> {
    /**
     * Defines fields supported by a prefixed find command.
     */
    public enum Field {
        NAME, PHONE, GUARDIAN_NAME, GUARDIAN_PHONE, EMAIL, ADDRESS, TAG,
        GUARDIAN_EMAIL, GUARDIAN_ADDRESS, GUARDIAN_RELATIONSHIP
    }

    private final Map<Field, List<String>> keywords;

    /**
     * Constructs a predicate for the requested fields.
     *
     * @param keywords Keywords to match within each field.
     */
    public FieldsContainKeywordsPredicate(Map<Field, List<String>> keywords) {
        this.keywords = Map.copyOf(keywords);
    }

    /**
     * Returns whether the student matches every requested field.
     */
    @Override
    public boolean test(Person person) {
        return keywords.entrySet().stream().allMatch(entry -> {
            if (entry.getKey() == Field.TAG) {
                return person.getTags().stream().anyMatch(tag -> entry.getValue().stream()
                        .anyMatch(keyword -> tag.tagName.toLowerCase(Locale.ROOT)
                                .contains(keyword.toLowerCase(Locale.ROOT))));
            }
            String value = switch (entry.getKey()) {
                case NAME -> person.getName().fullName;
                case PHONE -> person.getPhone().value;
                case EMAIL -> person.getEmail().value;
                case ADDRESS -> person.getAddress().value;
                case GUARDIAN_EMAIL -> person.getGuardian().map(guardian -> guardian.getEmail().value).orElse("");
                case GUARDIAN_ADDRESS -> person.getGuardian().map(guardian -> guardian.getAddress().value).orElse("");
                case GUARDIAN_RELATIONSHIP -> person.getGuardian()
                        .map(guardian -> guardian.getRelationship().value).orElse("");
                case TAG -> throw new IllegalStateException("Tag matching is handled separately");
                case GUARDIAN_NAME -> person.getGuardian().map(guardian -> guardian.getName().fullName).orElse("");
                case GUARDIAN_PHONE -> person.getGuardian().map(guardian -> guardian.getPhone().value).orElse("");
            };
            return entry.getValue().stream().anyMatch(keyword ->
                    value.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)));
        });
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FieldsContainKeywordsPredicate otherPredicate
                && keywords.equals(otherPredicate.keywords);
    }

    @Override
    public int hashCode() {
        return keywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
