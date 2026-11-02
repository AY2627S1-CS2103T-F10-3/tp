package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

/**
 * Matches every requested field, with any keyword sufficient within a field.
 */
public class FieldsContainKeywordsPredicate implements Predicate<Person> {
    /**
     * Defines fields supported by a prefixed find command.
     */
    public enum Field {
        NAME, PHONE, GUARDIAN_NAME, GUARDIAN_PHONE, EMAIL, ADDRESS, TAG,
        GUARDIAN_EMAIL, GUARDIAN_ADDRESS, GUARDIAN_RELATIONSHIP, LESSON
    }

    private final Map<Field, List<String>> keywords;
    private final YearMonth currentMonth;

    /**
     * Constructs a predicate for the requested fields.
     *
     * @param keywords Keywords to match within each field.
     */
    public FieldsContainKeywordsPredicate(Map<Field, List<String>> keywords) {
        this(keywords, YearMonth.now());
    }

    /**
     * Constructs a predicate using {@code currentMonth} to select each lesson's active revision.
     */
    FieldsContainKeywordsPredicate(Map<Field, List<String>> keywords, YearMonth currentMonth) {
        this.keywords = Map.copyOf(keywords);
        this.currentMonth = requireNonNull(currentMonth);
    }

    /**
     * Returns whether the student matches every requested field.
     */
    @Override
    public boolean test(Person person) {
        return keywords.entrySet().stream().allMatch(entry -> {
            if (entry.getKey() == Field.LESSON) {
                return person.getLessons().stream().map(lesson -> lesson.getRevisionFor(currentMonth))
                        .anyMatch(revision -> entry.getValue().stream()
                                .anyMatch(keyword -> matchesLessonId(revision.getId(), keyword)));
            }
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
                case LESSON -> throw new IllegalStateException("Lesson matching is handled separately");
            };
            return entry.getValue().stream().anyMatch(keyword ->
                    value.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)));
        });
    }

    private boolean matchesLessonId(String lessonId, String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        String normalizedId = lessonId.toLowerCase(Locale.ROOT);
        if (isCompleteLessonId(keyword)) {
            return normalizedId.equals(normalizedKeyword);
        }
        return normalizedId.contains(normalizedKeyword);
    }

    private boolean isCompleteLessonId(String keyword) {
        String[] components = keyword.split("-", -1);
        if (components.length != 5 || !Lesson.isValidYear(components[0])) {
            return false;
        }
        try {
            Subject.valueOf(components[1].toUpperCase(Locale.ROOT));
            Day.valueOf(components[2].toUpperCase(Locale.ROOT));
            Time startTime = Time.valueOf(components[3].toUpperCase(Locale.ROOT));
            Time endTime = Time.valueOf(components[4].toUpperCase(Locale.ROOT));
            return Lesson.isValidTimeRange(startTime, endTime);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FieldsContainKeywordsPredicate otherPredicate
                && keywords.equals(otherPredicate.keywords) && currentMonth.equals(otherPredicate.currentMonth);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(keywords, currentMonth);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).add("currentMonth", currentMonth).toString();
    }
}
