package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.Year;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a year from a lesson string.
     */
    public static Year parseYear(String year) throws ParseException {
        requireNonNull(year);
        try {
            return Year.parse(year.trim());
        } catch (RuntimeException exception) {
            throw new ParseException(Lesson.YEAR_CONSTRAINT, exception);
        }
    }

    /**
     * Parses a subject from a lesson string.
     */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        try {
            return Subject.valueOf(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(Lesson.SUBJECT_CONSTRAINT, exception);
        }
    }

    /**
     * Parses a day from a lesson string.
     */
    public static Day parseDay(String day) throws ParseException {
        requireNonNull(day);
        try {
            return Day.valueOf(day.trim());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(Lesson.DAY_CONSTRAINT, exception);
        }
    }

    /**
     * Parses a time from a lesson string.
     */
    public static Time parseTime(String time) throws ParseException {
        requireNonNull(time);
        try {
            return Time.valueOf(time.trim());
        } catch (IllegalArgumentException exception) {
            throw new ParseException(Lesson.TIME_CONSTRAINT, exception);
        }
    }

    /**
     * Parses a lesson cost.
     */
    public static int parseCost(String cost) throws ParseException {
        requireNonNull(cost);
        final int parsedCost;
        try {
            parsedCost = Integer.parseInt(cost.trim());
        } catch (NumberFormatException exception) {
            throw new ParseException(Lesson.COST_CONSTRAINT, exception);
        }
        if (!Lesson.isValidCost(parsedCost)) {
            throw new ParseException(Lesson.COST_CONSTRAINT);
        }
        return parsedCost;
    }

    /**
     * Parses a lesson string into a {@link Lesson}.
     *
     * @param lesson lesson in year-subject-day-start time-end time-cost format
     * @return the parsed lesson
     * @throws ParseException if any lesson component is invalid
     */
    public static Lesson parseLesson(String lesson) throws ParseException {
        requireNonNull(lesson);
        String trimmedLesson = lesson.trim();
        String[] lessonParts = trimmedLesson.split("-", -1);
        if (lessonParts.length != 6) {
            throw new ParseException(Lesson.CONSTRAINTS);
        }
        Year year = parseYear(lessonParts[0]);
        Subject subject = parseSubject(lessonParts[1]);
        Day day = parseDay(lessonParts[2]);
        Time startTime = parseTime(lessonParts[3]);
        Time endTime = parseTime(lessonParts[4]);
        int cost = parseCost(lessonParts[5]);
        return new Lesson(year, subject, day, startTime, endTime, cost);
    }

    /**
     * Parses a collection of lesson strings into a set of lessons.
     *
     * @param lessons lesson strings to parse
     * @return the parsed lessons
     * @throws ParseException if any lesson string is invalid
     */
    public static Set<Lesson> parseLessons(Collection<String> lessons) throws ParseException {
        requireNonNull(lessons);
        final Set<Lesson> lessonSet = new HashSet<>();
        for (String lesson : lessons) {
            lessonSet.add(parseLesson(lesson));
        }
        return lessonSet;
    }

}
