package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.exceptions.LessonException;

/**
 * Jackson-friendly version of {@link Lesson}. The flat fields describe the latest revision and are kept so that
 * files saved by older versions, which have no {@code uid} or {@code revisions}, still load.
 */
class JsonAdaptedLesson {
    private final String uid;
    private final String year;
    private final String subject;
    private final String day;
    private final String startTime;
    private final String endTime;
    private final int cost;
    private final List<JsonAdaptedLessonRevision> revisions = new ArrayList<>();

    /**
     * Constructs a JSON-adapted lesson with a single revision, as saved by older versions.
     */
    public JsonAdaptedLesson(String year, String subject, String day, String startTime, String endTime, int cost) {
        this(null, year, subject, day, startTime, endTime, cost, null);
    }

    /**
     * Constructs a JSON-adapted lesson from serialized fields.
     */
    @JsonCreator
    public JsonAdaptedLesson(@JsonProperty("uid") String uid, @JsonProperty("year") String year,
            @JsonProperty("subject") String subject, @JsonProperty("day") String day,
            @JsonProperty("startTime") String startTime, @JsonProperty("endTime") String endTime,
            @JsonProperty("cost") int cost,
            @JsonProperty("revisions") List<JsonAdaptedLessonRevision> revisions) {
        this.uid = uid;
        this.year = year;
        this.subject = subject;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        if (revisions != null) {
            this.revisions.addAll(revisions);
        }
    }

    /** Constructs a JSON-adapted lesson from a model lesson. */
    public JsonAdaptedLesson(Lesson source) {
        uid = source.getUid();
        year = source.getYear().toString();
        subject = source.getSubject().name();
        day = source.getDay().name();
        startTime = source.getStartTime().name();
        endTime = source.getEndTime().name();
        cost = source.getCost();
        revisions.addAll(source.getRevisions().stream().map(JsonAdaptedLessonRevision::new).toList());
    }

    /** Converts this JSON-adapted lesson into a model lesson. */
    public Lesson toModelType() throws IllegalValueException {
        if (uid != null && uid.isBlank()) {
            throw new IllegalValueException("Lesson uid must not be blank.");
        }
        List<LessonRevision> modelRevisions = new ArrayList<>();
        if (revisions.isEmpty()) {
            modelRevisions.add(new JsonAdaptedLessonRevision(year, subject, day, startTime, endTime, cost, null)
                    .toModelType());
        } else {
            for (JsonAdaptedLessonRevision revision : revisions) {
                if (revision == null) {
                    throw new IllegalValueException(Lesson.CONSTRAINTS);
                }
                modelRevisions.add(revision.toModelType());
            }
        }
        try {
            return new Lesson(uid, modelRevisions);
        } catch (LessonException exception) {
            throw new IllegalValueException(exception.getMessage(), exception);
        }
    }
}
