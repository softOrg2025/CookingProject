package testCases;
import cook.chef;
import cook.kitchen_manager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TaskNotificationSteps {
    private kitchen_manager kitchenManager = new kitchen_manager();
    private chef chef     = new chef("John Doe", "john@example.com", "password123");
private String taskName="prepare dinner";

    @Given("the kitchen manager has assigned a task")
    public void the_kitchen_manager_has_assigned_a_task() {

        kitchenManager.assignTask(taskName, chef);

    }
    @Then("the system should display the task notification")
    public void the_system_should_display_the_task_notification() {
        String task = chef.getSelectedTask();
        if (task != null) {
            System.out.println("Chef received task notification: " + task);
        } else {
            System.out.println("No task assigned.");
        }
    }

    @Given("the chef has received a task notification")
    public void the_chef_has_received_a_task_notification() {
        chef.receiveTask(taskName);

    }

    @When("the chef selects the task")
    public void the_chef_selects_the_task() {
        chef.selectTask(taskName);
    }

    @Then("the system should display the task details")
    public void the_system_should_display_the_task_details() {
    System.out.println(kitchenManager.getTaskDetails(taskName));
    }

    @Given("the chef has completed a task")
    public void the_chef_has_completed_a_task() {

    }

    @When("the chef marks the task as completed")
    public void the_chef_marks_the_task_as_completed() {
        chef.completeTask();
    }

    @Then("the system should update the task status")
    public void the_system_should_update_the_task_status() {
        if (chef.isTaskCompleted()) {
            System.out.println("The task \"" + chef.getSelectedTask() + "\" is completed.");
        } else {
            System.out.println("The task is not completed.");
        }
    }
}
