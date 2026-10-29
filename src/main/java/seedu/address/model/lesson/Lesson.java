package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import seedu.address.model.lesson.exceptions.LessonException;

/**
 * Represents a logical lesson with a stable internal ID and an immutable history of revisions.
 * The getters without a month or date describe the latest revision, which is what the lesson will be going
 * forward. Historical attendance and fees use {@link #getRevisionOn(LocalDate)} and {@link #getCostFor(YearMonth)}.
 */
public class Lesson {
    public static final String CONSTRAINTS = """
            Lessons are defined as `{year}-{subj}-{day}-{starttime}-{endtime}-{cost}`;
            student lesson references must use `{year}-{subj}-{day}-{starttime}-{endtime}` without a cost,
            where:
            year is a four-digit year from 2000 to 9999 eg. 2026
            subject is one of the allowed subjects
            day is any day of the week
            startTime and endTime are any allowed hour
            cost is any non negative integer
            """;
    public static final int MIN_YEAR = 2000;
    public static final int MAX_YEAR = 9999;
    public static final String YEAR_CONSTRAINT = "Year must be a four-digit year from 2000 to 9999 eg. 2026";
    public static final String SUBJECT_CONSTRAINT = "Subject must be one of the allowed subjects eg. ENGLISH";
    public static final String DAY_CONSTRAINT = "Day is any day of the week eg. MONDAY";
    public static final String TIME_CONSTRAINT = "Time must be one of the allowed hours between ten to twenty";
    public static final String TIME_ORDER_CONSTRAINT = "Start time must be before end time";
    public static final String COST_CONSTRAINT = "Cost must be a non negative integer";
    public static final String STUDENT_REFERENCE_CONSTRAINT = "Student lesson references must be in the form "
            + "{year}-{subj}-{day}-{starttime}-{endtime}, without a cost.";
    public static final String MESSAGE_REVISION_TOO_EARLY = "A lesson change must take effect on or after %s, "
            + "the month its latest change takes effect.";

    public static final String MESSAGE_REVISION_NOT_FUTURE = "A lesson change must take effect after %s, "
            + "the current billing month.";

    private final String uid;
    /** Revisions sorted by the month they take effect, earliest first. Never empty. */
    private final List<LessonRevision> revisions;

    /**
     * Creates a new lesson whose only revision applies from January of its year.
     * Its stable ID is independent of its schedule, so a vacated slot can be reused safely.
     */
    public Lesson(Year year, Subject subject, Day day, Time startTime, Time endTime, int cost) {
        this(UUID.randomUUID().toString(), List.of(new LessonRevision(requireNonNull(year), subject, day,
                startTime, endTime, cost,
                YearMonth.of(year.getValue(), 1))));
    }

