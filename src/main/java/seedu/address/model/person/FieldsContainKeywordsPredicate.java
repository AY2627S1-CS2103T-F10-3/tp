package seedu.address.model.person;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/** Matches every requested field, with any keyword sufficient within a field. */
public class FieldsContainKeywordsPredicate implements Predicate<Person> {
    /** Fields supported by a prefixed find command. */
    public enum Field {
        NAME, PHONE, GUARDIAN_NAME, GUARDIAN_PHONE
    }

    private final Map<Field, List<String>> keywords;

    public FieldsContainKeywordsPredicate(Map<Field, List<String>> keywords) {
        this.keywords = Map.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return keywords.entrySet().stream().allMatch(entry -> {
            String value = switch (entry.getKey()) {
                case NAME -> person.getName().fullName;
                case PHONE -> person.getPhone().value;
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
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
