Feature: Chef receives cooking task notifications
  Scenario: Alert when task is created
    Given a task is assigned to me
    When it is saved in the system
    Then I should receive a notification

  Scenario: Reminder before task deadline
    Given I have a task due in an hour
    When the time is near
    Then I should get a reminder alert

  Scenario: Notify changes to task schedule
    Given my task is rescheduled
    When the update is saved
    Then I should receive a notification about the change

#    done