    /**
     * Creates a lesson from its full revision history.
     *
     * @param uid stable internal ID; if null or blank, the ID of the earliest revision is used
     * @param revisions at least one revision, with no two taking effect in the same month
     */
    public Lesson(String uid, List<LessonRevision> revisions) {
        requireNonNull(revisions);
        if (revisions.isEmpty()) {
            throw new LessonException("A lesson must have at least one revision.");
        }
        List<LessonRevision> sorted = new ArrayList<>(revisions);
        sorted.sort(Comparator.comparing(LessonRevision::getEffectiveFrom));
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getEffectiveFrom().equals(sorted.get(i - 1).getEffectiveFrom())) {
                throw new LessonException("A lesson cannot have two revisions taking effect in the same month.");
            }
        }
        this.revisions = Collections.unmodifiableList(sorted);
        this.uid = uid == null || uid.isBlank() ? sorted.get(0).getId() : uid.trim();
    }

    public static boolean isValidCost(int cost) {
        return (cost >= 0);
    }

    /** Returns true if {@code startTime} is strictly before {@code endTime}. */
    public static boolean isValidTimeRange(Time startTime, Time endTime) {
        requireNonNull(startTime);
        requireNonNull(endTime);
        return startTime.ordinal() < endTime.ordinal();
    }

    /** Returns true if {@code year} is a valid yyyy year. */
    public static boolean isValidYear(String year) {
        if (year == null) {
            return false;
        }
        String trimmedYear = year.trim();
        if (!trimmedYear.matches("[0-9]{4}")) {
            return false;
        }
        int yearValue = Integer.parseInt(trimmedYear);
        return yearValue >= MIN_YEAR && yearValue <= MAX_YEAR;
    }

    /** Returns the stable internal ID, which does not change when the lesson is edited. */
    public String getUid() {
        return uid;
    }

    public List<LessonRevision> getRevisions() {
        return revisions;
    }

    public LessonRevision getLatestRevision() {
        return revisions.get(revisions.size() - 1);
    }

    /**
     * Returns the revision in effect during {@code month}, or the earliest revision if {@code month} is before
     * every revision.
     */
    public LessonRevision getRevisionFor(YearMonth month) {
        requireNonNull(month);
        LessonRevision result = revisions.get(0);
        for (LessonRevision revision : revisions) {
            if (!revision.getEffectiveFrom().isAfter(month)) {
                result = revision;
            }
        }
        return result;
    }

    /** Returns the revision in effect on {@code date}. */
    public LessonRevision getRevisionOn(LocalDate date) {
        requireNonNull(date);
        return getRevisionFor(YearMonth.from(date));
    }

    /**
     * Returns the fee charged for {@code month}: the cost of the revision in effect that month, if that revision
     * is held in the same year, or zero otherwise.
     */
    public int getCostFor(YearMonth month) {
        requireNonNull(month);
        return revisions.stream()
                .filter(revision -> !revision.getEffectiveFrom().isAfter(month))
                .reduce((earlier, later) -> later)
                .filter(revision -> revision.getYear().getValue() == month.getYear())
                .map(LessonRevision::getCost)
                .orElse(0);
    }

    /** Returns true if any revision of this lesson, past or current, has the given lesson ID. */
    public boolean hasRevisionId(String id) {
        return revisions.stream().anyMatch(revision -> revision.getId().equals(id));
    }

    /**
     * Returns a copy of this lesson with {@code revision} added. A revision taking effect in the same month as the
     * latest revision replaces it, so a change can be corrected before it takes effect.
     *
     * @throws LessonException if the revision is not in a future month or precedes the latest revision
     */
    public Lesson withRevision(LessonRevision revision) {
        return withRevision(revision, YearMonth.now());
    }

    /** Adds or replaces a future revision, using the supplied current billing month. */
    public Lesson withRevision(LessonRevision revision, YearMonth currentMonth) {
        requireNonNull(revision);
        requireNonNull(currentMonth);
        if (!revision.getEffectiveFrom().isAfter(currentMonth)) {
            throw new LessonException(String.format(MESSAGE_REVISION_NOT_FUTURE, currentMonth));
        }
        YearMonth latestMonth = getLatestRevision().getEffectiveFrom();
        if (revision.getEffectiveFrom().isBefore(latestMonth)) {
            throw new LessonException(String.format(MESSAGE_REVISION_TOO_EARLY, latestMonth));
        }
        List<LessonRevision> updated = new ArrayList<>(revisions);
        updated.removeIf(existing -> existing.getEffectiveFrom().equals(revision.getEffectiveFrom()));
        updated.add(revision);
        return new Lesson(uid, updated);
    }

    /** Returns a copy of this lesson with a different stable ID. */
    public Lesson withUid(String newUid) {
        return new Lesson(requireNonNull(newUid), revisions);
    }

    public Year getYear() {
        return getLatestRevision().getYear();
    }

    public Subject getSubject() {
        return getLatestRevision().getSubject();
    }

    public Day getDay() {
        return getLatestRevision().getDay();
    }

    public Time getStartTime() {
        return getLatestRevision().getStartTime();
    }

    public Time getEndTime() {
        return getLatestRevision().getEndTime();
    }

    public int getCost() {
        return getLatestRevision().getCost();
    }

    /** Returns true if a session can be held on {@code date} under the revision in effect on that date. */
    public boolean isValidSessionDate(LocalDate date) {
        return getRevisionOn(date).isValidSessionDate(date);
    }

    /** Returns the user-facing ID of the latest revision. */
    public String getId() {
        return getLatestRevision().getId();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Lesson otherLesson)) {
            return false;
        }
        return uid.equals(otherLesson.uid) && revisions.equals(otherLesson.revisions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, revisions);
    }

    @Override
    public String toString() {
        return String.format("%s-%d", getId(), getCost());
    }
}
