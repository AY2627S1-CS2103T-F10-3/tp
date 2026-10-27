package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Repository of lesson definitions, independent of students.
 */
public class LessonBook {
    private final Map<String, Lesson> lessons = new LinkedHashMap<>();

    /**
     * Adds a lesson definition if its ID is not already present.
     *
     * @param lesson lesson definition to add
     * @return {@code true} if the lesson was added, or {@code false} if it already exists
     */
    public boolean addLesson(Lesson lesson) {
        requireNonNull(lesson);
        return lessons.putIfAbsent(lesson.getId(), lesson) == null;
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

    public Lesson getLesson(String id) {
        Lesson lesson = lessons.get(id);
        if (lesson != null) {
            return lesson;
        }
        // Accept IDs written by older versions, which included the lesson cost.
        return lessons.values().stream()
                .filter(candidate -> candidate.toString().equals(id))
                .findFirst()
                .orElse(null);
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
