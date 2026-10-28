package seedu.address.model.util;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.guardian.Guardian;
import seedu.address.model.guardian.Relationship;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Subject;
import seedu.address.model.lesson.Time;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("primary4"), getLessonSet(2026, Subject.ENGLISH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30),
                Optional.of(new Guardian(new Name("Mary Yeoh"), new Phone("91234567"),
                        new Email("maryyeoh@example.com"), new Relationship("Mother"),
                        new Address("Blk 30 Geylang Street 29, #06-40"))),
                List.of(new Payment(new Amount("30"), YearMonth.of(2026, 10),
                        LocalDate.of(2026, 10, 3)))),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("primary5"), getLessonSet(2026, Subject.MATH, Day.TUESDAY,
                        Time.ELEVEN, Time.TWELVE, 35),
                Optional.of(new Guardian(new Name("James Yu"), new Phone("92345678"),
                        new Email("jamesyu@example.com"), new Relationship("Father"),
                        new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18")))),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("primary6"), getLessonSet(2026, Subject.SCIENCE, Day.WEDNESDAY,
                        Time.THIRTEEN, Time.FOURTEEN, 40),
                Optional.of(new Guardian(new Name("Maria Oliveiro"), new Phone("93456789"),
                        new Email("mariaoliveiro@example.com"), new Relationship("Mother"),
                        new Address("Blk 11 Ang Mo Kio Street 74, #11-04")))),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("primary4"), getLessonSet(2026, Subject.ENGLISH, Day.THURSDAY,
                        Time.FOURTEEN, Time.FIFTEEN, 30),
                Optional.of(new Guardian(new Name("Susan Li"), new Phone("94567890"),
                        new Email("susanli@example.com"), new Relationship("Mother"),
                        new Address("Blk 436 Serangoon Gardens Street 26, #16-43")))),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("primary5"), getLessonSet(2026, Subject.MATH, Day.FRIDAY,
                        Time.FIFTEEN, Time.SIXTEEN, 35),
                Optional.of(new Guardian(new Name("Ahmad Ibrahim"), new Phone("95678901"),
                        new Email("ahmadibrahim@example.com"), new Relationship("Father"),
                        new Address("Blk 47 Tampines Street 20, #17-35")))),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("primary6"), getLessonSet(2026, Subject.SCIENCE, Day.SATURDAY,
                        Time.SIXTEEN, Time.SEVENTEEN, 40),
                Optional.of(new Guardian(new Name("Priya Balakrishnan"), new Phone("96789012"),
                        new Email("priyab@example.com"), new Relationship("Mother"),
                        new Address("Blk 45 Aljunied Street 85, #11-31"))))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

    /** Returns a lesson set containing one lesson with the given details. */
    public static Set<Lesson> getLessonSet(int year, Subject subject, Day day,
                                           Time startTime, Time endTime, int cost) {
        return Set.of(new Lesson(Year.of(year), subject, day, startTime, endTime, cost));
    }

}
