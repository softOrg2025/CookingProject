#Feature: Admin generates financial reports
#  Scenario: Generate monthly report
#    Given I am a system administrator
#    When I select a date range
#    Then the system should show total income and orders
#
#  Scenario: Filter report by customer
#    Given I select a specific customer
#    When I run the report
#    Then I should see that customer’s transaction history
#
#  Scenario: Export report to CSV
#    Given a financial report is generated
#    When I click 'Export'
#    Then a CSV file should download