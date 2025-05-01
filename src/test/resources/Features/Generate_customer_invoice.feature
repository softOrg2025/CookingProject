Feature: Generate customer invoice
  Scenario: Receive invoice after order
    Given I place a custom meal order
    When the order is confirmed
    Then I should receive an invoice via email

  Scenario: View invoice in user account
    Given I am a customer
    When I go to my billing section
    Then I should see a downloadable copy of my invoice

  Scenario: Invoice includes customization details
    Given I made changes to my meal
    When the invoice is generated
    Then it should list the changes and updated price