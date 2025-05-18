Feature: Admin generates financial reports

  Scenario: Generate monthly report
    Given I am a system administrator
    When I select the date range from "2025-04-01" to "2025-04-30"
    Then the system should show total income and orders for that period

  Scenario: Filter report by customer
    Given I select the customer "Ali Khan"
    When I run the report
    Then I should see the transaction history for "Ali Khan"

  Scenario: Export report to CSV
    Given a financial report for "Ali Khan" is generated
    When I click 'Export'
    Then a CSV file named "Ali_Khan_Report.csv" should be downloaded
    And it should contain total income and all transactions