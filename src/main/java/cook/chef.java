package cook;

import java.util.*;

public class chef extends User {
    private final List<String> tasks;
    private final Map<String, Boolean> taskCompletionStatus;
    private String selectedTask;
    private final kitchen_manager kitchenManager;
    private final Map<String, String> taskDetailsStorage;
    private final List<String> internalNotifications;


    public chef(String name, String email, String password, kitchen_manager kitchenManager) {
        super(name, email, password, Role.Chef);
        this.tasks = new ArrayList<>();
        this.taskCompletionStatus = new HashMap<>();
        this.kitchenManager = kitchenManager;
        this.taskDetailsStorage = new HashMap<>(); // كان اسمه taskDetails
        this.internalNotifications = new ArrayList<>(); // كان اسمه notifications
    }




    public void receiveTask(String taskName) {
            tasks.add(taskName);
            taskCompletionStatus.put(taskName, false);

            receiveNotification("New task assigned: " + taskName);
    }


    public void receiveNotification(String message) {
        this.internalNotifications.add(message);

        System.out.println("Chef " + name + " received internal notification: " + message);
    }

    public void selectTask(String taskName) {
            selectedTask = taskName;
    }

    public List<String> getTasks() {
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public void completeTask() {
            taskCompletionStatus.put(selectedTask, true);
            receiveNotification("Task completed: " + selectedTask);
    }

    public boolean isTaskCompleted(String taskName) {
        return taskCompletionStatus.getOrDefault(taskName, false);
    }

    public String getSelectedTask() {
        return selectedTask;
    }


    public String getTaskDetails(String taskName) {
            return this.taskDetailsStorage.get(taskName);
    }


    public void receiveTaskWithDetails(String taskName, String details, String deadline) {

        if (!tasks.contains(taskName)) {
            receiveTask(taskName);
        }

        this.taskDetailsStorage.put(taskName, "Details: " + details + " | Deadline: " + deadline);

        receiveNotification("Task details updated for: " + taskName);
    }
}