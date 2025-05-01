package testCases;

import io.cucumber.java.en.*;

public class FinancialReportsSteps {

    @Given("I am a system administrator")
    public void i_am_a_system_administrator() {
        System.out.println("Logged in as system administrator");
    }

    @When("I select a date range")
    public void i_select_a_date_range() {
        System.out.println("Selected date range");
    }

    @Then("the system should show total income and orders")
    public void the_system_should_show_total_income_and_orders() {
        System.out.println("Displayed total income and orders");
    }

    @Given("I select a specific customer")
    public void i_select_a_specific_customer() {
        System.out.println("Selected a specific customer");
    }

    @When("I run the report")
    public void i_run_the_report() {
        System.out.println("Ran the report");
    }

    @Then("I should see that customer’s transaction history")
    public void i_should_see_that_customer_s_transaction_history() {
        System.out.println("Displayed customer's transaction history");
    }

    @Given("a financial report is generated")
    public void a_financial_report_is_generated() {
        System.out.println("Financial report generated");
    }

    @When("I click 'Export'")
    public void i_click_export() {
        System.out.println("Clicked export");
    }

    @Then("a CSV file should download")
    public void a_csv_file_should_download() {
        System.out.println("CSV file downloaded");
    }
}
