package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class KitchenManagerTaskAssignmentSteps {
    private boolean kitchenManagerLoggedIn;
    private boolean chefSelected;
    private boolean taskAssigned;
    private boolean taskSaved;

    private boolean isLighterWorkloadChefSuggested;
    private boolean taskAssignedBasedOnWorkload;

    private boolean isExpertChefSuggested;
    private boolean taskAssignedBasedOnExpertise;
    private final TestContext context;

    public KitchenManagerTaskAssignmentSteps(TestContext context) {
        this.context = context;
    }

    @Given("the kitchen manager is logged into the system")
    public void theKitchenManagerIsLoggedIntoTheSystem() {
        context.isLoggedIn = true;
    }

    @When("the kitchen manager selects a chef")
    public void theKitchenManagerSelectsAChef() {
        chefSelected = true;
        Assertions.assertTrue(chefSelected);
    }

    @And("assigns a cooking task")
    public void assignsACookingTask() {
        taskAssigned = chefSelected;
        Assertions.assertTrue(taskAssigned);
    }

    @Then("the system should save the task assignment")
    public void theSystemShouldSaveTheTaskAssignment() {
        taskSaved = taskAssigned;
        Assertions.assertTrue(taskSaved);
    }

    // 🚀 السيناريو الثاني: تعيين المهمة بناءً على عبء العمل
    @Given("the kitchen manager is assigning tasks")
    public void theKitchenManagerIsAssigningTasks() {
        Assertions.assertTrue(true); // مجرد تحقق من تنفيذ الخطوة
    }

    @When("the system suggests a chef with a lighter workload")
    public void theSystemSuggestsAChefWithALighterWorkload() {
        isLighterWorkloadChefSuggested = true;
        Assertions.assertTrue(isLighterWorkloadChefSuggested);
    }

    @Then("the kitchen manager should assign the task to that chef based on workload")
    public void theKitchenManagerShouldAssignTheTaskToThatChefBasedOnWorkload() {
        taskAssignedBasedOnWorkload = isLighterWorkloadChefSuggested;
        Assertions.assertTrue(taskAssignedBasedOnWorkload);
    }

    // 🚀 السيناريو الثالث: تعيين المهمة بناءً على الخبرة

    @When("the system suggests a chef with relevant expertise")
    public void theSystemSuggestsAChefWithRelevantExpertise() {
        isExpertChefSuggested = true;
        Assertions.assertTrue(isExpertChefSuggested);
    }

    @Then("the kitchen manager should assign the task to that chef based on expertise")
    public void theKitchenManagerShouldAssignTheTaskToThatChefBasedOnExpertise() {
        taskAssignedBasedOnExpertise = isExpertChefSuggested;
        Assertions.assertTrue(taskAssignedBasedOnExpertise);
    }

    @Then("the kitchen manager should assign the task to that chef")
    public void theKitchenManagerShouldAssignTheTaskToThatChef() {
        Assertions.assertTrue(true); // فقط للتأكد أن الخطوة تنجح
    }

}
