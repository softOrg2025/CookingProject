/*package cook;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Map;


public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static kitchen_manager manager;
    private static chef testChef;
    private static Customer testCustomer;

    public static void main(String[] args) {
        initializeTestData();
        displayWelcomeMessage();

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput();

            switch (choice) {
                case 1 -> testCustomerFunctionality();
                case 2 -> testChefFunctionality();
                case 3 -> testManagerFunctionality();
                case 4 -> testMealManagement();
                case 5 -> testAllergySystem();
                case 6 -> testLoginSystem();
                case 7 -> testInventorySystem();
                case 8 -> testIngredientsClass();
                case 9 -> testNotificationSystem();
                case 0 -> {
                    running = false;
                    System.out.println("Exiting the system. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeTestData() {

        InventoryService inventoryService = new InventoryService();
        manager = new kitchen_manager("Ali Manager", "manager@cook.com", "manager123", inventoryService);
        testChef = new chef("Mohamed Chef", "chef@cook.com", "chef123", manager);
        testCustomer = new Customer("Ahmed Customer", "customer@cook.com", "customer123");


        Application.users.add(manager);
        Application.users.add(testChef);
        Application.users.add(testCustomer);


        Meal pasta = new Meal(Arrays.asList("Pasta", "Tomato Sauce", "Cheese"), 'M', 12.99);
        pasta.setName("Pasta Carbonara");

        Meal salad = new Meal(Arrays.asList("Lettuce", "Tomato", "Cucumber", "Lemon"), 'S', 8.99);
        salad.setName("Fresh Salad");

        Meal dessert = new Meal(Arrays.asList("Milk", "Sugar", "Eggs"), 'L', 6.99);
        dessert.setName("Custard Dessert");

        Application.meals.add(pasta);
        Application.meals.add(salad);
        Application.meals.add(dessert);


        testCustomer.savePreferences("Vegetarian");
        testCustomer.saveAllergy("Milk");
    }

    private static void displayWelcomeMessage() {
        System.out.println("====================================");
        System.out.println("  Welcome to Cook System Tester");
        System.out.println("====================================");
        System.out.println("This program tests all functionality");
        System.out.println("of the cooking management system.");
        System.out.println();
    }

    private static void displayMainMenu() {
        System.out.println("\nMAIN TEST MENU");
        System.out.println("1. Test Customer Functionality");
        System.out.println("2. Test Chef Functionality");
        System.out.println("3. Test Manager Functionality");
        System.out.println("4. Test Meal Management");
        System.out.println("5. Test Allergy System");
        System.out.println("6. Test Login System");
        System.out.println("7. Test Inventory System");
        System.out.println("8. Test Ingredients Class");
        System.out.println("9. Test Notification System");
        System.out.println("0. Exit");
    }

    private static int getIntInput() {
        String prompt = "Enter your choice: ";
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number!");
            scanner.next();
            System.out.print(prompt);
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private static void testCustomerFunctionality() {
        System.out.println("\n=== CUSTOMER FUNCTIONALITY TEST ===");


        System.out.println("\nTesting preferences system...");
        boolean prefAdded = testCustomer.savePreferences("Spicy Food");
        System.out.println("Added 'Spicy Food' preference: " + (prefAdded ? "Success" : "Already exists"));


        System.out.println("\nTesting allergy system...");
        boolean allergyAdded = testCustomer.saveAllergy("Peanuts");
        System.out.println("Added 'Peanuts' allergy: " + (allergyAdded ? "Success" : "Already exists"));
        System.out.println("Check if 'Milk' allergy exists: " + testCustomer.allergyExist("Milk"));


        System.out.println("\nTesting custom meal creation...");
        testCustomer.selectIngredient("Pasta");
        testCustomer.selectIngredient("Tomato Sauce");
        boolean mealSaved = testCustomer.saveCustomMeal('M', 10.99);
        System.out.println("Custom meal saved: " + (mealSaved ? "Success" : "Failed (incompatible ingredients)"));


        testCustomer.selectIngredient("Milk");
        testCustomer.selectIngredient("Lemon");
        mealSaved = testCustomer.saveCustomMeal('S', 5.99);
        System.out.println("Custom meal with incompatible ingredients: " + (mealSaved ? "Success" : "Failed (as expected)"));
    }

    private static void testChefFunctionality() {
        System.out.println("\n=== CHEF FUNCTIONALITY TEST ===");

        manager.assignTask("Prepare Pasta", testChef);
        manager.assignTask("Make Salad", testChef);

        System.out.println("\nTesting Pasta Task Workflow:");
        testChef.selectTask("Prepare Pasta");
        System.out.println("Selected: " + testChef.getSelectedTask());
        System.out.println("Details: " + testChef.getTaskDetails(testChef.getSelectedTask()));

        testChef.completeTask();
        System.out.println("Completion Status: " + testChef.isTaskCompleted("Prepare Pasta"));

        System.out.println("\nTesting Salad Task Workflow:");
        testChef.selectTask("Make Salad");
        System.out.println("Selected: " + testChef.getSelectedTask());
        System.out.println("Details: " + testChef.getTaskDetails(testChef.getSelectedTask()));

        testChef.completeTask();
        System.out.println("Completion Status: " + testChef.isTaskCompleted("Make Salad"));

        System.out.println("\nTask Summary:");
        System.out.println("- Prepare Pasta completed: " + testChef.isTaskCompleted("Prepare Pasta"));
        System.out.println("- Make Salad completed: " + testChef.isTaskCompleted("Make Salad"));
    }


    private static void testManagerFunctionality() {
        System.out.println("\n=== MANAGER FUNCTIONALITY TEST ===");


        System.out.println("\nTesting task assignment...");
        manager.assignTask("Bake Cake", testChef);
        System.out.println("Task 'Bake Cake' assigned to chef.");


        System.out.println("\nTesting task details retrieval...");
        System.out.println("Details for 'Bake Cake': " + manager.getTaskDetails("Bake Cake"));
    }

    private static void testMealManagement() {
        System.out.println("\n=== MEAL MANAGEMENT TEST ===");

        System.out.println("\nTesting incompatible ingredients...");
        Meal testMeal = new Meal(Arrays.asList("Milk", "Lemon"), 'M', 9.99);
        testMeal.setName("Test Meal");
        System.out.println("Meal has incompatible ingredients: " + testMeal.hasIncompatibleIngredients());

        System.out.println("\nTesting ingredient substitution...");
        boolean subResult = testMeal.substituteIngredient("Milk", "Almond Milk");
        System.out.println("Substitution result: " + (subResult ? "Success" : "Failed"));
        System.out.println("Meal now has incompatible ingredients: " + testMeal.hasIncompatibleIngredients());

        System.out.println("\nTesting meal saving...");
        Application.saveMeal(testMeal);
    }

    private static void testAllergySystem() {
        System.out.println("\n=== ALLERGY SYSTEM TEST ===");


        List<String> allergies = testCustomer.getAllergies();
        System.out.println("Customer allergies: " + allergies);

        // Test meal exclusion
        System.out.println("\nTesting meal exclusion based on allergies...");
        List<Meal> safeMeals = Application.exclude(allergies);
        System.out.println("Safe meals for customer:");
        for (Meal meal : safeMeals) {
            System.out.println("- " + meal.getName());
        }


        System.out.println("\nTesting alternative ingredient suggestions...");
        List<String> alternatives = Meal.suggestAlternative("Milk");
        System.out.println("Alternatives for Milk: " + alternatives);
    }

    private static void testLoginSystem() {
        System.out.println("\n=== LOGIN SYSTEM TEST ===");


        System.out.println("\nTesting successful login...");
        User loggedInUser = Application.login("customer@cook.com", "customer123");
        System.out.println("Login result: " + (loggedInUser != null ? "Success (" + loggedInUser.getRole() + ")" : "Failed"));

        System.out.println("\nTesting failed login...");
        loggedInUser = Application.login("wrong@email.com", "wrongpass");
        System.out.println("Login result: " + (loggedInUser != null ? "Success" : "Failed (as expected)"));
    }

    private static void testInventorySystem() {
        System.out.println("\n=== INVENTORY SYSTEM TEST ===");


        InventoryService inventoryService = new InventoryService();
        inventoryService.addInventoryItem(new InventoryItem("Pasta", 20, 5, 2.99));
        inventoryService.addInventoryItem(new InventoryItem("Tomato Sauce", 15, 3, 1.99));
        inventoryService.addInventoryItem(new InventoryItem("Milk", 2, 5, 3.49)); // Low stock item


        System.out.println("\nTesting low stock detection...");
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        System.out.println("Low stock items:");
        for (InventoryItem item : lowStockItems) {
            System.out.printf("- %s (Quantity: %d, Threshold: %d)%n",
                    item.getIngredientName(), item.getQuantity(), item.getThreshold());
        }


        System.out.println("\nTesting restock suggestions...");
        Map<String, Integer> restockSuggestions = inventoryService.getRestockSuggestions();
        System.out.println("Restock suggestions:");
        restockSuggestions.forEach((name, qty) ->
                System.out.printf("- %s: %d units%n", name, qty));


        System.out.println("\nTesting stock usage...");
        System.out.println("Current Pasta stock: " + inventoryService.getCurrentStock("Pasta"));
        inventoryService.updateStock("Pasta", 3);
        System.out.println("After using 3 units, Pasta stock: " + inventoryService.getCurrentStock("Pasta"));


        System.out.println("\nTesting meal preparation impact...");
        Meal spaghetti = new Meal(Arrays.asList("Pasta", "Tomato Sauce"), 'L', 12.99);
        spaghetti.setName("Spaghetti");
        spaghetti.updateIngredientQuantity("Pasta", 2);
        spaghetti.updateIngredientQuantity("Tomato Sauce", 1);

        inventoryService.updateStockFromMealPreparation(spaghetti);
        System.out.println("After preparing Spaghetti:");
        System.out.println("- Pasta stock: " + inventoryService.getCurrentStock("Pasta"));
        System.out.println("- Tomato Sauce stock: " + inventoryService.getCurrentStock("Tomato Sauce"));
    }

    private static void testIngredientsClass() {
        System.out.println("\n=== INGREDIENTS CLASS TEST ===");

        Ingredients flour = new Ingredients("Flour", 10, 3, 0.99);
        System.out.println("Testing Ingredients class...");
        System.out.printf("Created: %s (Qty: %d, Threshold: %d, Price: %.2f)%n",
                flour.getName(), flour.getQuantity(), flour.getThreshold(), flour.getPrice());

        flour.use(3);
        System.out.println("After using 3 units, quantity: " + flour.getQuantity());


        System.out.println("Is low stock? " + flour.isLowStock());
        flour.use(5);
        System.out.println("After using 5 more units, is low stock? " + flour.isLowStock());
    }
    private static void testNotificationSystem() {
        System.out.println("\n=== NOTIFICATION SYSTEM TEST ===");

        NotificationService notificationService = new NotificationService();


        System.out.println("\nTesting notification sending...");
        notificationService.sendNotification("user1", "Task assigned: Prepare Salad");
        notificationService.sendNotification("user1", "Inventory low: Milk");
        notificationService.sendNotification("user2", "New order received");


        System.out.println("\nTesting notification retrieval...");
        List<String> user1Notifications = notificationService.getNotifications("user1");
        System.out.println("Notifications for user1:");
        user1Notifications.forEach(System.out::println);


        System.out.println("\nTesting notification clearing...");
        notificationService.clearNotifications("user1");
        System.out.println("After clearing, user1 notifications: " +
                notificationService.getNotifications("user1").size());
    }

}
*/