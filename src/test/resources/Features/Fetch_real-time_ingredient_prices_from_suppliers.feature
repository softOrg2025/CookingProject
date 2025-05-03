Feature: Fetch real-time ingredient prices from suppliers

  Scenario: View current prices from suppliers
    Given I am logged in as a manager
    When I open the supplier section
    Then I should see updated prices for ingredients

  Scenario: Compare prices between suppliers
    Given I have multiple supplier options
    When I view the prices
    Then I should be able to compare them side-by-side

  Scenario: Select best-priced supplier
    Given I need to restock an item
    When I compare prices
    Then I can choose the supplier with the best offer
