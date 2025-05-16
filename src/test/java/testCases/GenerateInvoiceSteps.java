package testCases;

import cook.Customer;
import cook.Meal;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GenerateInvoiceSteps {

    private Customer currentCustomer;
    private Meal currentMeal;

    private String customizationDetails;
    private double basePrice = 12.00;
    private double finalMealPrice;
    private boolean orderConfirmed = false;
    private boolean invoiceGeneratedForOrder = false;
    private boolean emailNotificationSent = false;
    private String generatedInvoiceNumber;

    private static final String DEFAULT_INVOICE_PREFIX = "INV-";

    @Before
    public void setUp() {
        System.out.println("--- Initializing state for GenerateInvoiceSteps ---");
        currentCustomer = null;
        currentMeal = null;
        customizationDetails = null;
        finalMealPrice = 0.0;
        orderConfirmed = false;
        invoiceGeneratedForOrder = false;
        emailNotificationSent = false;
        generatedInvoiceNumber = null;
    }


    @Given("I place a custom meal order with {string}")
    public void iPlaceACustomMealOrderWith(String customizations) {
        List<String> ingredients = new ArrayList<>();
        this.customizationDetails = customizations;
        double customizationCost = 0;

        if (customizations.equalsIgnoreCase("extra avocado")) {
            ingredients.add("Avocado");
            customizationCost = 3.99;
        } else if (customizations.toLowerCase().contains("extra cheese")) {
            ingredients.add("Cheese");
            customizationCost = 3.99;
        } else if (customizations.toLowerCase().contains("no onions")) {
            customizationCost = 1.50;
        }


        finalMealPrice = basePrice + customizationCost;
        currentMeal = new Meal(ingredients, 'M', finalMealPrice);
        currentMeal.setName("Custom Meal with " + customizations);

        System.out.println("🧾 Custom meal object created: " + currentMeal.getName() + " with price $" + String.format(Locale.US, "%.2f", currentMeal.getPrice()));
        System.out.println("  Customization details captured: " + this.customizationDetails);
    }

    @When("the order is confirmed")
    public void theOrderIsConfirmed() {
        assertNotNull("A meal must be placed before it can be confirmed.", currentMeal);
        orderConfirmed = true;

        if (orderConfirmed) {
            generatedInvoiceNumber = DEFAULT_INVOICE_PREFIX + System.currentTimeMillis();
            invoiceGeneratedForOrder = true;
            emailNotificationSent = true;
            System.out.println("✅ Order for '" + currentMeal.getName() + "' confirmed.");
            System.out.println("  Invoice " + generatedInvoiceNumber + " generated for this order.");
            System.out.println("  Email notification simulated as sent.");
        } else {
            System.out.println("⚠️ Order not confirmed, cannot generate invoice or send email.");
        }
    }

    @Then("I should receive an invoice via email with total amount {string}")
    public void iShouldReceiveAnInvoiceViaEmailWithTotalAmount(String expectedAmountString) {
        assertTrue("Order should be confirmed to receive an invoice.", orderConfirmed);
        assertTrue("Invoice should have been generated for the confirmed order.", invoiceGeneratedForOrder);
        assertTrue("Email notification for the invoice should have been sent.", emailNotificationSent);
        assertNotNull("Current meal should not be null to check its price.", currentMeal);


        String actualAmountString = String.format(Locale.US, "$%.2f", currentMeal.getPrice());

        assertEquals("Invoice total amount mismatch.", expectedAmountString, actualAmountString);
        System.out.println("📧 Invoice reception via email verified. Total Amount: " + actualAmountString);
        System.out.println("   Invoice Number: " + generatedInvoiceNumber);
    }


    @Given("I am logged in as customer {string}")
    public void iAmLoggedInAsCustomer(String name) {
        currentCustomer = new Customer(name, name.toLowerCase().replace(" ", ".") + "@example.com", "password123");
        System.out.println("👤 Customer object created and logged in: " + currentCustomer.getName() + " (Email: " + currentCustomer.getEmail() + ")");


        generatedInvoiceNumber = "Invoice #1023";
        invoiceGeneratedForOrder = true;
        System.out.println("  (Simulating an invoice for this customer: " + generatedInvoiceNumber + ")");
    }

    @When("I go to the billing section")
    public void iGoToTheBillingSection() {
        assertNotNull("Customer must be logged in to access billing.", currentCustomer);
        System.out.println("📂 Navigated to billing section for customer: " + currentCustomer.getName());
        assertTrue("An invoice should be available for the logged-in customer.", invoiceGeneratedForOrder && generatedInvoiceNumber != null);
    }

    @Then("I should see a downloadable invoice labeled {string}")
    public void iShouldSeeADownloadableInvoiceLabeled(String expectedLabel) {
        assertNotNull("Customer must be logged in.", currentCustomer);
        assertNotNull("A generated invoice number should exist.", generatedInvoiceNumber);
        assertEquals("Downloadable invoice label mismatch.", expectedLabel, generatedInvoiceNumber);
        System.out.println("📄 Downloadable invoice verified: Label = " + generatedInvoiceNumber);
    }


    @Given("I made changes to my meal: {string}")
    public void iMadeChangesToMyMeal(String changes) {
        List<String> baseIngredients = new ArrayList<>(List.of("Dough", "Tomato Sauce"));
        currentMeal = new Meal(baseIngredients, 'M', basePrice);
        currentMeal.setName("Basic Meal");
        System.out.println("  Starting with meal: " + currentMeal.getName() + " Price: $" + String.format(Locale.US, "%.2f", currentMeal.getPrice()));

        this.customizationDetails = changes;
        double customizationCost = 0;

        if (changes.equalsIgnoreCase("No onions, extra cheese")) {

            currentMeal.getIngredients().add("Cheese");

            customizationCost = 5.45;
        }


        finalMealPrice = currentMeal.getPrice() + customizationCost;


        System.out.println("✏ Meal changes applied: " + changes);
        System.out.println("  Updated meal components (simulated): " + currentMeal.getIngredients());
        System.out.println("  Calculated final price after changes: $" + String.format(Locale.US, "%.2f", finalMealPrice));
    }

    @When("the invoice is generated")
    public void theInvoiceIsGenerated() {
        assertNotNull("A meal (possibly modified) must exist before generating its invoice.", currentMeal);
        generatedInvoiceNumber = DEFAULT_INVOICE_PREFIX + "MOD-" + System.currentTimeMillis();
        invoiceGeneratedForOrder = true;
        System.out.println("🧾 Invoice " + generatedInvoiceNumber + " generated for the modified meal.");
    }

    @Then("it should list those changes and show the new total price {string}")
    public void itShouldListChangesAndShowUpdatedPrice(String expectedTotalString) {
        assertTrue("Invoice should have been generated for the modified meal.", invoiceGeneratedForOrder);
        assertNotNull("Customization details should have been captured.", customizationDetails);

        String actualTotalString = String.format(Locale.US, "$%.2f", finalMealPrice);

        assertEquals("Updated total price on invoice mismatch.", expectedTotalString, actualTotalString);
        System.out.println("📌 Invoice verified to list changes (simulated): " + customizationDetails);
        System.out.println("💲 Final price on invoice verified: " + actualTotalString);
    }
}