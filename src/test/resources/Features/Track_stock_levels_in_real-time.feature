Feature: Track stock levels in real-time
  Scenario: View current stock levels
    Given I am a kitchen manager
    When I open the inventory dashboard
    Then I should see updated quantities of all ingredients

  Scenario: Update stock after usage
    Given an ingredient is used in a meal
    When the meal is confirmed
    Then the ingredient stock should decrease accordingly

  Scenario: Highlight low-stock items
    Given stock levels are updated
    When any item goes below threshold
    Then the system should highlight it in red