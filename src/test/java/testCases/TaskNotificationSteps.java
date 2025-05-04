package testCases;
import cook.chef;
import cook.kitchen_manager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TaskNotificationSteps {
    private kitchen_manager kitchenManager = new kitchen_manager("Manager", "manager@restaurant.com", "password123");
    private chef chef = new chef("John Doe", "john.com", "password123", kitchenManager);
    private String taskName = "Prepare Dinner";




    @Given("the kitchen manager has assigned a task")
    public void the_kitchen_manager_has_assigned_a_task() {

        kitchenManager.assignTask(taskName, chef);

    }
    @Then("the system should display the task notification")
    public void the_system_should_display_the_task_notification() {
        String task = chef.getSelectedTask();
        if (task != null) {
            System.out.println("Notification: " + task);
        } else {
            System.out.println("No new task notifications");
        }
    }

    @Given("the chef has received a task notification")
    public void the_chef_has_received_a_task_notification() {
        kitchenManager.assignTask(taskName, chef);

    }

    @When("the chef selects the task")
    public void the_chef_selects_the_task() {

        chef.selectTask(taskName);
    }

    @Then("the system should display the task details")
    public void the_system_should_display_the_task_details() {
    System.out.println(chef.getTaskDetails(taskName));
    }

    @Given("the chef has completed a task")
    public void the_chef_has_completed_a_task() {
        kitchenManager.assignTask(taskName, chef);
        chef.selectTask(taskName);
    }

    @When("the chef marks the task as completed")
    public void the_chef_marks_the_task_as_completed() {
        chef.completeTask();
    }

    @Then("the system should update the task status")
    public void the_system_should_update_the_task_status() {
        boolean isCompleted = chef.isTaskCompleted(taskName);
        System.out.println("Task status updated. Completed: " + isCompleted);
    }
}
