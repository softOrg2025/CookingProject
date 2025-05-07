package cook;

import java.util.*;

public class NotificationService {
    private Map<String, List<String>> userNotifications;

    public NotificationService() {
        this.userNotifications = new HashMap<>();
    }

    public void sendNotification(String userId, String message) {
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
    }

    public List<String> getNotifications(String userId) {
        return userNotifications.getOrDefault(userId, Collections.emptyList());
    }

    public void clearNotifications(String userId) {
        userNotifications.remove(userId);
    }
}
