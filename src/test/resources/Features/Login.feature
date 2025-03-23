Feature: Customer LogIn

  As a customer
  I want to log in to the cooking system and also log out
  So that I can access my account and manage my preferences

  Scenario: Successful login with valid credentials
    Given the user is on the login page
    When the user enters valid username "testUser" and password "password123"
    Then the user should be redirected to the dashboard
    And the system should display a welcome message

  Scenario: Failed login with invalid username
    Given the user is on the login page
    When the user enters invalid username "wrongUser" and password "password123"
    Then the system should display an error message "Invalid username or password"

  Scenario: Failed login with invalid password
    Given the user is on the login page
    When the user enters valid username "testUser" and invalid password "wrongPassword"
    Then the system should display an error message "Invalid username or password"

  Scenario: Failed login with empty credentials
    Given the user is on the login page
    When the user enters empty username and password
    Then the system should display an error message "Username and password cannot be empty"
