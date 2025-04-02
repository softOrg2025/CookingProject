package cook;

import java.util.*;

public class kitchen_manager extends User {
    private Map<String, String> taskDetails;

    public kitchen_manager() {
        taskDetails = new HashMap<>();
    }


    public  void assignTask(String task, chef chef) {
        String taskDescription = "This is the description of " + task;
        String taskDeadline = "2025-04-03 6:00 PM";


        taskDetails.put(task, taskDescription + " | Deadline: " + taskDeadline);


        chef.receiveTask(task);
    }

    public String getTaskDetails(String taskName) {
        return taskDetails.get(taskName);
    }




}
