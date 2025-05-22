package cook;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static kitchen_manager systemManager;
    private static ChefOrderHistoryService orderHistoryService = new ChefOrderHistoryService();
    NotificationService notificationService = new NotificationService();


    public static void main(String[] args) {
        initializeSystem();
        showMainMenu();
        scanner.close();
    }

    private static void initializeSystem() {
        System.out.println("=== Initializing Cooking Management System ===");


        NotificationService notificationService = new NotificationService();
        InventoryService inventoryService = new InventoryService(Application.notificationService);


        systemManager = new kitchen_manager("Admin Chef", "admin@kitchen.com", "admin123", inventoryService);
        Application.users.add(systemManager);
        Application.currentUser = null;

        Application.meals.add(new Meal("Test Meal",
                Arrays.asList("Rice", "Vegetables"), 'M', 10.0));
        Application.meals.add(new Meal("Chicken Curry",
                Arrays.asList("Chicken", "Curry Powder", "Coconut Milk", "Rice"), 'L', 15.0));
        Application.meals.add(new Meal("Pasta Alfredo",
                Arrays.asList("Pasta", "Cream", "Cheese", "Butter"), 'M', 12.50));



        inventoryService.addInventoryItem(new InventoryItem("Tomato", 100, 20, 1.5));
        inventoryService.addInventoryItem(new InventoryItem("Chicken", 50, 10, 5.0));
        inventoryService.addInventoryItem(new InventoryItem("Rice", 200, 50, 2.0));
        inventoryService.addInventoryItem(new InventoryItem("Pasta", 150, 30, 1.0));
        inventoryService.addInventoryItem(new InventoryItem("Cream", 60, 15, 2.5));
        inventoryService.addInventoryItem(new InventoryItem("Cheese", 80, 20, 3.0));
        inventoryService.addInventoryItem(new InventoryItem("Curry Powder", 100, 10, 0.5));
        inventoryService.addInventoryItem(new InventoryItem("Coconut Milk", 40, 10, 1.8));


        systemManager.inventoryService = inventoryService;

        System.out.println("System initialized successfully!\n");
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            if (Application.currentUser != null) {
                System.out.println("Logged in as: " + Application.currentUser.getName() + " (" + Application.currentUser.getRole() + ")");
            }
            System.out.println("1. User Management (Register/Login/List Users)");
            System.out.println("2. Chef and Task Management");
            System.out.println("3. Customer and Meal System");
            System.out.println("4. Inventory Management (Manager View)");
            System.out.println("5. Order Processing and History");
            System.out.println("6. Notification System");
            if (Application.currentUser != null) {
                System.out.println("7. Logout");
            }
            System.out.println("8. Exit");
            System.out.print("Please choose an option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    userManagementMenu();
                    break;
                case 2:
                    handleChefTaskManagementMenu();
                    break;
                case 3:
                    handleCustomerMealSystemMenu();
                    break;
                case 4:
                    handleInventoryManagementMenu();
                    break;
                case 5:
                    handleOrderProcessingMenu();
                    break;
                case 6:
                    handleNotificationSystemMenu();
                    break;
                case 7:
                    if (Application.currentUser != null) {
                        logoutUser();
                    } else {
                        System.out.println("Invalid option, please try again.");
                    }
                    break;
                case 8:
                    System.out.println("Exiting system...");
                    return;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }

    private static void logoutUser() {
        if (Application.currentUser != null) {
            System.out.println(Application.currentUser.getName() + " logged out successfully.");
            Application.currentUser = null;
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    private static void handleChefTaskManagementMenu() {
        if (Application.currentUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        if (Application.currentUser.getRole() == Role.Chef) {
            chefMenu((chef) Application.currentUser);
        } else if (Application.currentUser.getRole() == Role.manager) {
            managerChefTaskSubMenu((kitchen_manager) Application.currentUser);
        } else {
            System.out.println("This option is primarily for Chefs and Managers.");
        }
    }

    private static void managerChefTaskSubMenu(kitchen_manager manager) {
        while (true) {
            System.out.println("\n=== Manager: Chef & Task Management ===");
            System.out.println("1. Assign Task to Chef");
            System.out.println("2. View All Chefs' Tasks (Feature to be added in kitchen_manager)"); // Placeholder
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    assignTaskToChef(manager);
                    break;
                case 2:
                    System.out.println("Feature 'View All Chefs' Tasks' not yet implemented for manager.");
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }


    private static void handleCustomerMealSystemMenu() {
        if (Application.currentUser == null) {
            System.out.println("Please log in as a Customer to access these features.");
            return;
        }
        if (Application.currentUser.getRole() == Role.Customer) {
            customerMenu((Customer) Application.currentUser);
        } else {
            System.out.println("This option is for Customers. You are logged in as " + Application.currentUser.getRole());
        }
    }

    private static void handleInventoryManagementMenu() {
        kitchen_manager activeManager = null;
        if (Application.currentUser != null && Application.currentUser.getRole() == Role.manager) {
            activeManager = (kitchen_manager) Application.currentUser;
        } else if (Application.currentUser == null || Application.currentUser.getRole() != Role.manager) {
            System.out.println("Accessing inventory as System Admin. For full manager functions, please log in as a Manager.");
            activeManager = systemManager;
        }

        if (activeManager == null) {
            System.out.println("Error: No manager context available for inventory.");
            return;
        }
        inventoryManagementMenu(activeManager);
    }


    private static void handleOrderProcessingMenu() {
        if (Application.currentUser == null) {
            System.out.println("Please log in to process orders or view history.");
            return;
        }
        orderProcessingMenu();
    }

    private static void handleNotificationSystemMenu() {
        if (Application.currentUser == null) {
            System.out.println("Please log in to view notifications.");
            return;
        }
        notificationSystemMenu();
    }


    private static void userManagementMenu() {
        while (true) {
            System.out.println("\n=== User Management ===");
            System.out.println("1. Register New User");
            System.out.println("2. Login");
            System.out.println("3. List All Users");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    if (Application.currentUser != null) return;
                    break;
                case 3:
                    listAllUsers();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void registerUser() {
        System.out.println("\n--- Register New User ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        for (User u : Application.users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Error: Email already exists. Please use a different email.");
                return;
            }
        }


        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("Select role:");
        System.out.println("1. Customer");
        System.out.println("2. Chef");
        System.out.println("3. Manager");
        System.out.print("Choose role: ");
        int roleChoice = -1;
        if (scanner.hasNextInt()) {
            roleChoice = scanner.nextInt();
        }
        scanner.nextLine();

        switch (roleChoice) {
            case 1:
                Customer customer = new Customer(name, email, password);
                Application.users.add(customer);
                break;
            case 2:

                chef newChef = new chef(name, email, password, systemManager);
                Application.users.add(newChef);
                systemManager.addChefToStaff(newChef);
                break;
            case 3:

                kitchen_manager manager = new kitchen_manager(name, email, password, systemManager.inventoryService);
                Application.users.add(manager);
                break;
            default:
                System.out.println("Invalid role selection! User not registered.");
                return;
        }

        System.out.println("User registered successfully!");
    }

    private static void loginUser() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = Application.login(email, password);
        if (Application.currentUser != null) {
            System.out.println("Login successful! Welcome " + Application.currentUser.getName());
        } else {
            System.out.println("Login failed: " + Application.getSystemMessage());
        }
    }

    private static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\n=== Customer Menu (" + customer.getName() + ") ===");
            System.out.println("1. View Available Meals");
            System.out.println("2. Add Allergy");
            System.out.println("3. Add Preference");
            System.out.println("4. Get Safe Meal Suggestions (based on allergies)");
            System.out.println("5. Save a Meal to Favorites");
            System.out.println("6. View Saved/Favorite Meals");
            System.out.println("7. Suggest Meal Plan (based on history & preferences)");
            System.out.println("8. Back to Main Menu (Logout will happen from Main Menu)");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAvailableMeals();
                    break;
                case 2:
                    addCustomerAllergy(customer);
                    break;
                case 3:
                    addCustomerPreference(customer);
                    break;
                case 4:
                    getSafeMeals(customer);
                    break;
                case 5:
                    saveMealToFavorites(customer);
                    break;
                case 6:
                    viewSavedMeals(customer);
                    break;
                case 7:
                    suggestMealPlanForCustomer(customer);
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void chefMenu(chef chef) {
        while (true) {
            System.out.println("\n=== Chef Menu (" + chef.getName() + ") ===");
            System.out.println("1. View My Tasks");
            System.out.println("2. Select Task");
            System.out.println("3. Complete Selected Task");
            System.out.println("4. View Details of Selected Task");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewChefTasks(chef);
                    break;
                case 2:
                    selectChefTask(chef);
                    break;
                case 3:
                    completeChefTask(chef);
                    break;
                case 4:
                    viewTaskDetails(chef);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void managerMenu(kitchen_manager manager) {
        while (true) {
            System.out.println("\n=== Manager Menu (" + manager.getName() + ") ===");
            System.out.println("1. Assign Task to Chef (Access via Chef & Task Management)");
            System.out.println("2. View/Manage Inventory (Access via Inventory Management)");
            System.out.println("3. View All Chefs");
            System.out.println("4. View All Users (Access via User Management)");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Please use 'Chef and Task Management' from the Main Menu.");
                    // assignTaskToChef(manager); // Or keep it here if desired
                    break;
                case 2:
                    System.out.println("Please use 'Inventory Management' from the Main Menu.");
                    // inventoryManagementMenu(manager); // Or keep it here
                    break;
                case 3:
                    viewAllChefs();
                    break;
                case 4:
                    System.out.println("Please use 'User Management -> List All Users' from the Main Menu.");
                    // listAllUsers();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }


    private static void viewAvailableMeals() {
        System.out.println("\n--- Available Meals ---");
        if (Application.meals.isEmpty()) {
            System.out.println("No meals available yet.");
            return;
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-3s %-20s %-30s %-10s%n", "No.", "Name", "Ingredients", "Price");
        System.out.println("---------------------------------------------------------------------");
        for (int i = 0; i < Application.meals.size(); i++) {
            Meal meal = Application.meals.get(i);
            System.out.printf("%-3d %-20s %-30s $%-9.2f%n",
                    i + 1, meal.getName(),
                    String.join(", ", meal.getIngredients()),
                    meal.getPrice());
        }
        System.out.println("---------------------------------------------------------------------");
    }

    private static void saveMealToFavorites(Customer customer) {
        viewAvailableMeals();
        if (Application.meals.isEmpty()) return;

        System.out.print("Enter the number of the meal to save to favorites: ");
        int mealNum = -1;
        if(scanner.hasNextInt()){
            mealNum = scanner.nextInt();
        }
        scanner.nextLine();

        if (mealNum < 1 || mealNum > Application.meals.size()) {
            System.out.println("Invalid meal number.");
            return;
        }
        Meal selectedMeal = Application.meals.get(mealNum - 1);

        System.out.print("Enter a name for this favorite meal (e.g., My Favorite " + selectedMeal.getName() + "): ");
        String favoriteName = scanner.nextLine();

        if (favoriteName.isEmpty()) {
            favoriteName = "Favorite " + selectedMeal.getName(); // Default name
        }

        customer.saveMeal(favoriteName, selectedMeal);
        System.out.println("'" + selectedMeal.getName() + "' saved as '" + favoriteName + "' in your favorites!");
    }


    private static void addCustomerAllergy(Customer customer) {
        System.out.print("\nEnter allergy to add (e.g., Peanuts, Shellfish): ");
        String allergy = scanner.nextLine();
        if (allergy.trim().isEmpty()) {
            System.out.println("Allergy cannot be empty.");
            return;
        }
        customer.saveAllergy(allergy);
        System.out.println("Allergy '" + allergy + "' added. Current allergies: " + customer.getAllergies());
    }

    private static void addCustomerPreference(Customer customer) {
        System.out.print("\nEnter preference to add (e.g., Spicy, Vegetarian, specific ingredient like Chicken): ");
        String preference = scanner.nextLine();
        if (preference.trim().isEmpty()) {
            System.out.println("Preference cannot be empty.");
            return;
        }
        customer.savePreferences(preference);
        System.out.println("Preference '" + preference + "' added. Current preferences: " + customer.getPreferences());
    }


    private static void getSafeMeals(Customer customer) {
        System.out.println("\n--- Safe Meal Suggestions (based on your allergies) ---");
        System.out.println("Your allergies: " + customer.getAllergies());
        List<Meal> safeMeals = Application.exclude(customer.getAllergies());

        if (safeMeals.isEmpty()) {
            System.out.println("No safe meals found based on your current allergies.");
        } else {
            System.out.printf("Found %d safe meals for you:%n", safeMeals.size());
            System.out.println("---------------------------------------------------------------------");
            System.out.printf("%-3s %-20s %-30s %-10s%n", "No.", "Name", "Ingredients", "Price");
            System.out.println("---------------------------------------------------------------------");
            for (int i = 0; i < safeMeals.size(); i++) {
                Meal meal = safeMeals.get(i);
                System.out.printf("%-3d %-20s %-30s $%-9.2f%n",
                        i + 1, meal.getName(),
                        String.join(", ", meal.getIngredients()),
                        meal.getPrice());
            }
            System.out.println("---------------------------------------------------------------------");
        }
    }

    private static void viewChefTasks(chef chef) {
        System.out.println("\n--- Your Tasks ---");
        List<String> tasks = chef.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("You have no tasks assigned.");
            return;
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("%-30s %-15s%n", "Task Name", "Status");
        System.out.println("--------------------------------------------------");
        for (String task : tasks) {
            System.out.printf("- %-28s (%s)%n",
                    task,
                    chef.isTaskCompleted(task) ? "Completed" : "Pending");
        }
        System.out.println("--------------------------------------------------");
    }

    private static void assignTaskToChef(kitchen_manager manager) {
        System.out.println("\n--- Assign New Task ---");

        List<chef> chefs = new ArrayList<>();
        for (User user : Application.users) {
            if (user.getRole() == Role.Chef) {
                chefs.add((chef) user);
            }
        }

        if (chefs.isEmpty()) {
            System.out.println("No chefs available to assign tasks to. Please register a chef first.");
            return;
        }

        System.out.println("Available Chefs:");
        for (int i = 0; i < chefs.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, chefs.get(i).getName(), chefs.get(i).getEmail());
        }

        System.out.print("Select chef by number: ");
        int chefChoiceIdx = -1;
        if (scanner.hasNextInt()) {
            chefChoiceIdx = scanner.nextInt() -1;
        }
        scanner.nextLine(); // Consume newline

        if (chefChoiceIdx < 0 || chefChoiceIdx >= chefs.size()) {
            System.out.println("Invalid chef selection!");
            return;
        }
        chef selectedChef = chefs.get(chefChoiceIdx);

        System.out.print("Enter task name: ");
        String taskName = scanner.nextLine();

        System.out.print("Enter task description: ");
        String description = scanner.nextLine();

        LocalDateTime deadline = null;
        while(deadline == null) {
            System.out.print("Enter deadline (dd-MM-yyyy HH:mm, e.g., 25-12-2023 14:30): ");
            String deadlineStr = scanner.nextLine();
            try {
                deadline = LocalDateTime.parse(deadlineStr,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use dd-MM-yyyy HH:mm. Try again.");
            }
        }

        manager.assignTask(taskName, selectedChef, description, deadline);
        // The message is already printed in kitchen_manager.assignTask
        // System.out.println("Task assigned successfully!");
    }

    private static void checkLowStockItems(kitchen_manager manager) {
        if (manager.inventoryService == null) {
            System.out.println("\nError: Inventory service is not available for this manager!");
            return;
        }

        System.out.println("\n--- Low Stock Items ---");
        List<InventoryItem> lowStockItems = manager.inventoryService.getLowStockItems();

        if (lowStockItems.isEmpty()) {
            System.out.println("No items are currently low on stock.");
            return;
        }
        System.out.println("+----------------------+------------+------------+");
        System.out.println("| Ingredient           | Quantity   | Threshold  |");
        System.out.println("+----------------------+------------+------------+");
        for (InventoryItem item : lowStockItems) {
            System.out.printf("| %-20s | %-10d | %-10d |%n",
                    item.getIngredientName(),
                    item.getQuantity(),
                    item.getThreshold());
        }
        System.out.println("+----------------------+------------+------------+");
    }

    public static void listAllUsers() {
        System.out.println("\n--- All Registered Users ---");
        if (Application.users.isEmpty()) {
            System.out.println("No users registered yet.");
            return;
        }
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-20s | %-25s | %-10s %n", "Name", "Email", "Role");
        System.out.println("----------------------------------------------------------");
        for (User user : Application.users) {
            System.out.printf("%-20s | %-25s | %-10s %n",
                    user.getName(),
                    user.getEmail(),
                    user.getRole());
        }
        System.out.println("----------------------------------------------------------");
    }


    private static void inventoryManagementMenu(kitchen_manager manager) {
        while (true) {
            System.out.println("\n=== Inventory Management (Manager: " + manager.getName() + ") ===");
            System.out.println("1. View Full Inventory");
            System.out.println("2. Check Low Stock Items");
            System.out.println("3. Create Purchase Order for Critical Stock");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewFullInventory(manager); // Changed from viewInventory
                    break;
                case 2:
                    checkLowStockItems(manager);
                    break;
                case 3:
                    createPurchaseOrder(manager);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void orderProcessingMenu() {
        while (true) {
            System.out.println("\n=== Order Processing & History ===");
            System.out.println("1. View My Order History (Customers)");
            System.out.println("2. View My Preferences (Customers - derived from history)");
            // System.out.println("3. Suggest Meal Plan (Customers - handled in Customer Menu)"); // Moved
            System.out.println("3. View All Order History (Managers - requires implementation)");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            if (Application.currentUser == null) {
                System.out.println("Please log in.");
                return;
            }

            switch (choice) {
                case 1:
                    if (Application.currentUser instanceof Customer) {
                        viewCustomerOrderHistory((Customer) Application.currentUser);
                    } else {
                        System.out.println("This option is for customers.");
                    }
                    break;
                case 2:
                    if (Application.currentUser instanceof Customer) {
                        viewCustomerDerivedPreferences((Customer) Application.currentUser);
                    } else {
                        System.out.println("This option is for customers.");
                    }
                    break;
                case 3:
                    if (Application.currentUser.getRole() == Role.manager) {
                        System.out.println("Feature 'View All Order History' for managers is not yet fully implemented.");
                        // Potentially iterate all customers and show their history, or have a global order log.
                    } else {
                        System.out.println("This option is for managers.");
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void notificationSystemMenu() {
        while (true) {
            System.out.println("\n=== Notification System (" + Application.currentUser.getName() + ") ===");
            System.out.println("1. View My Notifications");
            System.out.println("2. Clear My Notifications");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = -1;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewNotifications(); // Uses Application.currentUser
                    break;
                case 2:
                    clearNotifications(); // Uses Application.currentUser
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void viewSavedMeals(Customer customer) {
        System.out.println("\n--- " + customer.getName() + "'s Saved/Favorite Meals ---");
        Map<String, Meal> savedMeals = customer.getSavedMeals();

        if (savedMeals.isEmpty()) {
            System.out.println("No meals saved to favorites yet.");
            return;
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-25s | %-20s | %s%n", "Favorite Name", "Original Meal Name", "Ingredients");
        System.out.println("---------------------------------------------------------------------");
        for (Map.Entry<String, Meal> entry : savedMeals.entrySet()) {
            System.out.printf("%-25s | %-20s | %s%n",
                    entry.getKey(),
                    entry.getValue().getName(),
                    String.join(", ", entry.getValue().getIngredients()));
        }
        System.out.println("---------------------------------------------------------------------");
    }


    private static void selectChefTask(chef chef) {
        System.out.println("\n--- Select Task ---");
        List<String> tasks = chef.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to select.");
            return;
        }

        System.out.println("Your Tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, tasks.get(i), chef.isTaskCompleted(tasks.get(i)) ? "Completed" : "Pending");
        }

        System.out.print("Select task number to make active: ");
        int taskNumInput = -1;
        if (scanner.hasNextInt()){
            taskNumInput = scanner.nextInt();
        }
        scanner.nextLine(); // Consume newline

        if (taskNumInput < 1 || taskNumInput > tasks.size()) {
            System.out.println("Invalid task number!");
            return;
        }

        chef.selectTask(tasks.get(taskNumInput - 1));
        System.out.println("Task '" + chef.getSelectedTask() + "' is now the active selected task.");
    }

    private static void completeChefTask(chef chef) {
        if (chef.getSelectedTask() == null) {
            System.out.println("No task selected. Please select a task first using 'Select Task' option.");
            return;
        }
        if (chef.isTaskCompleted(chef.getSelectedTask())) {
            System.out.println("Task '" + chef.getSelectedTask() + "' is already completed.");
            return;
        }

        System.out.println("\n--- Complete Task ---");
        System.out.println("Marking task as completed: " + chef.getSelectedTask());
        chef.completeTask();

    }

    private static void viewTaskDetails(chef chef) {
        String taskToViewDetailsFor = chef.getSelectedTask();

        if (taskToViewDetailsFor == null) {
            System.out.println("No task currently selected. Please select a task first.");

            List<String> tasks = chef.getTasks();
            if (tasks.isEmpty()) {
                System.out.println("You have no tasks.");
                return;
            }
            System.out.println("Your tasks:");
            for(int i=0; i < tasks.size(); i++) {
                System.out.printf("%d. %s%n", i+1, tasks.get(i));
            }
            System.out.print("Enter task number to view details (or 0 to cancel): ");
            int choice = -1;
            if(scanner.hasNextInt()){
                choice = scanner.nextInt();
            }
            scanner.nextLine();
            if(choice <= 0 || choice > tasks.size()){
                return;
            }
            taskToViewDetailsFor = tasks.get(choice - 1);
        }

        System.out.println("\n--- Task Details ---");
        System.out.println("Task: " + taskToViewDetailsFor);
        String details = chef.getTaskDetails(taskToViewDetailsFor); // Chef class stores details
        if (details == null || details.isEmpty()){
            System.out.println("No specific details recorded for this task beyond its name.");
        } else {
            System.out.println("Details: " + details);
        }
    }

    private static void viewFullInventory(kitchen_manager manager) {
        System.out.println("\n--- Current Full Inventory (Manager: " + manager.getName() + ") ---");
        if (manager.inventoryService == null) {
            System.out.println("Inventory service not configured for this manager.");
            return;
        }
        Map<String, InventoryItem> allItems = manager.inventoryService.getInventoryItemsMap(); // Assuming this method exists

        if (allItems.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        System.out.println("+----------------------+------------+------------+--------------+");
        System.out.println("| Ingredient           | Quantity   | Threshold  | Unit Price   |");
        System.out.println("+----------------------+------------+------------+--------------+");
        for (InventoryItem item : allItems.values()) {
            System.out.printf("| %-20s | %-10d | %-10d | $%-11.2f |%n",
                    item.getIngredientName(),
                    item.getQuantity(),
                    item.getThreshold(),
                    item.getUnitPrice());
        }
        System.out.println("+----------------------+------------+------------+--------------+");
    }


    private static void createPurchaseOrder(kitchen_manager manager) {
        System.out.println("\n--- Create Purchase Order ---");
        if (manager.inventoryService == null) {
            System.out.println("Inventory service not configured for this manager.");
            return;
        }

        System.out.print("Enter ingredient name for purchase order: ");
        String ingredient = scanner.nextLine();

        System.out.print("Enter quantity to order: ");
        int quantity = -1;
        if (scanner.hasNextInt()){
            quantity = scanner.nextInt();
        }
        scanner.nextLine();
        if (quantity <=0) {
            System.out.println("Quantity must be positive.");
            return;
        }


        System.out.print("Enter supplier name: ");
        String supplier = scanner.nextLine();

        System.out.print("Enter unit price: $");
        double price = -1.0;
        if (scanner.hasNextDouble()){
            price = scanner.nextDouble();
        }
        scanner.nextLine();
        if (price < 0) {
            System.out.println("Price cannot be negative.");
            return;
        }

        PurchaseOrder po = manager.inventoryService.createPurchaseOrderForCriticalStock(
                ingredient, quantity, supplier, price);

        if (po != null) {
            System.out.println("Purchase order created successfully!");
            System.out.println("Order ID: " + po.getOrderId());
            System.out.println("Ingredient: " + po.getIngredientName());
            System.out.println("Quantity: " + po.getQuantity());
            System.out.println("Supplier: " + po.getSupplierName());
            System.out.println("Total Cost: $" + String.format("%.2f", po.getTotalPrice()));

            // Ask to send
            System.out.print("Do you want to send this purchase order to the supplier now? (yes/no): ");
            String sendChoice = scanner.nextLine().trim().toLowerCase();
            if (sendChoice.equals("yes")) {
                if (manager.inventoryService.sendPurchaseOrderToSupplier(po.getOrderId())) {
                    System.out.println("Purchase order " + po.getOrderId() + " marked as sent.");
                } else {
                    System.out.println("Failed to mark purchase order " + po.getOrderId() + " as sent (already sent or error).");
                }
            }

        } else {
            System.out.println("Failed to create purchase order (perhaps an issue in InventoryService).");
        }
    }

    private static void viewAllChefs() {
        System.out.println("\n--- All Registered Chefs ---");
        boolean found = false;
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-20s | %-25s %n", "Name", "Email");
        System.out.println("----------------------------------------------------------");
        for (User user : Application.users) {
            if (user.getRole() == Role.Chef) {
                System.out.printf("%-20s | %-25s %n", user.getName(), user.getEmail());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No chefs registered in the system.");
        }
        System.out.println("----------------------------------------------------------");
    }

    private static void viewCustomerOrderHistory(Customer customer) {
        System.out.println("\n--- Order History for " + customer.getName() + " ---");

        if (orderHistoryService.getCustomerOrderHistory(customer.getEmail()).isEmpty() && customer.getEmail().equals("testcust@example.com")) {
            System.out.println("(Adding some sample order history for " + customer.getName() + " for demonstration)");
            Meal meal1 = Application.meals.get(0); // Test Meal
            Meal meal2 = Application.meals.size() > 1 ? Application.meals.get(1) : new Meal("Sample Meal 2", Arrays.asList("ItemX", "ItemY"), 'S', 5.0);
            orderHistoryService.addOrder(customer.getEmail(), meal1);
            orderHistoryService.addOrder(customer.getEmail(), meal2);
            orderHistoryService.addOrder(customer.getEmail(), meal1); // Ordered Test Meal twice
        }


        List<Meal> history = orderHistoryService.getCustomerOrderHistory(customer.getEmail());
        if (history.isEmpty()) {
            System.out.println("No order history found for " + customer.getName() + ".");
            return;
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.printf("%-20s | %-30s | %-10s%n", "Meal Name", "Ingredients", "Price");
        System.out.println("---------------------------------------------------------------------");
        for (Meal meal : history) {
            System.out.printf("%-20s | %-30s | $%-9.2f%n",
                    meal.getName(),
                    String.join(", ", meal.getIngredients()),
                    meal.getPrice());
        }
        System.out.println("---------------------------------------------------------------------");
    }

    private static void viewCustomerDerivedPreferences(Customer customer) {
        System.out.println("\n--- Derived Preferences for " + customer.getName() + " (from order history) ---");

        // Ensure preferences are analyzed from history
        orderHistoryService.analyzePreferences(customer.getEmail());

        List<String> preferences = orderHistoryService.getCustomerPreferences(customer.getEmail());
        List<String> frequentMeals = orderHistoryService.identifyFrequentMeals(customer.getEmail());

        if (preferences.isEmpty() && frequentMeals.isEmpty()) {
            System.out.println("No significant preferences or frequent meals identified from order history yet.");
            System.out.println("Place some orders to build your preference profile.");
            return;
        }

        if (!frequentMeals.isEmpty()) {
            System.out.println("\nFrequently Ordered Meals (2+ times):");
            for (String mealName : frequentMeals) {
                System.out.println("- " + mealName);
            }
        }

        if (!preferences.isEmpty()) {
            System.out.println("\nCommonly Found Ingredients in Your Orders (Preferences):");
            // To make this more readable, count frequencies of ingredients
            Map<String, Long> ingredientCounts = preferences.stream()
                    .collect(java.util.stream.Collectors.groupingBy(String::toLowerCase, java.util.stream.Collectors.counting()));

            ingredientCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Show most frequent first
                    .forEach(entry -> System.out.printf("- %s (appeared in %d orders/meals)%n", entry.getKey(), entry.getValue()));
        } else {
            System.out.println("No common ingredients identified from your order history yet.");
        }
        System.out.println("\nNote: You can also set explicit preferences and allergies in the Customer Menu.");
    }

    private static void suggestMealPlanForCustomer(Customer customer) { // Renamed from suggestMealPlan
        System.out.println("\n--- Meal Plan Suggestions for " + customer.getName() + " ---");

        // 1. Ensure order history is populated (for demonstration if needed)
        if (orderHistoryService.getCustomerOrderHistory(customer.getEmail()).isEmpty() && customer.getEmail().equals("testcust@example.com")) {
            Meal meal1 = Application.meals.get(0);
            Meal meal2 = Application.meals.size() > 1 ? Application.meals.get(1) : new Meal("Sample Meal 2", Arrays.asList("ItemX", "ItemY"), 'S', 5.0);
            orderHistoryService.addOrder(customer.getEmail(), meal1);
            orderHistoryService.addOrder(customer.getEmail(), meal2);
            orderHistoryService.addOrder(customer.getEmail(), meal1);
        }

        // 2. Analyze preferences from history (ChefOrderHistoryService does this)
        orderHistoryService.analyzePreferences(customer.getEmail()); // Make sure this is called
        List<String> derivedPrefs = orderHistoryService.getCustomerPreferences(customer.getEmail()); // ingredients from history
        List<String> explicitPrefs = customer.getPreferences(); // customer's manually set preferences

        // Combine preferences (optional, could keep them separate)
        Set<String> allPreferences = new HashSet<>(derivedPrefs);
        allPreferences.addAll(explicitPrefs.stream().map(String::toLowerCase).toList());


        // 3. Get safe meals (excluding allergies)
        List<Meal> safeMeals = Application.exclude(customer.getAllergies());
        if (safeMeals.isEmpty()) {
            System.out.println("No meals available that match your allergy restrictions: " + customer.getAllergies());
            return;
        }

        // 4. Filter safe meals by combined preferences
        List<Meal> recommendedMeals = new ArrayList<>();
        if (allPreferences.isEmpty()) {
            System.out.println("No specific preferences identified or set. Showing all safe meals:");
            recommendedMeals.addAll(safeMeals);
        } else {
            System.out.println("Filtering based on your preferences: " + allPreferences);
            for (Meal meal : safeMeals) {
                boolean matchesPreference = false;
                for (String pref : allPreferences) {

                    if (meal.getName().toLowerCase().contains(pref) ||
                            meal.getIngredients().stream().anyMatch(ing -> ing.toLowerCase().contains(pref))) {
                        matchesPreference = true;
                        break;
                    }
                }
                if (matchesPreference) {
                    recommendedMeals.add(meal);
                }
            }
        }

        // 5. Display suggestions
        if (recommendedMeals.isEmpty()) {
            System.out.println("Could not find specific meals matching your combined preferences and allergies.");
            System.out.println("Consider broadening your preferences or checking available safe meals.");
            if (!safeMeals.isEmpty() && allPreferences.isEmpty()) {
                System.out.println("\nHowever, these meals are safe based on your allergies:");
                safeMeals.forEach(m -> System.out.println("- " + m.getName()));
            }
        } else {
            System.out.println("\nRecommended meals for you:");
            System.out.println("---------------------------------------------------------------------");
            System.out.printf("%-20s | %-30s | %-10s%n", "Meal Name", "Ingredients", "Price");
            System.out.println("---------------------------------------------------------------------");
            for (Meal meal : recommendedMeals) {
                System.out.printf("%-20s | %-30s | $%-9.2f%n",
                        meal.getName(),
                        String.join(", ", meal.getIngredients()),
                        meal.getPrice());
            }
            System.out.println("---------------------------------------------------------------------");
        }
    }

    private static void viewNotifications() {
        System.out.println("\n--- Notifications for " + Application.currentUser.getName() + " ---");
        List<String> notifications = Application.notificationService.getNotifications(
                Application.currentUser.getEmail());

        if (notifications.isEmpty()) {
            System.out.println("No new notifications.");
        } else {
            for (int i = 0; i < notifications.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, notifications.get(i));
            }
        }
    }

    private static void clearNotifications() {
        Application.notificationService.clearNotifications(Application.currentUser.getEmail());
        System.out.println("Notifications cleared successfully for " + Application.currentUser.getName() + "!");
    }
}