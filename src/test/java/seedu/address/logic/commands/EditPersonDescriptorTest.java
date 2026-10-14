package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_GUARDIAN_RELATIONSHIP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditPersonDescriptorTest {

    @Test
    public void equals() {
        // same values -> returns true
        EditPersonDescriptor descriptorWithSameValues = new EditPersonDescriptor(DESC_AMY);
        assertTrue(DESC_AMY.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(DESC_AMY.equals(DESC_AMY));

        // null -> returns false
        assertFalse(DESC_AMY.equals(null));

        // different types -> returns false
        assertFalse(DESC_AMY.equals(5));

        // different values -> returns false
        assertFalse(DESC_AMY.equals(DESC_BOB));

        // different name -> returns false
        EditPersonDescriptor editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withName(VALID_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withPhone(VALID_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different tags -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different guardian name -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withGuardianName(VALID_GUARDIAN_NAME_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different guardian phone -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different guardian email -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY).withGuardianEmail(VALID_GUARDIAN_EMAIL_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different guardian relationship -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY)
                .withGuardianRelationship(VALID_GUARDIAN_RELATIONSHIP_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));

        // different guardian address -> returns false
        editedAmy = new EditPersonDescriptorBuilder(DESC_AMY)
                .withGuardianAddress(VALID_GUARDIAN_ADDRESS_BOB).build();
        assertFalse(DESC_AMY.equals(editedAmy));
    }

    @Test
    public void isAnyFieldEdited() {
        assertFalse(new EditPersonDescriptor().isAnyFieldEdited());
        assertTrue(DESC_AMY.isAnyFieldEdited());

        // only a guardian field edited -> returns true
        assertTrue(new EditPersonDescriptorBuilder().withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).build()
                .isAnyFieldEdited());
    }

    @Test
    public void isAnyGuardianFieldEdited() {
        assertFalse(new EditPersonDescriptor().isAnyGuardianFieldEdited());

        // only student fields edited -> returns false
        assertFalse(DESC_AMY.isAnyGuardianFieldEdited());

        assertTrue(new EditPersonDescriptorBuilder().withGuardianAddress(VALID_GUARDIAN_ADDRESS_BOB).build()
                .isAnyGuardianFieldEdited());
    }

    @Test
    public void isEveryGuardianFieldEdited() {
        assertFalse(new EditPersonDescriptor().isEveryGuardianFieldEdited());

        // some guardian fields edited -> returns false
        assertFalse(new EditPersonDescriptorBuilder().withGuardianName(VALID_GUARDIAN_NAME_BOB)
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).build().isEveryGuardianFieldEdited());

        assertTrue(new EditPersonDescriptorBuilder().withGuardianName(VALID_GUARDIAN_NAME_BOB)
                .withGuardianPhone(VALID_GUARDIAN_PHONE_BOB).withGuardianEmail(VALID_GUARDIAN_EMAIL_BOB)
                .withGuardianRelationship(VALID_GUARDIAN_RELATIONSHIP_BOB)
                .withGuardianAddress(VALID_GUARDIAN_ADDRESS_BOB).build().isEveryGuardianFieldEdited());
    }

    @Test
    public void toStringMethod() {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        String expected = EditPersonDescriptor.class.getCanonicalName() + "{name="
                + editPersonDescriptor.getName().orElse(null) + ", phone="
                + editPersonDescriptor.getPhone().orElse(null) + ", email="
                + editPersonDescriptor.getEmail().orElse(null) + ", address="
                + editPersonDescriptor.getAddress().orElse(null) + ", tags="
                + editPersonDescriptor.getTags().orElse(null) + ", guardianName="
                + editPersonDescriptor.getGuardianName().orElse(null) + ", guardianPhone="
                + editPersonDescriptor.getGuardianPhone().orElse(null) + ", guardianEmail="
                + editPersonDescriptor.getGuardianEmail().orElse(null) + ", guardianRelationship="
                + editPersonDescriptor.getGuardianRelationship().orElse(null) + ", guardianAddress="
                + editPersonDescriptor.getGuardianAddress().orElse(null) + "}";
        assertEquals(expected, editPersonDescriptor.toString());
    }
}
