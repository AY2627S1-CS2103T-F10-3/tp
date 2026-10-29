package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonBook;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final LessonBook lessonBook = new LessonBook();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /* list overwrite operations */

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        LessonBook canonicalBook = new LessonBook();
        List<Person> canonicalPersons = persons.stream()
                .map(person -> canonicalizeLessons(person, canonicalBook)).toList();
        this.persons.setPersons(canonicalPersons);
        lessonBook.clear();
        canonicalBook.getLessons().values().forEach(lessonBook::addLesson);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        newData.getLessonList().forEach(lessonBook::addLesson);
    }

    /* person-level operations */

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        LessonBook updatedBook = copyLessonBook();
        persons.add(canonicalizeLessons(p, updatedBook));
        updatedBook.getLessons().values().forEach(lessonBook::addLesson);
    }

    private LessonBook copyLessonBook() {
        LessonBook copy = new LessonBook();
        lessonBook.getLessons().values().forEach(copy::addLesson);
        return copy;
    }

    /** Ensures every enrolment shares the catalog's lesson identity and revision history. */
    private static Person canonicalizeLessons(Person person, LessonBook catalog) {
        Set<Lesson> canonical = person.getLessons().stream().map(lesson -> {
            catalog.addLesson(lesson);
            return catalog.getLessonByCurrentId(lesson.getId());
        }).collect(Collectors.toSet());
        return person.withLessons(canonical);
    }

    /** Adds a shared lesson definition if it is not already present. */
    public boolean addLesson(Lesson lesson) {
        return lessonBook.addLesson(lesson);
    }

    /**
     * Replaces lesson {@code lessonId} with {@code editedLesson}, a newer version of the same logical lesson, and
     * updates every enrolment. Attendance is left untouched: each record keeps the ID of the revision in effect
     * when it was taken and stays linked to the lesson through its stable internal ID.
     *
     * @return false if the lesson does not exist or another lesson already has the edited lesson's current ID
     */
    public boolean editLesson(String lessonId, Lesson editedLesson) {
        requireNonNull(editedLesson);
        Lesson existing = lessonBook.getLesson(lessonId);
        if (existing == null) {
            return false;
        }
        Lesson clash = lessonBook.getLessonByCurrentId(editedLesson.getId());
        if (clash != null && !clash.getUid().equals(existing.getUid())) {
            return false;
        }
        Lesson replacement = editedLesson.getUid().equals(existing.getUid())
                ? editedLesson : editedLesson.withUid(existing.getUid());
        List<Person> updatedPersons = persons.asUnmodifiableObservableList().stream().map(person -> {
            Set<Lesson> lessons = person.getLessons().stream()
                    .map(lesson -> lesson.getUid().equals(existing.getUid()) ? replacement : lesson)
                    .collect(Collectors.toSet());
            return person.withLessons(lessons);
        }).toList();
        lessonBook.removeLesson(existing.getId());
        persons.setPersons(updatedPersons);
        lessonBook.addLesson(replacement);
        return true;
    }

    /** Removes a lesson definition and unenrols it from every student. */
    public boolean deleteLesson(String lessonId) {
        Lesson lesson = lessonBook.getLesson(lessonId);
        if (lesson == null) {
            return false;
        }
        List<Person> updatedPersons = persons.asUnmodifiableObservableList().stream()
                .map(person -> {
                    Set<Lesson> updatedLessons = new HashSet<>(person.getLessons());
                    updatedLessons.remove(lesson);
                    return person.withLessons(updatedLessons);
                }).toList();
        persons.setPersons(updatedPersons);
        lessonBook.removeLesson(lesson.getId());
        return true;
    }

    @Override
    public ObservableList<Lesson> getLessonList() {
        return lessonBook.asUnmodifiableObservableList();
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        LessonBook updatedBook = copyLessonBook();
        persons.setPerson(target, canonicalizeLessons(editedPerson, updatedBook));
        updatedBook.getLessons().values().forEach(lessonBook::addLesson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons);
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }
}
