package cook;

import javax.swing.*;
import java.util.*;

public class chef extends User{
    private List<String> tasks;
    private Map<String, Boolean> taskCompletionStatus;
    private String selectedTask;
    private kitchen_manager kitchenManager;

    public chef(String name, String email, String password, kitchen_manager kitchenManager) {
        super(name, email, password, Role.Chef);
        this.tasks = new ArrayList<>();
        this.taskCompletionStatus = new HashMap<>();
        this.kitchenManager = kitchenManager;
    }

    public void receiveTask(String taskName) {
        if (!tasks.contains(taskName)) {
            tasks.add(taskName);
            taskCompletionStatus.put(taskName, false);
            System.out.println("Received new task: " + taskName);
        }
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
            System.out.println("Marked task as completed: " + selectedTask);
        } else {
            System.out.println("No task selected to complete");
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
}
