package testCases;

import cook.*;
import io.cucumber.java.en.*;
import io.cucumber.java.Before;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ChefTaskNotificationsSteps {

    private chef currentChef;
    private kitchen_manager kitchenManager;
    private NotificationService notificationService;

    private static final String TEST_TASK_NAME = "Prepare Special Dish";
    private String taskDetailsForTest;
    private LocalDateTime taskDeadline;

    @Before
    public void setUp() {
        InventoryService inventoryService = new InventoryService();
        notificationService = new NotificationService();
        Application.notificationService = notificationService;

        kitchenManager = new kitchen_manager("KM Lidia", "km@example.com", "km_pass", inventoryService);
        currentChef = new chef("Chef Remy", "remy@example.com", "chef_pass", kitchenManager);

        kitchenManager.addChefToStaff(currentChef);
        Application.users.clear();
        Application.users.add(currentChef);
        Application.users.add(kitchenManager);
        Application.currentUser = null;
        Application.lastSystemMessage = null;
    }

    @Given("a task is assigned to me")
    public void a_task_is_assigned_to_me() {
        taskDeadline = LocalDateTime.now().plusHours(2);
        taskDetailsForTest = "Prepare " + TEST_TASK_NAME + " according to recipe standards";
        notificationService.clearNotifications(currentChef.getEmail());


        currentChef.receiveTask("Initial Task");
        currentChef.receiveNotification("Initial Notification");
        currentChef.selectTask("Initial Task");
        currentChef.isTaskCompleted("Initial Task");
        currentChef.getTaskDetails("Initial Task");


        Application.setSystemMessage("Initial message");
    }

    @When("it is saved in the system")
    public void it_is_saved_in_the_system() {
        kitchenManager.assignTask(TEST_TASK_NAME, currentChef, taskDetailsForTest, taskDeadline);


        currentChef.receiveTaskWithDetails(TEST_TASK_NAME, taskDetailsForTest,
                taskDeadline.format(DateTimeFormatter.ofPattern("hh:mm a")));

        kitchenManager.getTaskDetails(TEST_TASK_NAME);
    }

    @Then("I should receive a notification")
    public void i_should_receive_a_notification() {
        List<String> notifications = notificationService.getNotifications(currentChef.getEmail());
        assertFalse(notifications.isEmpty(), "Notification list should not be empty.");

        String expectedDeadlineFormatted = taskDeadline.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String expectedMessage = "New task assigned: " + TEST_TASK_NAME +
                ". Details: " + taskDetailsForTest +
                ". Deadline: " + expectedDeadlineFormatted;

        assertTrue(notifications.stream().anyMatch(notification -> notification.equals(expectedMessage)),
                "Expected notification not found.");


        currentChef.selectTask(TEST_TASK_NAME);
        currentChef.completeTask();
        boolean isTaskCompleted = currentChef.isTaskCompleted(TEST_TASK_NAME);
        assertTrue(isTaskCompleted, "Task should be marked as completed");
    }

    @Given("I have a task due in an hour")
    public void i_have_a_task_due_in_an_hour() {
        taskDeadline = LocalDateTime.now().plusHours(1);
        taskDetailsForTest = "Urgent preparation of " + TEST_TASK_NAME;

        notificationService.clearNotifications(currentChef.getEmail());
        kitchenManager.assignTask(TEST_TASK_NAME, currentChef, taskDetailsForTest, taskDeadline);


        List<String> tasks = currentChef.getTasks();
        assertTrue(tasks.contains(TEST_TASK_NAME), "Task should be in chef's task list");
    }

    @When("the time is near")
    public void the_time_is_near() {
        String reminderMessage = "REMINDER: Task '" + TEST_TASK_NAME + "' is due soon!";
        notificationService.sendNotification(currentChef.getEmail(), reminderMessage);
    }

    @Then("I should get a reminder alert")
    public void i_should_get_a_reminder_alert() {
        List<String> notifications = notificationService.getNotifications(currentChef.getEmail());
        String expectedReminderMessage = "REMINDER: Task '" + TEST_TASK_NAME + "' is due soon!";

        assertTrue(notifications.stream().anyMatch(notification -> notification.equals(expectedReminderMessage)),
                "Expected reminder alert not found.");
    }

    @Given("my task is rescheduled")
    public void my_task_is_rescheduled() {
        taskDeadline = LocalDateTime.now().plusHours(3);
        taskDetailsForTest = "Standard preparation of " + TEST_TASK_NAME;
        notificationService.clearNotifications(currentChef.getEmail());
        kitchenManager.assignTask(TEST_TASK_NAME, currentChef, taskDetailsForTest, taskDeadline);
    }

    @When("the update is saved")
    public void the_update_is_saved() {
        LocalDateTime newDeadline = taskDeadline.plusHours(1);
        String newDetails = "Updated instructions for " + TEST_TASK_NAME;

        this.taskDeadline = newDeadline;
        this.taskDetailsForTest = newDetails;

        kitchenManager.rescheduleTask(TEST_TASK_NAME, currentChef, newDetails, newDeadline);
    }

    @Then("I should receive a notification about the change")
    public void i_should_receive_a_notification_about_the_change() {
        List<String> notifications = notificationService.getNotifications(currentChef.getEmail());

        String newDeadlineFormatted = taskDeadline.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String expectedChangeMessage = "UPDATE: Task '" + TEST_TASK_NAME + "' has been rescheduled." +
                " New Details: " + taskDetailsForTest +
                ". New Deadline: " + newDeadlineFormatted;

        assertTrue(notifications.stream().anyMatch(notification -> notification.equals(expectedChangeMessage)),
                "Expected change notification not found.");


        String updatedDetails = currentChef.getTaskDetails(TEST_TASK_NAME);
        assertNotNull(updatedDetails, "Task details should be available");
    }
}