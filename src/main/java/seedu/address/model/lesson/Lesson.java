package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.Year;
import java.util.Objects;

import seedu.address.model.lesson.exceptions.LessonException;

/** Represents a lesson with a subject, schedule, and session cost. */
public class Lesson {
    public static final String CONSTRAINTS = """
            Lessons are defined as `{year}-{subj}-{day}-{starttime}-{endtime}-{cost}`;
            student lesson references must use `{year}-{subj}-{day}-{starttime}-{endtime}` without a cost,
            where:
            year is of the form yyyy eg. 2026
            subject is one of the allowed subjects
            day is any day of the week
            startTime and endTime are any allowed hour
            cost is any non negative integer
            """;
    public static final String YEAR_CONSTRAINT = "Year must be of the form yyyy eg. 2026";
    public static final String SUBJECT_CONSTRAINT = "Subject must be one of the allowed subjects eg. ENGLISH";
    public static final String DAY_CONSTRAINT = "Day is any day of the week eg. MONDAY";
    public static final String TIME_CONSTRAINT = "Time must be one of the allowed hours between ten to nineteen";
    public static final String TIME_ORDER_CONSTRAINT = "Start time must be before end time";
    public static final String COST_CONSTRAINT = "Cost must be a non negative integer";
    public static final String STUDENT_REFERENCE_CONSTRAINT = "Student lesson references must be in the form "
            + "{year}-{subj}-{day}-{starttime}-{endtime}, without a cost.";

    // Fields
    private final Year year;
    private final Subject subject;
    private final Day day;
    private final Time startTime;
    private final Time endTime;
    private final int cost;

    /**
     * Constructs a {@code Lesson}
     *
     * @param year Year lesson is conducted in.
     * @param subject Subject of the lesson.
     * @param day Day the lesson is conducted on.
     * @param startTime Time the lesson starts at.
     * @param endTime Time the lesson ends at.
     * @param cost Cost per session of a lesson.
     */
    public Lesson(Year year, Subject subject, Day day, Time startTime, Time endTime, int cost) {
        requireNonNull(year);
        requireNonNull(subject);
        requireNonNull(day);
        requireNonNull(startTime);
        requireNonNull(endTime);
        requireNonNull(cost);
        if (!isValidTimeRange(startTime, endTime)) {
            throw new LessonException(TIME_ORDER_CONSTRAINT);
        }
        this.year = year;
        this.subject = subject;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        if (isValidCost(cost)) {
            this.cost = cost;
        } else {
            throw new LessonException("Cost must be non-negative.");
        }
    }

    /**
     * Checks if the given cost is valid.
     *
     * @param cost Given cost.
     * @return Whether the given cost is valid.
     */
    public static boolean isValidCost(int cost) {
        return (cost >= 0);
    }

    /** Checks whether a lesson starts before it ends. */
    public static boolean isValidTimeRange(Time startTime, Time endTime) {
        requireNonNull(startTime);
        requireNonNull(endTime);
        return startTime.ordinal() < endTime.ordinal();
    }

    /** Checks if the given string represents a valid lesson year. */
    public static boolean isValidYear(String year) {
        if (year == null) {
            return false;
        }
        return year.trim().matches("[0-9]{4}");
    }

    /** Returns the year in which the lesson is conducted. */
    public Year getYear() {
        return year;
    }

    /** Returns the subject taught in the lesson. */
    public Subject getSubject() {
        return subject;
    }

    /** Returns the day on which the lesson is conducted. */
    public Day getDay() {
        return day;
    }

    /** Returns the lesson's start time. */
    public Time getStartTime() {
        return startTime;
    }

    /** Returns the lesson's end time. */
    public Time getEndTime() {
        return endTime;
    }

    /** Returns the cost of one lesson session. */
    public int getCost() {
        return cost;
    }

    /**
     * Returns whether {@code date} is a valid session date for this lesson.
     * A valid session must fall in the lesson's year and on its scheduled weekday.
     */
    public boolean isValidSessionDate(LocalDate date) {
        requireNonNull(date);
        return year.getValue() == date.getYear() && day.name().equals(date.getDayOfWeek().name());
    }

    /**
     * Returns the stable identifier used to link students to this lesson.
     * ID changes if any of the field changes, but there should be no changes due to
     * the structure of the tuition centre.
     */
    public String getId() {
        return String.format("%s-%s-%s-%s-%s", year, subject, day, startTime, endTime);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Lesson otherLesson)) {
            return false;
        }
        return year.equals(otherLesson.year) && subject == otherLesson.subject && day == otherLesson.day
                && startTime == otherLesson.startTime && endTime == otherLesson.endTime && cost == otherLesson.cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, subject, day, startTime, endTime, cost);
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%s-%s-%s-%d", year, subject, day, startTime, endTime, cost);
    }
}
