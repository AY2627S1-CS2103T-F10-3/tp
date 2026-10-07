package seedu.address.model.lesson.exceptions;

public class LessonException extends RuntimeException {
    /**
     * Constructs a lesson exception with the given detail message.
     *
     * @param message detail message describing the lesson error
     */
    public LessonException(String message) {
        super(message);
    }
}
