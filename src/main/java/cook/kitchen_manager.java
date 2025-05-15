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


}
