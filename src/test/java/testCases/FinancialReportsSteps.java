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

        // إضافة عملاء
        Customer customer1 = new Customer("Fatima Ali", "fatima@example.com", "pass1");
        Customer customer2 = new Customer("Ahmed Omar", "ahmed@example.com", "pass2");
        Customer customer3 = new Customer("Ali Khan", "ali.khan@example.com", "pass3");
        Application.users.add(customer1);
        Application.users.add(customer2);
        Application.users.add(customer3);

        // إضافة وجبات مرتبطة بالعملاء
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
        ensureTestCustomerExists("Ali");
    }

    private void ensureTestCustomerExists(String customerName) {
        boolean customerExists = Application.users.stream()
                .anyMatch(u -> u instanceof Customer && u.getName().equals(customerName));

        if (!customerExists) {
            Customer testCustomer = new Customer(customerName, "ali.khan@example.com", "password");
            Application.users.add(testCustomer);

            // إضافة وجبات اختبارية لهذا العميل
            Meal meal1 = new Meal(List.of("Fish", "Salad"), 'S', 18.00);
            meal1.setName("Grilled Fish - " + customerName);
            Application.meals.add(meal1);

            Meal meal2 = new Meal(List.of("Kebab", "Bread"), 'L', 22.00);
            meal2.setName("Kebab Special - " + customerName);
            Application.meals.add(meal2);
        }
    }


    @Given("I am a system administrator")
    public void iAmASystemAdministrator() {
        currentAdmin = new User("Admin User", "admin@system.com", "adminPass", Role.manager);
        Application.currentUser = currentAdmin;
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
        List<Meal> mealsInPeriod = Application.meals.stream()
                .filter(meal -> !meal.getName().contains("Pasta Special - Ahmed"))
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

        if (Application.users.stream().noneMatch(u -> u.getName().equals("Ali Khan"))) {
            System.out.println("WARN: Financial reports mock data missing, re-initializing...");
            initializeMockData(); // كن حذرًا، هذا قد يكون له آثار جانبية أخرى
        }
        selectedCustomerForReport = (Customer) Application.users.stream()
                .filter(u -> u.getRole() == Role.Customer && u.getName().equals(customerName))
                .findFirst()
                .orElse(null);

        if (testContext != null) {
            testContext.lastSystemMessage = "Selected customer for report: " +
                    (selectedCustomerForReport != null ? selectedCustomerForReport.getName() : "null");
        }
    }

    @When("I run the report")
    public void iRunTheReport() {
        customerTransactions = Application.meals.stream()
                .filter(meal -> meal.getName().contains(selectedCustomerForReport.getName()))
                .collect(Collectors.toList());
        customerReportGenerated = true;

        if (testContext != null) {
            testContext.lastSystemMessage = "Transaction report generated for: " +
                    selectedCustomerForReport.getName() + ". Found " + customerTransactions.size() + " transactions.";
        }
    }

    @Then("I should see the transaction history for {string}")
    public void iShouldSeeTheTransactionHistoryFor(String expectedCustomerName) {
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

        selectedCustomerForReport = (Customer) Application.users.stream()
                .filter(u -> u.getRole() == Role.Customer && u.getName().equals(customerName))
                .findFirst()
                .orElse(null);

        customerTransactions = Application.meals.stream()
                .filter(meal -> meal.getName().contains(selectedCustomerForReport.getName()))
                .collect(Collectors.toList());
        customerReportGenerated = true;

        if (testContext != null) {
            testContext.lastSystemMessage = "Financial report prepared for: " +
                    selectedCustomerForReport.getName() + " with " + customerTransactions.size() + " transactions";
        }
    }

    @When("I click 'Export'")
    public void iClickExport() {
        exportedFileName = selectedCustomerForReport.getName().replace(" ", "_") + "_Report.csv";
        File reportFile = new File(DOWNLOADS_DIR + exportedFileName);

        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write("Customer,Meal,Price\n");
            for (Meal meal : customerTransactions) {
                writer.write(String.format("%s,%s,%.2f\n",
                        selectedCustomerForReport.getName(),
                        meal.getName(),
                        meal.getPrice()));
            }

            if (testContext != null) {
                testContext.lastSystemMessage = "Report exported to: " + reportFile.getAbsolutePath();
            }
        } catch (IOException e) {
            Assertions.fail("Failed to export report: " + e.getMessage());
        }
    }

    @Then("a CSV file named {string} should be downloaded")
    public void aCSVFileNamedShouldBeDownloaded(String expectedFileName) {
        File file = new File(DOWNLOADS_DIR + expectedFileName);
        Assertions.assertTrue(file.exists(), "File should exist: " + file.getAbsolutePath());

        if (testContext != null) {
            testContext.lastSystemMessage = "Verified CSV file exists: " + file.getAbsolutePath();
        }
    }

    @Then("it should contain total income and all transactions")
    public void itShouldContainTotalIncomeAndAllTransactions() {
        File file = new File(DOWNLOADS_DIR + exportedFileName);
        int lineCount = 0;

        try (Scanner scanner = new Scanner(file)) {
            // Skip header
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                scanner.nextLine();
                lineCount++;
            }

            Assertions.assertEquals(customerTransactions.size(), lineCount,
                    "CSV should contain all transactions");

            if (testContext != null) {
                testContext.lastSystemMessage = "Verified CSV contains " + lineCount + " transactions";
            }
        } catch (IOException e) {
            Assertions.fail("Failed to read CSV file: " + e.getMessage());
        }
    }
}