---
layout: page
title: User Guide
---

# EduReg

EduReg is a desktop application that centralizes student-parent relationships, class rosters, fee payments, and
educational records into a unified CLI tool, enabling tuition centre admins to update enrollments, log attendance, and
track assignment submissions significantly faster than multi-step GUI systems.

* Table of Contents
  {:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version
   prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

2. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-F10-3/tp/releases).

3. Copy the file to the folder you want to use as the _home folder_ for EduReg.

4. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar edureg.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![EduReg interface showing the command box, results panel, and student directory](images/Ui.png)

5. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open
   the help window.<br>
   Some example commands you can try:

    * `view` : Lists all students.

    * `addstudent n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 gn/Mary Tan gp/91234567 ge/mary@example.com gr/Mother ga/123 Example Road`
      : Adds a student named `John Doe` to EduReg.

    * `delete 3` : Deletes the 3rd student shown in the current directory.

    * `clear` : Deletes all students.

    * `exit` : Exits the app.

6. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `addstudent n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `view`, `exit`, and `clear`, are
  ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines
  as space characters surrounding line-breaks may be omitted when copied over to the application.

</div>

### Viewing help: `help`

Opens a window containing the link to the EduReg User Guide. Use **Copy URL** to copy the link.

![EduReg Help window showing the User Guide link](images/helpMessage.png)

Format: `help`

### Adding a student: `addstudent`

Adds a student to the address book.

Format:
`addstudent n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​ [l/LESSON]…​
[gn/GUARDIAN_NAME gp/GUARDIAN_PHONE ge/GUARDIAN_EMAIL gr/RELATIONSHIP ga/GUARDIAN_ADDRESS]`

All five guardian fields (`gn/`, `gp/`, `ge/`, `gr/`, `ga/`) are optional when adding a student, but if any field is supplied the rest must be too.
Each must be non-blank and supplied once. Guardian name, phone, email, and address follow the same
validation rules as the student's corresponding fields. Relationship accepts non-blank text such as
`Mother` or `Legal guardian`. Guardian details are saved with the student and can be changed with `edit`. 

Lessons must first be created with `addlesson`. The `l/LESSON_ID` values in `addstudent` must
match lessons already in the lesson catalog; `addstudent` does not create new lessons.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A student can have any number of tags or lessons, including zero.
</div>


### Viewing students and contact values: `view`

With no selector, shows all persons in the address book. An optional selector shows each
distinct value and the number of contacts that have it. Summaries include all saved contacts,
regardless of the current view filter, and sort values alphabetically. Each tag is counted
separately.

Format: `view [/name | /phone | /email | /address | /tag | /lesson | /guardian | /payment | /attendance]`

Examples:

* `view` shows all students
* `view /email` shows each distinct email and its frequency.
* `view /lesson` shows each distinct lesson and how many students are enrolled in it.

### Editing a student: `edit`

Edits an existing person in the address book.

Format:
`edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [gn/GUARDIAN_NAME] [gp/GUARDIAN_PHONE] [ge/GUARDIAN_EMAIL] [gr/RELATIONSHIP] [ga/GUARDIAN_ADDRESS] [t/TAG]…​ [l/LESSON]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list.
  The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* Guardian fields can be edited individually. Guardian fields that are not provided keep their existing values.
* If the student has no guardian (for example, a record created before guardians were required), you must provide
  all five guardian fields (`gn/`, `gp/`, `ge/`, `gr/`, `ga/`) to add one.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.
* When editing lessons, all of the person's existing lessons are replaced by the given lessons; adding lessons is not
  cumulative. Each lesson must already exist in the catalog; use `addlesson` first. Lesson values must be five-part
  IDs without a cost; the catalog supplies the lesson cost.
* To remove all of a person's lessons, enter `l/` without a lesson after it.

Examples:

* `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567`
  and `johndoe@example.com` respectively.
