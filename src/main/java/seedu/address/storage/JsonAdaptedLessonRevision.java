package seedu.address.storage;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.lesson.exceptions.LessonException;

/** Jackson-friendly version of {@link LessonRevision}. */
class JsonAdaptedLessonRevision {
    public static final String MESSAGE_INVALID_EFFECTIVE_FROM = "Lesson revision's effectiveFrom must be yyyy-MM.";

    private final String year;
    private final String subject;
    private final String day;
    private final String startTime;
    private final String endTime;
    private final int cost;
    private final String effectiveFrom;

    /**
     * Constructs a JSON-adapted lesson revision from serialized fields.
     */
    @JsonCreator
    public JsonAdaptedLessonRevision(@JsonProperty("year") String year, @JsonProperty("subject") String subject,
            @JsonProperty("day") String day, @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime, @JsonProperty("cost") int cost,
            @JsonProperty("effectiveFrom") String effectiveFrom) {
        this.year = year;
        this.subject = subject;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.effectiveFrom = effectiveFrom;
    }

    /** Constructs a JSON-adapted lesson revision from a model revision. */
    public JsonAdaptedLessonRevision(LessonRevision source) {
        year = source.getYear().toString();
        subject = source.getSubject().name();
        day = source.getDay().name();
        startTime = source.getStartTime().name();
        endTime = source.getEndTime().name();
        cost = source.getCost();
        effectiveFrom = source.getEffectiveFrom().toString();
    }

    /**
     * Converts this JSON-adapted revision into a model revision. A missing {@code effectiveFrom} defaults to
     * January of the revision's year.
     */
    public LessonRevision toModelType() throws IllegalValueException {
        try {
            if (!Lesson.isValidYear(year)) {
                throw new IllegalArgumentException(Lesson.YEAR_CONSTRAINT);
            }
            Year modelYear = Year.parse(year.trim());
            YearMonth modelEffectiveFrom = effectiveFrom == null
                    ? YearMonth.of(modelYear.getValue(), 1) : parseEffectiveFrom(effectiveFrom);
            return new LessonRevision(modelYear, Subject.valueOf(subject.trim()), Day.valueOf(day.trim()),
                    Time.valueOf(startTime.trim()), Time.valueOf(endTime.trim()), cost, modelEffectiveFrom);
        } catch (IllegalArgumentException | NullPointerException | LessonException exception) {
            throw new IllegalValueException(Lesson.CONSTRAINTS, exception);
        }
    }

    private static YearMonth parseEffectiveFrom(String value) throws IllegalValueException {
        try {
            return YearMonth.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(MESSAGE_INVALID_EFFECTIVE_FROM, exception);
        }
    }
}
