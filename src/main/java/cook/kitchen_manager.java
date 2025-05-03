package cook;

import java.util.*;

public class kitchen_manager extends User {
    private Map<String, String> taskDetails;

    public kitchen_manager(String name, String email, String password) {
        super(name, email, password, Role.manager);
        this.taskDetails = new HashMap<>();
    }

    public void assignTask(String taskName, chef chef) {
        String details = "Prepare " + taskName + " according to recipe standards";
        String deadline = "Today by 6:00 PM";
        taskDetails.put(taskName, "Description: " + details + " | Deadline: " + deadline);
        chef.receiveTask(taskName);
        System.out.println("Assigned task: " + taskName + " to chef " + chef.name);
    }

    public String getTaskDetails(String taskName) {
        return taskDetails.getOrDefault(taskName, "No details available for: " + taskName);
    }



}
