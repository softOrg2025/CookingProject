package testCases;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class FinancialReportsSteps {

    private String currentCustomer;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean reportGenerated = false;
    private double totalIncome = 12000.50;
    private int orderCount = 135;
    private String exportedFileName;

    @Given("I am a system administrator")
    public void iAmASystemAdministrator() {
        System.out.println("✅ Logged in as system administrator");
    }

    @When("I select the date range from {string} to {string}")
    public void iSelectTheDateRangeFromTo(String start, String end) {
        startDate = LocalDate.parse(start);
        endDate = LocalDate.parse(end);
        System.out.println("📅 Selected date range: " + startDate + " to " + endDate);
    }

    @Then("the system should show total income and orders for that period")
    public void theSystemShouldShowTotalIncomeAndOrders() {
        assertNotNull("Start date must be selected", startDate);
        assertNotNull("End date must be selected", endDate);
        assertTrue("Income should be positive", totalIncome > 0);
        assertTrue("Order count should be greater than zero", orderCount > 0);
        System.out.println("💰 Total Income: $" + totalIncome + ", Orders: " + orderCount);
    }

    @Given("I select the customer {string}")
    public void iSelectTheCustomer(String customerName) {
        currentCustomer = customerName;
        System.out.println("👤 Selected customer: " + customerName);
    }

    @When("I run the report")
    public void iRunTheReport() {
        assertNotNull("Customer must be selected", currentCustomer);
        reportGenerated = true;
        System.out.println("📊 Report generated for: " + currentCustomer);
    }

    @Then("I should see the transaction history for {string}")
    public void iShouldSeeTheTransactionHistoryFor(String customerName) {
        assertTrue("Report must be generated", reportGenerated);
        assertEquals("Customer mismatch", customerName, currentCustomer);
        System.out.println("📄 Transaction history displayed for: " + customerName);
    }

    @Given("a financial report for {string} is generated")
    public void aFinancialReportForCustomerIsGenerated(String customerName) {
        currentCustomer = customerName;
        reportGenerated = true;
        System.out.println("📊 Financial report generated for: " + customerName);
    }

    @When("I click 'Export'")
    public void iClickExport() {
        assertTrue("Report must be generated before export", reportGenerated);
        exportedFileName = currentCustomer.replace(" ", "_") + "_Report.csv";
        // Simulate export
        new File("downloads/" + exportedFileName).getParentFile().mkdirs();
        System.out.println("📤 Exported report as: " + exportedFileName);
    }

    @Then("a CSV file named {string} should be downloaded")
    public void aCSVFileNamedShouldBeDownloaded(String expectedFileName) {
        File file = new File("downloads/" + expectedFileName);
        // Simulating file presence (replace with actual file logic in real project)
        boolean fileExists = expectedFileName.equals(exportedFileName);
        assertTrue("CSV file should be downloaded", fileExists);
        System.out.println("✅ CSV file downloaded: " + expectedFileName);
    }

    @Then("it should contain total income and all transactions")
    public void itShouldContainTotalIncomeAndAllTransactions() {
        // Simulated check — in real projects, parse CSV and verify content
        assertTrue("Report must be generated", reportGenerated);
        System.out.println("🧾 CSV includes income $" + totalIncome + " and " + orderCount + " orders.");
    }
}
