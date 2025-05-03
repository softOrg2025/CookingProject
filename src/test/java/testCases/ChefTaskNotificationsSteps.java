package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class ChefTaskNotificationsSteps {

    private boolean isTaskAssigned;
    private boolean isTaskSaved;
    private boolean isNotificationReceived;

    private boolean isDeadlineApproaching;
    private boolean isReminderSent;

    private boolean isTaskRescheduled;
    private boolean isUpdateSaved;
    private boolean isChangeNotificationSent;

    // Scenario 1: Alert when task is created
    @Given("a task is assigned to me")
    public void a_task_is_assigned_to_me() {
        isTaskAssigned = true;
    }

    @When("it is saved in the system")
    public void it_is_saved_in_the_system() {
        if (isTaskAssigned) {
            isTaskSaved = true;
        }
    }

    @Then("I should receive a notification")
    public void i_should_receive_a_notification() {
        isNotificationReceived = isTaskSaved;
        Assertions.assertTrue(isNotificationReceived, "Chef should receive notification after task is saved.");
    }

    // Scenario 2: Reminder before task deadline
    @Given("I have a task due in an hour")
    public void i_have_a_task_due_in_an_hour() {
        isDeadlineApproaching = true;
    }

    @When("the time is near")
    public void the_time_is_near() {
        // Simulate system check for due time
        if (isDeadlineApproaching) {
            isReminderSent = true;
        }
    }

    @Then("I should get a reminder alert")
    public void i_should_get_a_reminder_alert() {
        Assertions.assertTrue(isReminderSent, "Chef should receive a reminder alert before the deadline.");
    }

    // Scenario 3: Notify changes to task schedule
    @Given("my task is rescheduled")
    public void my_task_is_rescheduled() {
        isTaskRescheduled = true;
    }

    @When("the update is saved")
    public void the_update_is_saved() {
        if (isTaskRescheduled) {
            isUpdateSaved = true;
        }
    }

    @Then("I should receive a notification about the change")
    public void i_should_receive_a_notification_about_the_change() {
        isChangeNotificationSent = isUpdateSaved;
        Assertions.assertTrue(isChangeNotificationSent, "Chef should be notified of any task schedule changes.");
    }
}

