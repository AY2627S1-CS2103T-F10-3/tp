package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.lesson.exceptions.LessonException;

/**
 * Repository of lesson definitions, independent of students.
 */
public class LessonBook {
    private final Map<String, Lesson> lessons = new LinkedHashMap<>();

    /**
     * Adds a lesson definition if its current ID is not already present.
     * Rejects duplicate stable IDs instead of silently changing an identity held by student references.
     *
     * @param lesson lesson definition to add
     * @return {@code true} if the lesson was added, or {@code false} if it already exists
     */
    public boolean addLesson(Lesson lesson) {
        requireNonNull(lesson);
        if (lessons.containsKey(lesson.getId())) {
            return false;
        }
        if (getLessonByUid(lesson.getUid()) != null) {
            throw new LessonException("Duplicate lesson UID: " + lesson.getUid());
        }
        lessons.put(lesson.getId(), lesson);
        return true;
    }

    public void clear() {
        lessons.clear();
    }

    /**
     * Checks whether a lesson with the same ID is already stored.
     *
     * @param lesson lesson definition to look up
     * @return {@code true} if a matching lesson exists
     */
    public boolean hasLesson(Lesson lesson) {
        requireNonNull(lesson);
        return lessons.containsKey(lesson.getId());
    }

    /**
     * Returns the lesson whose current ID, stable internal ID or legacy ID (with cost) is {@code id},
     * or null if there is none.
     */
    public Lesson getLesson(String id) {
        Lesson lesson = lessons.get(id);
        if (lesson != null) {
            return lesson;
        }
        lesson = getLessonByUid(id);
        if (lesson != null) {
            return lesson;
        }
        // Accept IDs written by older versions, which included the lesson cost.
        return lessons.values().stream()
                .filter(candidate -> candidate.toString().equals(id))
                .findFirst()
                .orElse(null);
    }

    /** Returns the lesson with the given current ID only, or null if there is none. */
    public Lesson getLessonByCurrentId(String id) {
        return lessons.get(id);
    }

    /** Returns the lesson with the given stable internal ID, or null if there is none. */
    public Lesson getLessonByUid(String uid) {
        return lessons.values().stream()
                .filter(candidate -> candidate.getUid().equals(uid))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the only lesson that has a revision with the given ID, or null if absent or ambiguous.
     * Legacy attendance has no UID; a reused slot must not silently link it to the wrong lesson.
     */
    public Lesson findLessonWithRevisionId(String id) {
        List<Lesson> matches = lessons.values().stream()
                .filter(candidate -> candidate.hasRevisionId(id)).limit(2).toList();
        return matches.size() == 1 ? matches.getFirst() : null;
    }

    public boolean removeLesson(String id) {
        return lessons.remove(id) != null;
    }

    public Map<String, Lesson> getLessons() {
        return Collections.unmodifiableMap(lessons);
    }

    /**
     * Returns the lesson definitions as an unmodifiable observable list.
     */
    public ObservableList<Lesson> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(lessons.values()));
    }
}
