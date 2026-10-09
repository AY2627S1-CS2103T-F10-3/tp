package seedu.address.storage;

import java.time.Year;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;

/** Jackson-friendly version of {@link Lesson}. */
class JsonAdaptedLesson {
    private final String year;
    private final String subject;
    private final String day;
    private final String startTime;
    private final String endTime;
    private final int cost;

    /**
     * Constructs a JSON-adapted lesson from serialized fields.
     */
    @JsonCreator
    public JsonAdaptedLesson(@JsonProperty("year") String year, @JsonProperty("subject") String subject,
            @JsonProperty("day") String day, @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime, @JsonProperty("cost") int cost) {
        this.year = year;
        this.subject = subject;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
    }

    /** Constructs a JSON-adapted lesson from a model lesson. */
    public JsonAdaptedLesson(Lesson source) {
        year = source.getYear().toString();
        subject = source.getSubject().name();
        day = source.getDay().name();
        startTime = source.getStartTime().name();
        endTime = source.getEndTime().name();
        cost = source.getCost();
    }

    /** Converts this JSON-adapted lesson into a model lesson. */
    public Lesson toModelType() throws IllegalValueException {
        try {
            if (!Lesson.isValidYear(year)) {
                throw new IllegalArgumentException(Lesson.YEAR_CONSTRAINT);
            }
            Year modelYear = Year.parse(year.trim());
            Subject modelSubject = Subject.valueOf(subject.trim());
            Day modelDay = Day.valueOf(day.trim());
            Time modelStartTime = Time.valueOf(startTime.trim());
            Time modelEndTime = Time.valueOf(endTime.trim());
            if (!Lesson.isValidCost(cost)) {
                throw new IllegalArgumentException(Lesson.COST_CONSTRAINT);
            }
            return new Lesson(modelYear, modelSubject, modelDay, modelStartTime, modelEndTime, cost);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalValueException(Lesson.CONSTRAINTS, exception);
        }
    }
}
