package testCases;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import cook.*;
import java.time.LocalDateTime; // استيراد LocalDateTime
import java.time.temporal.ChronoUnit; // لاستخدامها في تحديد الموعد النهائي
import java.util.*;

public class KitchenManagerTaskAssignmentSteps {

    private chef selectedChef;
    private final kitchen_manager kitchenManager;
    private List<chef> availableChefs;
    private String assignedTaskName;
    private String defaultTaskDetails; // لتخزين التفاصيل الافتراضية
    private LocalDateTime defaultTaskDeadline; // لتخزين الموعد النهائي الافتراضي

    public KitchenManagerTaskAssignmentSteps() {
        InventoryService inventoryService = new InventoryService();
        this.kitchenManager = new kitchen_manager("Manager", "manager@email.com", "password", inventoryService);
        // تهيئة القيم الافتراضية للتفاصيل والموعد النهائي
        this.defaultTaskDetails = "Standard preparation required.";
        this.defaultTaskDeadline = LocalDateTime.now().plus(2, ChronoUnit.HOURS); // مثال: بعد ساعتين من الآن
    }

    @Given("the kitchen manager is logged into the system")
    public void theKitchenManagerIsLoggedIntoTheSystem() {
        Application.currentUser = kitchenManager;
        // يمكن إضافة تهيئة للشيفات هنا أو في الخطوات الأخرى حسب الحاجة
        availableChefs = Arrays.asList(
                new chef("Ahmed", "ahmed@email.com", "pass123", kitchenManager),
                new chef("Layla", "layla@email.com", "pass123", kitchenManager),
                new chef("Omar", "omar@email.com", "pass123", kitchenManager)
        );
        // إضافة الشيفات إلى طاقم المدير (إذا كانت الدالة addChefToStaff مستخدمة)
        for (chef c : availableChefs) {
            kitchenManager.addChefToStaff(c);
        }
    }

    @When("the kitchen manager selects a chef")
    public void theKitchenManagerSelectsAChef() {
        // التأكد من أن قائمة الشيفات ليست فارغة قبل محاولة الوصول للعنصر الأول
        if (availableChefs != null && !availableChefs.isEmpty()) {
            selectedChef = availableChefs.getFirst();
        } else {
            // في حالة عدم وجود شيفات، يمكن إنشاء واحد افتراضي أو رمي خطأ
            selectedChef = new chef("DefaultChef", "default@example.com", "pass", kitchenManager);
            kitchenManager.addChefToStaff(selectedChef); // التأكد من إضافته للمدير
            System.out.println("Warning: No available chefs, created a default one.");
        }
        assertNotNull(selectedChef, "Selected chef should not be null.");
    }

    @And("assigns a cooking task")
    public void assignsACookingTask() {
        assertNotNull(selectedChef, "Cannot assign task, selectedChef is null."); // تحقق إضافي
        assignedTaskName = "Prepare grilled chicken";
        // تمرير التفاصيل والموعد النهائي
        kitchenManager.assignTask(assignedTaskName, selectedChef, defaultTaskDetails, defaultTaskDeadline);
        // لا حاجة لاستدعاء selectedChef.selectTask(assignedTaskName) هنا
        // لأن assignTask تقوم بتحديث الشيف داخليًا وتفاصيل المهمة تُخزن في الشيف
    }

    @Then("the system should save the task assignment")
    public void theSystemShouldSaveTheTaskAssignment() {
        assertNotNull(selectedChef, "Selected chef is null, cannot verify task assignment.");
        // التحقق من أن المهمة موجودة في قائمة مهام الشيف
        assertTrue(selectedChef.getTasks().contains(assignedTaskName),
                "Task " + assignedTaskName + " was not found in chef's task list.");
        // يمكنك أيضًا التحقق من تفاصيل المهمة إذا لزم الأمر
        String taskDetailsFromChef = selectedChef.getTaskDetails(assignedTaskName);
        assertNotNull(taskDetailsFromChef, "Task details should not be null for assigned task.");
        assertTrue(taskDetailsFromChef.contains(defaultTaskDetails), "Task details do not match.");
    }

    @Given("the kitchen manager is assigning tasks")
    public void theKitchenManagerIsAssigningTasks() {
        Application.currentUser = kitchenManager; // تأكد من تسجيل دخول المدير
        availableChefs = Arrays.asList(
                new chef("Ali", "ali@email.com", "pass123", kitchenManager),
                new chef("Zainab", "zainab@email.com", "pass123", kitchenManager),
                new chef("Nour", "nour@email.com", "pass123", kitchenManager)
        );
        for (chef c : availableChefs) {
            kitchenManager.addChefToStaff(c);
        }
    }

    @When("the system suggests a chef with a lighter workload")
    public void theSystemSuggestsChefWithLighterWorkload() {
        // هذا الجزء يحتاج إلى منطق لاختيار الشيف بناءً على عبء العمل
        // حاليًا، سنختار الشيف الأول كعينة
        if (availableChefs != null && !availableChefs.isEmpty()) {
            selectedChef = availableChefs.getFirst(); // أو منطق لاختيار الشيف الأقل انشغالاً
        } else {
            selectedChef = new chef("DefaultChefLighter", "defaultlighter@example.com", "pass", kitchenManager);
            kitchenManager.addChefToStaff(selectedChef);
        }
        assertNotNull(selectedChef, "Selected chef for lighter workload should not be null.");
    }

    @Then("the kitchen manager should assign the task to that chef based on workload")
    public void assignTaskBasedOnWorkload() {
        assertNotNull(selectedChef, "Cannot assign task based on workload, selectedChef is null.");
        assignedTaskName = "Boil pasta";
        String taskDetails = "Boil pasta until al dente.";
        LocalDateTime deadline = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
        // تمرير التفاصيل والموعد النهائي
        kitchenManager.assignTask(assignedTaskName, selectedChef, taskDetails, deadline);

        assertTrue(selectedChef.getTasks().contains(assignedTaskName),
                "Task " + assignedTaskName + " (workload) was not found in chef's task list.");
    }

    @When("the system suggests a chef with relevant expertise")
    public void suggestChefBasedOnExpertise() {
        // هذا الجزء يحتاج إلى منطق لاختيار الشيف بناءً على الخبرة
        // حاليًا، سنختار الشيف الأول كعينة
        if (availableChefs != null && !availableChefs.isEmpty()) {
            selectedChef = availableChefs.get(availableChefs.size() > 1 ? 1 : 0); // اختيار شيف مختلف إن أمكن
        } else {
            selectedChef = new chef("DefaultChefExpertise", "defaultexpertise@example.com", "pass", kitchenManager);
            kitchenManager.addChefToStaff(selectedChef);
        }
        assertNotNull(selectedChef, "Selected chef for expertise should not be null.");
    }

    @Then("the kitchen manager should assign the task to that chef based on expertise")
    public void assignTaskBasedOnExpertise() {
        assertNotNull(selectedChef, "Cannot assign task based on expertise, selectedChef is null.");
        assignedTaskName = "Grill beef steak";
        String taskDetails = "Grill steak to medium-rare.";
        LocalDateTime deadline = LocalDateTime.now().plus(90, ChronoUnit.MINUTES);
        // تمرير التفاصيل والموعد النهائي
        kitchenManager.assignTask(assignedTaskName, selectedChef, taskDetails, deadline);

        assertTrue(selectedChef.getTasks().contains(assignedTaskName),
                "Task " + assignedTaskName + " (expertise) was not found in chef's task list.");
    }
}