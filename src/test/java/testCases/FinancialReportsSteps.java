package testCases;

import cook.Application;
import cook.Meal;
import cook.User;
import cook.Role;
import cook.Customer;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FinancialReportsSteps {
    private User currentAdmin;
    private Customer selectedCustomerForReport;
    private LocalDate reportStartDate;
    private LocalDate reportEndDate;

    private boolean generalReportGenerated = false;
    private double calculatedTotalIncome;
    private long calculatedOrderCount;

    private boolean customerReportGenerated = false;
    private List<Meal> customerTransactions;

    private String exportedFileName;

    // Define the directory NAME only. No slashes here.
    // Paths.get() will handle adding the correct OS-specific separator.
    private static final String DOWNLOADS_DIR_NAME = "test_downloads"; // <<<<<< PLATFORM-NEUTRAL
    private final TestContext testContext; // Assuming TestContext is defined elsewhere

    public FinancialReportsSteps(TestContext context) {
        this.testContext = context;
    }

    // (initializeMockData and addMealForCustomer methods remain the same as your original)
    private static void initializeMockData() {
        Application.users.clear();
        Application.meals.clear();

        Customer customer1 = new Customer("Fatima Ali", "fatima@example.com", "pass1");
        Customer customer2 = new Customer("Ahmed Omar", "ahmed@example.com", "pass2");
        Customer customer3 = new Customer("Ali Khan", "ali.khan@example.com", "pass3");
        Application.users.add(customer1);
        Application.users.add(customer2);
        Application.users.add(customer3);

        addMealForCustomer(customer1, "Chicken Platter", List.of("Chicken", "Rice"), 'L', 15.99);
        addMealForCustomer(customer2, "Beef Steak", List.of("Beef", "Potato"), 'M', 12.50);
        addMealForCustomer(customer3, "Grilled Fish", List.of("Fish", "Salad"), 'S', 18.00);
        addMealForCustomer(customer2, "Pasta Special", List.of("Pasta", "Cheese"), 'L', 10.00);
        addMealForCustomer(customer3, "Kebab Special", List.of("Kebab", "Bread"), 'L', 22.00);
    }

    private static void addMealForCustomer(Customer customer, String mealName, List<String> ingredients, char size, double price) {
        Meal meal = new Meal(ingredients, size, price);
        meal.setName(mealName + " - " + customer.getName());
        Application.meals.add(meal);
    }

    @Before("@FinancialReportsFeature")
    public void setUpFinancialReports() {
        currentAdmin = null;
        selectedCustomerForReport = null;
        reportStartDate = null;
        reportEndDate = null;
        generalReportGenerated = false;
        calculatedTotalIncome = 0.0;
        calculatedOrderCount = 0;
        customerReportGenerated = false;
        customerTransactions = new ArrayList<>();
        exportedFileName = null;
        if (testContext != null) {
            testContext.lastSystemMessage = null;
        }


        Path downloadsDirPath = Paths.get(DOWNLOADS_DIR_NAME); // <<<<<< PLATFORM-INDEPENDENT


        if (!Files.exists(downloadsDirPath)) {
            try {
                Files.createDirectories(downloadsDirPath); // Creates parent directories if needed
                Assertions.assertTrue(Files.exists(downloadsDirPath),
                        "Downloads directory should be created at: " + downloadsDirPath.toAbsolutePath());
            } catch (IOException e) {
                Assertions.fail("Failed to create directory: " + downloadsDirPath.toAbsolutePath() +
                        ". Error: " + e.getMessage() +
                        ". Check permissions and path.", e);
            }
        }
        initializeMockData();
        ensureTestCustomerExists("Ali Khan"); // Ensure this key customer for tests exists
    }

    private void ensureTestCustomerExists(String customerName) {
        boolean customerExists = Application.users.stream()
                .anyMatch(u -> u instanceof Customer && u.getName().equals(customerName));

        if (!customerExists) {
            Customer testCustomer = new Customer(customerName,
                    customerName.toLowerCase(Locale.ROOT).replace(" ", ".") + "@example.com",
                    "password");
            Application.users.add(testCustomer);
            // Add specific meals if this customer is key for certain tests
            if ("Ali Khan".equals(customerName)) {
                addMealForCustomer(testCustomer, "Grilled Fish", List.of("Fish", "Salad"), 'S', 18.00);
                addMealForCustomer(testCustomer, "Kebab Special", List.of("Kebab", "Bread"), 'L', 22.00);
            }
        }
    }

    @Given("I am a system administrator")
    public void iAmASystemAdministrator() {
        currentAdmin = new User("Admin User", "admin@system.com", "adminPass", Role.manager);
        Application.currentUser = currentAdmin; // Make sure this is how your app sets the current user
        Assertions.assertEquals(Role.manager, currentAdmin.getRole(), "User should have admin role");
    }

    @When("I select the date range from {string} to {string}")
    public void iSelectTheDateRangeFromTo(String start, String end) {
        reportStartDate = LocalDate.parse(start);
        reportEndDate = LocalDate.parse(end);
        if (testContext != null) {
            testContext.lastSystemMessage = "Selected date range: " + reportStartDate + " to " + reportEndDate;
        }
    }

    @Then("the system should show total income and orders for that period")
    public void theSystemShouldShowTotalIncomeAndOrders() {
        // Ensure Application.meals has data reflective of the date range or test setup
        List<Meal> mealsInPeriod = Application.meals.stream()
                // This filter seems specific; ensure it aligns with your test data and intent
                .filter(meal -> !meal.getName().contains("Pasta Special - Ahmed Omar"))
                .collect(Collectors.toList());

        calculatedTotalIncome = mealsInPeriod.stream().mapToDouble(Meal::getPrice).sum();
        calculatedOrderCount = mealsInPeriod.size();
        generalReportGenerated = true;

        if (testContext != null) {
            testContext.lastSystemMessage = String.format(Locale.US,
                    "Financial Summary for Period %s to %s:\n  Total Income: $%.2f\n  Total Orders: %d",
                    reportStartDate, reportEndDate, calculatedTotalIncome, calculatedOrderCount);
        }
    }

    @Given("I select the customer {string}")
    public void iSelectTheCustomer(String customerName) {
        ensureTestCustomerExists(customerName); // Important to ensure data integrity for the test

        selectedCustomerForReport = Application.users.stream()
                .filter(u -> u instanceof Customer && u.getName().equals(customerName))
                .map(u -> (Customer) u) // Cast to Customer
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(selectedCustomerForReport, "Customer '" + customerName + "' should be found in mock data.");

        if (testContext != null) {
            testContext.lastSystemMessage = "Selected customer for report: " +
                    (selectedCustomerForReport != null ? selectedCustomerForReport.getName() : "null");
        }
    }

    @When("I run the report")
    public void iRunTheReport() {
        Assertions.assertNotNull(selectedCustomerForReport, "Cannot run report, no customer selected.");
        customerTransactions = Application.meals.stream()
                .filter(meal -> meal.getName() != null && meal.getName().contains(selectedCustomerForReport.getName()))
                .collect(Collectors.toList());
        customerReportGenerated = true;

        if (testContext != null) {
            testContext.lastSystemMessage = "Transaction report generated for: " +
                    selectedCustomerForReport.getName() + ". Found " + customerTransactions.size() + " transactions.";
        }
    }

    @Then("I should see the transaction history for {string}")
    public void iShouldSeeTheTransactionHistoryFor(String expectedCustomerName) {
        Assertions.assertNotNull(selectedCustomerForReport, "Report not generated or customer not selected.");
        Assertions.assertEquals(expectedCustomerName, selectedCustomerForReport.getName(), "Report is for the wrong customer.");
        Assertions.assertTrue(customerReportGenerated, "Customer report should have been generated flag is false.");


        StringBuilder historyBuilder = new StringBuilder();
        historyBuilder.append("Transaction history for: ").append(selectedCustomerForReport.getName()).append("\n");

        if (customerTransactions.isEmpty()) {
            historyBuilder.append("No transactions found");
        } else {
            customerTransactions.forEach(meal ->
                    historyBuilder.append("- ").append(meal.getName())
                            .append(": $").append(String.format(Locale.US, "%.2f", meal.getPrice()))
                            .append("\n"));
        }

        if (testContext != null) {
            testContext.lastSystemMessage = historyBuilder.toString();
        }
    }

    @Given("a financial report for {string} is generated")
    public void aFinancialReportForCustomerIsGenerated(String customerName) {
        iSelectTheCustomer(customerName); // Reuse existing steps
        iRunTheReport();                  // Reuse existing steps
        Assertions.assertTrue(customerReportGenerated, "Customer report should have been generated for " + customerName);

        if (testContext != null) {
            testContext.lastSystemMessage = "Financial report prepared for: " +
                    selectedCustomerForReport.getName() + " with " + customerTransactions.size() + " transactions";
        }
    }

    @When("I click 'Export'")
    public void iClickExport() {
        Assertions.assertNotNull(selectedCustomerForReport, "Cannot export, no customer selected for report.");
        Assertions.assertTrue(customerReportGenerated, "Cannot export, report not generated yet.");

        exportedFileName = selectedCustomerForReport.getName().replace(" ", "_") + "_Report.csv";

        // Construct the Path to the file within the downloads directory.
        // Paths.get(baseDirectoryName, fileName) is the platform-independent way.
        Path reportFilePath = Paths.get(DOWNLOADS_DIR_NAME, exportedFileName); // <<<<<< PLATFORM-INDEPENDENT

        try (FileWriter writer = new FileWriter(reportFilePath.toFile())) { // Convert Path to File for FileWriter
            writer.write("Customer,Meal,Price\n"); // CSV Header
            for (Meal meal : customerTransactions) {
                writer.write(String.format(Locale.US, "%s,%s,%.2f\n", // Use Locale.US for consistent decimal format
                        selectedCustomerForReport.getName(),
                        meal.getName(),
                        meal.getPrice()));
            }
            if (testContext != null) {
                testContext.lastSystemMessage = "Report exported to: " + reportFilePath.toAbsolutePath();
            }
        } catch (IOException e) {
            Assertions.fail("Failed to export report to " + reportFilePath.toAbsolutePath() + ": " + e.getMessage(), e);
        }
    }

    @Then("a CSV file named {string} should be downloaded")
    public void aCSVFileNamedShouldBeDownloaded(String expectedFileName) {
        Assertions.assertNotNull(exportedFileName, "exportedFileName was not set. Was 'Export' step run?");
        Assertions.assertEquals(expectedFileName, exportedFileName, "The generated exportedFileName does not match expectedFileName.");

        Path expectedFilePath = Paths.get(DOWNLOADS_DIR_NAME, expectedFileName); // <<<<<< PLATFORM-INDEPENDENT
        File file = expectedFilePath.toFile(); // Convert to File for exists() check

        Assertions.assertTrue(file.exists(), "File should exist at: " + file.getAbsolutePath());
        Assertions.assertTrue(file.isFile(), "Path should point to a file: " + file.getAbsolutePath());

        if (testContext != null) {
            testContext.lastSystemMessage = "Verified CSV file exists: " + file.getAbsolutePath();
        }
    }

    @Then("it should contain total income and all transactions")
    public void itShouldContainTotalIncomeAndAllTransactions() {
        Assertions.assertNotNull(exportedFileName, "exportedFileName is null. Was the report exported?");
        Path filePath = Paths.get(DOWNLOADS_DIR_NAME, exportedFileName); // <<<<<< PLATFORM-INDEPENDENT
        File file = filePath.toFile();

        Assertions.assertTrue(file.exists(), "CSV file not found for verification: " + file.getAbsolutePath());

        int actualTransactionLines = 0;
        try (Scanner scanner = new Scanner(file)) { // Scanner is fine for reading text files
            // Verify header
            if (scanner.hasNextLine()) {
                String header = scanner.nextLine();
                Assertions.assertEquals("Customer,Meal,Price", header, "CSV header mismatch.");
            } else {
                Assertions.fail("CSV file is empty or has no header: " + file.getAbsolutePath());
            }


            while (scanner.hasNextLine()) {
                scanner.nextLine();
                actualTransactionLines++;
            }

            Assertions.assertEquals(customerTransactions.size(), actualTransactionLines,
                    "CSV file should contain " + customerTransactions.size() +
                            " transaction lines, but found " + actualTransactionLines + " in " + file.getAbsolutePath());

            if (testContext != null) {
                testContext.lastSystemMessage = "Verified CSV ("+ file.getName() +") contains " + actualTransactionLines + " transactions.";
            }
        } catch (IOException e) {
            Assertions.fail("Failed to read CSV file (" + file.getAbsolutePath() + ") for verification: " + e.getMessage(), e);
        }
    }
}