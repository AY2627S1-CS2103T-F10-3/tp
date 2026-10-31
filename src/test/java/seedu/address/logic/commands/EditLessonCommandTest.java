package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_LESSON_MATH;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPayments.PAYMENT_NOVEMBER;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.attendance.Attendance;
import seedu.address.model.attendance.AttendanceStatus;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.LessonRevision;
import seedu.address.model.lesson.Time;
import seedu.address.model.payment.Amount;
import seedu.address.model.payment.Payment;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class EditLessonCommandTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-29T12:00:00Z"), ZoneOffset.UTC);
    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final YearMonth NOVEMBER = YearMonth.of(2026, 11);
    private static final YearMonth DECEMBER = YearMonth.of(2026, 12);

    @Test
    public void execute_partialEdit_updatesLesson() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40);
        command.execute(model);

        assertEquals(40, model.getAddressBook().getLessonList().get(0).getCost());
    }

    @Test
    public void execute_editEnrolledLesson_updatesStudentReference() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Person student = new PersonBuilder().withLessons(VALID_LESSON_MATH).build();
        model.addPerson(student);

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), Day.MONDAY,
                Time.TEN, Time.ELEVEN, 40);
        command.execute(model);

        assertEquals(Set.of(model.getAddressBook().getLessonList().get(0)),
                model.getAddressBook().getPersonList().get(0).getLessons());
        assertEquals(40, model.getAddressBook().getPersonList().get(0).getLessons().iterator().next().getCost());
    }

    @Test
    public void execute_missingLesson_throwsCommandException() {
        ModelManager model = new ModelManager();
        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40);

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_invalidFinalTimeRange_throwsCommandException() {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);

        EditLessonCommand command = new EditLessonCommand(VALID_LESSON_MATH.getId(), null,
                Time.TWELVE, null, null);

        assertThrows(CommandException.class, EditLessonCommand.MESSAGE_INVALID_TIME_RANGE, () ->
                command.execute(model));
        assertEquals(VALID_LESSON_MATH, model.getAddressBook().getLessonList().get(0));
    }

    @Test
    public void execute_editLesson_keepsHistoricalAttendanceUnchanged() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Attendance historicalAttendance = new Attendance(VALID_LESSON_MATH.getId(), VALID_LESSON_MATH.getUid(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH)
                .withAttendance(historicalAttendance).build());

        new EditLessonCommand(VALID_LESSON_MATH.getId(), Day.TUESDAY, null, null, null, NOVEMBER, CLOCK)
                .execute(model);

        assertEquals(List.of(historicalAttendance), model.getFilteredPersonList().getFirst().getAttendance());
        assertEquals("2026-MATH-TUESDAY-TEN-ELEVEN", model.getAddressBook().getLessonList().getFirst().getId());
    }

    @Test
    public void execute_editLesson_keepsEarlierRevisionForSessionsBeforeEffectiveMonth() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH).build());
        new EditLessonCommand(VALID_LESSON_MATH.getId(), Day.TUESDAY, null, null, null, NOVEMBER, CLOCK)
                .execute(model);
        String newId = model.getAddressBook().getLessonList().getFirst().getId();
        String uid = VALID_LESSON_MATH.getUid();

        // An October Monday is still a valid session and is recorded under the October timing.
        new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(newId, LocalDate.of(2026, 10, 26), AttendanceStatus.PRESENT)).execute(model);
        // A November Tuesday follows the new timing.
        new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(newId, LocalDate.of(2026, 11, 3), AttendanceStatus.ABSENT)).execute(model);
        assertEquals(List.of(
                new Attendance(VALID_LESSON_MATH.getId(), uid, LocalDate.of(2026, 10, 26), AttendanceStatus.PRESENT),
                new Attendance(newId, uid, LocalDate.of(2026, 11, 3), AttendanceStatus.ABSENT)),
                model.getFilteredPersonList().getFirst().getAttendance());

        // A November Monday no longer matches the schedule.
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(newId, LocalDate.of(2026, 11, 2), AttendanceStatus.PRESENT)), model,
                AttendanceCommand.MESSAGE_INVALID_SESSION_DATE);
        // The same session cannot be recorded again under the old lesson ID.
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(VALID_LESSON_MATH.getId(), LocalDate.of(2026, 10, 26), AttendanceStatus.ABSENT)),
                model, Person.MESSAGE_DUPLICATE_ATTENDANCE);
    }

    @Test
    public void execute_editLesson_viewAttendanceShowsScheduleChange() throws Exception {
        ModelManager model = new ModelManager();
        Attendance recorded = new Attendance(VALID_LESSON_MATH.getId(), VALID_LESSON_MATH.getUid(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        Person student = new PersonBuilder().withLessons(VALID_LESSON_MATH).withAttendance(recorded).build();
        model.addPerson(student);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), Day.TUESDAY, null, null, null, NOVEMBER, CLOCK)
                .execute(model);

        assertEquals(String.format(ViewAttendanceCommand.MESSAGE_HEADER, student.getName())
                + "\n1. 2026-MATH-MONDAY-TEN-ELEVEN on 2026-10-19: PRESENT"
                + "\n" + String.format(ViewAttendanceCommand.MESSAGE_SCHEDULE_CHANGED, "TUESDAY TEN-ELEVEN"),
                new ViewAttendanceCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_editCost_onlyChangesFeesFromEffectiveMonth() throws Exception {
        ModelManager model = new ModelManager();
        Payment octoberPayment = new Payment(new Amount("30"), OCTOBER, LocalDate.of(2026, 10, 3));
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH).withPayments(octoberPayment).build());

        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40, NOVEMBER, CLOCK)
                .execute(model);

        Person updated = model.getFilteredPersonList().getFirst();
        assertEquals(30, updated.getFeesDue(OCTOBER));
        assertEquals(0, updated.getOutstandingFees(OCTOBER));
        assertEquals(40, updated.getFeesDue(NOVEMBER));
        assertEquals(40, updated.getFeesDue(YearMonth.of(2026, 12)));
        assertEquals(List.of(octoberPayment), updated.getPayments());
    }

    @Test
    public void execute_multipleEdits_eachMonthUsesItsOwnRevision() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH).build());
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40, NOVEMBER, CLOCK)
                .execute(model);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 50, YearMonth.of(2026, 12), CLOCK)
                .execute(model);

        Person updated = model.getFilteredPersonList().getFirst();
        assertEquals(30, updated.getFeesDue(OCTOBER));
        assertEquals(40, updated.getFeesDue(NOVEMBER));
        assertEquals(50, updated.getFeesDue(YearMonth.of(2026, 12)));
        assertEquals(3, model.getAddressBook().getLessonList().getFirst().getRevisions().size());
    }

    @Test
    public void execute_effectiveMonthBeforeLatestChange_allowsFutureRevision() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40, DECEMBER, CLOCK)
                .execute(model);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 35, NOVEMBER, CLOCK)
                .execute(model);

        Lesson lesson = model.getAddressBook().getLessonList().getFirst();
        assertEquals(35, lesson.getCostFor(NOVEMBER));
        assertEquals(40, lesson.getCostFor(DECEMBER));
        assertEquals(3, lesson.getRevisions().size());
    }

    @Test
    public void execute_effectiveMonthOutsideLessonYear_throwsAndAllowsValidRevision() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);

        assertCommandFailure(new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40,
                YearMonth.of(2027, 1), CLOCK), model,
                String.format(Lesson.MESSAGE_REVISION_OUTSIDE_YEAR, VALID_LESSON_MATH.getYear()));

        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 35, NOVEMBER, CLOCK)
                .execute(model);

        Lesson lesson = model.getAddressBook().getLessonList().getFirst();
        assertEquals(35, lesson.getCostFor(NOVEMBER));
        assertEquals(2, lesson.getRevisions().size());
    }

    @Test
    public void execute_sameEffectiveMonth_replacesPendingChange() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40, NOVEMBER, CLOCK)
                .execute(model);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 45, NOVEMBER, CLOCK)
                .execute(model);

        Lesson lesson = model.getAddressBook().getLessonList().getFirst();
        assertEquals(2, lesson.getRevisions().size());
        assertEquals(45, lesson.getCostFor(NOVEMBER));
    }

    @Test
    public void execute_noEffectiveMonth_defaultsToNextMonth() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Clock clock = Clock.fixed(Instant.parse("2026-10-28T12:00:00Z"), ZoneOffset.UTC);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40, null, clock)
                .execute(model);

        Lesson lesson = model.getAddressBook().getLessonList().getFirst();
        assertEquals(NOVEMBER, lesson.getLatestRevision().getEffectiveFrom());
        assertEquals(30, lesson.getCostFor(OCTOBER));
    }

    @Test
    public void execute_editLesson_leavesFormerStudentsAttendanceUnchanged() throws Exception {
        ModelManager model = new ModelManager();
        model.addLesson(VALID_LESSON_MATH);
        Attendance recorded = new Attendance(VALID_LESSON_MATH.getId(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.ABSENT);
        Attendance unrelated = new Attendance("2026-ENGLISH-MONDAY-TEN-ELEVEN",
                recorded.getDate(), AttendanceStatus.PRESENT);
        Person formerStudent = new PersonBuilder().withLessons().withPayments(PAYMENT_NOVEMBER)
                .withAttendance(recorded, unrelated).build();
        model.addPerson(formerStudent);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, Time.TWELVE, null)
                .execute(model);

        assertEquals(formerStudent, model.getAddressBook().getPersonList().getFirst());
    }

    @Test
    public void execute_currentOrPastMonth_rejectsWithoutChangingPaidFees() {
        for (YearMonth month : List.of(OCTOBER, OCTOBER.minusMonths(1))) {
            ModelManager model = new ModelManager();
            Payment payment = new Payment(new Amount("30"), month, LocalDate.of(2026, 10, 3));
            Person student = new PersonBuilder().withLessons(VALID_LESSON_MATH).withPayments(payment).build();
            model.addPerson(student);

            assertCommandFailure(new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40,
                    month, CLOCK), model, String.format(Lesson.MESSAGE_REVISION_NOT_FUTURE, OCTOBER));

            assertEquals(student, model.getFilteredPersonList().getFirst());
            assertEquals(0, model.getFilteredPersonList().getFirst().getOutstandingFees(month));
            assertEquals(List.of(VALID_LESSON_MATH), model.getAddressBook().getLessonList());
        }
    }

    @Test
    public void execute_replaceActiveRevision_rejectsAndPreservesHistory() {
        Lesson active = VALID_LESSON_MATH.withRevision(new LessonRevision(VALID_LESSON_MATH.getYear(),
                VALID_LESSON_MATH.getSubject(), Day.MONDAY, Time.TEN, Time.ELEVEN, 35, OCTOBER),
                OCTOBER.minusMonths(1));
        ModelManager model = new ModelManager();
        model.addLesson(active);

        assertCommandFailure(new EditLessonCommand(active.getId(), null, null, null, 40, OCTOBER, CLOCK),
                model, String.format(Lesson.MESSAGE_REVISION_NOT_FUTURE, OCTOBER));

        assertEquals(List.of(active), model.getAddressBook().getLessonList());
        assertEquals(35, model.getAddressBook().getLessonList().getFirst().getCostFor(OCTOBER));
    }

    @Test
    public void execute_multipleScheduleEdits_attendanceUsesEachHistoricalRevision() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(new PersonBuilder().withLessons(VALID_LESSON_MATH).build());
        new EditLessonCommand(VALID_LESSON_MATH.getId(), Day.TUESDAY, null, null, 40, NOVEMBER, CLOCK)
                .execute(model);
        String novemberId = model.getAddressBook().getLessonList().getFirst().getId();
        new EditLessonCommand(novemberId, Day.WEDNESDAY, null, null, 50, NOVEMBER.plusMonths(1), CLOCK)
                .execute(model);
        Lesson latest = model.getAddressBook().getLessonList().getFirst();
        List<LocalDate> dates = List.of(LocalDate.of(2026, 10, 26), LocalDate.of(2026, 11, 3),
                LocalDate.of(2026, 12, 2));
        for (LocalDate date : dates) {
            new AttendanceCommand(INDEX_FIRST_PERSON, new Attendance(latest.getId(), date, AttendanceStatus.PRESENT))
                    .execute(model);
        }
        assertEquals(List.of(
                new Attendance(VALID_LESSON_MATH.getId(), latest.getUid(), dates.get(0), AttendanceStatus.PRESENT),
                new Attendance(novemberId, latest.getUid(), dates.get(1), AttendanceStatus.PRESENT),
                new Attendance(latest.getId(), latest.getUid(), dates.get(2), AttendanceStatus.PRESENT)),
                model.getFilteredPersonList().getFirst().getAttendance());
        assertCommandFailure(new AttendanceCommand(INDEX_FIRST_PERSON,
                new Attendance(novemberId, dates.get(1), AttendanceStatus.ABSENT)), model,
                Person.MESSAGE_DUPLICATE_ATTENDANCE);
    }

    @Test
    public void execute_costOnlyEdit_doesNotDisplayScheduleChange() throws Exception {
        ModelManager model = new ModelManager();
        Attendance recorded = new Attendance(VALID_LESSON_MATH.getId(), VALID_LESSON_MATH.getUid(),
                LocalDate.of(2026, 10, 19), AttendanceStatus.PRESENT);
        Person student = new PersonBuilder().withLessons(VALID_LESSON_MATH).withAttendance(recorded).build();
        model.addPerson(student);
        new EditLessonCommand(VALID_LESSON_MATH.getId(), null, null, null, 40, NOVEMBER, CLOCK).execute(model);

        assertEquals(String.format(ViewAttendanceCommand.MESSAGE_HEADER, student.getName())
                + "\n1. 2026-MATH-MONDAY-TEN-ELEVEN on 2026-10-19: PRESENT",
                new ViewAttendanceCommand(INDEX_FIRST_PERSON).execute(model).getFeedbackToUser());
    }

}
