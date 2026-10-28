package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Objects;

import seedu.address.model.lesson.exceptions.LessonException;

/**
 * An immutable snapshot of a lesson's schedule and monthly cost, effective from a billing month onwards.
 * Editing a lesson creates a new revision instead of overwriting an old one, so attendance and fees for
 * earlier months keep using the timing and cost that applied at that time.
 */
public final class LessonRevision {
    private final Year year;
    private final Subject subject;
    private final Day day;
    private final Time startTime;
    private final Time endTime;
    private final int cost;
    private final YearMonth effectiveFrom;

    /**
     * Creates a revision. Every field must be present, the start time must be before the end time and the
     * cost must be non-negative.
     */
    public LessonRevision(Year year, Subject subject, Day day, Time startTime, Time endTime, int cost,
            YearMonth effectiveFrom) {
        requireNonNull(year);
        requireNonNull(subject);
        requireNonNull(day);
        requireNonNull(startTime);
        requireNonNull(endTime);
        requireNonNull(effectiveFrom);
        if (!Lesson.isValidTimeRange(startTime, endTime)) {
            throw new LessonException(Lesson.TIME_ORDER_CONSTRAINT);
        }
        if (!Lesson.isValidCost(cost)) {
            throw new LessonException("Cost must be non-negative.");
        }
        this.year = year;
        this.subject = subject;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.effectiveFrom = effectiveFrom;
    }

    public Year getYear() {
        return year;
    }

    public Subject getSubject() {
        return subject;
    }

    public Day getDay() {
        return day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public int getCost() {
        return cost;
    }

    public YearMonth getEffectiveFrom() {
        return effectiveFrom;
    }

    /** Returns a copy of this revision that takes effect from {@code month} instead. */
    public LessonRevision withEffectiveFrom(YearMonth month) {
        return new LessonRevision(year, subject, day, startTime, endTime, cost, month);
    }

    /** Returns the user-facing ID of this revision, eg. {@code 2026-MATH-MONDAY-TEN-ELEVEN}. */
    public String getId() {
        return String.format("%s-%s-%s-%s-%s", year, subject, day, startTime, endTime);
    }

    /** Returns the weekly slot of this revision, eg. {@code TUESDAY TEN-ELEVEN}. */
    public String getScheduleDescription() {
        return String.format("%s %s-%s", day, startTime, endTime);
    }

    /** Returns true if a session of this revision can be held on {@code date}. */
    public boolean isValidSessionDate(LocalDate date) {
        requireNonNull(date);
        return year.getValue() == date.getYear() && day.name().equals(date.getDayOfWeek().name());
    }

    /** Returns true if both revisions describe the same schedule and cost, ignoring when they take effect. */
    public boolean hasSameDetails(LessonRevision other) {
        return other != null && year.equals(other.year) && subject == other.subject && day == other.day
                && startTime == other.startTime && endTime == other.endTime && cost == other.cost;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LessonRevision otherRevision)) {
            return false;
        }
        return hasSameDetails(otherRevision) && effectiveFrom.equals(otherRevision.effectiveFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, subject, day, startTime, endTime, cost, effectiveFrom);
    }

    @Override
    public String toString() {
        return String.format("%s-%d from %s", getId(), cost, effectiveFrom);
    }
}
