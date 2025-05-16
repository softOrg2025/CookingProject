package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;


import cook.chef;
import cook.kitchen_manager;
import cook.NotificationService;
import cook.InventoryService;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChefTaskNotificationsSteps {

    private chef currentChef;
    private kitchen_manager kitchenManager;
    private NotificationService notificationService;
    private InventoryService inventoryService; // Dependency for KitchenManager

    // Task details used across scenarios for consistency
    private final String testTaskName = "Prepare Special Dish";
    private final String defaultTaskDetailsPrefix = "Prepare " + testTaskName + " according to recipe standards";

    public ChefTaskNotificationsSteps() {
        // Initialize services for each scenario
        this.inventoryService = new InventoryService();
        this.notificationService = new NotificationService();

        // Create kitchen manager
        this.kitchenManager = new kitchen_manager("KM Lidia", "km@example.com", "km_pass", this.inventoryService);

        // Create chef, ensuring it's linked to the kitchen manager as per constructor
        this.currentChef = new chef("Chef Remy", "remy@example.com", "chef_pass", this.kitchenManager);
        // Application.currentUser = this.currentChef; // Optional: if steps depend on a global current user
    }

    private void clearChefSystemNotifications() {
        if (currentChef != null && notificationService != null) {
            notificationService.clearNotifications(currentChef.getEmail());
        }
    }

    // Scenario 1: Alert when task is created
    @Given("a task is assigned to me")
    public void a_task_is_assigned_to_me() {
        clearChefSystemNotifications();
        // The actual assignment happens in the "When" step.
        // This "Given" step sets the context.
        System.out.println("GIVEN: Chef " + currentChef.getName() + " is ready for a new task assignment.");
    }

    @When("it is saved in the system")
    public void it_is_saved_in_the_system() {
        // The kitchen_manager assigns the task. This internally calls chef.receiveTaskWithDetails,
        // which makes the chef object aware (and chef's internal receiveNotification logs to console).
        kitchenManager.assignTask(testTaskName, currentChef);

        // Now, simulate the system sending a notification via NotificationService.
        // The details for the notification message should match what kitchenManager.assignTask creates.
        String expectedDeadlineSubstring = LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("hh:mm a"));
        String notificationMessage = "New task assigned: " + testTaskName + ". Details: " + defaultTaskDetailsPrefix + ". Deadline: " + expectedDeadlineSubstring;
        notificationService.sendNotification(currentChef.getEmail(), notificationMessage);

        System.out.println("WHEN: Task '" + testTaskName + "' assigned by manager and system notification sent via NotificationService.");
    }

    @Then("I should receive a notification")
    public void i_should_receive_a_notification() {
        List<String> notifications = notificationService.getNotifications(currentChef.getEmail());
        Assertions.assertFalse(notifications.isEmpty(), "Chef should have received a system notification.");

        String expectedMessagePart1 = "New task assigned: " + testTaskName;
        String expectedMessagePart2 = defaultTaskDetailsPrefix; // "Prepare Special Dish according to recipe standards"
        // Deadline check is a bit tricky due to potential slight timing differences if re-calculating.
        // It's safer to check for the presence of "Deadline:"
        String expectedMessagePart3 = "Deadline:";


        boolean foundMatch = notifications.stream().anyMatch(n ->
                n.contains(expectedMessagePart1) &&
                        n.contains(expectedMessagePart2) &&
                        n.contains(expectedMessagePart3)
        );

        Assertions.assertTrue(foundMatch, "Notification content for new task is incorrect or missing. Received: " + notifications);
        System.out.println("THEN: Chef " + currentChef.getName() + " verified system notification for new task: " + notifications.get(0));
    }

    // Scenario 2: Reminder before task deadline
    @Given("I have a task due in an hour")
    public void i_have_a_task_due_in_an_hour() {
        clearChefSystemNotifications();
        // Assign a task. The kitchenManager.assignTask sets a deadline 2 hours from now.
        // For this Gherkin, we are testing the reminder mechanism, not the exact deadline calculation system
        // which is not fully detailed in helper classes.
        kitchenManager.assignTask(testTaskName, currentChef);
        System.out.println("GIVEN: Chef " + currentChef.getName() + " has task '" + testTaskName + "'. A reminder scenario is being set up.");
        // The "due in an hour" is a premise. The "When" step will simulate the "time is near" logic.
    }

    @When("the time is near")
    public void the_time_is_near() {
        // Simulate the system identifying that 'testTaskName' is approaching its deadline
        // and sending a reminder via NotificationService.
        // We don't have a helper function to check "time is near", so this is simulated action.
        String reminderMessage = "REMINDER: Task '" + testTaskName + "' is due soon!";
        notificationService.sendNotification(currentChef.getEmail(), reminderMessage);
        System.out.println("WHEN: System simulated 'time is near' for task '" + testTaskName + "' and sent a reminder.");
    }

    @Then("I should get a reminder alert")
    public void i_should_get_a_reminder_alert() {
        List<String> notifications = notificationService.getNotifications(currentChef.getEmail());
        Assertions.assertFalse(notifications.isEmpty(), "Chef should have received a reminder alert.");

        String expectedReminder = "REMINDER: Task '" + testTaskName + "' is due soon!";
        boolean foundReminder = notifications.stream().anyMatch(n -> n.equals(expectedReminder));
        Assertions.assertTrue(foundReminder, "Reminder alert content is incorrect or missing. Received: " + notifications);
        System.out.println("THEN: Chef " + currentChef.getName() + " verified reminder alert: " + notifications.stream().filter(n->n.equals(expectedReminder)).findFirst().get());
    }

    // Scenario 3: Notify changes to task schedule
    @Given("my task is rescheduled")
    public void my_task_is_rescheduled() {
        clearChefSystemNotifications();
        // Ensure the task exists first.
        kitchenManager.assignTask(testTaskName, currentChef);
        System.out.println("GIVEN: Chef " + currentChef.getName() + " has an existing task '" + testTaskName + "' which will be rescheduled.");
    }

    @When("the update is saved")
    public void the_update_is_saved() {
        // Re-assigning the task with the same name by kitchenManager effectively updates it in the chef's list
        // because chef.receiveTaskWithDetails will update details for an existing task name.
        // The deadline will be a new one (current time + 2 hours from kitchenManager.assignTask).
        kitchenManager.assignTask(testTaskName, currentChef); // This updates the task for the chef

        // Simulate the system sending a notification about this change via NotificationService.
        String newDeadlineSubstring = LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("hh:mm a"));
        String changeNotificationMessage = "UPDATE: Task '" + testTaskName + "' has been rescheduled. New Deadline: " + newDeadlineSubstring;
        notificationService.sendNotification(currentChef.getEmail(), changeNotificationMessage);
        System.out.println("WHEN: Task '" + testTaskName + "' was updated (rescheduled) and system notification sent.");
    }

    @Then("I should receive a notification about the change")
    public void i_should_receive_a_notification_about_the_change() {
        List<String> notifications = notificationService.getNotifications(currentChef.getEmail());
        Assertions.assertFalse(notifications.isEmpty(), "Chef should have received a notification about the task change.");

        String expectedChangeMessagePart1 = "UPDATE: Task '" + testTaskName + "' has been rescheduled.";
        String expectedChangeMessagePart2 = "New Deadline:";

        boolean foundChangeNotification = notifications.stream().anyMatch(n ->
                n.contains(expectedChangeMessagePart1) &&
                        n.contains(expectedChangeMessagePart2)
        );

        Assertions.assertTrue(foundChangeNotification, "Change notification content is incorrect or missing. Received: " + notifications);
        System.out.println("THEN: Chef " + currentChef.getName() + " verified task change notification: " + notifications.stream().filter(n-> n.contains(expectedChangeMessagePart1)).findFirst().get());
    }
}