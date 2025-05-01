package testCases;

import io.cucumber.java.en.*;

public class ChefTaskNotificationsSteps {

    @Given("a task is assigned to me")
    public void a_task_is_assigned_to_me() {
        System.out.println("Task assigned to chef");
    }

    @When("it is saved in the system")
    public void it_is_saved_in_the_system() {
        System.out.println("Task saved in system");
    }

    @Then("I should receive a notification")
    public void i_should_receive_a_notification() {
        System.out.println("Notification received");
    }

    @Given("I have a task due in an hour")
    public void i_have_a_task_due_in_an_hour() {
        System.out.println("Task deadline approaching");
    }

    @When("the time is near")
    public void the_time_is_near() {
        System.out.println("It is almost time for the task");
    }

    @Then("I should get a reminder alert")
    public void i_should_get_a_reminder_alert() {
        System.out.println("Reminder alert sent");
    }

    @Given("my task is rescheduled")
    public void my_task_is_rescheduled() {
        System.out.println("Task rescheduled");
    }

    @When("the update is saved")
    public void the_update_is_saved() {
        System.out.println("Task update saved");
    }

    @Then("I should receive a notification about the change")
    public void i_should_receive_a_notification_about_the_change() {
        System.out.println("Notification of task change sent");
    }
}
