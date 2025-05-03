Feature: Generate customer invoice

  Scenario: Receive invoice after order
    Given I place a custom meal order with "extra avocado"
    When the order is confirmed
    Then I should receive an invoice via email with total amount "$15.99"

  Scenario: View invoice in user account
    Given I am logged in as customer "Fatima Ali"
    When I go to the billing section
    Then I should see a downloadable invoice labeled "Invoice #1023"

  Scenario: Invoice includes customization details
    Given I made changes to my meal: "No onions, extra cheese"
    When the invoice is generated
    Then it should list those changes and show the new total price "$17.45"