* `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.
* `edit 3 gp/98765432 gr/Father` Edits the guardian phone number and relationship of the 3rd person to be `98765432` and
  `Father` respectively. (Note: this example requires the student to already have guardian information stored)
* `edit 1 l/2026-MATH-MONDAY-TEN-ELEVEN l/2026-ENGLISH-WEDNESDAY-THIRTEEN-FOURTEEN` Replaces the lessons of the
  1st person with the two given lessons.
* `edit 2 l/` Removes all lessons of the 2nd person.

### Locating students by student or guardian details: `find`

Finds students using case-insensitive partial matches. Without a prefix, keywords match student names only.

Format:
`find KEYWORD [MORE_KEYWORDS]` OR
`find [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG] [gn/GUARDIAN_NAME] [gp/GUARDIAN_PHONE]
[ge/GUARDIAN_EMAIL] [ga/GUARDIAN_ADDRESS] [gr/RELATIONSHIP] [l/LESSON] [paid/MONTH | unpaid/MONTH]`

* Multiple keywords within one field use `OR`; different fields use `AND`.
* Supported prefixes search the corresponding student, guardian, tag, or lesson field. Results always show students.
* Searches are partial and case-insensitive.
* A complete [lesson ID](#adding-a-lesson-addlesson) must match exactly. A lesson search can therefore be used to show a class roster, for example,
  `find l/2026-MATH-MONDAY-TEN-ELEVEN`.
* `paid/MONTH` and `unpaid/MONTH` search the student's total fees for that month. Students with no fees due are excluded,
  and the two payment prefixes cannot be combined.
* `t/` searches explicitly assigned tags only; it does not calculate payment status.
* Use at least one non-empty value. Repeated or unsupported prefixes, or mixing prefixed and unprefixed criteria, are
  rejected.

Examples:

* `find alex david` finds students whose names contain `alex` or `david`.
* `find n/Alice gp/9876` requires both the student name and guardian phone number to match.
* `find e/school.com a/Tampines` requires both email and address to match.
* `find l/math unpaid/2026-10` finds Maths students with outstanding October fees.
* `find t/secondary1 secondary2` matches either tag fragment.
* `find unpaid/2026-10` finds students with no payment or only partial payment for October 2026.

  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Adding a lesson: `addlesson`

Creates a reusable lesson definition in the lesson catalog.

Format: `addlesson y/YEAR s/SUBJECT d/DAY st/START_TIME et/END_TIME c/COST`

Example: `addlesson y/2026 s/MATH d/MONDAY st/TEN et/ELEVEN c/30`

Lessons store the following data:
1. Year (in the form yyyy eg. 2026)
2. Subject (any of English, Math, or Science)
3. Day (any day of the week eg. Monday)
4. Start time (any hour between 10am to 8pm in 24h time eg. TEN for 10am)
5. End time (same format as start time)
6. Cost (any non-negative integer)

Each lesson can be referenced by an ID, which is defined as so: `{year}-{subject}-{day}-{start_time}-{end_time}`
eg. `2026-MATH-MONDAY-TEN-ELEVEN`

The lesson ID should be used when enrolling students with `addstudent` or `edit`.

### Deleting a lesson: `deletelesson`

Removes a lesson from the lesson catalog and removes all students from it.

Format: `deletelesson LESSON_ID`

Example: `deletelesson 2026-MATH-MONDAY-TEN-ELEVEN`

The student's recorded attendance and payments are retained, but the deleted lesson no longer
contributes to enrolled-class fee summaries.

### Editing a lesson: `editlesson`

Changes an existing lesson from a billing month onwards and propagates the change to all enrolled students.
Months before that keep the lesson's earlier timing and cost.

Format: `editlesson CURRENT_ID [d/DAY] [st/START_TIME] [et/END_TIME] [c/COST] [from/YYYY-MM]`

* At least one lesson field must be supplied. 
* Fields not supplied retain their current values.
* `from/` is the first billing month the change applies to. If it is omitted, the change applies from next month, or from the latest pending change if that is later.
The effective month must be later than the current month and cannot be earlier than the latest revision.
* Only a pending revision can be replaced using the same month. 
* Current and past billing months cannot be edited.
* The lesson year and subject cannot be changed because that defines the lesson.
* After the edit, refer to the lesson by its new ID.
* Sessions before the change takes effect can still be recorded with either ID. 
* Recorded payments are never changed. The fees due for each month use the lesson cost in effect that month.

`CURRENT_ID` uses the [lesson ID](#adding-a-lesson-addlesson) format.

Examples:
* `editlesson 2026-MATH-MONDAY-TEN-ELEVEN c/40 from/2026-11` charges \$40 from November 2026. October is still
  charged at the old cost, so a student who already paid for October stays fully paid.
* `editlesson 2026-MATH-MONDAY-TEN-ELEVEN d/TUESDAY` moves the lesson to Tuesdays from next month.

### Recording a fee payment: `pay`

Records a fee payment for a student.

Format: `pay INDEX amt/AMOUNT m/MONTH [d/DATE]`

* Records the payment for the student at the specified `INDEX`. The index refers to the index number shown in the
  displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* `AMOUNT` is a positive whole number of dollars, for example `120`.
* `MONTH` is the billing month that the payment is for, in the form `yyyy-MM`, for example `2026-10`.
* `DATE` is the date the payment was received, in the form `yyyy-MM-dd`. If you leave it out, today's date is used.
* A student can make more than one payment for the same month, for example when paying in parts.
* The student must have fees due for `MONTH`, that is, at least one lesson with a cost in that month's year. Otherwise
  the payment is rejected.
* `AMOUNT` can't be more than the amount still outstanding for `MONTH`. For example, if \$30 is due and \$10 has been
  paid, the largest payment accepted is \$20.
* After recording the payment, EduReg shows the fees due, the total paid and the amount still outstanding for that
  month.
 
Examples:
* `pay 2 amt/35 m/2026-10` records a \$35 payment for October 2026 from the 2nd student, received today.
* `pay 3 amt/20 m/2026-10 d/2026-10-03` records a \$20 part payment for October 2026 from the 3rd student, received
  on 3 October 2026.

**How monthly fees are calculated** \
A lesson's cost is its monthly fee. The fees due for a month are the sum of the costs of the student's lessons in
that month's year. For example, a student taking `2026-MATH-MONDAY-TEN-ELEVEN-30` and
`2026-ENGLISH-WEDNESDAY-THIRTEEN-FOURTEEN-40` owes \$70 for each month of 2026.

Each student card shows the fees for the current month, either **Paid** or the amount outstanding. Students with no
lessons in the current year don't show a fees line.

### Viewing a student's payments: `viewpayments`

Lists the payments recorded for a student, numbered in the order they were recorded.

Format: `viewpayments INDEX`

* Lists the payments of the student at the specified `INDEX`. The index **must be a positive integer** 1, 2, 3, …​
* Use the payment numbers shown here with `deletepayment`.

Example:

* `viewpayments 1` lists the payments of the 1st student.

### Deleting a payment: `deletepayment`

Deletes a payment that was recorded by mistake.

Format: `deletepayment INDEX i/PAYMENT_INDEX`

* Deletes the payment numbered `PAYMENT_INDEX` from the student at the specified `INDEX`.
* `PAYMENT_INDEX` is the number shown by [`viewpayments`](#viewing-a-students-payments-viewpayments).
  It **must be a positive integer** 1, 2, 3, …​

Example:

* `viewpayments 1` followed by `deletepayment 1 i/2` deletes the 2nd payment of the 1st student.

### Recording attendance: `attendance`

Records whether a student attended one scheduled lesson session.

Format: `attendance INDEX l/LESSON_ID d/DATE s/STATUS`

* `INDEX` is the student's positive integer index in the currently displayed list, including after `find`.
* The student must be enrolled in the specified lesson. `LESSON_ID` is the lesson's year, subject, weekday,
  start time and end time, **without the cost**. For a displayed lesson `2026-MATH-MONDAY-TEN-ELEVEN-120`,
  use `l/2026-MATH-MONDAY-TEN-ELEVEN`. Lesson IDs are case-sensitive.
* `DATE` is required in `yyyy-MM-dd` form and must match the lesson's year and weekday.
* `STATUS` is `present` or `absent`, case-insensitive. A session without a record is **not automatically absent**.
* Only one record is allowed for a student, lesson and date, even if the status differs. To correct a mistake,
  use `viewattendance` and `deleteattendance`, then record the corrected status.
* Attendance is saved with the student. Editing student details or changing lesson enrolments preserves the history.
  Payment commands also preserve attendance records.
* The alpha version accepts future session dates and only supports the lesson's regular weekday, not rescheduled sessions.

Examples (assuming student 1 is enrolled in the Monday Math lesson above):

* `attendance 1 l/2026-MATH-MONDAY-TEN-ELEVEN d/2026-10-19 s/present`
* `attendance 1 l/2026-MATH-MONDAY-TEN-ELEVEN d/2026-10-26 s/absent`

### Viewing attendance: `viewattendance`

Format: `viewattendance INDEX`

Lists the student's attendance records from earliest to latest date, with numbers starting at 1.
Records on the same date retain their recording order. Each entry shows the
lesson ID, date and status. If there are no records, EduReg displays `No attendance recorded.`

Example: `viewattendance 1`

### Deleting attendance: `deleteattendance`

Format: `deleteattendance INDEX i/ATTENDANCE_INDEX`

Deletes the record numbered `ATTENDANCE_INDEX` in that student's `viewattendance` output. Both indices must
be positive integers. Records are renumbered after additions or deletions, so run `viewattendance` again
before deleting another record, especially after adding a backdated entry.
Historical records can still be viewed and deleted after the student leaves the lesson.

Example: `viewattendance 1` followed by `deleteattendance 1 i/2` removes the second attendance record.

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:

* `view` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Importing students and lessons: `importcsv`

Imports students and lessons from CSV files in the `data` folder of the EduReg home folder. Existing data is kept;
duplicate students are skipped and reported.

Format: `importcsv`

Before running the command, create these files:

* `data/importlessons.csv` with the required columns:
  `year,subject,day,start_time,end_time,cost`
* `data/importstudents.csv` with the required columns:
  `name,phone,email,address`

* The student file may also contain `tags`, `lessons`, and all five guardian columns:
`guardian_name`, `guardian_phone`, `guardian_email`, `guardian_relationship`, and `guardian_address`.
If any guardian column is included, all five must be included. 
* [Lesson IDs](#adding-a-lesson-addlesson) in the `lessons` column must refer to lessons in `importlessons.csv`
* Separate multiple tags or lessons with commas or semicolons.
* Lesson IDs are generated from the lesson fields automatically. Do not include an ID or cost
in the student lesson references. Invalid CSV rows, duplicate lesson IDs, unknown lesson IDs, or conflicting existing
lesson definitions are rejected with an error identifying the relevant file and row.

Example:

`data/importlessons.csv`:

```csv
year,subject,day,start_time,end_time,cost
2026,MATH,MONDAY,TEN,ELEVEN,30
```

`data/importstudents.csv`:

```csv
name,phone,email,address,tags,lessons
James Ho,91234567,james@example.com,"123, Clementi Road",friend,2026-MATH-MONDAY-TEN-ELEVEN
```

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

EduReg automatically saves data after every command. You do not need to save manually.

### Editing the data file

EduReg data is saved automatically as a JSON file `[JAR file location]/data/edureg.json`. Advanced users are
welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, EduReg starts with an empty address book at the next run.
The invalid file remains on disk until you run a command (EduReg saves after every command).
Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the EduReg to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range).
Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your
previous EduReg home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only
   the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the
   application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut
   `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to
   manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add Student** | `addstudent n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​ [l/LESSON_ID]…​ [gn/GUARDIAN_NAME gp/GUARDIAN_PHONE ge/GUARDIAN_EMAIL gr/RELATIONSHIP ga/GUARDIAN_ADDRESS]`<br> e.g., `addstudent n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend gn/Mary Tan gp/91234567 ge/mary@example.com gr/Mother ga/123 Example Road`
