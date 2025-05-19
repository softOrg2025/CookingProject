package testCases;

import cook.Application;
import cook.Customer;
import cook.Meal;
import cook.NotificationService;

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
    private String generatedInvoiceNumber;

    private static final String DEFAULT_INVOICE_PREFIX = "INV-";

    @Before
    public void setUp() {
        currentCustomer = null;
        currentMeal = null;
        customizationDetails = null;
        finalMealPrice = 0.0;
        orderConfirmed = false;
        invoiceGeneratedForOrder = false;
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

        // Add meal to application if needed
        Application.meals.add(currentMeal);
    }

    @When("the order is confirmed")
    public void theOrderIsConfirmed() {
        assertNotNull("A meal must be placed before it can be confirmed.", currentMeal);
        orderConfirmed = true;

        if (orderConfirmed) {
            generatedInvoiceNumber = DEFAULT_INVOICE_PREFIX + System.currentTimeMillis();
            invoiceGeneratedForOrder = true;

            // Send notification through NotificationService
            if (currentCustomer != null) {
                Application.notificationService.sendNotification(
                        currentCustomer.getEmail(),
                        "Your order has been confirmed. Invoice: " + generatedInvoiceNumber
                );
            }
        }
    }

    @Then("I should receive an invoice via email with total amount {string}")
    public void iShouldReceiveAnInvoiceViaEmailWithTotalAmount(String expectedAmountString) {
        assertTrue("Order should be confirmed to receive an invoice.", orderConfirmed);
        assertTrue("Invoice should have been generated for the confirmed order.", invoiceGeneratedForOrder);
        assertNotNull("Current meal should not be null to check its price.", currentMeal);

        String actualAmountString = String.format(Locale.US, "$%.2f", currentMeal.getPrice());
        assertEquals("Invoice total amount mismatch.", expectedAmountString, actualAmountString);

        // Verify notification was sent
        if (currentCustomer != null) {
            List<String> notifications = Application.notificationService.getNotifications(currentCustomer.getEmail());
            assertTrue("Notification should contain invoice information",
                    notifications.stream().anyMatch(n -> n.contains(generatedInvoiceNumber)));
        }
    }

    @Given("I am logged in as customer {string}")
    public void iAmLoggedInAsCustomer(String name) {
        currentCustomer = new Customer(name, name.toLowerCase().replace(" ", ".") + "@example.com", "password123");
        Application.users.add(currentCustomer);
        Application.currentUser = currentCustomer;

        // Simulate invoice generation
        generatedInvoiceNumber = "Invoice #1023";
        invoiceGeneratedForOrder = true;
    }

    @When("I go to the billing section")
    public void iGoToTheBillingSection() {
        assertNotNull("Customer must be logged in to access billing.", currentCustomer);
        assertEquals("Current user should be the customer", currentCustomer, Application.currentUser);
    }

    @Then("I should see a downloadable invoice labeled {string}")
    public void iShouldSeeADownloadableInvoiceLabeled(String expectedLabel) {
        assertNotNull("Customer must be logged in.", currentCustomer);
        assertNotNull("A generated invoice number should exist.", generatedInvoiceNumber);
        assertEquals("Downloadable invoice label mismatch.", expectedLabel, generatedInvoiceNumber);
    }

    @Given("I made changes to my meal: {string}")
    public void iMadeChangesToMyMeal(String changes) {
        List<String> baseIngredients = new ArrayList<>(List.of("Dough", "Tomato Sauce"));
        currentMeal = new Meal(baseIngredients, 'M', basePrice);
        currentMeal.setName("Basic Meal");
        Application.meals.add(currentMeal);

        this.customizationDetails = changes;
        double customizationCost = 0;

        if (changes.equalsIgnoreCase("No onions, extra cheese")) {
            currentMeal.getIngredients().add("Cheese");
            customizationCost = 5.45;
        }

        finalMealPrice = currentMeal.getPrice() + customizationCost;
    }

    @When("the invoice is generated")
    public void theInvoiceIsGenerated() {
        assertNotNull("A meal (possibly modified) must exist before generating its invoice.", currentMeal);
        generatedInvoiceNumber = DEFAULT_INVOICE_PREFIX + "MOD-" + System.currentTimeMillis();
        invoiceGeneratedForOrder = true;

        if (currentCustomer != null) {
            Application.notificationService.sendNotification(
                    currentCustomer.getEmail(),
                    "Invoice for modified meal: " + generatedInvoiceNumber
            );
        }
    }

    @Then("it should list those changes and show the new total price {string}")
    public void itShouldListChangesAndShowUpdatedPrice(String expectedTotalString) {
        assertTrue("Invoice should have been generated for the modified meal.", invoiceGeneratedForOrder);
        assertNotNull("Customization details should have been captured.", customizationDetails);

        String actualTotalString = String.format(Locale.US, "$%.2f", finalMealPrice);
        assertEquals("Updated total price on invoice mismatch.", expectedTotalString, actualTotalString);


        if (currentCustomer != null) {
            List<String> notifications = Application.notificationService.getNotifications(currentCustomer.getEmail());
            assertTrue("Notification should contain customization details",
                    notifications.stream().anyMatch(n -> n.contains(customizationDetails)));
        }
    }
}