package cook;

import javax.swing.*;
import java.util.*;

public class chef extends User{
    private String name;
    private String email;
    private String password;
    private boolean isTaskCompleted;
    private String assignedTask;
    private List<String> tasks ;
    private Map<String, String> taskDetails;
    private boolean isTaskSelected;
    private String selectedTask;
    private Map<String, Boolean> taskCompletionStatus;



    public chef(String name , String email , String password ){
        super(name , email , password , Role.Chef);
        this.isTaskCompleted = false;
        this.tasks = new ArrayList<>();
        this.taskDetails = new HashMap<>();
        this.isTaskSelected=false;
        this.taskCompletionStatus = new HashMap<>();
    }


    public void approveSubstitution(Meal meal, String oldIngredient, String newIngredient) {
        meal.substituteIngredient(oldIngredient, newIngredient);
        System.out.println("Chef approved substitution for " + oldIngredient + " to " + newIngredient);
    }


    public void rejectSubstitution() {
        System.out.println("Chef rejected substitution.");
    }

    public void receiveTask(String task) {
        this.assignedTask = task;
        this.isTaskCompleted = false;
        tasks.add(task);
        taskCompletionStatus.put(task, false);



    }

    public void selectTask(String taskName) {
        if (tasks.contains(taskName)) {
            selectedTask = taskName;
            System.out.println("Chef selected the task: " + taskName);
        } else {
            System.out.println("Task \"" + taskName + "\" not found.");
        }
    }

    // Get the details of the selected task
    public String getSelectedTaskDetails() {
        if (selectedTask != null) {
            return "Task Details: \n" +
                    "Name: " + selectedTask + "\n" +
                    "Details: " + taskDetails.get(selectedTask);
        } else {
            return "No task selected.";
        }
    }


    public List<String> getTaskNames() {
        return tasks;
    }


    public String getSelectedTask() {
        return selectedTask;
    }


    public void completeTask() {
        if (selectedTask != null) {
            taskCompletionStatus.put(selectedTask, true);
            System.out.println("Task \"" + selectedTask + "\" has been marked as completed.");
        } else {
            System.out.println("No task selected to mark as completed.");
        }
    }


    public boolean isTaskCompleted() {
        return selectedTask != null && taskCompletionStatus.getOrDefault(selectedTask, false);
    }
}
