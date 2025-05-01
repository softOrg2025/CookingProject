Feature: Manager receives low-stock alerts

  Scenario: Notify when threshold is reached
    Given an ingredient drops below its restock level
    When the system detects it
    Then the kitchen manager should be notified

  Scenario: Include quantity and item name in alert
    Given an alert is triggered
    When the manager opens the notification
    Then it should list the item name and quantity left

  Scenario: Group alerts for multiple items
    Given several items are low
    When the alert is generated
    Then it should combine them in one message