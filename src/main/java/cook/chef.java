package cook;


import java.util.*;

public class chef extends User{
    private List<String> tasks;
    private Map<String, Boolean> taskCompletionStatus;
    private String selectedTask;
    private kitchen_manager kitchenManager;
    private Map<String, String> taskDetails;
    private List<String> notifications;


    public chef(String name, String email, String password, kitchen_manager kitchenManager) {
        super(name, email, password, Role.Chef);
        this.tasks = new ArrayList<>();
        this.taskCompletionStatus = new HashMap<>();
        this.kitchenManager = kitchenManager;
        this.taskDetails = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    public void receiveTask(String taskName) {
        if (!tasks.contains(taskName)) {
            tasks.add(taskName);
            taskCompletionStatus.put(taskName, false);
            receiveNotification("New task assigned: " + taskName);
        }
    }

    public void receiveNotification(String message) {
        notifications.add(message);
        System.out.println("Chef " + name + " received notification: " + message);
    }

    public void selectTask(String taskName) {
        if (tasks.contains(taskName)) {
            selectedTask = taskName;
        } else {
            System.out.println("Task not found: " + taskName);
        }
    }

    public void completeTask() {
        if (selectedTask != null) {
            taskCompletionStatus.put(selectedTask, true);
            receiveNotification("Task completed: " + selectedTask);
        }
    }

    public boolean isTaskCompleted(String taskName) {
        return taskCompletionStatus.getOrDefault(taskName, false);
    }

    public String getSelectedTask() {
        return selectedTask;
    }

    public String getTaskDetails(String taskName) {
        if (kitchenManager != null) {
            return kitchenManager.getTaskDetails(taskName);
        }
        return "No task details available";
    }
    public void receiveTaskWithDetails(String taskName, String details, String deadline) {
        receiveTask(taskName);
        taskDetails.put(taskName, "Details: " + details + " | Deadline: " + deadline);
        receiveNotification("Task details updated for: " + taskName);
    }



}
