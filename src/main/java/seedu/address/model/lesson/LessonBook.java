package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** Repository of lesson definitions, independent of students. */
public class LessonBook {
    private final Map<String, Lesson> lessons = new HashMap<>();

    public boolean addLesson(Lesson lesson) {
        requireNonNull(lesson);
        return lessons.putIfAbsent(lesson.getId(), lesson) == null;
    }

    public void clear() {
        lessons.clear();
    }

    public boolean hasLesson(Lesson lesson) {
        requireNonNull(lesson);
        return lessons.containsKey(lesson.getId());
    }

    public Lesson getLesson(String id) { return lessons.get(id); }

    public Map<String, Lesson> getLessons() { return Collections.unmodifiableMap(lessons); }

    /** Returns the lesson definitions as an unmodifiable observable list. */
    public ObservableList<Lesson> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(lessons.values()));
    }
}
