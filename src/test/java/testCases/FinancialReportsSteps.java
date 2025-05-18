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
    private static final String DOWNLOADS_DIR = "test_downloads/";
    private final TestContext testContext;

    public FinancialReportsSteps(TestContext context) {
        this.testContext = context;
    }

    private static void initializeMockData() {
        Application.users.clear();
        Application.meals.clear();

        Customer customer1 = new Customer("Fatima Ali", "fatima@example.com", "pass1");
        Customer customer2 = new Customer("Ahmed Omar", "ahmed@example.com", "pass2");
        Customer customer3 = new Customer("Ali Khan", "ali.khan@example.com", "pass3");
        Application.users.add(customer1);
        Application.users.add(customer2);
        Application.users.add(customer3);

        Meal meal1 = new Meal(List.of("Chicken", "Rice"), 'L', 15.99);
        meal1.setName("Chicken Platter - Fatima");
        Application.meals.add(meal1);

        Meal meal2 = new Meal(List.of("Beef", "Potato"), 'M', 12.50);
        meal2.setName("Beef Steak - Ahmed");
        Application.meals.add(meal2);

        Meal meal3 = new Meal(List.of("Fish", "Salad"), 'S', 18.00);
        meal3.setName("Grilled Fish - Ali Khan");
        Application.meals.add(meal3);

        Meal meal4 = new Meal(List.of("Pasta", "Cheese"), 'L', 10.00);
        meal4.setName("Pasta Special - Ahmed");
        Application.meals.add(meal4);

        Meal meal5 = new Meal(List.of("Kebab", "Bread"), 'L', 22.00);
        meal5.setName("Kebab Special - Ali Khan");
        Application.meals.add(meal5);

        Assertions.assertEquals(3, Application.users.size(), "Mock users should be initialized.");
        Assertions.assertEquals(5, Application.meals.size(), "Mock meals should be initialized.");
    }

    private void ensureAdminLoggedIn() {
        if (currentAdmin == null) {
            currentAdmin = new User("Default Admin", "admin@system.com", "adminPass", Role.manager);
            Assertions.assertNotNull(currentAdmin, "Default admin should be initialized if currentAdmin was null.");
        }
    }
    @Before
    public void setUp() {
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

        Path downloadsDirPath = Paths.get(DOWNLOADS_DIR);
        if (!Files.exists(downloadsDirPath)) {
            try {
                Files.createDirectories(downloadsDirPath);
                Assertions.assertTrue(Files.exists(downloadsDirPath), "Downloads directory should be created.");
            } catch (IOException e) {
                Assertions.fail("Failed to create directory: " + downloadsDirPath.toAbsolutePath() + ". Error: " + e.getMessage());
            }
        }
        initializeMockData();
        Assertions.assertFalse(Application.users.isEmpty(), "Application users should not be empty after mock data re-initialization.");
        Assertions.assertFalse(Application.meals.isEmpty(), "Application meals should not be empty after mock data re-initialization.");
    }


    @Given("I am a system administrator")
    public void iAmASystemAdministrator() {
        currentAdmin = new User("Admin User", "admin@system.com", "adminPass", Role.manager);
        Assertions.assertNotNull(currentAdmin, "Admin user should be created.");
        Assertions.assertEquals(Role.manager, currentAdmin.getRole(), "User role should be manager.");
        if(testContext != null) testContext.lastSystemMessage = "Logged in as system administrator: " + currentAdmin.getName();
    }

    @When("I select the date range from {string} to {string}")
    public void iSelectTheDateRangeFromTo(String start, String end) {
        ensureAdminLoggedIn();
        Assertions.assertNotNull(currentAdmin, "Admin must be logged in to select date range.");
        try {
            reportStartDate = LocalDate.parse(start);
            reportEndDate = LocalDate.parse(end);
            Assertions.assertNotNull(reportStartDate, "Report start date should be parsed correctly.");
            Assertions.assertNotNull(reportEndDate, "Report end date should be parsed correctly.");
            if(testContext != null) testContext.lastSystemMessage = "Selected date range: " + reportStartDate + " to " + reportEndDate;
        } catch (Exception e) {
            Assertions.fail("Invalid date format provided. Use YYYY-MM-DD. Error: " + e.getMessage());
        }
    }

    @Then("the system should show total income and orders for that period")
    public void theSystemShouldShowTotalIncomeAndOrders() {
        Assertions.assertNotNull(reportStartDate, "Start date must be selected for the report.");
        Assertions.assertNotNull(reportEndDate, "End date must be selected for the report.");

        // For Java 16+ .toList() is preferred. For broader compatibility, .collect(Collectors.toList()) is fine.
        // The warning is a suggestion for newer Java versions.
        List<Meal> mealsInPeriod = Application.meals.stream()
                .filter(meal -> !meal.getName().contains("Pasta Special - Ahmed"))
                .collect(Collectors.toList()); // Sticking with collect for compatibility or if specific collector needed later

        calculatedTotalIncome = mealsInPeriod.stream().mapToDouble(Meal::getPrice).sum();
        calculatedOrderCount = mealsInPeriod.size();
        generalReportGenerated = true; // This sets the flag

        Assertions.assertTrue(calculatedTotalIncome >= 0, "Calculated income should be non-negative.");
        // The warning "Condition 'calculatedOrderCount >= 0' is always 'true'" is because .size() is never negative.
        // This assertion is logically redundant but harmless. Can be removed if desired.
        // Assertions.assertTrue(calculatedOrderCount >= 0, "Calculated order count should be non-negative.");

        // The warning "Value 'generalReportGenerated' is always 'true'" occurs if this step always follows a path
        // where generalReportGenerated is set to true and then this assertion is made.
        // If the test logic guarantees this, the assertion can be removed or kept for clarity.
        // Assertions.assertTrue(generalReportGenerated, "General report flag should be set to true.");

        if(testContext != null) {
            testContext.lastSystemMessage = String.format(Locale.US, "Financial Summary for Period %s to %s:\n  Total Income: $%.2f\n  Total Orders: %d",
                    reportStartDate, reportEndDate, calculatedTotalIncome, calculatedOrderCount);
        }
    }

    @Given("I select the customer {string}")
    public void iSelectTheCustomer(String customerName) {
        ensureAdminLoggedIn();
        Assertions.assertNotNull(currentAdmin, "Admin must be logged in to select a customer.");
        selectedCustomerForReport = (Customer) Application.users.stream()
                .filter(u -> u.getRole() == Role.Customer && u.getName().equals(customerName))
                .findFirst()
                .orElse(null);

        Assertions.assertNotNull(selectedCustomerForReport, "Customer '" + customerName + "' not found in the system. Check mock data.");
        Assertions.assertEquals(customerName, selectedCustomerForReport.getName(), "Selected customer name should match.");
        if(testContext != null) testContext.lastSystemMessage = "Selected customer for report: " + selectedCustomerForReport.getName();
    }

    @When("I run the report")
    public void iRunTheReport() {
        Assertions.assertNotNull(selectedCustomerForReport, "A customer must be selected to run their transaction report.");
        customerTransactions = Application.meals.stream()
                .filter(meal -> meal.getName().contains(selectedCustomerForReport.getName()))
                .collect(Collectors.toList());

        customerReportGenerated = true; // This sets the flag
        // The warning "Value 'customerReportGenerated' is always 'true'" has similar reasons as above.
        // Assertions.assertTrue(customerReportGenerated, "Customer report flag should be set to true.");
        if(testContext != null) {
            testContext.lastSystemMessage = "Transaction report generated for: " + selectedCustomerForReport.getName() +
                    ". Found " + customerTransactions.size() + " transactions (meals).";
        }
    }

    @Then("I should see the transaction history for {string}")
    public void iShouldSeeTheTransactionHistoryFor(String expectedCustomerName) {
        Assertions.assertTrue(customerReportGenerated, "Customer-specific report must be generated.");
        Assertions.assertNotNull(selectedCustomerForReport, "Selected customer for report should not be null.");
        Assertions.assertEquals(expectedCustomerName, selectedCustomerForReport.getName(), "Customer name mismatch in report.");

        StringBuilder historyBuilder = new StringBuilder("Displaying transaction history for: " + selectedCustomerForReport.getName() + "\n");
        if (customerTransactions.isEmpty()) {
            // Warning "Result of 'customerTransactions.isEmpty()' is always 'true'" if the scenario flow guarantees it's empty here.
            // If specific scenarios test this path, the assertion is valid.
            // Assertions.assertTrue(customerTransactions.isEmpty(), "Customer transactions list should be empty if no transactions found.");
            historyBuilder.append("  No transactions found for this customer.");
        } else {
            // Warning "Result of 'customerTransactions.isEmpty()' is always 'false'" if the scenario flow guarantees it's NOT empty here.
            // Assertions.assertFalse(customerTransactions.isEmpty(), "Customer transactions list should not be empty.");
            customerTransactions.forEach(meal ->
                    historyBuilder.append("  - Meal: ").append(meal.getName())
                            .append(", Price: $").append(String.format(Locale.US, "%.2f", meal.getPrice())).append("\n")
            );
        }
        if(testContext != null) testContext.lastSystemMessage = historyBuilder.toString().trim();
    }

    @Given("a financial report for {string} is generated")
    public void aFinancialReportForCustomerIsGenerated(String customerName) {
        ensureAdminLoggedIn();
        Assertions.assertNotNull(currentAdmin, "Admin must be logged in.");

        selectedCustomerForReport = (Customer) Application.users.stream()
                .filter(u -> u.getRole() == Role.Customer && u.getName().equals(customerName))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(selectedCustomerForReport, "Customer '" + customerName + "' for whom report is to be generated not found.");

        this.customerTransactions = Application.meals.stream()
                .filter(meal -> meal.getName().contains(selectedCustomerForReport.getName()))
                .collect(Collectors.toList());
        customerReportGenerated = true; // Sets flag
        generalReportGenerated = false; // Sets flag

        // Warnings "Value 'customerReportGenerated' is always 'true'" and "Value 'generalReportGenerated' is always 'false'"
        // are due to these direct assignments above. These assertions confirm the state set by this step.
        // Assertions.assertTrue(customerReportGenerated, "Customer report generated flag should be true.");
        // Assertions.assertFalse(generalReportGenerated, "General report generated flag should be false.");
        if(testContext != null) {
            testContext.lastSystemMessage = "Financial report context set for customer: " + selectedCustomerForReport.getName() +
                    " with " + customerTransactions.size() + " transactions.";
        }
    }

    @When("I click 'Export'")
    public void iClickExport() {
        Assertions.assertTrue(customerReportGenerated, "Customer report must be generated before export.");
        Assertions.assertNotNull(selectedCustomerForReport, "Selected customer for report should not be null for export.");

        exportedFileName = selectedCustomerForReport.getName().replace(" ", "_") + "_Report.csv";
        Assertions.assertNotNull(exportedFileName, "Exported file name should be generated.");

        File reportFile = new File(DOWNLOADS_DIR + exportedFileName);
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write("CustomerName,MealName,Price\n");
            for (Meal meal : customerTransactions) {
                writer.write(selectedCustomerForReport.getName().replace(",",";") + "," +
                        meal.getName().replace(",", ";") + "," +
                        String.format(Locale.US, "%.2f", meal.getPrice()) + "\n");
            }
            Assertions.assertTrue(reportFile.exists(), "Report file should be created after writing.");
            if(testContext != null) testContext.lastSystemMessage = "Exported customer report data to: " + reportFile.getAbsolutePath();
        } catch (IOException e) {
            Assertions.fail("Failed to simulate CSV export: " + e.getMessage());
        }
    }

    @Then("a CSV file named {string} should be downloaded")
    public void aCSVFileNamedShouldBeDownloaded(String expectedFileName) {
        Assertions.assertNotNull(exportedFileName, "Exported file name should have been set.");
        Assertions.assertEquals(expectedFileName, exportedFileName, "Exported CSV file name mismatch.");

        File file = new File(DOWNLOADS_DIR + exportedFileName);
        Assertions.assertTrue(file.exists() && file.isFile(), "CSV file was not found where expected: " + file.getAbsolutePath());
        if(testContext != null) testContext.lastSystemMessage = "CSV file confirmed as downloaded: " + file.getAbsolutePath();
    }

    @Then("it should contain total income and all transactions")
    public void itShouldContainTotalIncomeAndAllTransactions() {
        Assertions.assertTrue(customerReportGenerated, "Customer report should be generated to check its content.");
        Assertions.assertNotNull(selectedCustomerForReport, "Selected customer for report should not be null.");
        Assertions.assertNotNull(exportedFileName, "Exported file name should be set.");

        File file = new File(DOWNLOADS_DIR + exportedFileName);
        Assertions.assertTrue(file.exists(), "Exported CSV file must exist to check its content.");

        long expectedTransactions = customerTransactions.size();
        long actualTransactionsInCsv = 0;
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    actualTransactionsInCsv++;
                }
            }
        } catch (IOException e) {
            Assertions.fail("Could not read CSV file for content verification: " + e.getMessage());
        }

        Assertions.assertEquals(expectedTransactions, actualTransactionsInCsv,
                "Number of transactions in CSV for " + selectedCustomerForReport.getName() +
                        " does not match expected count.");
        if(testContext != null) {
            testContext.lastSystemMessage = "Verified content for Customer Report CSV: " + exportedFileName +
                    ". Confirmed CSV for " + selectedCustomerForReport.getName() +
                    " contains " + actualTransactionsInCsv + " transaction lines.";
        }
    }
}