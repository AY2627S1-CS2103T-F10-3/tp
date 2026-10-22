package seedu.address.ui;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label lessons;
    @FXML
    private Label fees;
    @FXML
    private VBox guardianDetails;
    @FXML
    private Label guardianName;
    @FXML
    private Label guardianPhone;
    @FXML
    private Label guardianEmail;
    @FXML
    private Label guardianRelationship;
    @FXML
    private Label guardianAddress;
    @FXML
    private Label guardianMissing;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        boolean hasGuardian = person.getGuardian().isPresent();
        guardianDetails.setVisible(hasGuardian);
        guardianDetails.setManaged(hasGuardian);
        guardianMissing.setVisible(!hasGuardian);
        guardianMissing.setManaged(!hasGuardian);
        person.getGuardian().ifPresent(guardian -> {
            guardianName.setText("Name: " + guardian.getName().fullName);
            guardianPhone.setText("Phone: " + guardian.getPhone().value);
            guardianEmail.setText("Email: " + guardian.getEmail().value);
            guardianRelationship.setText("Relationship: " + guardian.getRelationship().value);
            guardianAddress.setText("Address: " + guardian.getAddress().value);
        });
        lessons.setText(person.getLessons().stream()
                .map(lesson -> String.format("%s-%s-%s-%s-%s-%d", lesson.getYear(), lesson.getSubject(),
                        lesson.getDay(), lesson.getStartTime(), lesson.getEndTime(), lesson.getCost()))
                .sorted()
                .collect(Collectors.joining(", ", "Lessons: ", "")));
        YearMonth currentMonth = YearMonth.now();
        int feesDue = person.getFeesDue(currentMonth);
        int outstanding = person.getOutstandingFees(currentMonth);
        fees.setText(String.format("Fees (%s): %s", currentMonth.format(MONTH_FORMAT),
                outstanding == 0 ? "Paid" : "$" + outstanding + " outstanding"));
        fees.setVisible(feesDue > 0);
        fees.setManaged(feesDue > 0);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }
}
