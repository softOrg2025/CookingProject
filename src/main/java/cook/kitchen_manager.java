package cook;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class kitchen_manager extends User {
    private Map<String, String> taskDetails; // TaskName -> "Description: ... | Deadline: ..."
    private InventoryService inventoryService;
    private List<chef> kitchenStaff; // تأكد من وجود هذا الحقل
    private Map<String, chef> taskAssignments; // TaskName -> Chef

    public kitchen_manager(String name, String email, String password, InventoryService inventoryService) {
        super(name, email, password, Role.manager);
        this.taskDetails = new HashMap<>();
        this.inventoryService = inventoryService;
        this.kitchenStaff = new ArrayList<>(); // وتأكد من تهيئته هنا
        this.taskAssignments = new HashMap<>();
    }

    // دالة لإضافة شيف للطاقم
    public void addChefToStaff(chef c) {
        if (c != null && !kitchenStaff.contains(c)) {
            kitchenStaff.add(c);
        }
    }

    // تأكد أن هذه الدالة تقبل 4 وسطاء
    public void assignTask(String taskName, chef assignedChef, String details, LocalDateTime deadlineDateTime) {
        String deadlineFormatted = deadlineDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String fullTaskDetails = "Description: " + details + " | Deadline: " + deadlineFormatted;

        taskDetails.put(taskName, fullTaskDetails);
        taskAssignments.put(taskName, assignedChef);

        assignedChef.receiveTaskWithDetails(taskName, details, deadlineFormatted);

        String notificationMessage = "New task assigned: " + taskName +
                ". Details: " + details +
                ". Deadline: " + deadlineFormatted;
        Application.notificationService.sendNotification(assignedChef.getEmail(), notificationMessage);
        System.out.println("Assigned task: " + taskName + " to chef " + assignedChef.getName() + " and sent notification via Application Service.");
    }

    // تأكد أن هذه الدالة موجودة وتقبل 4 وسطاء
    public void rescheduleTask(String taskName, chef assignedChef, String newDetails, LocalDateTime newDeadlineDateTime) {
        if (taskAssignments.containsKey(taskName) && taskAssignments.get(taskName).equals(assignedChef)) {
            String newDeadlineFormatted = newDeadlineDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
            String fullTaskDetails = "Description: " + newDetails + " | Deadline: " + newDeadlineFormatted;

            taskDetails.put(taskName, fullTaskDetails);
            assignedChef.receiveTaskWithDetails(taskName, newDetails, newDeadlineFormatted);

            String notificationMessage = "UPDATE: Task '" + taskName + "' has been rescheduled." +
                    " New Details: " + newDetails +
                    ". New Deadline: " + newDeadlineFormatted;
            Application.notificationService.sendNotification(assignedChef.getEmail(), notificationMessage);
            System.out.println("Rescheduled task: " + taskName + " for chef " + assignedChef.getName() + " and sent notification via Application Service.");
        } else {
            System.out.println("Cannot reschedule task: " + taskName + ". Not found or not assigned to " + assignedChef.getName());
        }
    }

    public String getTaskDetails(String taskName) {
        return taskDetails.getOrDefault(taskName, "No details available for: " + taskName);
    }
}