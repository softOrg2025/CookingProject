package cook;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
                case 0 -> {
                    running = false;
                    System.out.println("Exiting the system. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeTestData() {

        manager = new kitchen_manager("Ali Manager", "manager@cook.com", "manager123");
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
}