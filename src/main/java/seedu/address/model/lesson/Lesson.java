package seedu.address.model.lesson;

import seedu.address.model.lesson.exceptions.LessonException;

import java.time.Year;

import static java.util.Objects.requireNonNull;

public class Lesson {
    public static String CONSTRAINTS = """
            Lessons must be in the form `{year}-{subj}-{day}-{starttime}-{endtime}-{cost}`,
            where:
            year is of the form yyyy eg. 2026
            subject is one of the allowed subjects
            day is any day of the week
            startTime and endTime are any allowed hour
            cost is any non negative integer
            """;
    public static String YEAR_CONSTRAINT = "Year must be of the form yyyy eg. 2026";
    public static String SUBJECT_CONSTRAINT = "Subject must be one of the allowed subjects eg. ENGLISH";
    public static String DAY_CONSTRAINT  = "Day is any day of the week eg. MONDAY";
    public static String TIME_CONSTRAINT = "Time must be one of the allowed hours between ten to nineteen";
    public static String COST_CONSTRAINT = "Cost must be a non negative integer";

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

    public Year getYear() { return year; }
    public Subject getSubject() { return subject; }
    public Day getDay() { return day; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }
    public int getCost() { return cost; }

}
