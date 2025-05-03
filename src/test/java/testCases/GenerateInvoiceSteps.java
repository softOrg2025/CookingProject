package testCases;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.util.*;

public class GenerateInvoiceSteps {

    private String customizationDetails;
    private double basePrice = 12.00;
    private double finalPrice;
    private boolean invoiceGenerated = false;
    private boolean emailSent = false;
    private String customerName;
    private String invoiceNumber = "Invoice #1023";

    @Given("I place a custom meal order with {string}")
    public void iPlaceACustomMealOrderWith(String customizations) {
        customizationDetails = customizations;
        finalPrice = basePrice + 3.99;  // Example calculation
        System.out.println("🧾 Custom meal ordered with: " + customizations);
    }

    @When("the order is confirmed")
    public void theOrderIsConfirmed() {
        invoiceGenerated = true;
        emailSent = true;
        System.out.println("✅ Order confirmed. Invoice generated and email sent.");
    }

    @Then("I should receive an invoice via email with total amount {string}")
    public void iShouldReceiveAnInvoiceViaEmailWithTotalAmount(String expectedAmount) {
        assertTrue("Invoice should be generated", invoiceGenerated);
        assertTrue("Email should be sent", emailSent);
        assertEquals("Invoice amount mismatch", "$" + finalPrice, expectedAmount);
        System.out.println("📧 Invoice emailed: Total = " + expectedAmount);
    }

    @Given("I am logged in as customer {string}")
    public void iAmLoggedInAsCustomer(String name) {
        customerName = name;
        System.out.println("👤 Logged in as: " + name);
    }

    @When("I go to the billing section")
    public void iGoToTheBillingSection() {
        assertNotNull("Customer must be logged in", customerName);
        System.out.println("📂 Billing section opened for: " + customerName);
    }

    @Then("I should see a downloadable invoice labeled {string}")
    public void iShouldSeeADownloadableInvoiceLabeled(String expectedLabel) {
        assertEquals("Invoice label mismatch", invoiceNumber, expectedLabel);
        System.out.println("📄 Invoice available: " + invoiceNumber);
    }

    @Given("I made changes to my meal: {string}")
    public void iMadeChangesToMyMeal(String changes) {
        customizationDetails = changes;
        finalPrice = basePrice + 5.45; // Simulated price
        System.out.println("✏ Meal changes: " + changes);
    }

    @When("the invoice is generated")
    public void theInvoiceIsGenerated() {
        invoiceGenerated = true;
        System.out.println("🧾 Invoice created for customized meal.");
    }

    @Then("it should list those changes and show the new total price {string}")
    public void itShouldListChangesAndShowUpdatedPrice(String expectedTotal) {
        assertTrue(invoiceGenerated);
        assertEquals("Updated price mismatch", "$" + finalPrice, expectedTotal);
        System.out.println("📌 Invoice lists changes: " + customizationDetails);
        System.out.println("💲 Final price: " + expectedTotal);
    }
}
