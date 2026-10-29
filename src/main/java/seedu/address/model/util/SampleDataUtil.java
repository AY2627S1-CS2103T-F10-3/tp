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
            new Person(new Name("Ethan Tan"), new Phone("81234567"), new Email("ethan.tan@example.com"),
                new Address("Blk 123 Bukit Batok Street 11"),
                    getTagSet("Primary5", "PrincessElizabethPrimarySchool"),
                getLessonSet(2026, Subject.ENGLISH, Day.MONDAY, Time.TEN, Time.ELEVEN, 30),
                Optional.of(new Guardian(new Name("Michelle Tan"), new Phone("98830021"),
                        new Email("michelle.tan@example.com"), new Relationship("Mother"),
                        new Address("Blk 123 Bukit Batok Street 11"))),
                List.of(new Payment(new Amount("30"), YearMonth.of(2026, 10),
                        LocalDate.of(2026, 10, 3)))),
            new Person(new Name("Priya Nair"), new Phone("82345678"), new Email("priya.nair@example.com"),
                new Address("Blk 45 Tampines Street 42"), getTagSet("Primary6"),
                getLessonSet(2026, Subject.MATH, Day.TUESDAY, Time.ELEVEN, Time.TWELVE, 35),
                Optional.of(new Guardian(new Name("Raj Nair"), new Phone("98830022"),
                        new Email("raj.nair@example.com"), new Relationship("Father"),
                        new Address("Blk 45 Tampines Street 42")))),
            new Person(new Name("Marcus Lim"), new Phone("83456789"), new Email("marcus.lim@example.com"),
                new Address("Blk 78 Ang Mo Kio Avenue 4"), getTagSet("Secondary1"),
                getLessonSet(2026, Subject.SCIENCE, Day.WEDNESDAY, Time.THIRTEEN, Time.FOURTEEN, 40),
                Optional.of(new Guardian(new Name("Grace Lim"), new Phone("98830023"),
                        new Email("grace.lim@example.com"), new Relationship("Mother"),
                        new Address("Blk 78 Ang Mo Kio Avenue 4")))),
            new Person(new Name("Nur Aisyah Rahman"), new Phone("84567890"),
                new Email("aisyah.rahman@example.com"), new Address("Blk 210 Pasir Ris Street 21"),
                getTagSet("Secondary2"),
                getLessonSet(2026, Subject.ENGLISH, Day.THURSDAY, Time.FOURTEEN, Time.FIFTEEN, 30),
                Optional.of(new Guardian(new Name("Hafiz Rahman"), new Phone("98830024"),
                        new Email("hafiz.rahman@example.com"), new Relationship("Father"),
                        new Address("Blk 210 Pasir Ris Street 21")))),
            new Person(new Name("Lucas Wong"), new Phone("85678901"), new Email("lucas.wong@example.com"),
                new Address("Blk 9 Clementi Avenue 3"), getTagSet("Primary4"),
                getLessonSet(2026, Subject.MATH, Day.FRIDAY, Time.FIFTEEN, Time.SIXTEEN, 35),
                    Optional.empty()),
            new Person(new Name("Ryan Koh"), new Phone("87890123"), new Email("ryan.koh@example.com"),
                new Address("Blk 31 Jurong West Street 81"), getTagSet("Primary6"),
                Set.of(), Optional.empty()),
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
