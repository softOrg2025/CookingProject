package cook;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class kitchen_manager extends User {
    private Map<String, String> taskDetails;
    private InventoryService inventoryService;
    private List<chef> kitchenStaff;
    private Map<String, chef> taskAssignments;

    public kitchen_manager(String name, String email, String password, InventoryService inventoryService) {
        super(name, email, password, Role.manager);
        this.taskDetails = new HashMap<>();
        this.inventoryService = inventoryService;
        this.kitchenStaff = new ArrayList<>();
        this.taskAssignments = new HashMap<>();
    }

    public void assignTask(String taskName, chef chef) {
        String details = "Prepare " + taskName + " according to recipe standards";
        String deadline = LocalDateTime.now().plusHours(2).format(DateTimeFormatter.ofPattern("hh:mm a"));

        taskDetails.put(taskName, "Description: " + details + " | Deadline: " + deadline);
        taskAssignments.put(taskName, chef);

        chef.receiveTaskWithDetails(taskName, details, deadline);
        System.out.println("Assigned task: " + taskName + " to chef " + chef.name);
    }


    public String getTaskDetails(String taskName) {
        return taskDetails.getOrDefault(taskName, "No details available for: " + taskName);
    }

    // Staff Management Methods
    public void addChef(chef newChef) {
        kitchenStaff.add(newChef);
        System.out.println("Added new chef: " + newChef.name);
    }

    public List<chef> getKitchenStaff() {
        return Collections.unmodifiableList(kitchenStaff);
    }

    // Inventory Management Methods
    public List<InventoryItem> checkLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    public void checkAndRestock() {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        if (!lowStockItems.isEmpty()) {
            System.out.println("Low stock items needing restock:");
            lowStockItems.forEach(item ->
                    System.out.printf("- %s (Current: %d, Threshold: %d)%n",
                            item.getIngredientName(),
                            item.getQuantity(),
                            item.getThreshold())
            );
        } else {
            System.out.println("All stock levels are adequate");
        }
    }

    public void generatePurchaseOrder(String ingredientName, int quantity, String supplier, double price) {
        System.out.printf("Generated PO: %d units of %s from %s at $%.2f each%n",
                quantity, ingredientName, supplier, price);
        // Implementation would send actual PO to supplier
    }

    // Kitchen Operations
    public void startShift() {
        System.out.println("Kitchen shift started. Staff present: " + kitchenStaff.size());
    }

    public void endShift() {
        System.out.println("Closing kitchen operations. Tasks completed:");
        taskDetails.keySet().forEach(System.out::println);
    }

    public void rescheduleTask(String taskName, String newDeadline) {
        if (taskDetails.containsKey(taskName)) {
            String currentDetails = taskDetails.get(taskName);
            taskDetails.put(taskName, currentDetails.split("\\|")[0] + " | Deadline: " + newDeadline);

            chef assignedChef = taskAssignments.get(taskName);
            if (assignedChef != null) {
                assignedChef.receiveNotification("Task rescheduled: " + taskName + " new deadline: " + newDeadline);
            }
        }
    }

    public void checkDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        taskDetails.forEach((taskName, details) -> {
            String deadlineStr = details.split("Deadline: ")[1];
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr, DateTimeFormatter.ofPattern("hh:mm a"));

            if (deadline.isBefore(now.plusHours(1))) {
                chef assignedChef = taskAssignments.get(taskName);
                if (assignedChef != null) {
                    assignedChef.receiveNotification("REMINDER: Task due soon: " + taskName);
                }
            }
        });
    }




}
