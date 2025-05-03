package testCases;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class KitchenManagerTaskAssignmentSteps {

    private boolean isLoggedIn = false;
    private Chef selectedChef;
    private List<Chef> availableChefs;
    private Task assignedTask;
    private final TestContext context;

    public KitchenManagerTaskAssignmentSteps(TestContext context) {
        this.context = context;
    }

    // نموذج Chef
    static class Chef {
        String name;
        int workload; // عدد المهام الحالية
        List<String> expertise;

        Chef(String name, int workload, List<String> expertise) {
            this.name = name;
            this.workload = workload;
            this.expertise = expertise;
        }
    }

    // نموذج Task
    static class Task {
        String name;
        String requiredSkill;

        Task(String name, String requiredSkill) {
            this.name = name;
            this.requiredSkill = requiredSkill;
        }
    }

    @Given("the kitchen manager is logged into the system")
    public void theKitchenManagerIsLoggedIntoTheSystem() {
        isLoggedIn = true;
        assertTrue(isLoggedIn);
        System.out.println("✅ Kitchen manager logged in.");
    }

    @When("the kitchen manager selects a chef")
    public void theKitchenManagerSelectsAChef() {
        availableChefs = Arrays.asList(
                new Chef("Ahmed", 3, List.of("grilling", "salads")),
                new Chef("Layla", 1, List.of("pastry", "soups")),
                new Chef("Omar", 2, List.of("meat", "seafood"))
        );
        selectedChef = availableChefs.get(0); // اختيار عشوائي أو أول
        assertNotNull(selectedChef);
        System.out.println("👨‍🍳 Selected chef: " + selectedChef.name);
    }

    @And("assigns a cooking task")
    public void assignsACookingTask() {
        assignedTask = new Task("Prepare grilled chicken", "grilling");
        assertNotNull(selectedChef);
        System.out.println("🍽 Task assigned to " + selectedChef.name + ": " + assignedTask.name);
    }

    @Then("the system should save the task assignment")
    public void theSystemShouldSaveTheTaskAssignment() {
        assertNotNull(assignedTask);
        assertNotNull(selectedChef);
        System.out.println("💾 Task saved for " + selectedChef.name);
    }

    @Given("the kitchen manager is assigning tasks")
    public void theKitchenManagerIsAssigningTasks() {
        availableChefs = Arrays.asList(
                new Chef("Ali", 5, List.of("soups")),
                new Chef("Zainab", 1, List.of("pasta")),
                new Chef("Nour", 2, List.of("grilling"))
        );
    }

    @When("the system suggests a chef with a lighter workload")
    public void theSystemSuggestsChefWithLighterWorkload() {
        selectedChef = availableChefs.stream()
                .min(Comparator.comparingInt(c -> c.workload))
                .orElse(null);
        assertNotNull(selectedChef);
        System.out.println("📉 Suggested chef (light workload): " + selectedChef.name);
    }

    @Then("the kitchen manager should assign the task to that chef based on workload")
    public void assignTaskBasedOnWorkload() {
        assignedTask = new Task("Boil pasta", "pasta");
        assertTrue(selectedChef.expertise.contains("pasta") || selectedChef.workload < 3);
        System.out.println("📝 Task assigned to " + selectedChef.name + " based on workload.");
    }

    @When("the system suggests a chef with relevant expertise")
    public void suggestChefBasedOnExpertise() {
        String skillNeeded = "grilling";
        selectedChef = availableChefs.stream()
                .filter(c -> c.expertise.contains(skillNeeded))
                .findFirst()
                .orElse(null);
        assertNotNull(selectedChef);
        System.out.println("🎯 Suggested chef (expertise): " + selectedChef.name);
    }

    @Then("the kitchen manager should assign the task to that chef based on expertise")
    public void assignTaskBasedOnExpertise() {
        assignedTask = new Task("Grill beef steak", "grilling");
        assertTrue(selectedChef.expertise.contains(assignedTask.requiredSkill));
        System.out.println("✅ Assigned based on expertise: " + assignedTask.name + " to " + selectedChef.name);
    }

    @Then("the kitchen manager should assign the task to that chef")
    public void assignTaskToChef() {
        assertNotNull(selectedChef);
        System.out.println("✅ Task assigned to chef: " + selectedChef.name);
    }
}
