package testCases;

import cook.InventoryService;
import cook.chef;
import cook.kitchen_manager;
import cook.Application;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.Before;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskNotificationSteps {

    private final InventoryService inventoryService;
    private final kitchen_manager kitchenManager;
    private final chef chefInstance;
    private final String taskName;
    private String defaultTaskDetails;
    private LocalDateTime defaultTaskDeadline;

    public TaskNotificationSteps() {
        this.inventoryService = new InventoryService();
        this.kitchenManager = new kitchen_manager("Manager", "manager@restaurant.com", "password123", inventoryService);
        this.chefInstance = new chef("John Doe", "john.doe@example.com", "password123", kitchenManager);
        this.kitchenManager.addChefToStaff(this.chefInstance);
        this.taskName = "Prepare Dinner";
        this.defaultTaskDetails = "Prepare dinner special for tonight.";
        this.defaultTaskDeadline = LocalDateTime.now().plusHours(3);

        if (Application.notificationService == null) {
            Application.notificationService = new cook.NotificationService();
        }
    }

    @Before
    public void setUp() {
        Application.notificationService.clearNotifications(chefInstance.getEmail());
    }

    @Given("the kitchen manager has assigned a task")
    public void the_kitchen_manager_has_assigned_a_task() {
        kitchenManager.assignTask(taskName, chefInstance, defaultTaskDetails, defaultTaskDeadline);
        assertTrue(chefInstance.getTasks().contains(taskName));
    }

    @Then("the system should display the task notification")
    public void the_system_should_display_the_task_notification() {
        List<String> notifications = Application.notificationService.getNotifications(chefInstance.getEmail());
        assertFalse(notifications.isEmpty());
        assertTrue(notifications.get(0).contains(taskName));
    }

    @Given("the chef has received a task notification")
    public void the_chef_has_received_a_task_notification() {
        kitchenManager.assignTask(taskName, chefInstance, defaultTaskDetails, defaultTaskDeadline);
        List<String> tasks = chefInstance.getTasks();
        assertTrue(tasks.contains(taskName));
    }

    @When("the chef selects the task")
    public void the_chef_selects_the_task() {
        chefInstance.selectTask(taskName);
        assertEquals(taskName, chefInstance.getSelectedTask());
    }

    @Then("the system should display the task details")
    public void the_system_should_display_the_task_details() {
        String details = chefInstance.getTaskDetails(taskName);
        assertNotNull(details);
        assertTrue(details.contains(defaultTaskDetails));
    }

    @Given("the chef has completed a task")
    public void the_chef_has_completed_a_task() {
        kitchenManager.assignTask(taskName, chefInstance, defaultTaskDetails, defaultTaskDeadline);
        chefInstance.selectTask(taskName);
        assertFalse(chefInstance.isTaskCompleted(taskName));
    }

    @When("the chef marks the task as completed")
    public void the_chef_marks_the_task_as_completed() {
        chefInstance.completeTask();
        assertTrue(chefInstance.isTaskCompleted(taskName));
    }

    @Then("the system should update the task status")
    public void the_system_should_update_the_task_status() {
        assertTrue(chefInstance.isTaskCompleted(taskName));
    }
}