**Find** | `find KEYWORD [MORE_KEYWORDS]` or field-specific searches (see `find` section)<br> e.g., `find n/Alice gp/9876`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​ [l/LESSON_ID]…​ [gn/GUARDIAN_NAME] [gp/GUARDIAN_PHONE] [ge/GUARDIAN_EMAIL] [gr/RELATIONSHIP] [ga/GUARDIAN_ADDRESS]`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com gp/91234567`
**View** | `view [/name | /phone | /email | /address | /tag | /lesson | /guardian | /payment | /attendance]`
**Add Lesson** | `addlesson y/YEAR s/SUBJECT d/DAY st/START_TIME et/END_TIME c/COST`<br> e.g., `addlesson y/2026 s/MATH d/MONDAY st/TEN et/ELEVEN c/30`
**Delete Lesson** | `deletelesson LESSON_ID`<br> e.g., `deletelesson 2026-MATH-MONDAY-TEN-ELEVEN`
**Edit Lesson** | `editlesson CURRENT_ID [d/DAY] [st/START_TIME] [et/END_TIME] [c/COST] [from/YYYY-MM]`<br> e.g., `editlesson 2026-MATH-MONDAY-TEN-ELEVEN c/40 from/2026-11`
**Pay** | `pay INDEX amt/AMOUNT m/MONTH [d/DATE]`<br> e.g., `pay 2 amt/35 m/2026-10 d/2026-10-03`
**View Payments** | `viewpayments INDEX`<br> e.g., `viewpayments 1`
**Delete Payment** | `deletepayment INDEX i/PAYMENT_INDEX`<br> e.g., `deletepayment 1 i/2`
**Attendance** | `attendance INDEX l/LESSON_ID d/DATE s/STATUS`
**View Attendance** | `viewattendance INDEX`
**Delete Attendance** | `deleteattendance INDEX i/ATTENDANCE_INDEX`
**Import CSV** | `importcsv`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Help** | `help`
**Exit** | `exit`
